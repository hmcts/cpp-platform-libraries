package uk.gov.moj.cpp.accesscontrol.test.utils.matcher;

import uk.gov.moj.cpp.accesscontrol.drools.Outcome;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.kie.api.runtime.ExecutionResults;

public class OutcomeMatcher extends BaseMatcher<ExecutionResults> {

    private static final String OUTCOME_IDENTIFIER = "outcome";

    private boolean outcome = false;

    @Override
    public boolean matches(Object o) {
        if (o instanceof ExecutionResults) {
            final ExecutionResults executionResults = (ExecutionResults) o;
            final Outcome oc = (Outcome) executionResults.getValue(OUTCOME_IDENTIFIER);
            if (oc.isSuccess() == outcome) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("true, but was %s", outcome));
    }

    public static OutcomeMatcher outcome() {
        return new OutcomeMatcher();
    }

    public OutcomeMatcher successful() {
        outcome = true;
        return this;
    }

    public OutcomeMatcher failure() {
        outcome = false;
        return this;
    }
}
