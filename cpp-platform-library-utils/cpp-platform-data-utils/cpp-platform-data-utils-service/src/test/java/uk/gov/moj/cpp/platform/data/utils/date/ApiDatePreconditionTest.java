package uk.gov.moj.cpp.platform.data.utils.date;

import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.enveloper.EnvelopeFactory;

import javax.ws.rs.BadRequestException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

public class ApiDatePreconditionTest {
    private static JsonEnvelope newEnvelope(final String fromDate, final String toDate) {
        return EnvelopeFactory.createEnvelope(
                "commandName",
                getJsonBuilderFactory().createObjectBuilder()
                        .add("fromDate", fromDate)
                        .add("toDate", toDate)
                        .build());
    }

    @Test
    public void shouldThrowExceptionIfInvalidFromDateFormat() throws Exception {
        final JsonEnvelope envelope = newEnvelope("20120505", "2016-05-05");

        assertThrows(BadRequestException.class, () -> ApiDatePrecondition.checkDateRange(envelope));
    }

    @Test
    public void shouldThrowExceptionIfInvalidToDateFormat() throws Exception {
        final JsonEnvelope envelope = newEnvelope("2012-05-05", "20160505");

        assertThrows(BadRequestException.class, () -> ApiDatePrecondition.checkDateRange(envelope));
    }

    @Test
    public void shouldThrowExceptionIfInvalidDateRange() throws Exception {
        final JsonEnvelope envelope = newEnvelope("2016-05-05", "2012-05-05");

        assertThrows(BadRequestException.class, () -> ApiDatePrecondition.checkDateRange(envelope));
    }

    @Test
    public void shouldContinueIfValidDateRange() throws Exception {
        final JsonEnvelope envelope = newEnvelope("2012-05-05", "2016-05-05");

        ApiDatePrecondition.checkDateRange(envelope);
    }

    @Test
    public void shouldContinueIfValidDateRangeAsString() throws Exception {

        ApiDatePrecondition.checkDateRange("2016-05-05", "2016-05-05");
    }

    @Test
    public void shouldThrowExceptionIfInvalidDateRangeAsString() throws Exception {

        assertThrows(BadRequestException.class, () -> ApiDatePrecondition.checkDateRange("2016-05-05", "2015-05-05"));
    }

    @Test
    public void shouldThrowExceptionIfInvalidDateRangeAsStringGreaterThan31Days() throws Exception {

        assertThrows(BadRequestException.class, () -> ApiDatePrecondition.checkDateRange("2015-05-05", "2016-05-05"));
    }

    @Test
    public void shouldThrowExceptionIfInvalidDateRangeAsStringGreaterThan2Days() throws Exception {

        assertThrows(BadRequestException.class, () -> ApiDatePrecondition.checkDateRange("2015-05-05", "2015-05-07",2));
    }

    @Test
    public void shouldContinueValidDateRangeAsString() throws Exception {
        ApiDatePrecondition.checkDateRange("2015-05-05", "2015-05-06",2);
    }

}