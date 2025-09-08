package uk.gov.moj.cpp.systemidmapper.client;

public enum ResultCode {
    OK, CONFLICT;

    public static ResultCode valueOf(int httpCode) {
        switch (httpCode) {
            case 200:
                return OK;
            case 409:
                return CONFLICT;
            default:
                throw new IllegalStateException("Unexpected response code: " + httpCode);
        }
    }
}