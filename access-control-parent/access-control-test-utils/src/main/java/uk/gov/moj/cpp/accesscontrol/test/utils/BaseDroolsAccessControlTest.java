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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
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
    public void setup() throws Exception {
        jsonEnvelopeBuilder = envelope();
        final KieServices kieServices = KieServices.get();
        final ClassLoader classLoader = getClass().getClassLoader();

        final URL kmoduleUrl = classLoader.getResource("META-INF/kmodule.xml");
        if (kmoduleUrl == null) {
            throw new IllegalStateException("No META-INF/kmodule.xml found on classpath");
        }

        // Use KieFileSystem to build the KieContainer. Drools 10.x classpath scanning via
        // ClassLoader.getResources() does not work correctly when surefire uses a manifest-only
        // JAR classpath. Loading DRL files explicitly from the classpath root directory works
        // around this limitation.
        final KieFileSystem kfs = kieServices.newKieFileSystem();

        try (InputStream is = classLoader.getResourceAsStream("META-INF/kmodule.xml")) {
            kfs.writeKModuleXML(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }

        // Derive the classpath root from the kmodule.xml URL and walk it for DRL files.
        // This works for file: URLs (i.e. target/classes directories in Maven builds).
        final String kmoduleUrlStr = kmoduleUrl.toExternalForm();
        if (kmoduleUrlStr.startsWith("file:")) {
            final String rootUrlStr = kmoduleUrlStr.substring(0, kmoduleUrlStr.length() - "META-INF/kmodule.xml".length());
            final File rootDir = new File(new URI(rootUrlStr));
            addDrlFilesToKfs(kfs, rootDir, rootDir);
        }

        final KieBuilder kieBuilder = kieServices.newKieBuilder(kfs, classLoader);
        kieBuilder.buildAll();

        final Results results = kieBuilder.getResults();
        for (final Message msg : results.getMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("Drools build error in " + msg.getPath() + ": " + msg.getText());
        }

        final KieContainer kieContainer = kieServices.newKieContainer(
                kieServices.getRepository().getDefaultReleaseId(), classLoader);

        final KieSessionConfiguration sessionConfiguration = kieContainer.getKieSessionConfiguration(kSessionName);
        kSession = kieContainer.newStatelessKieSession(kSessionName, sessionConfiguration);

        injectProviderMocks();
    }

    private void addDrlFilesToKfs(final KieFileSystem kfs, final File rootDir, final File currentDir) throws IOException {
        for (final File file : Objects.requireNonNull(currentDir.listFiles())) {
            if (file.isDirectory()) {
                addDrlFilesToKfs(kfs, rootDir, file);
            } else if (file.getName().endsWith(".drl")) {
                // KieFileSystem expects DRL files under src/main/resources/ so that
                // KieBuilderImpl.copySourceToTarget() strips this prefix when processing
                final String relativePath = "src/main/resources/" + rootDir.toURI().relativize(file.toURI()).toString();
                kfs.write(relativePath, new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
            }
        }
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
