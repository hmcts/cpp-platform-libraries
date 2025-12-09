package uk.gov.moj.cpp.system.documentgenerator.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import javax.ws.rs.client.Client;

import org.junit.jupiter.api.Test;

public class HttpClientFactoryTest {

    private HttpClientFactory underTest = new HttpClientFactory();

    @Test
    public void shouldGetSameObjectOnEveryRequest() {

        Client client_1 = null;
        Client client_2 = null;
        try {
            client_1 = underTest.getClient();
            client_2 = underTest.getClient();

            assertThat(client_1, is(client_2));
        } finally {
            closeQuietly(client_1);
            closeQuietly(client_2);
        }
    }

    private void closeQuietly(final Client client) {
        if (client != null) {
            client.close();
        }
    }

}
