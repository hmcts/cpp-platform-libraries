package uk.gov.moj.cpp.systemidmapper.client;

public class MappingNotFoundException extends RuntimeException {

    public MappingNotFoundException(final String message) {
        super(message);
    }
}
