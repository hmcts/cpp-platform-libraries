package uk.gov.moj.cpp.accesscontrol.assignment.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.apache.openejb.OpenEjbContainer;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Application;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.openejb.util.NetworkUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.runner.RunWith;
import org.slf4j.helpers.NOPLogger;
import uk.gov.justice.api.provider.RemoteAccessControl2AssignmentQueryApi;
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
import uk.gov.justice.services.messaging.DefaultJsonObjectEnvelopeConverter;
import uk.gov.justice.services.messaging.MetadataBuilder;
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;
import uk.gov.justice.services.test.utils.common.validator.DummyJsonSchemaValidator;
import uk.gov.justice.services.test.utils.core.handler.registry.TestHandlerRegistryCacheProducer;
import uk.gov.moj.cpp.accesscontrol.assignment.providers.util.AssignmentQueryJsonBuilder;
import uk.gov.moj.cpp.accesscontrol.drools.Action;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;

@RunWith(ApplicationComposer.class)
@WireMockTest(httpPort = 8080)
public class AssignmentProviderIT {

    private static final String ASSIGNMENT_PATH = "/assignment-query-api/query/api/rest/assignment/assignments.*";
    private static final String MIME_TYPE = "application/vnd.assignment.query.assignments+json";

    private static int port = -1;

    @Inject
    AssignmentProvider assignmentProvider;


    private static final String ASSIGNED_USER = randomUUID().toString();
    private static final String NOT_ASSIGNED_USER = randomUUID().toString();
    private static final String REVIEW_ID = randomUUID().toString();

    private AssignmentQueryJsonBuilder builder = new AssignmentQueryJsonBuilder();

    @BeforeAll
    public static void beforeClass() {
        port = NetworkUtil.getNextAvailablePort();

    }

    @Configuration
    public Properties properties() {
        return new PropertiesBuilder()
                .p("httpejbd.port", Integer.toString(port))
                .p(OpenEjbContainer.OPENEJB_EMBEDDED_REMOTABLE, "true")
                .build();
    }

    @Module
    @Classes(cdi = true, value = {
            AssignmentProvider.class,
            RemoteAccessControl2AssignmentQueryApi.class,

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
            TestHandlerRegistryCacheProducer.class
    })
    public WebApp war() {
        return new WebApp()
                .contextRoot("charging-provider")
                .addServlet("TestApp", Application.class.getName());
    }

    @BeforeEach
    public void setup() {
        WireMock.reset();
    }

    @Test
    public void shouldAllowAssignedUser() {
        stub(ASSIGNED_USER);
        assertThat(assignmentProvider.isUserAssignedByReviewId(defaultAction(ASSIGNED_USER)), is(true));
    }

    @Test
    public void shouldNotAllowAssignedUserWhoIsNotAssigned() {
        stub(ASSIGNED_USER);
        assertThat(assignmentProvider.isUserAssignedByReviewId(defaultAction(NOT_ASSIGNED_USER)), is(false));
    }

    private void stub(final String assignee) {
        stubFor(get(urlPathMatching(ASSIGNMENT_PATH))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withHeader("Content-Type", MIME_TYPE)
                        .withHeader("CPPID", randomUUID().toString())
                        .withBody(builder.withAssignee(assignee).build().toString())));

    }

    private Action defaultAction(final String userId) {
        return new Action(envelope().with(metadata().withUserId(userId)).withPayloadOf(REVIEW_ID, "reviewId").build());
    }

    private MetadataBuilder metadata() {
        return metadataOf(randomUUID(), "someName");
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
