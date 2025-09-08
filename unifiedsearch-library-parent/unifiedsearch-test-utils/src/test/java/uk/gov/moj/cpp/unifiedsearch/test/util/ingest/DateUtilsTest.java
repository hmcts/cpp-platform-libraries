package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.util.UtcClock;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateUtilsTest {

    @Mock
    private UtcClock clock;

    @InjectMocks
    private DateUtils dateUtils;

    @Test
    public void shouldReturnRandomDateInLastThreeMonths() {

        final ZonedDateTime now = new UtcClock().now();
        final LocalDate today = now.toLocalDate();

        when(clock.now()).thenReturn(now);

        final LocalDate threeMonthsAgo = today.minusMonths(3);

        final LocalDate returnedDate = dateUtils.randomDateInLastThreeMonths();

        assertTrue(returnedDate.isAfter(threeMonthsAgo) || returnedDate.isEqual(threeMonthsAgo));
        assertTrue(returnedDate.isBefore(today) || returnedDate.isEqual(today));
    }

    @Test
    public void shouldReturnRandomDateInLastThreeMonthsOrNull() {

        final ZonedDateTime now = new UtcClock().now();
        final LocalDate today = now.toLocalDate();

        when(clock.now()).thenReturn(now);
        final LocalDate threeMonthsAgo = today.minusMonths(3);

        boolean nullChecked = false;
        boolean dateChecked = false;
        int breakCounter = 0;

        while ((!nullChecked && !dateChecked)) {

            final LocalDate returnedDate = dateUtils.randomDateInLastThreeMonths();

            if (returnedDate == null) {
                nullChecked = true;
            } else {
                assertTrue(returnedDate.isAfter(threeMonthsAgo) || returnedDate.isEqual(threeMonthsAgo));
                assertTrue(returnedDate.isBefore(today) || returnedDate.isEqual(today));
                dateChecked = true;
            }

            if (++breakCounter > 10) {
                fail();
            }
        }
    }

    @Test
    public void shouldReturnRandomDateInNextThreeMonths() {

        final ZonedDateTime now = new UtcClock().now();
        final LocalDate today = now.toLocalDate();
        final LocalDate threeMonthsAway = today.plusMonths(3);

        when(clock.now()).thenReturn(now);

        final LocalDate returnedDate = dateUtils.randomDateInNextThreeMonths();

        assertNotNull(returnedDate);
        assertTrue(returnedDate.isAfter(today) || returnedDate.isEqual(today));
        assertTrue(returnedDate.isBefore(threeMonthsAway) || returnedDate.isEqual(threeMonthsAway));
    }

    @Test
    public void shouldReturnRandomDateWithinThreeMonthsOf() {

        final ZonedDateTime now = new UtcClock().now();
        final LocalDate today = now.toLocalDate();

        final LocalDate startDate = today.plusDays(5);
        final LocalDate endDate = startDate.plusMonths(3);

        when(clock.now()).thenReturn(now);

        final LocalDate returnedDate = dateUtils.randomDateWithinThreeMonthsOf(startDate);

        assertTrue(returnedDate.isAfter(startDate) || returnedDate.isEqual(startDate));
        assertTrue(returnedDate.isBefore(endDate) || returnedDate.isEqual(endDate));
    }

    @Test
    public void shouldReturnRandomDateWithinFiveDaysOf() {

        final ZonedDateTime now = new UtcClock().now();
        final LocalDate today = now.toLocalDate();

        final LocalDate startDate = today.plusDays(5);
        final LocalDate endDate = startDate.plusDays(5);

        final LocalDate returnedDate = dateUtils.randomDateWithinFiveDaysOf(startDate);

        assertNotNull(returnedDate);
        assertTrue(returnedDate.isAfter(startDate) || returnedDate.isEqual(startDate));
        assertTrue(returnedDate.isBefore(endDate) || returnedDate.isEqual(endDate));
    }

    @Test
    public void shouldReturnRandomDateOfBirthOver18() {

        final ZonedDateTime now = new UtcClock().now();
        final LocalDate today = now.toLocalDate();
        final LocalDate eighteenYearsAgo = today.minusYears(18);

        when(clock.now()).thenReturn(now);

        final LocalDate returnedDate = dateUtils.randomDateOfBirthOver18();

        assertNotNull(returnedDate);
        assertTrue(returnedDate.isBefore(eighteenYearsAgo));
    }

    @Test
    public void shouldReturnRandomDateRange() {

        final ZonedDateTime now = new UtcClock().now();

        when(clock.now()).thenReturn(now);

        final List<LocalDate> dateRange = dateUtils.randomDateRange();

        assertNotNull(dateRange);
        assertTrue(dateRange.size() > 0);
    }

    @Test
    public void shouldReturnRandomDateRangeAsElasticsearchDateString() {
        final ZonedDateTime now = new UtcClock().now();

        when(clock.now()).thenReturn(now);

        final List<String> dateRange = dateUtils.randomDateRangeAsElasticsearchDateString();

        assertNotNull(dateRange);
        assertTrue(dateRange.size() > 0);

        dateRange.forEach(dateString -> LocalDate.parse(dateString, ISO_LOCAL_DATE));
    }

    @Test
    public void shouldReturnElasticsearchDateString() {

        final ZonedDateTime now = new UtcClock().now();
        final LocalDate today = now.toLocalDate();

        assertThat(dateUtils.toElasticsearchDateString(today), is(now.format(ISO_LOCAL_DATE)));
    }
}