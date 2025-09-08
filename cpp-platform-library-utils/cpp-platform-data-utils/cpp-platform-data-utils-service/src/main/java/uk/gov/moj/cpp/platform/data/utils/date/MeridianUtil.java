package uk.gov.moj.cpp.platform.data.utils.date;

import static java.lang.String.valueOf;
import static java.util.EnumSet.range;
import static uk.gov.moj.cpp.platform.data.utils.date.Meridian.FIVE_PM;
import static uk.gov.moj.cpp.platform.data.utils.date.Meridian.ONE_PM;
import static uk.gov.moj.cpp.platform.data.utils.date.Meridian.TWELVE_AM;
import static uk.gov.moj.cpp.platform.data.utils.date.Meridian.TWELVE_PM;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeridianUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeridianUtil.class);
    private static final EnumSet<Meridian> amMeridian = range(TWELVE_AM, TWELVE_PM);
    private static final EnumSet<Meridian> pmMeridian = range(ONE_PM, FIVE_PM);

    private MeridianUtil() {
    }

    public static String getMeridian(final ZonedDateTime hearingDaySittingDay) {

        final String pattern = "HH";

        final String hour = hearingDaySittingDay.format(DateTimeFormatter.ofPattern(pattern));

        final boolean isAmMeridian = amMeridian.stream().anyMatch(meridian -> checkMeridian(meridian.getValue(), hour));
        final boolean isPmMeridian = pmMeridian.stream().anyMatch(meridian -> checkMeridian(meridian.getValue(), hour));

        if (isAmMeridian) {
            return "AM";
        }

        if (isPmMeridian) {
            return "PM";
        }

        LOGGER.info("Session {} does not fall within AM or PM range", hour);

        return "AD";
    }

    private static boolean checkMeridian(final String value, final String hour) {
        return valueOf(value).equals(hour);
    }
}
