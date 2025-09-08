package uk.gov.justice.services.unifiedsearch.client.search;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.apache.lucene.search.TotalHits.Relation.EQUAL_TO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.converter.ObjectToJsonObjectConverter;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import java.io.IOException;

import javax.json.JsonArray;
import javax.json.JsonObject;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
    private ObjectToJsonObjectConverter objectToJsonObjectConverter;
    @Mock
    private JsonObject hitAsJsonObject;

    @Mock
    private Object hitAsObject;

    private SearchHits searchHits;
    private SearchHit searchHit;

    @InjectMocks
    private SearchResultConverter searchResultConverter;

    @BeforeEach
    public void setUp() {
        searchHit = new SearchHit(1);
        searchHits = new SearchHits(new SearchHit[]{searchHit}, new TotalHits(1, EQUAL_TO), 1F);
    }

    @Test
    public void shouldThrowException() throws Exception {
        when(objectMapper.readValue(searchHit.getSourceAsString(), Object.class)).thenThrow(new JsonGenerationException("Oops", mock(JsonGenerator.class)));

        var e = assertThrows(UnifiedSearchClientException.class, () -> searchResultConverter.toJsonArray(searchHits, Object.class));
        assertThat(e.getMessage(), is("Failed to deserialize search response"));

        verify(objectMapper).readValue(searchHit.getSourceAsString(), Object.class);
        verifyNoMoreInteractions(objectMapper, objectToJsonObjectConverter);
    }

    @Test
    public void shouldToJsonArray() throws IOException {
        when(objectMapper.readValue(searchHit.getSourceAsString(), Object.class)).thenReturn(hitAsObject);
        when(objectToJsonObjectConverter.convert(hitAsObject)).thenReturn(hitAsJsonObject);

        final JsonArray actualResult = searchResultConverter.toJsonArray(searchHits, Object.class);

        verify(objectMapper).readValue(searchHit.getSourceAsString(), Object.class);
        verify(objectToJsonObjectConverter).convert(hitAsObject);

        assertThat(actualResult, is(notNullValue()));
        assertThat(actualResult, hasSize(1));

        verifyNoMoreInteractions(objectMapper, objectToJsonObjectConverter);
    }
}