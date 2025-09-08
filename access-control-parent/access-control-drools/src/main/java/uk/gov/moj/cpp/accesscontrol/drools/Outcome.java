package uk.gov.moj.cpp.accesscontrol.drools;

/**
 * Represents the access control outcome having fired all drools rules.
 */
public class Outcome {

    private boolean success = false;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }
}

