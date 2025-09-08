package uk.gov.moj.cpp.featurecontrol.remote;

public class SignedRequestException extends RuntimeException {
    public SignedRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
