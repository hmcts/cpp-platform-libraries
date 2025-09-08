package uk.gov.moj.cpp.platform.data.utils.date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.moj.cpp.platform.data.utils.date.MeridianUtil.getMeridian;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

public class MeridianUtilTest {

    @Test
    public void shouldGetAmMeridian() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T11:15:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("AM"));
    }

    @Test
    public void shouldGetAmMeridianAt9am() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T09:15:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("AM"));
    }

    @Test
    public void shouldGetPmMeridianAt2pm() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T14:00:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("PM"));
    }

    @Test
    public void shouldGetPmMeridian() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T14:15:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("PM"));
    }

    @Test
    public void shouldGetPmMeridianBetween1PmAnd2Pm() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T13:15:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("PM"));
    }

    @Test
    public void shouldGetAdMeridianForBeforeAm() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T19:15:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("AD"));
    }

    @Test
    public void shouldGetAdMeridianWhenMeridianBefore9am() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T08:15:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("AD"));
    }

    @Test
    public void shouldGetAdMeridianForAfterPm() {
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("2019-12-02T02:15:30-05:00");

        final String meridian = getMeridian(zonedDateTime);

        assertThat(meridian, is("AD"));
    }
}