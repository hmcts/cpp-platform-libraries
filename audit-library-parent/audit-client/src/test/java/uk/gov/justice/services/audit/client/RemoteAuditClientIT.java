package uk.gov.justice.services.audit.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.openejb.OpenEjbContainer;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.apache.openejb.testng.PropertiesBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.runner.RunWith;
import org.slf4j.helpers.NOPLogger;
import uk.gov.justice.api.audit.RemoteAuditClient2MessageAuditAuditingEvent;
import uk.gov.justice.services.clients.core.DefaultRestClientHelper;
import uk.gov.justice.services.clients.core.DefaultRestClientProcessor;
import uk.gov.justice.services.clients.core.webclient.BaseUriFactory;
import uk.gov.justice.services.clients.core.webclient.ContextMatcher;
import uk.gov.justice.services.clients.core.webclient.MockServerPortProvider;
import uk.gov.justice.services.clients.core.webclient.WebTargetFactoryFactory;
import uk.gov.justice.services.common.annotation.ComponentNameExtractor;
import uk.gov.justice.services.common.configuration.GlobalValueProducer;
import uk.gov.justice.services.common.configuration.JndiBasedServiceContextNameProvider;
import uk.gov.justice.services.common.converter.JsonObjectConvertersProducer;
import uk.gov.justice.services.common.converter.StringToJsonObjectConverter;
import uk.gov.justice.services.common.converter.ZonedDateTimes;
import uk.gov.justice.services.common.http.DefaultServerPortProvider;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.core.accesscontrol.AllowAllPolicyEvaluator;
import uk.gov.justice.services.core.dispatcher.DispatcherCache;
import uk.gov.justice.services.core.dispatcher.DispatcherConfiguration;
import uk.gov.justice.services.core.dispatcher.DispatcherFactory;
import uk.gov.justice.services.core.dispatcher.EnvelopePayloadTypeConverter;
import uk.gov.justice.services.core.dispatcher.JsonEnvelopeRepacker;
import uk.gov.justice.services.core.dispatcher.ServiceComponentObserver;
import uk.gov.justice.services.core.dispatcher.SystemUserProvider;
import uk.gov.justice.services.core.dispatcher.SystemUserUtil;
import uk.gov.justice.services.core.envelope.EnvelopeInspector;
import uk.gov.justice.services.core.envelope.EnvelopeValidationExceptionHandlerProducer;
import uk.gov.justice.services.core.envelope.MediaTypeProvider;
import uk.gov.justice.services.core.enveloper.DefaultEnveloper;
import uk.gov.justice.services.core.extension.BeanInstantiater;
import uk.gov.justice.services.core.extension.ServiceComponentScanner;
import uk.gov.justice.services.core.mapping.DefaultMediaTypesMappingCache;
import uk.gov.justice.services.core.mapping.DefaultNameToMediaTypeConverter;
import uk.gov.justice.services.core.mapping.MediaTypesMappingCacheInitialiser;
import uk.gov.justice.services.core.producers.EnvelopeValidatorFactory;
import uk.gov.justice.services.core.producers.RequestResponseEnvelopeValidatorFactory;
import uk.gov.justice.services.core.producers.RequesterProducer;
import uk.gov.justice.services.core.producers.SenderProducer;
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;
import uk.gov.justice.services.test.utils.common.validator.DummyJsonSchemaValidator;
import uk.gov.justice.services.test.utils.core.handler.registry.TestHandlerRegistryCacheProducer;
import uk.gov.justice.services.test.utils.messaging.jms.DummyJmsEnvelopeSender;

import static java.time.ZonedDateTime.now;
import static java.util.UUID.randomUUID;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;

/**
 * Integration tests for the generated JAX-RS classes.
 */
@RunWith(ApplicationComposer.class)
public class RemoteAuditClientIT {

    private static final String CONTEXT_NAME = "context-command-api";
    private static final String COMPONENT = "test-component";
    private static final String MESSAGE = "a message";
    private static final String TIMESTAMP = ZonedDateTimes.toString(now());
    private static final UUID UUID = randomUUID();
    private static final String AUDIT_EVENT_ACTION_NAME = "audit.events.audit-recorded";

    @Resource(name = "auditing.event")
    private Topic auditEventsDestination;

    @Resource
    private ConnectionFactory factory;

    private Connection connection;

    private Session session;

    @Inject
    private RemoteAuditClient remoteAuditClient;

    @Configuration
    public Properties properties() {
        return new PropertiesBuilder()
                .property(OpenEjbContainer.OPENEJB_EMBEDDED_REMOTABLE, "true")
                .build();
    }

    @Module
    @Classes(cdi = true, value = {
            RemoteAuditClient.class,
            RemoteAuditClient2MessageAuditAuditingEvent.class,

            NOPLogger.class,
            RequesterProducer.class,
            EnvelopePayloadTypeConverter.class,
            ObjectMapper.class,
            JsonEnvelopeRepacker.class,
            SystemUserUtil.class,
            TestSystemUserProvider.class,

            DefaultNameToMediaTypeConverter.class,
            MediaTypeProvider.class,
            DefaultMediaTypesMappingCache.class,
            MediaTypesMappingCacheInitialiser.class,
            EnvelopeInspector.class,
            DispatcherConfiguration.class,
            GlobalValueProducer.class,

            JsonObjectConvertersProducer.class,
            DefaultEnveloper.class,
            UtcClock.class,

            RequestResponseEnvelopeValidatorFactory.class,
            EnvelopeValidatorFactory.class,
            DummyJsonSchemaValidator.class,
            EnvelopeValidationExceptionHandlerProducer.class,

            ComponentNameExtractor.class,
            DefaultTraceLogger.class,
            DefaultRestClientProcessor.class,
            DefaultRestClientHelper.class,
            DefaultJsonObjectEnvelopeConverter.class,
            StringToJsonObjectConverter.class,
            WebTargetFactoryFactory.class,
            BaseUriFactory.class,
            DefaultServerPortProvider.class,
            MockServerPortProvider.class,
            ContextMatcher.class,
            JndiBasedServiceContextNameProvider.class,

            ServiceComponentObserver.class,
            ServiceComponentScanner.class,
            DispatcherCache.class,
            DispatcherFactory.class,
            BeanInstantiater.class,
            AllowAllPolicyEvaluator.class,
            TestHandlerRegistryCacheProducer.class,

            SenderProducer.class,
            DummyJmsEnvelopeSender.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("audit-client-test");
    }

    @BeforeEach
    public void setup() throws Exception {
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @AfterEach
    public void after() throws JMSException {
        connection.close();
        session.close();
    }

    @Test
    public void commandControllerDispatcherShouldReceiveCommandA() throws JMSException {
        remoteAuditClient.auditEntry(createEnvelope(), COMPONENT);

        //TODO Add listener/consumer to topic
    }

    private JsonEnvelope createEnvelope() {
        return envelope()
                .with(metadataOf(UUID, AUDIT_EVENT_ACTION_NAME))
                .withPayloadOf(TIMESTAMP, "timestamp")
                .withPayloadOf(CONTEXT_NAME, "origin")
                .withPayloadOf(MESSAGE, "message")
                .build();
    }

    @Alternative
    @Priority(2)
    public static class TestSystemUserProvider implements SystemUserProvider {
        private static final UUID SYSTEM_USER_ID = UUID.randomUUID();

        @Override
        public Optional<UUID> getContextSystemUserId() {
            return Optional.of(SYSTEM_USER_ID);
        }
    }
}
