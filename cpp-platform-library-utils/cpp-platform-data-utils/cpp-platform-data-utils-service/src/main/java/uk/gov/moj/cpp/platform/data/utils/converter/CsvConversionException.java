package uk.gov.moj.cpp.platform.data.utils.converter;

public class CsvConversionException extends RuntimeException {
    public CsvConversionException(final String message, final Exception e) {
        super(message, e);
    }
}
