package uk.gov.moj.cpp.featurecontrol.remote;

public class AzureRequestException extends RuntimeException{
    public AzureRequestException(String message, Throwable ex) {
        super(message, ex);
    }
}
