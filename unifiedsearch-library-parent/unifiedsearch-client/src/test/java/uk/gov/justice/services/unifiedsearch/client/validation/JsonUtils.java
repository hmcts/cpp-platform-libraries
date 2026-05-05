package uk.gov.justice.services.unifiedsearch.client.validation;

import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;

import static uk.gov.justice.services.messaging.JsonObjects.getJsonReaderFactory;

public class JsonUtils {

    public static JsonObject jsonObjectFromFile(final String filePath) throws IOException {
        try (final InputStream inputStream = JsonUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            final JsonReader jsonReader = getJsonReaderFactory().createReader(inputStream);
            return jsonReader.readObject();
        }
    }
}
