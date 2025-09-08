package uk.gov.justice.services.unifiedsearch.client.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.justice.services.unifiedsearch.client.utils.IndexFileNameUtils.generateIndexJsonSchemaFilePath;

import org.junit.jupiter.api.Test;

public class IndexFileNameUtilsTest {

    @Test
    public void shouldGenerateIndexJsonSchemaFilePath() {
        assertThat(generateIndexJsonSchemaFilePath("crime_case_index"), is("/json/schema/crime-case-index-schema.json"));
    }
}