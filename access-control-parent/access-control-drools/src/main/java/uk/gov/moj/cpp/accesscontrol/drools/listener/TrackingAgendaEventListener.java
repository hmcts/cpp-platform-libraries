package uk.gov.moj.cpp.accesscontrol.drools.listener;

import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrackingAgendaEventListener.class);

    @Override
    public void afterMatchFired(final AfterMatchFiredEvent event) {
        LOG.debug("Rule fired: {}", event.getMatch().getRule().getName());
    }
}