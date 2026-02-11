package uk.gov.justice.services.unifiedsearch.client.factory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;


import java.util.HashMap;
import java.util.Map;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import org.junit.jupiter.api.Test;

public class UpdateRequestFactoryTest {

    @Test
    public void shouldCreateUpdateRequest() {
        final String index = "test";
        final String documentId = "123";
        Map<String, Object> docMap = new HashMap();

        final UpdateRequestFactory updateRequestFactory = new UpdateRequestFactory();
        final UpdateRequest updateRequest = updateRequestFactory.updateRequest(index, documentId, docMap, IndexRequest.of( t-> t.index("").document("")));

        assertThat(updateRequest, instanceOf(UpdateRequest.class));
        assertThat(updateRequest.refresh(), is(nullValue()));
    }
}