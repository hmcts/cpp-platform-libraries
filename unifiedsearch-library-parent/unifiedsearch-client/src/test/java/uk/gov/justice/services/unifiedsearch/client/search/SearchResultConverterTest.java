package uk.gov.justice.services.unifiedsearch.client.search;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.JsonpMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.unifiedsearch.client.domain.CaseDetails;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import javax.json.JsonArray;
import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.core.search.Hit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SearchResultConverterTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JsonpMapper jsonpMapper;

    @Mock
    private JsonObject hitAsJsonObject;

    private Hit<JsonData> searchHit;

    @InjectMocks
    private SearchResultConverter searchResultConverter;

    @BeforeEach
    public void setUp() {
        searchHit = Hit.<JsonData>of(h -> h.index("dummy-index").id("1").score(1.0).source(JsonData.of(new CaseDetails())));
    }

    @Test
    public void shouldThrowException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any(Object.class))).thenThrow(new IllegalArgumentException("Oops", mock(Throwable.class)));

        var e = assertThrows(UnifiedSearchClientException.class, () -> searchResultConverter.toJsonArray(singletonList(searchHit), Object.class));
        assertThat(e.getMessage(), is("Failed to deserialize search response"));

        verify(objectMapper).writeValueAsString(any(CaseDetails.class));
        verifyNoMoreInteractions(objectMapper);
    }

    @Test
    public void shouldToJsonArray() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any(CaseDetails.class))).thenReturn("{}");

        final JsonArray actualResult = searchResultConverter.toJsonArray(singletonList(searchHit), Object.class);

        verify(objectMapper).writeValueAsString(any(CaseDetails.class));

        assertThat(actualResult, is(notNullValue()));
        assertThat(actualResult, hasSize(1));

        verifyNoMoreInteractions(objectMapper);
    }
}