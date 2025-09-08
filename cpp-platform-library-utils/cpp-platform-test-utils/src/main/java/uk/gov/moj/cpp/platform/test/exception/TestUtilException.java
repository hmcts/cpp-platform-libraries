package uk.gov.moj.cpp.platform.test.exception;

public class TestUtilException extends RuntimeException {
    public TestUtilException(final String message) {
        super(message);
    }


    public TestUtilException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
