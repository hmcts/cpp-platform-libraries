package uk.gov.moj.cpp.activiti.cdi;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static java.util.Collections.emptyMap;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessManagerServiceTest {

    private static final String PROCESS_INSTANCE_ID = randomUUID().toString();

    @Mock
    private Logger logger;

    @Mock
    private RuntimeService runtimeService;

    @InjectMocks
    private ProcessManagerService processManagerService;

    @Test
    public void shouldStartProcess() {
        final String processModelId = "CPP-Workflow-Main";
        final Map<String, Object> actualParameters = emptyMap();

        processManagerService.startProcessModel(processModelId, actualParameters);

        verify(runtimeService, times(1)).startProcessInstanceByKey(eq(processModelId), eq(actualParameters));
    }

    @Test
    public void shouldSignalProcessTask() {
        final String flowStep = "waitForRecordChargedCaseImportResult";

        processManagerService.signalProcess(PROCESS_INSTANCE_ID, flowStep);
        verify(runtimeService, times(1)).signal(PROCESS_INSTANCE_ID);
    }

    @Test
    public void shouldSignalProcessTaskWithParameters() {
        final String flowStep = "waitForConsolidateChargedCasesResponse";
        final Map<String, Object> parameters = emptyMap();

        processManagerService.signalProcessWithParameters(PROCESS_INSTANCE_ID, flowStep, parameters);

        verify(runtimeService, times(1)).signal(PROCESS_INSTANCE_ID, parameters);
    }

    @Test
    public void shouldSignalProcessTaskWithParametersAndWorkflowId() throws TaskNotAvailableException {
        final Map<String, Object> parameters = emptyMap();

        final String workflowId = "workflow id";
        final String activitiFlowStep = "myBeanName";
        final String executionId = "execution id";

        final Execution execution = mock(Execution.class);
        final ExecutionQuery executionQuery = mock(ExecutionQuery.class);

        when(runtimeService.createExecutionQuery()).thenReturn(executionQuery);
        when(executionQuery.processInstanceId(workflowId)).thenReturn(executionQuery);
        when(executionQuery.activityId(activitiFlowStep)).thenReturn(executionQuery);
        when(executionQuery.singleResult()).thenReturn(execution);

        when(execution.getId()).thenReturn(executionId);

        processManagerService.signalProcessWithParametersAndWorkflowId(workflowId, activitiFlowStep, parameters);

        verify(runtimeService).signal(executionId, parameters);
    }

    @Test
    public void shouldSignalProcessTaskWithParametersAndWorkflowIdAndNoParameters() throws TaskNotAvailableException {
        final Map<String, Object> parameters = null;

        final String workflowId = "workflow id";
        final String activitiFlowStep = "myBeanName";
        final String executionId = "execution id";

        final Execution execution = mock(Execution.class);
        final ExecutionQuery executionQuery = mock(ExecutionQuery.class);

        when(runtimeService.createExecutionQuery()).thenReturn(executionQuery);
        when(executionQuery.processInstanceId(workflowId)).thenReturn(executionQuery);
        when(executionQuery.activityId(activitiFlowStep)).thenReturn(executionQuery);
        when(executionQuery.singleResult()).thenReturn(execution);

        when(execution.getId()).thenReturn(executionId);

        processManagerService.signalProcessWithParametersAndWorkflowId(workflowId, activitiFlowStep, parameters);

        verify(runtimeService).signal(executionId);
    }

    @Test
    public void shouldSignalProcessForActivitiWithCorrectWorkflowIdAndBeanName() throws Exception {

        final String workflowId = "workflow id";
        final String activitiFlowStep = "myBeanName";
        final String executionId = "execution id";

        final Execution execution = mock(Execution.class);
        final ExecutionQuery executionQuery = mock(ExecutionQuery.class);

        when(runtimeService.createExecutionQuery()).thenReturn(executionQuery);
        when(executionQuery.processInstanceId(workflowId)).thenReturn(executionQuery);
        when(executionQuery.activityId(activitiFlowStep)).thenReturn(executionQuery);
        when(executionQuery.singleResult()).thenReturn(execution);

        when(execution.getId()).thenReturn(executionId);

        processManagerService.signalWaitingProcess(workflowId, activitiFlowStep);

        verify(runtimeService).signal(executionId);
    }

    @Test
    public void shouldSignalProcessForActivitiWithCorrectWorkflowIdBeanNameAndParameters() throws Exception {

        final String workflowId = "workflow id";
        final String activitiFlowStep = "myBeanName";
        final String executionId = "execution id";

        final Map<String, Object> parameters = someParameters();

        final Execution execution = mock(Execution.class);
        final ExecutionQuery executionQuery = mock(ExecutionQuery.class);

        when(runtimeService.createExecutionQuery()).thenReturn(executionQuery);
        when(executionQuery.processInstanceId(workflowId)).thenReturn(executionQuery);
        when(executionQuery.activityId(activitiFlowStep)).thenReturn(executionQuery);
        when(executionQuery.singleResult()).thenReturn(execution);

        when(execution.getId()).thenReturn(executionId);

        processManagerService.signalWaitingProcessWithParameters(workflowId, activitiFlowStep, parameters);

        verify(runtimeService).signal(executionId, parameters);
    }

    @Test
    public void shouldThrowARuntimeExceptionIfNoExecutionFound() throws Exception {

        final String workflowId = "workflow id";
        final String activitiFlowStep = "myBeanName";
        final String executionId = "execution id";

        final ExecutionQuery executionQuery = mock(ExecutionQuery.class);

        when(runtimeService.createExecutionQuery()).thenReturn(executionQuery);
        when(executionQuery.processInstanceId(workflowId)).thenReturn(executionQuery);
        when(executionQuery.activityId(activitiFlowStep)).thenReturn(executionQuery);
        when(executionQuery.singleResult()).thenReturn(null);

        try {
            processManagerService.signalWaitingProcess(workflowId, activitiFlowStep);
            fail();
        } catch (final TaskNotAvailableException expected) {
            assertThat(expected.getMessage(), is("Receive task is null for Signal process with id: 'workflow id' with step 'myBeanName'"));
        }

        verify(runtimeService, never()).signal(executionId);
    }

    @Test
    public void shouldThrowARuntimeExceptionIfNoExecutionFoundWithParameters() throws Exception {

        final String workflowId = "workflow id";
        final String activitiFlowStep = "myBeanName";
        final String executionId = "execution id";

        final Map<String, Object> parameters = someParameters();

        final ExecutionQuery executionQuery = mock(ExecutionQuery.class);

        when(runtimeService.createExecutionQuery()).thenReturn(executionQuery);
        when(executionQuery.processInstanceId(workflowId)).thenReturn(executionQuery);
        when(executionQuery.activityId(activitiFlowStep)).thenReturn(executionQuery);
        when(executionQuery.singleResult()).thenReturn(null);

        try {
            processManagerService.signalWaitingProcessWithParameters(workflowId, activitiFlowStep, parameters);
            fail();
        } catch (final TaskNotAvailableException expected) {
            assertThat(expected.getMessage(), is("Receive task is null for Signal process with id: 'workflow id' with step 'myBeanName'"));
        }

        verify(runtimeService, never()).signal(executionId, parameters);
    }

    private Map<String, Object> someParameters() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("key", "value");
        return parameters;
    }
}