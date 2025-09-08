package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SimpleDateStringMatcher  extends TypeSafeMatcher<String> {

    protected boolean matchesSafely(final String maybeDate) {
        return maybeDate != null && maybeDate.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    public void describeTo(final Description description) {
        description.appendText("a string matching the pattern of a date");
    }

    public static Matcher<String> isADateString() {
        return new SimpleDateStringMatcher();
    }

}
