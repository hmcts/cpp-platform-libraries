package uk.gov.moj.cpp.accesscontrol.progression.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.runner.RunWith;
import org.slf4j.helpers.NOPLogger;
import uk.gov.justice.api.provider.RemoteAccessControl2ProgressionQueryApi;
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
import uk.gov.justice.services.messaging.logging.DefaultTraceLogger;
import uk.gov.justice.services.test.utils.common.validator.DummyJsonSchemaValidator;
import uk.gov.justice.services.test.utils.core.handler.registry.TestHandlerRegistryCacheProducer;
import uk.gov.moj.cpp.accesscontrol.drools.Action;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder.envelope;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataOf;
import static uk.gov.moj.cpp.accesscontrol.progression.test.util.ProgressionSearchCaseByMaterialIdJsonResponseBuilder.aSearchCaseByMaterialIdJsonResponse;

@RunWith(ApplicationComposer.class)
@WireMockTest(httpPort = 8080)
public class ProgressionProviderIT {

    private static final String CASE_ID_KEY = "caseId";
    private static final String MATERIAL_ID_KEY = "materialId";


    private static final String MATERIAL_ID = UUID.randomUUID().toString();
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String PROGRESSION_QUERY_API_QUERY_API_REST_SEARCH = "/progression-query-api/query/api/rest/progression/search?q=%s";
    private static final String PROGRESSION_QUERY_CASES_SEARCH_BY_MATERIAL_ID = "application/vnd.progression.query.cases-search-by-material-id+json";


    private static int port = -1;

    @Inject
    private ProgressionProvider progressionProvider;

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
            ProgressionProvider.class,
            ProsecutingAuthority.class,
            RemoteAccessControl2ProgressionQueryApi.class,

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
                .contextRoot("progression-provider")
                .addServlet("TestApp", Application.class.getName());
    }

    @Test
    public void isMaterialFromCPSProsecutedCase_shouldReturnTrue_ifCaseIsCPS() throws Exception {
        stubProgressionSearchByMaterialIdWithProsecutingAuthority(ProsecutingAuthority.CPS.name());

        assertTrue(progressionProvider.isMaterialFromCPSProsecutedCase(getAction(USER_ID, MATERIAL_ID_KEY, MATERIAL_ID)));
    }

    @Test
    public void isMaterialFromCPSProsecutedCase_shouldReturnFalse_ifCaseIsNotCPS() throws Exception {
        stubProgressionSearchByMaterialIdWithProsecutingAuthority("TFL");

        assertFalse(progressionProvider.isMaterialFromCPSProsecutedCase(getAction(USER_ID, MATERIAL_ID_KEY, MATERIAL_ID)));
    }


    private void stubProgressionSearchByMaterialIdWithProsecutingAuthority(String prosecutingAuthority) {
        stubFor(get(urlEqualTo(format(PROGRESSION_QUERY_API_QUERY_API_REST_SEARCH, MATERIAL_ID)))
                .withHeader("Accept", equalTo(PROGRESSION_QUERY_CASES_SEARCH_BY_MATERIAL_ID))
                .willReturn(aResponse()
                        .withStatus(OK.getStatusCode())
                        .withHeader("Content-Type", PROGRESSION_QUERY_CASES_SEARCH_BY_MATERIAL_ID)
                        .withHeader("CPPID", randomUUID().toString())
                        .withBody(aSearchCaseByMaterialIdJsonResponse()
                                .withProsecutingAuthority(prosecutingAuthority)
                                .build().toString())));
    }


    private Action getAction(final String userId, final String key, final String value) {
        return new Action(envelope().with(metadataOf(randomUUID(), "dummy")
                .withUserId(userId))
                .withPayloadOf(value, key)
                .build());
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
