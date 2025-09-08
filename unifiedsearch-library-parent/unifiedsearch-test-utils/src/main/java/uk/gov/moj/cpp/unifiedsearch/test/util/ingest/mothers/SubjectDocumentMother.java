package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.UUID.randomUUID;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.AddressDocumentMother.defaultAddressDocumentMother;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.SubjectDocument;

public class SubjectDocumentMother {

    private SubjectDocumentMother() {
        throw new IllegalStateException("Utility class");
    }

    public static SubjectDocument defaultSubjectDocumentMother() {
        final SubjectDocument subjectDocument = new SubjectDocument();
        subjectDocument.setSubjectId(randomUUID().toString());
        subjectDocument.setFirstName("First_Name");
        subjectDocument.setLastName("Last_Name");
        subjectDocument.setMiddleName("Middle_Name");
        subjectDocument.setDateOfBirth("Date_Of_Birth");
        subjectDocument.setMasterDefendantId(randomUUID().toString());
        subjectDocument.setAddress(defaultAddressDocumentMother());
        return subjectDocument;
    }


}
