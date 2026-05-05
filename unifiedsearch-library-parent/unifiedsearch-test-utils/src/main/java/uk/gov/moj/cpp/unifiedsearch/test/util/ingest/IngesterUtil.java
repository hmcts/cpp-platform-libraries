package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

import uk.gov.justice.services.test.utils.core.messaging.Poller;

import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;

import static uk.gov.justice.services.messaging.JsonObjects.getJsonReaderFactory;

public class IngesterUtil {

    private static final Poller poller = new Poller(100, 1000L);

    public static JsonObject jsonFromString(final String jsonObjectStr) {
        JsonObject object;
        try (JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(jsonObjectStr))) {
            object = jsonReader.readObject();
        }

        return object;
    }

    public static Poller getPoller() {
        return poller;
    }
}
