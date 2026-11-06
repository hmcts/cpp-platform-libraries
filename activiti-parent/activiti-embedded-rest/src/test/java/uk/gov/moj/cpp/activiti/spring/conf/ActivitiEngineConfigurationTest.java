package uk.gov.moj.cpp.activiti.spring.conf;

import static jakarta.transaction.Status.STATUS_NO_TRANSACTION;
import static org.activiti.engine.impl.history.HistoryLevel.AUDIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.moj.cpp.activiti.spring.CustomProcessEngineConfiguration;

import javax.naming.NamingException;
import javax.sql.DataSource;

import jakarta.transaction.TransactionManager;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jndi.JndiTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
class ActivitiEngineConfigurationTest {

    private static final String TRANSACTION_MANAGER_JNDI_NAME = "java:jboss/TransactionManager";
    private static final String APP_NAME_JNDI_NAME = "java:app/AppName";
    private static final String APP_NAME = "stagingprosecutorsspi-service";

    @Mock
    private TransactionManager transactionManager;

    @Mock
    private DataSource dataSource;

    @Mock
    private ProcessEngine processEngine;

    @Mock
    private ProcessEngineFactoryBean processEngineFactoryBean;

    @Mock
    private ProcessEngineConfigurationImpl processEngineConfiguration;

    @AfterEach
    void clearSpringTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void shouldCreateAJakartaJtaTransactionManagerOverTheWildFlyTransactionManager() throws Exception {
        final PlatformTransactionManager platformTransactionManager;
        try (MockedConstruction<JndiTemplate> jndiTemplates = mockConstruction(JndiTemplate.class, (jndiTemplate, context) ->
                when(jndiTemplate.lookup(TRANSACTION_MANAGER_JNDI_NAME, TransactionManager.class)).thenReturn(transactionManager))) {
            platformTransactionManager = new ActivitiEngineConfiguration().transactionManager();

            verify(jndiTemplates.constructed().get(0)).lookup(TRANSACTION_MANAGER_JNDI_NAME, TransactionManager.class);
        }

        assertThat(platformTransactionManager, instanceOf(JakartaJtaTransactionManager.class));

        when(transactionManager.getStatus()).thenReturn(STATUS_NO_TRANSACTION);
        platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

        verify(transactionManager).begin();
    }

    @Test
    void shouldConfigureTheProcessEngineForTheWildFlyDataSourceAndTransactionManager() {
        final ProcessEngineConfigurationImpl configuredProcessEngine;
        try (MockedConstruction<JndiTemplate> jndiTemplates = mockConstruction(JndiTemplate.class, (jndiTemplate, context) -> {
            lenient().when(jndiTemplate.lookup(any(), eq(DataSource.class))).thenReturn(dataSource);
            lenient().when(jndiTemplate.lookup(APP_NAME_JNDI_NAME, String.class)).thenReturn(APP_NAME);
            lenient().when(jndiTemplate.lookup(TRANSACTION_MANAGER_JNDI_NAME, TransactionManager.class)).thenReturn(transactionManager);
        })) {
            configuredProcessEngine = new ActivitiEngineConfiguration().processEngineConfiguration();

            assertThat(jndiTemplates.constructed().size(), is(3));
        }

        assertThat(configuredProcessEngine, instanceOf(CustomProcessEngineConfiguration.class));
        final SpringProcessEngineConfiguration springProcessEngineConfiguration = (SpringProcessEngineConfiguration) configuredProcessEngine;
        assertThat(springProcessEngineConfiguration.getDatabaseType(), is("postgres"));
        assertThat(springProcessEngineConfiguration.getDatabaseSchemaUpdate(), is("false"));
        assertThat(springProcessEngineConfiguration.isTransactionsExternallyManaged(), is(true));
        assertThat(springProcessEngineConfiguration.getTransactionManager(), instanceOf(JakartaJtaTransactionManager.class));
        assertThat(springProcessEngineConfiguration.isJobExecutorActivate(), is(true));
        assertThat(springProcessEngineConfiguration.isAsyncExecutorEnabled(), is(false));
        assertThat(springProcessEngineConfiguration.isAsyncExecutorActivate(), is(false));
        assertThat(springProcessEngineConfiguration.getHistoryLevel(), is(AUDIT));
        assertThat(springProcessEngineConfiguration.getDeploymentName(), is(APP_NAME));
        assertThat(springProcessEngineConfiguration.getDeploymentMode(), is("custom"));
        assertThat(springProcessEngineConfiguration.getDeploymentResources().length, is(1));
        assertThat(springProcessEngineConfiguration.getDeploymentResources()[0].getFilename(), is("test.bpmn20.xml"));
        assertThat(springProcessEngineConfiguration.isCreateDiagramOnDeploy(), is(true));
        assertThat(springProcessEngineConfiguration.getExpressionManager(), instanceOf(CdiAwareExpressionManager.class));
    }

    @Test
    void shouldReportAConfigurationProblemWhenTheAppNameCannotBeLookedUp() {
        try (MockedConstruction<JndiTemplate> jndiTemplates = mockConstruction(JndiTemplate.class, (jndiTemplate, context) ->
                when(jndiTemplate.lookup(APP_NAME_JNDI_NAME, String.class)).thenThrow(new NamingException("no app name")))) {
            final ActivitiEngineConfiguration activitiEngineConfiguration = new ActivitiEngineConfiguration();

            assertThrows(ActivitiEngineConfigException.class, activitiEngineConfiguration::processEngineConfiguration);
            assertThat(jndiTemplates.constructed().size(), is(1));
        }
    }

    @Test
    void shouldBuildTheProcessEngineFactoryBeanFromTheProcessEngineConfiguration() {
        final ActivitiEngineConfiguration activitiEngineConfiguration = spy(new ActivitiEngineConfiguration());
        doReturn(processEngineConfiguration).when(activitiEngineConfiguration).processEngineConfiguration();

        final ProcessEngineFactoryBean factoryBean = activitiEngineConfiguration.processEngineFactoryBean();

        assertThat(factoryBean.getProcessEngineConfiguration(), is(sameInstance(processEngineConfiguration)));
    }

    @Test
    void shouldTakeTheProcessEngineFromTheProcessEngineFactoryBean() throws Exception {
        final ActivitiEngineConfiguration activitiEngineConfiguration = spy(new ActivitiEngineConfiguration());
        doReturn(processEngineFactoryBean).when(activitiEngineConfiguration).processEngineFactoryBean();
        when(processEngineFactoryBean.getObject()).thenReturn(processEngine);

        assertThat(activitiEngineConfiguration.processEngine(), is(sameInstance(processEngine)));
    }

    @Test
    void shouldExposeTheServicesOfTheProcessEngine(@Mock final RepositoryService repositoryService,
                                                    @Mock final RuntimeService runtimeService,
                                                    @Mock final TaskService taskService,
                                                    @Mock final HistoryService historyService,
                                                    @Mock final FormService formService,
                                                    @Mock final IdentityService identityService,
                                                    @Mock final ManagementService managementService) throws Exception {
        final ActivitiEngineConfiguration activitiEngineConfiguration = spy(new ActivitiEngineConfiguration());
        doReturn(processEngine).when(activitiEngineConfiguration).processEngine();
        when(processEngine.getRepositoryService()).thenReturn(repositoryService);
        when(processEngine.getRuntimeService()).thenReturn(runtimeService);
        when(processEngine.getTaskService()).thenReturn(taskService);
        when(processEngine.getHistoryService()).thenReturn(historyService);
        when(processEngine.getFormService()).thenReturn(formService);
        when(processEngine.getIdentityService()).thenReturn(identityService);
        when(processEngine.getManagementService()).thenReturn(managementService);

        assertThat(activitiEngineConfiguration.repositoryService(), is(sameInstance(repositoryService)));
        assertThat(activitiEngineConfiguration.runtimeService(), is(sameInstance(runtimeService)));
        assertThat(activitiEngineConfiguration.taskService(), is(sameInstance(taskService)));
        assertThat(activitiEngineConfiguration.historyService(), is(sameInstance(historyService)));
        assertThat(activitiEngineConfiguration.formService(), is(sameInstance(formService)));
        assertThat(activitiEngineConfiguration.identityService(), is(sameInstance(identityService)));
        assertThat(activitiEngineConfiguration.managementService(), is(sameInstance(managementService)));
    }
}
