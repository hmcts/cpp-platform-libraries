package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.AddressDocument;

public class AddressDocumentMother {

    private AddressDocumentMother() {
        throw new IllegalStateException("Utility class");
    }

    public static AddressDocument defaultAddressDocumentMother() {
        final AddressDocument.Builder addressDocument = new AddressDocument.Builder();
        addressDocument.setAddress1("address1");
        addressDocument.setAddress2("address2");
        addressDocument.setAddress3("address3");
        addressDocument.setAddress4("address4");
        addressDocument.setAddress5("address5");
        addressDocument.setPostCode("postcode");
        return addressDocument.build();
    }
}
