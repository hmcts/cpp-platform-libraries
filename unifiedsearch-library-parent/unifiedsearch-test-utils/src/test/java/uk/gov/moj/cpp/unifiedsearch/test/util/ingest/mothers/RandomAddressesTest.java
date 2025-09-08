package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomAddresses.randomAddress;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomAddresses.randomAddressObject;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomAddresses.randomPostcode;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.AddressDocument;

import org.junit.jupiter.api.Test;

public class RandomAddressesTest {

    @Test
    public void shouldCreateRandomAddress() {
        assertThat(randomAddress(), is(notNullValue()));
    }

    @Test
    public void shouldCreateRandomPostcode() {
        assertThat(randomPostcode(), is(notNullValue()));
    }

    @Test
    public void shouldCreateRandomAddressObject() {
        final AddressDocument address = randomAddressObject();

        assertThat(address, is(notNullValue()));
        assertThat(address.getAddress1(), is(notNullValue()));
        assertThat(address.getAddress2(), is(notNullValue()));
        assertThat(address.getAddress3(), is(notNullValue()));
        assertThat(address.getPostCode(), is(notNullValue()));
    }
}