package uk.gov.moj.cpp.systemidmapper.client;

import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;

import static uk.gov.justice.services.messaging.JsonObjects.jsonReaderFactory;

public class FileUtil {

    public static JsonObject givenPayload(final String filePath) {
        try (final InputStream inputStream = FileUtil.class.getResourceAsStream(filePath)) {
            final JsonReader jsonReader = jsonReaderFactory.createReader(inputStream);
            return jsonReader.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
