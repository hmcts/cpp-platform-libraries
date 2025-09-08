package uk.gov.moj.cpp.accesscontrol.drools;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Captor;
import uk.gov.justice.services.core.accesscontrol.AccessControlViolation;
import uk.gov.justice.services.core.annotation.Component;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.StatelessKieSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LocalRulesDroolsAccessPolicyEvaluatorTest {

    private LocalRulesDroolsAccessPolicyEvaluator policyEvaluator;

    @Mock
    private StatelessKieSession ksession1;

    @Mock
    private StatelessKieSession ksession2;

    @Mock
    private JsonEnvelope jsonEnvelope;

    @Mock
    private ExecutionResults executionResults1;

    @Mock
    private Outcome outcome1;

    @Mock
    KieSessionFactory kieSessionFactory;

    @Captor
    private ArgumentCaptor<BatchExecutionCommandImpl> commandListCaptor;

    @BeforeEach
    public void setup() {
        policyEvaluator = new LocalRulesDroolsAccessPolicyEvaluator();
        policyEvaluator.kieSessionFactory = kieSessionFactory;
        when(kieSessionFactory.getKieSession(Component.COMMAND_API.toString())).thenReturn(ksession1);
    }

    @Test
    public void shouldThrowAccessControlViolation() throws Exception {
        when(ksession1.execute(commandListCaptor.capture())).thenReturn(executionResults1);
        when(executionResults1.getValue("outcome")).thenReturn(outcome1);
        when(outcome1.isSuccess()).thenReturn(false);

        Optional<AccessControlViolation> accessControlViolation = policyEvaluator.checkAccessPolicyFor(Component.COMMAND_API.toString(), jsonEnvelope);

        assertThat(accessControlViolation.isPresent(), equalTo(true));
        assertThat(accessControlViolation.get().getReason(), equalTo("Rules failed to match"));

        verifyCommands(commandListCaptor);
    }

    @Test
    public void shouldSuccessfullyExecuteRule() throws Exception {
        when(ksession1.execute(commandListCaptor.capture())).thenReturn(executionResults1);
        when(executionResults1.getValue("outcome")).thenReturn(outcome1);
        when(outcome1.isSuccess()).thenReturn(true);

        Optional<AccessControlViolation> accessControlViolation = policyEvaluator.checkAccessPolicyFor(Component.COMMAND_API.toString(), jsonEnvelope);

        assertThat(accessControlViolation.isPresent(), equalTo(false));
        verifyCommands(commandListCaptor);
    }

    @Test
    public void shouldThrowAccessViolationWhenAllTheSessionExecutionsFail() throws Exception {
        final Map<String, StatelessKieSession> kieSessions = new HashMap<>();
        kieSessions.put("command", ksession1);
        kieSessions.put("query", ksession2);
        when(kieSessionFactory.getKieSession(Component.COMMAND_API.toString())).thenReturn(ksession1);

        when(ksession1.execute(commandListCaptor.capture())).thenReturn(executionResults1);
        when(executionResults1.getValue("outcome")).thenReturn(outcome1);
        when(outcome1.isSuccess()).thenReturn(false);

        Optional<AccessControlViolation> accessControlViolation = policyEvaluator.checkAccessPolicyFor(Component.COMMAND_API.toString(), jsonEnvelope);

        assertThat(accessControlViolation.isPresent(), equalTo(true));
        verifyCommands(commandListCaptor);
    }

    @Test
    public void shouldNotThrowAccessViolationWhenOneSessionExecutionPasses() throws Exception {
        final Map<String, StatelessKieSession> kieSessions = new HashMap<>();
        kieSessions.put("command", ksession1);
        kieSessions.put("query", ksession2);

        when(ksession1.execute(commandListCaptor.capture())).thenReturn(executionResults1);
        when(executionResults1.getValue("outcome")).thenReturn(outcome1);
        when(outcome1.isSuccess()).thenReturn(false);

        Optional<AccessControlViolation> accessControlViolation = policyEvaluator.checkAccessPolicyFor(Component.COMMAND_API.toString(), jsonEnvelope);

        assertThat(accessControlViolation.isPresent(), equalTo(true));
        verifyCommands(commandListCaptor);
    }

    private void verifyCommands(final ArgumentCaptor<BatchExecutionCommandImpl> commandListCaptor) {
        BatchExecutionCommandImpl executionCommand = commandListCaptor.getValue();
        final List<Command> commands = executionCommand.getCommands();
        assertThat(commands, IsCollectionWithSize.hasSize(2));

        for (Command c : commands) {
            assertThat(((InsertObjectCommand) c).getObject().getClass(), anyOf(equalTo(Action.class), equalTo(Outcome.class)));
            assertThat(((InsertObjectCommand) c).getOutIdentifier(), anyOf(equalTo("outcome"), equalTo("action")));
        }

        if (commands.get(0) instanceof InsertObjectCommand) {
            ((InsertObjectCommand) commands.get(0)).getObject();
            ((InsertObjectCommand) commands.get(0)).getOutIdentifier();
        }
    }
}