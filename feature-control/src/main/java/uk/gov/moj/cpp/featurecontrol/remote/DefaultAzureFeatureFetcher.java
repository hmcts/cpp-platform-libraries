package uk.gov.moj.cpp.featurecontrol.remote;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import uk.gov.justice.services.common.configuration.GlobalValue;
import uk.gov.justice.services.core.featurecontrol.FeatureFetcher;
import uk.gov.justice.services.core.featurecontrol.domain.Feature;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonReaderFactory;

@Alternative
@Priority(1)
public class DefaultAzureFeatureFetcher implements FeatureFetcher {

    @Inject
    private Logger logger;

    private static final String ITEMS = "items";
    private static final String KEY_VALUE_URI = "/kv?label=%s";
    private static final String FEATURE_PREFIX = ".appconfig.featureflag/";


    @Inject
    @GlobalValue(key = "featureLabel", defaultValue = EMPTY)
    private String featureLabel;

    @Inject
    @GlobalValue(key = "featureManagerConnectionString", defaultValue = EMPTY)
    private String featureManagerConnectionString;

    @Inject
    private AzureRestClient azureRestClient;


    /**
     * Fetches the Features from Azure App Configuration. Returns an empty list only if the
     * connection to Azure is not configured. If Azure can't be reached, or doesn't return a list
     * of Features, an {@link AzureRequestException} is thrown so that the caller can tell a failed
     * fetch from there being no Features.
     */
    @Override
    public List<Feature> fetchFeatures() {

        if (validateJndiProperties()) {
            logger.info("fetching Features from azure ...");

            final String[] connectionDetails = splitConnectionString();
            final JsonObject response = azureRestClient.executeGet(
                    getUrl(connectionDetails[0]),
                    getId(connectionDetails[1]),
                    getSecret(connectionDetails[2]));

            return featureItemsFrom(response)
                    .getValuesAs(JsonObject.class)
                    .stream()
                    .filter(f -> StringUtils.isNotEmpty(f.getString("content_type")))
                    .map(ft -> new Feature(removeFeaturePrefix(ft.getString("key")), convertToBoolean(ft.getString("value"))))
                    .collect(toList());
        }

        return emptyList();
    }

    private JsonArray featureItemsFrom(final JsonObject response) {
        if (response.containsKey(ITEMS)) {
            return response.getJsonArray(ITEMS);
        }

        throw new AzureRequestException(format("Failed to fetch features from azure; response has no '%s': %s", ITEMS, response));
    }

    private boolean validateJndiProperties() {
        if (isEmpty(featureManagerConnectionString)) {
            logger.warn("featureManagerConnectionString is not set as JNDI property. Not calling azure. Returning empty feature list.");
            return false;
        }

        if (isEmpty(featureLabel)) {
            logger.warn("featureLabel is not set as JNDI property. Not calling azure. Returning empty feature list.");
            return false;
        }
        return true;
    }

    private String removeFeaturePrefix(final String key) {
        return key.replace(FEATURE_PREFIX, "");
    }

    private boolean convertToBoolean(final String value) {
        try (JsonReader reader = getJsonReaderFactory().createReader(new StringReader(value))) {
            return reader.readObject().getBoolean("enabled");
        }
    }

    private String[] splitConnectionString() {
        return featureManagerConnectionString.split(";");
    }

    private String getUrl(final String value) {
        return value.replace("Endpoint=", "") + format(KEY_VALUE_URI, featureLabel);
    }

    private String getId(final String value) {
        return value.replace("Id=", "");
    }

    private String getSecret(final String value) {
        return value.replace("Secret=", "");
    }

}
