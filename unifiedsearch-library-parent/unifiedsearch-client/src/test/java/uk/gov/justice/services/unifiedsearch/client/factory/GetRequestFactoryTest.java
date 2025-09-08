package uk.gov.justice.services.unifiedsearch.client.factory;

import static java.util.UUID.randomUUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.util.UUID;

import org.elasticsearch.action.get.GetRequest;
import org.junit.jupiter.api.Test;

public class GetRequestFactoryTest {
    
    @Test
    public void shouldCreateGetRequest() {
        final UUID documentId = randomUUID();
        final String index = "test";
        final GetRequest getRequest = new GetRequestFactory().getRequest(index, documentId);
        assertThat(getRequest, instanceOf(GetRequest.class));

        assertThat(getRequest.id(), is(documentId.toString()));
        assertThat(getRequest.index(), is(index));
    }
}
