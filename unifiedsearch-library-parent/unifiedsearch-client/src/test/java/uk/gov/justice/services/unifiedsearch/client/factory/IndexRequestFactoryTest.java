package uk.gov.justice.services.unifiedsearch.client.factory;

import static java.util.UUID.randomUUID;
import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.NONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.unifiedsearch.client.index.UnifiedSearchIndexerHelper;

import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;

import org.elasticsearch.action.index.IndexRequest;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class IndexRequestFactoryTest {

    @Spy
    private UnifiedSearchIndexerHelper unifiedSearchIndexerHelper;

    @InjectMocks
    private IndexRequestFactory indexRequestFactory;

    @Test
    public void shouldCreateIndexRequest() {

        final UUID caseId = randomUUID();
        final String index = "test";

        final JsonObject document = Json.createObjectBuilder().add("document", "document")
                .add("caseId", caseId.toString()).build();

        final IndexRequest indexRequest = indexRequestFactory.indexRequest(index, document, 1l, 1l);

        assertThat(indexRequest, instanceOf(IndexRequest.class));
        assertThat(indexRequest.id(), is(caseId.toString()));
        assertThat(indexRequest.index(), is(index));
        assertThat(indexRequest.getRefreshPolicy().getValue(), is(NONE.getValue()));
        assertThat(indexRequest.ifSeqNo(), is(1l));
        assertThat(indexRequest.ifPrimaryTerm(), is(1l));
    }
}
