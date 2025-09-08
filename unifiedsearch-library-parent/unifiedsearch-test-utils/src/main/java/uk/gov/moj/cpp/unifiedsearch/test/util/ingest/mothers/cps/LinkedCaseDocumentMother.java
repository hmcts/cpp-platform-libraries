package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static java.util.UUID.randomUUID;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.LinkedCaseDocument;

import java.util.ArrayList;
import java.util.List;

public class LinkedCaseDocumentMother {

    public static LinkedCaseDocument defaultLinkedCase() {
        return defaultLinkedCaseAsBuilder().build();
    }


    public static List<LinkedCaseDocument> defaultLinkedCases(final int count) {

        final List<LinkedCaseDocument> linkedCaseList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            linkedCaseList.add(defaultLinkedCaseAsBuilder().build());
        }

        return linkedCaseList;
    }

    public static List<LinkedCaseDocument.Builder> defaultLinkedCasesAsBuilder(final int count) {

        final List<LinkedCaseDocument.Builder> applicationBuilderList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            applicationBuilderList.add(defaultLinkedCaseAsBuilder());
        }

        return applicationBuilderList;
    }


    public static LinkedCaseDocument.Builder defaultLinkedCaseAsBuilder() {

        final LinkedCaseDocument.Builder builder = new LinkedCaseDocument.Builder();
        builder.withLinkedCaseId(randomUUID().toString())
                .withManuallyLinked(true);

        return builder;

    }

}
