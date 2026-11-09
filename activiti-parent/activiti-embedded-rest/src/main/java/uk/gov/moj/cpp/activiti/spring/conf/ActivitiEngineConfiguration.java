package uk.gov.moj.cpp.activiti.spring.conf;

import uk.gov.moj.cpp.activiti.spring.CustomProcessEngineConfiguration;

import java.io.IOException;
import java.util.Locale;

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
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jndi.JndiTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ActivitiEngineConfiguration {

    private static final String TRANSACTION_MANAGER_JNDI_NAME = "java:jboss/TransactionManager";

    @Value("${cpp.activiti.datasource.name}")
    private String dataSourceJndiName;

    @Value("${cpp.activiti.databaseSchemaUpdate}")
    private boolean databaseSchemaUpdate = false;

    @Value("${cpp.activiti.transactionsExternallyManaged}")
    private boolean transactionsExternallyManaged = true;

    @Value("${cpp.activiti.jobExecutorActivate}")
    private boolean jobExecutorActivate = true;

    @Value("${cpp.activiti.asyncExecutorEnabled}")
    private boolean asyncExecutorEnabled = false;

    @Value("${cpp.activiti.asyncExecutorActivate}")
    private boolean asyncExecutorActivate = false;

    @Value("${cpp.activiti.createDiagramOnDeploy}")
    private boolean createDiagramOnDeploy = true;

    /**
     * Activiti history level: none, activity, audit or full. Defaults to audit, the level this library has always
     * used. A context that ran its own engine on J17 with history switched off (e.g. staging-prosecutors-spi) sets
     * none to keep writing nothing to the act_hi_* tables.
     */
    @Value("${cpp.activiti.historyLevel:audit}")
    private String historyLevel = HistoryLevel.AUDIT.getKey();

    @Bean
    public DataSource dataSource() throws NamingException {
        return new JndiTemplate().lookup(dataSourceJndiName, DataSource.class);
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() throws NamingException {
        // Activiti calls this manager around every engine command. On its job executor there is no container
        // transaction, so the manager has to begin one: otherwise each statement commits on its own and a failed job
        // cannot roll back the JMS messages it has already sent (PEG-3680).
        return new JakartaJtaTransactionManager(new JndiTemplate().lookup(TRANSACTION_MANAGER_JNDI_NAME, TransactionManager.class));
    }

    @Bean(name="processEngineFactoryBean")
    public ProcessEngineFactoryBean processEngineFactoryBean()  {
        final ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
        return factoryBean;
    }

    @Bean(name="processEngine")
    public ProcessEngine processEngine() throws Exception {
        // Safe to call the getObject() on the @Bean annotated processEngineFactoryBean(), will be
        // the fully initialized object instanced from the factory and will NOT be created more than once
        return processEngineFactoryBean().getObject();
    }

    @Bean(name="processEngineConfiguration")
    public ProcessEngineConfigurationImpl processEngineConfiguration() {

        final Resource[] resources;
        try {
            resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:/processes/*.bpmn20.xml");
            final String appName = new JndiTemplate().lookup("java:app/AppName", String.class);

            final CustomProcessEngineConfiguration processEngineConfiguration = new CustomProcessEngineConfiguration();
            processEngineConfiguration.setDataSource(dataSource());
            processEngineConfiguration.setDatabaseType("postgres");
            processEngineConfiguration.setDatabaseSchemaUpdate(String.valueOf(databaseSchemaUpdate));
            processEngineConfiguration.setTransactionsExternallyManaged(transactionsExternallyManaged);
            processEngineConfiguration.setTransactionManager(transactionManager());
            processEngineConfiguration.setJobExecutorActivate(jobExecutorActivate);
            processEngineConfiguration.setAsyncExecutorEnabled(asyncExecutorEnabled);
            processEngineConfiguration.setAsyncExecutorActivate(asyncExecutorActivate);
            processEngineConfiguration.setHistoryLevel(historyLevel());
            processEngineConfiguration.setDeploymentName(appName);
            processEngineConfiguration.setDeploymentResources(resources);
            processEngineConfiguration.setDeploymentMode("custom");
            processEngineConfiguration.setCreateDiagramOnDeploy(createDiagramOnDeploy);
            processEngineConfiguration.setExpressionManager(new CdiAwareExpressionManager());
            return processEngineConfiguration;
        } catch (IOException|NamingException e ) {
            throw new ActivitiEngineConfigException("Problems while configuring Activiti Engine", e);
        }
    }

    private HistoryLevel historyLevel() {
        final String key = historyLevel == null || historyLevel.isBlank() ? HistoryLevel.AUDIT.getKey() : historyLevel.trim().toLowerCase(Locale.ROOT);
        try {
            return HistoryLevel.getHistoryLevelForKey(key);
        } catch (final RuntimeException e) {
            throw new ActivitiEngineConfigException("Invalid cpp.activiti.historyLevel '" + historyLevel + "': expected one of none, activity, audit, full", e);
        }
    }

    @Bean
    public RepositoryService repositoryService() throws Exception {
        return processEngine().getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() throws Exception {
        return processEngine().getRuntimeService();
    }

    @Bean
    public TaskService taskService() throws Exception {
        return processEngine().getTaskService();
    }

    @Bean
    public HistoryService historyService() throws Exception {
        return processEngine().getHistoryService();
    }

    @Bean
    public FormService formService() throws Exception {
        return processEngine().getFormService();
    }

    @Bean
    public IdentityService identityService() throws Exception {
        return processEngine().getIdentityService();
    }

    @Bean
    public ManagementService managementService() throws Exception {
        return processEngine().getManagementService();
    }
}
