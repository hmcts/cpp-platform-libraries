package uk.gov.moj.cpp.featurecontrol.remote;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.core.featurecontrol.DefaultFeatureControlGuard;
import uk.gov.justice.services.core.featurecontrol.FeatureStoreFacade;
import uk.gov.justice.services.core.featurecontrol.local.LocalFeatureFileLocator; // gitleaks:allow
import uk.gov.justice.services.core.featurecontrol.local.LocalFeatureStore; // gitleaks:allow
import uk.gov.justice.services.core.featurecontrol.remote.CachingFeatureProviderTimerBean;
import uk.gov.justice.services.core.featurecontrol.remote.FeatureControlConfiguration;
import uk.gov.justice.services.core.featurecontrol.remote.FeatureFetcherFacade;
import uk.gov.justice.services.core.featurecontrol.remote.NonCachingFeatureProvider;
import uk.gov.justice.services.core.featurecontrol.remote.RemoteFeatureStore;

import java.util.Base64;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Runs the real feature control classes from the framework and from this module against a
 * WireMock stand-in for Azure App Configuration, so the HTTP call, request signing, JSON parsing,
 * caching and feature lookup are all exercised together.
 *
 * The cache refresh is triggered by calling reloadFeatures() directly, as the EJB timer would.
 */
public class FeatureControlAzureIT {

    private static final String FEATURE_NAME = "some-feature";
    private static final String FEATURE_LABEL = "TEST";
    private static final String AZURE_CREDENTIAL = "test-credential";
    private static final String AZURE_SECRET = Base64.getEncoder().encodeToString("test-secret".getBytes(UTF_8));

    private WireMockServer azureAppConfiguration;
    private FeatureControlConfiguration featureControlConfiguration;
    private CachingFeatureProviderTimerBean cachingFeatureProviderTimerBean;
    private DefaultFeatureControlGuard defaultFeatureControlGuard;

    @BeforeEach
    public void startAzureAppConfigurationStubAndWireTheFeatureControlClasses() {
        azureAppConfiguration = new WireMockServer(options().dynamicPort());
        azureAppConfiguration.start();

        final AzureSignedRequest azureSignedRequest = new AzureSignedRequest();
        setField(azureSignedRequest, "clock", new UtcClock());

        final AzureRestClient azureRestClient = new AzureRestClient();
        setField(azureRestClient, "azureSignedRequest", azureSignedRequest);

        final DefaultAzureFeatureFetcher defaultAzureFeatureFetcher = new DefaultAzureFeatureFetcher();
        setField(defaultAzureFeatureFetcher, "logger", getLogger(DefaultAzureFeatureFetcher.class));
        setField(defaultAzureFeatureFetcher, "featureLabel", FEATURE_LABEL);
        setField(defaultAzureFeatureFetcher, "featureManagerConnectionString", format("Endpoint=%s;Id=%s;Secret=%s", azureAppConfiguration.baseUrl(), AZURE_CREDENTIAL, AZURE_SECRET));
        setField(defaultAzureFeatureFetcher, "azureRestClient", azureRestClient);

        featureControlConfiguration = new FeatureControlConfiguration();
        setField(featureControlConfiguration, "featureControlEnabled", "true");
        setField(featureControlConfiguration, "featureControlCacheEnabled", "true");

        final FeatureFetcherFacade featureFetcherFacade = new FeatureFetcherFacade();
        setField(featureFetcherFacade, "featureControlConfiguration", featureControlConfiguration);
        setField(featureFetcherFacade, "featureFetcher", defaultAzureFeatureFetcher);
        setField(featureFetcherFacade, "logger", getLogger(FeatureFetcherFacade.class));

        cachingFeatureProviderTimerBean = new CachingFeatureProviderTimerBean();
        setField(cachingFeatureProviderTimerBean, "featureFetcherFacade", featureFetcherFacade);
        setField(cachingFeatureProviderTimerBean, "logger", getLogger(CachingFeatureProviderTimerBean.class));

        final NonCachingFeatureProvider nonCachingFeatureProvider = new NonCachingFeatureProvider();
        setField(nonCachingFeatureProvider, "featureFetcherFacade", featureFetcherFacade);

        final RemoteFeatureStore remoteFeatureStore = new RemoteFeatureStore();
        setField(remoteFeatureStore, "cachingFeatureProviderTimerBean", cachingFeatureProviderTimerBean);
        setField(remoteFeatureStore, "nonCachingFeatureProvider", nonCachingFeatureProvider);
        setField(remoteFeatureStore, "featureControlConfiguration", featureControlConfiguration);

        final LocalFeatureFileLocator localFeatureFileLocator = mock(LocalFeatureFileLocator.class);
        when(localFeatureFileLocator.findLocalFeatureFileLocation("feature-control.yaml")).thenReturn(empty());

        final LocalFeatureStore localFeatureStore = new LocalFeatureStore();
        setField(localFeatureStore, "localFeatureFileLocator", localFeatureFileLocator);
        setField(localFeatureStore, "logger", getLogger(LocalFeatureStore.class));

        final FeatureStoreFacade featureStoreFacade = new FeatureStoreFacade();
        setField(featureStoreFacade, "localFeatureStore", localFeatureStore);
        setField(featureStoreFacade, "remoteFeatureStore", remoteFeatureStore);

        defaultFeatureControlGuard = new DefaultFeatureControlGuard();
        setField(defaultFeatureControlGuard, "featureStore", featureStoreFacade);
    }

    @AfterEach
    public void stopAzureAppConfigurationStub() {
        azureAppConfiguration.stop();
    }

    @Test
    public void shouldEnableAFeatureThatAzureReturnsAsEnabledUsingASignedRequest() {

        azureReturnsTheFeatureAs(true);

        cachingFeatureProviderTimerBean.reloadFeatures();

        assertThat(defaultFeatureControlGuard.isFeatureEnabled(FEATURE_NAME), is(true));

        azureAppConfiguration.verify(getRequestedFor(urlPathEqualTo("/kv"))
                .withQueryParam("label", equalTo(FEATURE_LABEL))
                .withHeader("Authorization", matching("HMAC-SHA256 Credential=" + AZURE_CREDENTIAL + ", SignedHeaders=x-ms-date;host;x-ms-content-sha256, Signature=.+"))
                .withHeader("x-ms-date", matching(".+"))
                .withHeader("x-ms-content-sha256", matching(".+")));
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403, 429, 500, 503})
    public void shouldKeepAFeatureEnabledIfRefreshingFromAzureFailsWithHttpStatus(final int httpStatus) {

        azureReturnsTheFeatureAs(true);
        cachingFeatureProviderTimerBean.reloadFeatures();

        azureReturnsHttpStatus(httpStatus);
        cachingFeatureProviderTimerBean.reloadFeatures();

        assertThat(defaultFeatureControlGuard.isFeatureEnabled(FEATURE_NAME), is(true));
    }

    @Test
    public void shouldKeepAFeatureEnabledIfAzureCannotBeReachedWhenRefreshing() {

        azureReturnsTheFeatureAs(true);
        cachingFeatureProviderTimerBean.reloadFeatures();

        azureAppConfiguration.stop();
        cachingFeatureProviderTimerBean.reloadFeatures();

        assertThat(defaultFeatureControlGuard.isFeatureEnabled(FEATURE_NAME), is(true));
    }

    @Test
    public void shouldKeepAFeatureEnabledIfAzureReturnsAResponseWithNoFeatureListWhenRefreshing() {

        azureReturnsTheFeatureAs(true);
        cachingFeatureProviderTimerBean.reloadFeatures();

        azureAppConfiguration.resetAll();
        azureAppConfiguration.stubFor(get(urlPathEqualTo("/kv")).willReturn(okJson("{}")));
        cachingFeatureProviderTimerBean.reloadFeatures();

        assertThat(defaultFeatureControlGuard.isFeatureEnabled(FEATURE_NAME), is(true));
    }

    @Test
    public void shouldDisableAFeatureWhenAzureRecoversFromAFailureWithTheFeatureDisabled() {

        azureReturnsTheFeatureAs(true);
        cachingFeatureProviderTimerBean.reloadFeatures();

        azureReturnsHttpStatus(503);
        cachingFeatureProviderTimerBean.reloadFeatures();

        azureReturnsTheFeatureAs(false);
        cachingFeatureProviderTimerBean.reloadFeatures();

        assertThat(defaultFeatureControlGuard.isFeatureEnabled(FEATURE_NAME), is(false));
    }

    @Test
    public void shouldThrowRatherThanReportAFeatureAsDisabledIfAzureFailsWhenTheCacheIsNotEnabled() {

        setField(featureControlConfiguration, "featureControlCacheEnabled", "false");
        azureReturnsHttpStatus(503);

        final AzureRequestException azureRequestException = assertThrows(AzureRequestException.class, () ->
                defaultFeatureControlGuard.isFeatureEnabled(FEATURE_NAME));

        assertThat(azureRequestException.getMessage(), is(format("Failed to fetch features from azure; url %s/kv?label=%s returned HTTP status 503", azureAppConfiguration.baseUrl(), FEATURE_LABEL)));
    }

    private void azureReturnsTheFeatureAs(final boolean enabled) {
        final String featureFlagValue = getJsonBuilderFactory().createObjectBuilder()
                .add("id", FEATURE_NAME)
                .add("enabled", enabled)
                .build()
                .toString();

        final String azureResponse = getJsonBuilderFactory().createObjectBuilder()
                .add("items", getJsonBuilderFactory().createArrayBuilder()
                        .add(getJsonBuilderFactory().createObjectBuilder()
                                .add("key", ".appconfig.featureflag/" + FEATURE_NAME)
                                .add("label", FEATURE_LABEL)
                                .add("content_type", "application/vnd.microsoft.appconfig.ff+json;charset=utf-8")
                                .add("value", featureFlagValue)))
                .build()
                .toString();

        azureAppConfiguration.resetAll();
        azureAppConfiguration.stubFor(get(urlPathEqualTo("/kv"))
                .withQueryParam("label", equalTo(FEATURE_LABEL))
                .willReturn(okJson(azureResponse)));
    }

    private void azureReturnsHttpStatus(final int httpStatus) {
        azureAppConfiguration.resetAll();
        azureAppConfiguration.stubFor(get(urlPathEqualTo("/kv"))
                .willReturn(aResponse().withStatus(httpStatus)));
    }
}
