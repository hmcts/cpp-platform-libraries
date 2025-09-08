package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.ThreadLocalRandom.current;
import static uk.gov.moj.cpp.unifiedsearch.test.util.constant.CaseType.PROSECUTION;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.ProsecutingAuthority;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.CaseDocument;

import java.util.ArrayList;
import java.util.List;

public class CaseDocumentMother {

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
                                                            final String caseReference,
                                                            final boolean isCrown) {

        return defaultCaseAsBuilder(caseId, caseReference, current().nextBoolean(), current().nextBoolean(), isCrown);
    }

    public static CaseDocument.Builder defaultCaseAsBuilder(final String caseId,
                                                            final String caseReference,
                                                            final boolean isSjp,
                                                            final boolean isMagistrates,
                                                            final boolean isCrown) {

        final CaseDocument.Builder builder = new CaseDocument.Builder();
        final String prosecutingAuthority = randomProsecutingAuthority();

        builder.withCaseId(caseId)
                .withCaseReference(caseReference)
                .withProsecutingAuthority(prosecutingAuthority)
                .withCaseStatus("ACTIVE")
                .with_case_type(PROSECUTION.name())
                .with_is_sjp(isSjp)
                .with_is_magistrates(isMagistrates)
                .with_is_crown(isCrown)
                .with_is_charging(current().nextBoolean());

        if (isSjp) {
            final DateUtils dateUtils = new DateUtils();
            builder.withSjpNoticeServed(
                    dateUtils.toElasticsearchDateString(
                            dateUtils.randomDateInLastThreeMonthsOrNull()));
        }

        builder.withParties(PartyDocumentMother.defaultPartyBuildersList())
                .withApplications(ApplicationDocumentMother.defaultApplicationsAsBuilder(1))
                .withHearings(HearingDocumentMother.defaultHearingsAsBuilder(1));

        return builder;

    }

    public static CaseDocument.Builder defaultCaseAsBuilder() {

        final CaseDocument.Builder builder = new CaseDocument.Builder();
        final String prosecutingAuthority = randomProsecutingAuthority();
        final boolean isSjp = current().nextBoolean();

        builder.withCaseId(randomUUID().toString())
                .withCaseReference(randomCaseReference(prosecutingAuthority))
                .withProsecutingAuthority(prosecutingAuthority)
                .withCaseStatus("ACTIVE")
                .with_case_type(PROSECUTION.name())
                .with_is_sjp(isSjp)
                .with_is_magistrates(current().nextBoolean())
                .with_is_crown(current().nextBoolean())
                .with_is_charging(current().nextBoolean());

        if (isSjp) {
            final DateUtils dateUtils = new DateUtils();
            builder.withSjpNoticeServed(
                    dateUtils.toElasticsearchDateString(
                            dateUtils.randomDateInLastThreeMonthsOrNull()));
        }

        builder.withParties(PartyDocumentMother.defaultPartyBuildersList())
                .withApplications(ApplicationDocumentMother.defaultApplicationsAsBuilder(1))
                .withHearings(HearingDocumentMother.defaultHearingsAsBuilder(1));

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
