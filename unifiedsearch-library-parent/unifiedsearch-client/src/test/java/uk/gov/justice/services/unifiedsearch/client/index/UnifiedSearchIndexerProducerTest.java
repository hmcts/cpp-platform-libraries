package uk.gov.justice.services.unifiedsearch.client.index;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.cdi.QualifierAnnotationExtractor;
import uk.gov.justice.services.unifiedsearch.UnifiedSearchName;
import uk.gov.justice.services.unifiedsearch.client.utils.IndexInfo;
import uk.gov.justice.services.unifiedsearch.client.utils.UnifiedSearchClientException;

import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UnifiedSearchIndexerProducerTest {

    @Mock
    private QualifierAnnotationExtractor qualifierAnnotationExtractor;

    @Mock
    private DefaultUnifiedSearchIndexer searchIndexer;

    @InjectMocks
    private UnifiedSearchIndexerProducer unifiedSearchIndexerProducer;


    @Test
    public void shouldProduceUnifiedSearchIndexer() throws Exception {

        final InjectionPoint injectionPoint = mock(InjectionPoint.class);

        final UnifiedSearchName unifiedSearchName = mock(UnifiedSearchName.class);

        when(qualifierAnnotationExtractor.getFrom(injectionPoint, UnifiedSearchName.class)).thenReturn(unifiedSearchName);
        when(unifiedSearchName.value()).thenReturn(IndexInfo.CRIME_CASE.getIndexName());

        assertThat(unifiedSearchIndexerProducer.unifiedSearchClient(injectionPoint), is(searchIndexer));
    }

    @Test
    public void shouldThrowExceptionIfIndexNameNotFound() throws Exception {

        try {
            final InjectionPoint injectionPoint = mock(InjectionPoint.class);

            final UnifiedSearchName unifiedSearchName = mock(UnifiedSearchName.class);

            when(qualifierAnnotationExtractor.getFrom(injectionPoint, UnifiedSearchName.class)).thenReturn(unifiedSearchName);
            when(unifiedSearchName.value()).thenReturn("unknown_index");

            unifiedSearchIndexerProducer.unifiedSearchClient(injectionPoint);
            fail();
        } catch (final UnifiedSearchClientException expected) {
            assertThat(expected.getMessage(), is("Unknown @UnifiedSearchName value: unknown_index"));
        }
    }
}


