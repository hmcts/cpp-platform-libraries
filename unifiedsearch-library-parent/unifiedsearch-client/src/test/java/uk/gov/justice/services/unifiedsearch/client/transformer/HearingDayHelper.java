package uk.gov.justice.services.unifiedsearch.client.transformer;

import static org.hamcrest.MatcherAssert.assertThat;

import uk.gov.justice.services.unifiedsearch.client.domain.HearingDay;

import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;

public class HearingDayHelper {

    public static void assertHearingDatesWithinHearingDay(final List<HearingDay> hearingDays, final List<String> expectedHearingDates) {
        final List<String> hearingDates = hearingDays.stream()
                .map(HearingDay::getSittingDay)
                .collect(Collectors.toList());

        assertThat(hearingDates, Matchers.is(expectedHearingDates));
    }
}
