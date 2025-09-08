package uk.gov.moj.cpp.platform.data.utils.date;

import static java.lang.String.format;
import static java.time.LocalDate.parse;
import static java.time.ZoneOffset.UTC;

import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjects;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.json.JsonObject;
import javax.ws.rs.BadRequestException;

public final class ApiDatePrecondition {

    private static final String FROM_DATE_IS_IN_BAD_FORMAT = "From Date: %s is in bad format";
    private static final String TO_DATE_IS_IN_BAD_FORMAT = "To Date: %s is in bad format";
    private static final String FROM_DATE_IS_AFTER_TO_DATE = "From Date: %s is after To Date: %s";
    private static final String MORE_THAN_N_DAYS_BETWEEN_FROM_DATE_AND_TO_DATE =
            "More than %s days between From Date: %s and To Date: %s";

    private ApiDatePrecondition() {
    }

    public static void checkDateRange(final String fromDateIn, final String toDateIn) {
        checkDateRange(fromDateIn, toDateIn, 31);
    }

    public static void checkDateRange(final String fromDateIn, final String toDateIn, final int maxAllowedDayDifference) {
        checkFormat(fromDateIn, toDateIn);

        final ZonedDateTime from = parse(fromDateIn).atStartOfDay(UTC);
        final ZonedDateTime to = parse(toDateIn).atStartOfDay(UTC);

        if (from.isAfter(to)) {
            throw new BadRequestException(format(FROM_DATE_IS_AFTER_TO_DATE, fromDateIn, toDateIn));
        }
        if (ChronoUnit.DAYS.between(from, to) + 1 > maxAllowedDayDifference) {
            throw new BadRequestException(format(MORE_THAN_N_DAYS_BETWEEN_FROM_DATE_AND_TO_DATE, maxAllowedDayDifference, fromDateIn, toDateIn));
        }
    }

    public static void checkDateRange(final JsonEnvelope query) {
        final JsonObject payload = query.payloadAsJsonObject();
        checkFormat(JsonObjects.getString(payload, "fromDate").orElse(null), JsonObjects.getString(payload, "toDate").orElse(null));

        if (!DateValidator.isDateRangeValid(payload)) {
            throw new BadRequestException("Invalid Date range supplied");
        }
    }

    public static void checkFormat(final String fromDateIn, final String toDateIn) {
        final Optional<String> fromDate = Optional.ofNullable(fromDateIn);
        final Optional<String> toDate = Optional.ofNullable(toDateIn);

        if (fromDate.isPresent() && !DateValidator.isDateFormatValid(fromDate.get())) {
            throw new BadRequestException(format(FROM_DATE_IS_IN_BAD_FORMAT, fromDate.get()));
        }

        if (toDate.isPresent() && !DateValidator.isDateFormatValid(toDate.get())) {
            throw new BadRequestException(format(TO_DATE_IS_IN_BAD_FORMAT, toDate.get()));
        }
    }
}
