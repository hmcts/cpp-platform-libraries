package uk.gov.moj.cpp.featurecontrol.remote;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.core.featurecontrol.domain.Feature;
import uk.gov.moj.cpp.featurecontrol.remote.utils.StubFileReader;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class DefaultAzureFeatureFetcherTest {

    @InjectMocks
    private DefaultAzureFeatureFetcher defaultAzureFeatureFetcher;

    @Mock
    private AzureRestClient azureRestClient;

    @Mock
    private Logger logger;

    @Test
    public void shouldGetTheListOfFeaturesFromAzure() throws IOException {
        setField(defaultAzureFeatureFetcher, "featureLabel", "STE11");
        setField(defaultAzureFeatureFetcher, "featureManagerConnectionString", "Endpoint=http://localhost:8080;Id=myId;Secret=mySecret");
        when(azureRestClient.executeGet(any(), any(), any())).thenReturn(new StubFileReader().getFileContentAsJson("azure-feature-list.json"));

        final List<Feature> features = defaultAzureFeatureFetcher.fetchFeatures();

        assertThat(features.isEmpty(), is(false));
        assertThat(features.get(0).getFeatureName(), is("bulkscan-reviewNewDocuments"));
        assertThat(features.get(0).isEnabled(), is(true));
    }

    @Test
    public void shouldGetEmptyListIfFeatureLabelIsNotSet() throws IOException {
        setField(defaultAzureFeatureFetcher, "featureManagerConnectionString", "Endpoint=http://localhost:8080;Id=myId;Secret=mySecret");

        final List<Feature> features = defaultAzureFeatureFetcher.fetchFeatures();

        assertThat(features.isEmpty(), is(true));
    }

    @Test
    public void shouldGetEmptyListIfFeatureManagerConnectionStringIsNotSet() throws IOException {
        setField(defaultAzureFeatureFetcher, "featureLabel", "STE11");

        final List<Feature> features = defaultAzureFeatureFetcher.fetchFeatures();

        assertThat(features.isEmpty(), is(true));
    }
}
