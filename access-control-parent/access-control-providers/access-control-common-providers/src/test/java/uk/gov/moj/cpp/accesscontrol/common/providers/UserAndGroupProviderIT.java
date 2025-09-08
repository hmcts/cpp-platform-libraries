package uk.gov.moj.cpp.accesscontrol.common.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.apache.openejb.OpenEjbContainer;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Configuration;
import org.apache.openejb.testing.Module;
import org.apache.openejb.testng.PropertiesBuilder;
import org.apache.openejb.util.NetworkUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.slf4j.helpers.NOPLogger;
import uk.gov.justice.api.provider.RemoteAccessControl2UsersgroupsQueryApi;
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
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.test.util.Group;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;
import static uk.gov.moj.cpp.accesscontrol.test.util.GroupsJsonBuilder.groupsJson;

@RunWith(ApplicationComposer.class)
@WireMockTest(httpPort = 8080)
public class UserAndGroupProviderIT {

    private static final String USERS_GROUPS_PATH = "/usersgroups-query-api/query/api/rest/usersgroups/users/%s/groups?groupName=%s";
    private static final String MIME_TYPE = "application/vnd.usersgroups.groups+json";

    private static int port = -1;

    @Inject
    UserAndGroupProvider userAndGroupProvider;

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
            UserAndGroupProvider.class,
            RemoteAccessControl2UsersgroupsQueryApi.class,

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
                .contextRoot("user-and-group-provider")
                .addServlet("TestApp", Application.class.getName());
    }

    @Test
    public void shouldReturnTrueIfUsersGroupContextResponseContainsGroup() throws Exception {

        final String userId = randomUUID().toString();
        final String groupId = randomUUID().toString();
        final String groupName = "Judges";

        List<Group> groups = Arrays.asList(new Group(groupId, groupName));
        stubFor(get(urlEqualTo(format(USERS_GROUPS_PATH, userId, groupName)))
                .withHeader("Accept", equalTo(MIME_TYPE))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withHeader("Content-Type", MIME_TYPE)
                        .withHeader("CPPID", randomUUID().toString())
                        .withBody(groupsJson().withGroups(groups).toString())));

        final Action action =
                new Action(envelope()
                        .with(metadata().withUserId(userId)).build());

        assertTrue(userAndGroupProvider.isInGroup(action, groupName));
    }

    @Test
    public void shouldReturnFalseIfUsersGroupContextResponseDoesNotContainGroup() throws Exception {

        final String userId = randomUUID().toString();
        final String groupName = "Judges";

        stubFor(get(urlEqualTo(format(USERS_GROUPS_PATH, userId, groupName)))
                .withHeader("Accept", equalTo(MIME_TYPE))
                .willReturn(aResponse()
                        .withStatus(NOT_FOUND.getStatusCode())));

        final Action action =
                new Action(envelope().with(metadata().withUserId(userId)).build());

        assertFalse(userAndGroupProvider.isInGroup(action, groupName));
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
