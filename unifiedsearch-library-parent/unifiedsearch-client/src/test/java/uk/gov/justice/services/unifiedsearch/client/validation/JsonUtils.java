package uk.gov.justice.services.unifiedsearch.client.validation;

import java.io.IOException;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JsonUtils {

    public static JsonObject jsonObjectFromFile(final String filePath) throws IOException {
        try (final InputStream inputStream = JsonUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            final JsonReader jsonReader = Json.createReader(inputStream);
            return jsonReader.readObject();
        }
    }
}
