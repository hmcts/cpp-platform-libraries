package uk.gov.moj.cpp.activiti.cdi;

import static java.lang.String.format;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.slf4j.Logger;

@ApplicationScoped
public class ProcessManagerService {

    private Logger logger;

    private RuntimeService runtimeService;

    public ProcessManagerService() {
        // Empty
    }

    @Inject
    public ProcessManagerService(final RuntimeService runtimeService, final Logger logger) {
        this.runtimeService = runtimeService;
        this.logger = logger;
    }

    public void startProcessModel(final String processModelId, final Map<String, Object> parameters) {
        runtimeService.startProcessInstanceByKey(processModelId, parameters);
    }

    public void signalProcess(final String processInstanceId, final String processTaskId) {
        signalProcessWithParameters(processInstanceId, processTaskId, null);
    }

    public void signalProcessWithParameters(final String processInstanceId,
                                            final String processTaskId,
                                            final Map<String, Object> parameters) {

        logger.trace("Signal process instance with id: {} at step : {}", processInstanceId, processTaskId);

        try {
            if (parameters != null) {
                runtimeService.signal(processInstanceId, parameters);
            } else {
                runtimeService.signal(processInstanceId);
            }
        } catch (final Exception e) {
            throw new ActivitiException(format("Error signalling process instance with id: %s at step : %s", processInstanceId, processTaskId), e);
        }
    }

    public void signalWaitingProcess(final String workflowId, final String javaDelegateName) throws TaskNotAvailableException {
        signalWaitingProcessWithParameters(workflowId, javaDelegateName, null);
    }

    public void signalWaitingProcessWithParameters(final String workflowId,
                                            final String processTaskId,
                                            final Map<String, Object> parameters) throws TaskNotAvailableException {

        final Execution execution = retrieveProcessWithActivityId(workflowId, processTaskId);

        if (parameters != null) {
            runtimeService.signal(execution.getId(), parameters);
        } else {
            runtimeService.signal(execution.getId());
        }
    }

    public void signalProcessWithParametersAndWorkflowId(final String workflowId,
                                            final String javaDelegateName,
                                            final Map<String, Object> parameters) throws TaskNotAvailableException {

        final Execution execution = retrieveProcessWithActivityId(workflowId, javaDelegateName);

        if (parameters != null) {
            runtimeService.signal(execution.getId(), parameters);
        } else {
            runtimeService.signal(execution.getId());
        }
    }

    private Execution retrieveProcessWithActivityId(final String workflowId, final String javaDelegateName) throws TaskNotAvailableException {

        final Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(workflowId)
                .activityId(javaDelegateName)
                .singleResult();

        if (execution != null) {
            return execution;
        }

        throw new TaskNotAvailableException(format("Receive task is null for Signal process with id: '%s' with step '%s'", workflowId, javaDelegateName));
    }
}