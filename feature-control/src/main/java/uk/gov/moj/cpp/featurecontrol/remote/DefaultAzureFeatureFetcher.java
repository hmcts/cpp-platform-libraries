package uk.gov.moj.cpp.featurecontrol.remote;


import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static javax.json.Json.createReader;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import uk.gov.justice.services.common.configuration.GlobalValue;
import uk.gov.justice.services.core.featurecontrol.FeatureFetcher;
import uk.gov.justice.services.core.featurecontrol.domain.Feature;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

@Alternative
@Priority(1)
public class DefaultAzureFeatureFetcher implements FeatureFetcher {

    @Inject
    private Logger logger;

    private static final String LOGGER_FAILURE_MESSAGE = "Failed to fetch Features from azure {}";
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


    @Override
    public List<Feature> fetchFeatures() {

        if (!validateJndiProperties()) {
            return emptyList();
        }

        logger.info("fetching Features from azure ...");

        try {
            String[] connectionDetails = splitConnectionString();
            return Optional.ofNullable(
                    azureRestClient.executeGet(
                            getUrl(connectionDetails[0]),
                            getId(connectionDetails[1]),
                            getSecret(connectionDetails[2]))
                            .getJsonArray("items"))
                    .orElse(Json.createArrayBuilder().build())
                    .getValuesAs(JsonObject.class)
                    .stream()
                    .filter(f -> StringUtils.isNotEmpty(f.getString("content_type")))
                    .map(ft -> new Feature(removeFeaturePrefix(ft.getString("key")), convertToBoolean(ft.getString("value"))))
                    .collect(toList());
        } catch (AzureRequestException ex) {
            logger.error(LOGGER_FAILURE_MESSAGE, ex);
        }

        return emptyList();
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
        try (JsonReader reader = createReader(new StringReader(value))) {
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
