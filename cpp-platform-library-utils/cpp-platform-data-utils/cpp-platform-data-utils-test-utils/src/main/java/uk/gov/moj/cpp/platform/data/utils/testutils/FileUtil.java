package uk.gov.moj.cpp.platform.data.utils.testutils;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonReaderFactory;

/**
 * Utility class for reading json response from a file.
 */
public class FileUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    public static JsonObject toJsonObject(final String jsonContent) {
        try (final StringReader stringReader = new StringReader(jsonContent);
             final JsonReader jsonReader = getJsonReaderFactory().createReader(stringReader)) {
            return jsonReader.readObject();
        }
    }

    public static JsonObject givenPayload(final String filePath) {
        return toJsonObject(getPayload(filePath));
    }

    public static byte[] getDocumentBytesFromFile(final String filepath) {
        final String payload = getPayload(filepath);
        if (payload == null) {
            return new byte[0];
        }

        return payload.getBytes(Charset.defaultCharset());
    }

    public static String getPayload(final String path) {
        String request = null;
        try {
            request = Resources.toString(
                    Resources.getResource(path),
                    Charset.defaultCharset()
            );
        } catch (IOException e) {
            final String errorDescription = "Error consuming file from location " + path;
            LOGGER.error(errorDescription, e);
            fail(errorDescription);
        }

        return request;
    }

}
