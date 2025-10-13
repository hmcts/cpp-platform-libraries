package uk.gov.moj.cpp.featurecontrol.remote.utils;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.justice.services.messaging.JsonObjects.jsonReaderFactory;

public class StubFileReader {

    private final Logger LOGGER = LoggerFactory.getLogger(StubFileReader.class);

    public String getPayload(String path) {
        String request = null;
        try {
            request = Resources.toString(
                    Resources.getResource(path),
                    Charset.defaultCharset()
            );
        } catch (Exception e) {
            LOGGER.error("Error consuming file from location {}", path, e);
            fail("Error consuming file from location " + path);
        }
        return request;
    }


    public JsonObject getFileContentAsJson(final String path) {
        return getJsonObject(getPayload(path));
    }

    public JsonObject getJsonObject(final String json) {
        try (final JsonReader reader = jsonReaderFactory.createReader(new StringReader(json))) {
            return reader.readObject();
        }
    }
}
