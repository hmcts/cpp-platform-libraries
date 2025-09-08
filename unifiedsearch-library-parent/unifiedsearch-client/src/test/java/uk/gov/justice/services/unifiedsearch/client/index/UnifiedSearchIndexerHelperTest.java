package uk.gov.justice.services.unifiedsearch.client.index;

import static java.util.UUID.randomUUID;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import java.util.UUID;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.junit.jupiter.api.Test;

public class UnifiedSearchIndexerHelperTest {

    private final UnifiedSearchIndexerHelper unifiedSearchIndexerHelper = new UnifiedSearchIndexerHelper();

    @Test
    public void shouldGetCaseId() {

        final UUID caseId = randomUUID();

        final JsonObject index = createObjectBuilder()
                .add("caseId", caseId.toString())
                .build();

        assertThat(unifiedSearchIndexerHelper.getCaseId(index), is(caseId));
    }

    @Test
    public void shouldFailIfNoCaseIdFound() {
        try {
            final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();
            jsonObjectBuilder.add("unknownAttribute", "123455");

            unifiedSearchIndexerHelper.getCaseId(jsonObjectBuilder.build());

            fail();
        } catch (final UnifiedSearchClientException expected) {
            assertThat(expected.getMessage(), is("Case id not present for index"));

        }
    }
}
