package uk.gov.moj.cpp.system.documentgenerator.client;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.services.test.utils.core.random.RandomGenerator.STRING;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.common.rest.ServerPortProvider;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DocumentGeneratorClientProducerTest {

    @InjectMocks
    private DocumentGeneratorClientProducer underTest;

    @Mock
    private ServerPortProvider serverPortProvider;

    @Test
    public void shouldCreateInstanceOfDocumentGeneratorClient() {
        given(serverPortProvider.getDefaultPort()).willReturn("8080");

        assertThat(underTest.documentGeneratorClient(), instanceOf(DocumentGeneratorClient.class));
    }

    @Test
    public void shouldUsePortProvidedByServerPortProvider() {
        given(serverPortProvider.getDefaultPort()).willReturn(STRING.next());

        underTest.documentGeneratorClient();

        verify(serverPortProvider).getDefaultPort();
    }
    
}
