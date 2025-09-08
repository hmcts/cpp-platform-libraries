package uk.gov.moj.cpp.accesscontrol.test.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;
import static uk.gov.moj.cpp.accesscontrol.drools.util.ProviderNameUtil.getProviderName;
import static uk.gov.moj.cpp.accesscontrol.test.utils.matcher.OutcomeMatcher.outcome;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.drools.Outcome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import static org.hamcrest.MatcherAssert.assertThat;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseDroolsAccessControlTest {

    private static final String ACTION_IDENTIFIER = "action";
    private static final String OUTCOME_IDENTIFIER = "outcome";

    private JsonEnvelopeBuilder jsonEnvelopeBuilder;

    private final String kSessionName;

    public BaseDroolsAccessControlTest(final String kSessionName) {
        this.kSessionName = kSessionName;
    }

    protected StatelessKieSession kSession;

    @BeforeEach
    public void setup() {
        jsonEnvelopeBuilder = envelope();
        final KieServices kieServices = KieServices.get();
        final KieContainer kieContainer = kieServices.getKieClasspathContainer();

        final KieSessionConfiguration sessionConfiguration = kieContainer.getKieSessionConfiguration(kSessionName);
        kSession = kieContainer.newStatelessKieSession(kSessionName, sessionConfiguration);

        injectProviderMocks();
    }


    /**
     * A map of providers that are to be injected into the kie session as global entities.
     *
     * The map should be in the form of the class of the provider and the actual mocked provider
     * entity.
     *
     * <p>Usage: return ImmutableMap.&lt;Class, Object&gt;builder().put(UserAndGroupProvider.class,
     * userAndGroupProvider).build(); </p>
     *
     * @return a map of provider classes with associated mock objects.
     */
    protected abstract Map<Class<?>, Object> getProviderMocks();

    protected Action createActionFor(final String actionName) {
        final Map<String, String> metadata = new HashMap<>();
        metadata.putIfAbsent("id", UUID.randomUUID().toString());
        metadata.putIfAbsent("name", actionName);

        return createActionFor(metadata);
    }

    protected Action createActionFor(final Map<String, String> metadata) {
        // TODO - This needs to be updated to use the commented out line when the library is referncing the new framework version.
//        return new Action(jsonEnvelopeBuilder.withMetadataOf(metadata).build();
        return new Action(jsonEnvelopeBuilder.with(metadataOf(UUID.randomUUID().toString(), metadata.get("name"))).build());
    }

    protected ExecutionResults executeRulesWith(final Action action) {
        final List<Command<?>> commandList = new ArrayList<>();

        commandList.add(CommandFactory.newInsert(action, ACTION_IDENTIFIER));
        commandList.add(CommandFactory.newInsert(new Outcome(), OUTCOME_IDENTIFIER));

        return kSession.execute(CommandFactory.newBatchExecution(commandList));
    }

    protected void assertSuccessfulOutcome(final ExecutionResults results) {
        assertThat(results, is(outcome().successful()));
    }

    protected void assertFailureOutcome(final ExecutionResults results) {
        assertThat(results, is(outcome().failure()));
    }

    protected void injectProviderMocks() {
        final Map<Class<?>, Object> providerMocks = getProviderMocks();

        for (final Class<?> c : providerMocks.keySet()) {
            kSession.setGlobal(getProviderName(c.getSimpleName()), providerMocks.get(c));
        }
    }
}
