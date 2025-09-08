package uk.gov.moj.cpp.platform.data.utils.testutils.jms;

public enum ContextSubscriptionName {
    MI_SYSTEM_DATA("miSystemData"),
    MI_REPORT_DATA("miReportData"),;

    private final String subscriptionName;

    ContextSubscriptionName(final String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }
}
