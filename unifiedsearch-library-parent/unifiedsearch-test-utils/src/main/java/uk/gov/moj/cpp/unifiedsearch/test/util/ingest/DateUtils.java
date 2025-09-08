package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.stream.Collectors.toList;

import uk.gov.justice.services.common.util.UtcClock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

public class DateUtils {

    private final UtcClock clock;

    public DateUtils() {
        this(new UtcClock());
    }

    @VisibleForTesting
    public DateUtils(final UtcClock clock) {
        this.clock = clock;
    }

    public LocalDate randomDateInLastThreeMonths() {

        final LocalDate localDate = clock.now().toLocalDate();
        long startDay = localDate.minusMonths(3).toEpochDay();
        long endDay = localDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public LocalDate randomDateInLastThreeMonthsOrNull() {

        if (ThreadLocalRandom.current().nextBoolean()) {
            final LocalDate localDate = clock.now().toLocalDate();
            long startDay = localDate.minusMonths(3).toEpochDay();
            long endDay = localDate.toEpochDay();
            long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
            return LocalDate.ofEpochDay(randomDay);
        }

        return null;
    }


    public LocalDate randomDateInNextThreeMonths() {
        final LocalDate localDate = clock.now().toLocalDate();
        long startDay = localDate.toEpochDay();
        long endDay = localDate.plusMonths(3).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public LocalDate randomDateInNextThreeMonthsOrNull() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            final LocalDate localDate = clock.now().toLocalDate();
            long startDay = localDate.toEpochDay();
            long endDay = localDate.plusMonths(3).toEpochDay();
            long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        }

        return null;
    }

    public LocalDate randomDateWithinThreeMonthsOf(final LocalDate dateFrom) {

        final LocalDate localDate = clock.now().toLocalDate();
        long startDay = dateFrom.toEpochDay();
        long endDay = localDate.plusMonths(3).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public LocalDate randomDateWithinFiveDaysOf(final LocalDate dateFrom) {
        long startDay = dateFrom.toEpochDay();
        long endDay = dateFrom.plusDays(5).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return LocalDate.ofEpochDay(randomDay);
    }


    public LocalDate randomDateOfBirthOver18() {
        final LocalDate localDate = clock.now().toLocalDate();
        long startDay = localDate.minusYears(75).toEpochDay();
        long endDay = localDate.minusYears(18).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return LocalDate.ofEpochDay(randomDay);
    }


    public List<LocalDate> randomDateRange() {

        final List<LocalDate> dateRangeList = new ArrayList<>();
        final LocalDate dateFrom = randomDateInNextThreeMonths();

        dateRangeList.add(dateFrom);
        if (ThreadLocalRandom.current().nextBoolean()) {
            dateRangeList.add(randomDateWithinFiveDaysOf(dateFrom));
        }

        return dateRangeList;
    }

    public List<String> randomDateRangeAsElasticsearchDateString() {

        final List<LocalDate> dateRangeList = randomDateRange();

        return dateRangeList.stream().map(this::toElasticsearchDateString).collect(toList());
    }

    public String toElasticsearchDateString(final LocalDate localDate) {
        if (localDate == null) {
            return null;
        } else {
            return ISO_LOCAL_DATE.format(localDate);
        }
    }
}
