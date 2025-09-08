package uk.gov.moj.cpp.accesscontrol.drools.listener;

import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackingRuleRuntimeEventListener implements RuleRuntimeEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrackingRuleRuntimeEventListener.class);

    @Override
    public void objectInserted(final ObjectInsertedEvent objectInsertedEvent) {
        LOG.debug("Fact inserted to KB: {}", objectInsertedEvent.getObject());
    }

    @Override
    public void objectUpdated(final ObjectUpdatedEvent objectUpdatedEvent) {
        LOG.debug("Fact updated: {}", objectUpdatedEvent.getFactHandle().toExternalForm());
    }

    @Override
    public void objectDeleted(final ObjectDeletedEvent objectDeletedEvent) {
        // Do nothing
    }
}
