package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.util.UtcClock;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateTimeUtilsTest {

    @Mock
    private UtcClock clock;

    @InjectMocks
    private DateTimeUtils dateTimeUtils;

    @Test
    public void randomDateTimeInTheNextMonthAsElasticsearchDateString() {
        final ZonedDateTime zonedDateTime = new UtcClock().now();
        final LocalDateTime now = zonedDateTime.toLocalDateTime();
        final LocalDateTime threeMonthsFromNow = now.plusMonths(3);

        when(clock.now()).thenReturn(zonedDateTime);
        final String testDateString = dateTimeUtils.randomDateTimeInTheNextMonthAsElasticsearchDateString();

        assertNotNull(testDateString);

        final LocalDateTime testDateTime = LocalDateTime.parse(testDateString);

        assertTrue(testDateTime.isAfter(now.plusSeconds(1)));
        assertTrue(threeMonthsFromNow.plusSeconds(1).isAfter(testDateTime));
    }

    @Test
    public void randomDateTimeInTheNextMonths() {

        final ZonedDateTime zonedDateTime = new UtcClock().now();
        final LocalDateTime now = zonedDateTime.toLocalDateTime();
        final LocalDateTime threeMonthsFromNow = now.plusMonths(3);

        when(clock.now()).thenReturn(zonedDateTime);
        
        final LocalDateTime testDateTime = dateTimeUtils.randomDateTimeInTheNextMonths();

        assertTrue(testDateTime.isAfter(now.plusSeconds(1)));
        assertTrue(threeMonthsFromNow.plusSeconds(1).isAfter(testDateTime));
    }

    @Test
    public void randomWorkingHoursTime() {

        final LocalTime start = LocalTime.of(9, 0, 0, 0).minusSeconds(1);
        final LocalTime end = LocalTime.of(17, 0, 0, 0).plusSeconds(1);

        final LocalTime testTime = dateTimeUtils.randomWorkingHoursTime();

        assertTrue(testTime.isAfter(start));
        assertTrue(testTime.isBefore(end));
    }

    @Test
    public void randomSequentialDateRange() {

        final ZonedDateTime now = new UtcClock().now();

        when(clock.now()).thenReturn(now);

        final List<LocalDateTime> testDateTimes = dateTimeUtils.randomSequentialDateRange();

        assertEquals(testDateTimes.get(1), testDateTimes.get(0).plusDays(1));
        assertEquals(testDateTimes.get(2), testDateTimes.get(0).plusDays(2));

    }

    @Test
    public void randomSequentialDateRangeAsElasticsearchDateString() {

        final ZonedDateTime now = new UtcClock().now();

        when(clock.now()).thenReturn(now);

        final List<String> testDateTimeStrings = dateTimeUtils.randomSequentialDateRangeAsElasticsearchDateString();
        final List<LocalDateTime> testDateTimes = testDateTimeStrings.stream().map(LocalDateTime::parse).collect(toList());

        assertEquals(testDateTimes.get(1), testDateTimes.get(0).plusDays(1));
        assertEquals(testDateTimes.get(2), testDateTimes.get(0).plusDays(2));

    }

    @Test
    public void toElasticsearchDateTimeString() {

        final LocalDateTime testDateTime = LocalDateTime.of(2019, 6, 23, 11, 24, 0, 0);
        assertEquals("2019-06-23T11:24:00", dateTimeUtils.toElasticsearchDateTimeString(testDateTime));
    }
}