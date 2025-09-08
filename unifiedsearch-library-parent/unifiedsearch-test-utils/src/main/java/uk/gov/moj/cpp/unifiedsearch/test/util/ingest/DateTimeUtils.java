package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.stream.Collectors.toList;

import uk.gov.justice.services.common.util.UtcClock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.annotations.VisibleForTesting;

public class DateTimeUtils {

    private final UtcClock clock;

    public DateTimeUtils() {
        this(new UtcClock());
    }

    @VisibleForTesting
    public DateTimeUtils(final UtcClock clock) {
        this.clock = clock;
    }

    public String randomDateTimeInTheNextMonthAsElasticsearchDateString() {
         return toElasticsearchDateTimeString(randomDateTimeInTheNextMonths());
    }

    public LocalDateTime randomDateTimeInTheNextMonths() {
        final LocalDate localDate = clock.now().toLocalDate().plusDays(1);
        long startDay = localDate.toEpochDay();
        long endDay = localDate.plusMonths(1).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return LocalDate.ofEpochDay(randomDay).atTime(randomWorkingHoursTime());
    }

    public String randomTimeForDay(final String day) {

        final LocalDateTime dateTime = LocalDate.parse(day).atTime(randomWorkingHoursTime());
        return toElasticsearchDateTimeString(dateTime);
    }

    public LocalTime randomWorkingHoursTime() {
        return LocalTime.of(ThreadLocalRandom.current().nextInt(9, 17), 0, 0, 0);
    }

    public List<LocalDateTime> randomSequentialDateRange() {

        final List<LocalDateTime> dateRangeList = new ArrayList<>(3);

        final LocalDateTime start = randomDateTimeInTheNextMonths();
        dateRangeList.add(start);
        dateRangeList.add(start.plusDays(1));
        dateRangeList.add(start.plusDays(2));
        return dateRangeList;
    }

    public List<String> randomSequentialDateRangeAsElasticsearchDateString() {

        final List<LocalDateTime> dateTimeRangeList = randomSequentialDateRange();
        final DateTimeUtils dateTimeUtils = new DateTimeUtils(clock);

        return dateTimeRangeList.stream().map(dateTimeUtils::toElasticsearchDateTimeString).collect(toList());
    }


    public String toElasticsearchDateTimeString(final TemporalAccessor dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return ISO_LOCAL_DATE_TIME.format(dateTime);
        }
    }
}