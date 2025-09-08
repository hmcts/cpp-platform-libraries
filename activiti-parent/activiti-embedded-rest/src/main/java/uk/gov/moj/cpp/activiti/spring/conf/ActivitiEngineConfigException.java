package uk.gov.moj.cpp.activiti.spring.conf;

public class ActivitiEngineConfigException extends RuntimeException {

    public ActivitiEngineConfigException() {
        super();
    }

    public ActivitiEngineConfigException(final String message) {
        super(message);
    }

    public ActivitiEngineConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ActivitiEngineConfigException(final Throwable cause) {
        super(cause);
    }

    protected ActivitiEngineConfigException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
