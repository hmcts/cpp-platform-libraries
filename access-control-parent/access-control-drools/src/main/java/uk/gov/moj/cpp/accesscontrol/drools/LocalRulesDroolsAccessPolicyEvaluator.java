package uk.gov.moj.cpp.accesscontrol.drools;

import uk.gov.justice.services.core.accesscontrol.AccessControlViolation;
import uk.gov.justice.services.core.accesscontrol.PolicyEvaluator;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

/**
 * An implementation of a {@link PolicyEvaluator} that supports Drools with local rule files.
 */
@ApplicationScoped
@Alternative
@Priority(5)
public class LocalRulesDroolsAccessPolicyEvaluator implements PolicyEvaluator {

    private static final String ACTION_IDENTIFIER = "action";
    private static final String OUTCOME_IDENTIFIER = "outcome";

    @Inject
    KieSessionFactory kieSessionFactory;

    public Optional<AccessControlViolation> checkAccessPolicyFor(final String component,
                                                                 final JsonEnvelope jsonEnvelope) {
        final List<Command> commandList = new ArrayList<>();
        commandList.add(CommandFactory.newInsert(new Action(jsonEnvelope), ACTION_IDENTIFIER));
        commandList.add(CommandFactory.newInsert(new Outcome(), OUTCOME_IDENTIFIER));

        final StatelessKieSession statelessKieSession = kieSessionFactory.getKieSession(component);

        final ExecutionResults results =
                statelessKieSession.execute(CommandFactory.newBatchExecution(commandList));

        final Outcome outcome = (Outcome) results.getValue(OUTCOME_IDENTIFIER);

        return outcome.isSuccess() ?
                Optional.empty() :
                Optional.of(new AccessControlViolation("Rules failed to match"));
    }

}
