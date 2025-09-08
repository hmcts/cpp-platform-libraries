package uk.gov.moj.cpp.activiti.cdi;

public class TaskNotAvailableException extends Exception {

    public TaskNotAvailableException(final String message) {
        super(message);
    }
}
