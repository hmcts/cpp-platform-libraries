package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.cps;

import static com.google.common.collect.ImmutableList.of;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.ProsecutingAuthority;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument;

import java.util.ArrayList;
import java.util.List;

public class CaseDocumentMother {

    public static final List<String> HERTFORDSHIRE = of("41");
    public static final String HERTFORDSHIRE_CPS_AREA = "180";
    public static final String HERTFORDSHIRE_CPS_UNIT = "490";

    public static CaseDocument defaultCase() {
        return defaultCaseAsBuilder().build();
    }


    public static List<CaseDocument> defaultCases(final int count) {

        final List<CaseDocument> caseList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            caseList.add(defaultCase());
        }

        return caseList;
    }


    public static List<CaseDocument.Builder> defaultCasesAsBuilderList(final int count) {

        final List<CaseDocument.Builder> caseBuilderList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            caseBuilderList.add(defaultCaseAsBuilder());
        }

        return caseBuilderList;

    }

    public static CaseDocument.Builder defaultCaseAsBuilder(final String caseId,
                                                            final String caseReference) {

        final CaseDocument.Builder builder = new CaseDocument.Builder();

        builder.withCaseId(caseId)
                .withUrn(caseReference)
                .withCaseStatusCode("ACTIVE")
                .withCaseType("CHARGED")
                .withCpsUnitCode(HERTFORDSHIRE_CPS_UNIT)
                .withCjsAreaCode(HERTFORDSHIRE)
                .withCpsAreaCode(HERTFORDSHIRE_CPS_AREA)
                .withCrownAdvocate("Crown")
                .withOperationName("Trial")
                .withParalegalOfficer("Advocate")
                .withProsecutor("TFL")
                .withWitnessCareUnitCode("WITNESSUNIT")
                .withWitnessCareOfficer("ROY");


        builder.withParties(PartyDocumentMother.defaultPartyList())
                .withHearings(HearingDocumentMother.defaultHearings(1))
                .withLinkedCases(LinkedCaseDocumentMother.defaultLinkedCases(1));

        return builder;

    }

    public static CaseDocument.Builder defaultCaseAsBuilder() {

        final CaseDocument.Builder builder = new CaseDocument.Builder();
        final String prosecutingAuthority = randomProsecutingAuthority();

        builder.withCaseId(randomUUID().toString())
                .withUrn(randomCaseReference(prosecutingAuthority))
                .withCaseStatusCode("ACTIVE")
                .withCaseStatusCode("ACTIVE")
                .withCaseType("CHARGED")
                .withCpsUnitCode(HERTFORDSHIRE_CPS_UNIT)
                .withCjsAreaCode(HERTFORDSHIRE)
                .withCpsAreaCode(HERTFORDSHIRE_CPS_AREA)
                .withCrownAdvocate("Crown")
                .withOperationName("Trial")
                .withParalegalOfficer("Advocate")
                .withProsecutor("TFL")
                .withWitnessCareUnitCode("WITNESSUNIT")
                .withWitnessCareOfficer("ROY");

        builder.withParties(PartyDocumentMother.defaultPartyList())
                .withHearings(HearingDocumentMother.defaultHearings(1));

        return builder;

    }

    private static String randomProsecutingAuthority() {
        final int index = current().nextInt(0, 3);
        return ProsecutingAuthority.values()[index].name();
    }

    private static String randomCaseReference(final String prosecutionAuthority) {
        return prosecutionAuthority + current().nextInt(100000, 999999);
    }


}
