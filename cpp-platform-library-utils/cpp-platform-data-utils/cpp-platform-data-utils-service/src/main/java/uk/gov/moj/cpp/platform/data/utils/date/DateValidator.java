package uk.gov.moj.cpp.platform.data.utils.date;

import static java.time.LocalDate.parse;
import static java.time.ZoneOffset.UTC;

import uk.gov.justice.services.common.converter.LocalDates;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.messaging.JsonObjects;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateValidator.class);

    private DateValidator() {
    }

    public static boolean isDateRangeValid(final JsonObject payload) {
        final Optional<String> fromDate = JsonObjects.getString(payload, "fromDate");
        final Optional<String> toDate = JsonObjects.getString(payload, "toDate");

        if(fromDate.isPresent() && toDate.isPresent()) {
            final ZonedDateTime from = parse(fromDate.get()).atStartOfDay(UTC);
            final ZonedDateTime to = parse(toDate.get()).atStartOfDay(UTC);

            final ZonedDateTime now = new UtcClock().now();

            return to.isAfter(from) && from.isBefore(now) && to.isBefore(now);
        }
        return false;
    }

    public static boolean isDateFormatValid(final String date) {
        try {
            LocalDates.from(date);
        } catch (final DateTimeParseException e) {
            LOGGER.warn("Invalid Date supplied: {} and exception is {}", date, e);
            return false;
        }
        return true;
    }
}
