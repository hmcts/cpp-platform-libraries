package uk.gov.justice.services.unifiedsearch.client.utils;

import static java.lang.String.format;

public class IndexFileNameUtils {

    private IndexFileNameUtils() {
    }

    public static String generateIndexJsonSchemaFilePath(final String indexName) {
        final String prefix = indexName.replaceAll("_", "-");
        return format("/json/schema/%s-schema.json", prefix);
    }
}
