package uk.gov.moj.cpp.platform.azure.utils;

public class AzureBlobClientException extends RuntimeException {
    public AzureBlobClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AzureBlobClientException(final String message) {
        super(message);
    }

}
