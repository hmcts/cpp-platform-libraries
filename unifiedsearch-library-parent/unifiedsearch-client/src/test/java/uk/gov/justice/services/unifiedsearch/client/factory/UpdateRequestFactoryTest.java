package uk.gov.justice.services.unifiedsearch.client.factory;

import static org.elasticsearch.action.support.WriteRequest.RefreshPolicy.NONE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.junit.jupiter.api.Test;

public class UpdateRequestFactoryTest {

    @Test
    public void shouldCreateUpdateRequest() {
        final String index = "test";
        final String documentId = "123";
        final String caseDetailsString = "12345";

        final UpdateRequestFactory updateRequestFactory = new UpdateRequestFactory();
        final UpdateRequest updateRequest = updateRequestFactory.updateRequest(index, documentId, caseDetailsString, new IndexRequest());

        assertThat(updateRequest, instanceOf(UpdateRequest.class));
        assertThat(updateRequest.getRefreshPolicy().getValue(), is(NONE.getValue()));
    }
}