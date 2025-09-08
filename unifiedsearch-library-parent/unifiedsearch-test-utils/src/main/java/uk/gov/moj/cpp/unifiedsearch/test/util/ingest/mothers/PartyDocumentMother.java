package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.OffenceDocumentMother.defaultOffenceDocument;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomFirstName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomLastName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomMiddleName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RandomNames.randomOrganisationName;
import static uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers.RepresentationOrderDocumentMother.defaultRepresentationOrder;

import uk.gov.moj.cpp.unifiedsearch.test.util.constant.PartyType;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.DateUtils;
import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.PartyDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PartyDocumentMother {

    private static final List<String> TITLES = asList("Mr", "Mrs", "Miss", "Dr");

    private static final List<String> PNC_ID_LIST = asList(
            "201919860982E", "201948440256E", "201999687295E", "201997230735C", "201956444649G", "201914065971G", "201992774241F",
            "201917494804D", "201964240968C", "201957219612C", "201953216993D", "201957811808E", "201956885969E", "201960304232C",
            "201991938009B", "201923630788G", "201960156611A", "201994607738C", "201934287904C", "201996347966G", "201975182760E",
            "201996740395B", "201997995589A", "201987916342E", "201916916333D", "201918095346C", "201937194081E", "201959642145D",
            "201993340289B", "201923706455B", "201938405696C", "201986807152E", "201977450878B", "201982075901D", "201923491283C",
            "201964902382B", "201921410369C", "201932203395A", "201967307339C", "201986796810G", "201994738838A", "201949471508D",
            "201911628067F", "201953727208G", "201983373138G", "201910383132E", "201966177212D", "201916048670G", "201989755317A",
            "201914498249D", "201989623336F", "201988129377E", "201960823060G", "201996120062C", "201923124429G", "201988930313F",
            "201954908347F", "201943427707A", "201955125796E", "201992111589G", "201920417076E", "201922586835A", "201993780516C",
            "201964464007B", "201918266696G", "201939324120G", "201996942555D", "201936416874D", "201944834225F", "201932993875F",
            "201919275174A", "201925425505B", "201933972470C", "201945873569A", "201933594661C", "201922494332G", "201988534339G",
            "201922114255F", "201940408012C", "201962335679G", "201927732438F", "201937360812A", "201959784637A", "201950556120B",
            "201992907213F", "201933664890A", "201970169793D", "201983098783B", "201921271792E", "201962221378E", "201985289630F",
            "201971438051B", "201939930990D", "201961441559F", "201933657095B", "201912727750F", "201953274804F", "201988076864A",
            "201951438722F", "201979178914G", "201966657684B", "201918346856E", "201932281186B", "201998774548F", "201951021200F",
            "201961408881D", "201983131499G", "201918319516F", "201972822628F", "201950276605C", "201994012810B", "201990064748G",
            "201994652245F", "201946616887D", "201949521938F", "201961829205D", "201984598729G", "201917925882A", "201924052892A",
            "201947315246C"
    );


    public static List<PartyDocument> defaultPartyList() {
        final List<PartyDocument> partyList = new ArrayList<>();

        final int count = ThreadLocalRandom.current().nextInt(0, 4);
        for (int i = 0; i < count; i++) {
            partyList.add(defaultParty());
        }

        return partyList;
    }

    public static List<PartyDocument.Builder> defaultPartyBuildersList() {

        final List<PartyDocument.Builder> partyBuilderList = new ArrayList<>();

        final int count = ThreadLocalRandom.current().nextInt(0, 4);
        for (int i = 0; i < count; i++) {
            partyBuilderList.add(defaultPartyAsBuilder());
        }

        return partyBuilderList;
    }


    public static PartyDocument defaultParty() {
        return defaultPartyAsBuilder().build();
    }


    public static PartyDocument.Builder defaultPartyAsBuilder() {

        final PartyDocument.Builder builder = new PartyDocument.Builder();
        final DateUtils dateUtils = new DateUtils();
        builder.withPartyId(randomUUID().toString());
        if (ThreadLocalRandom.current().nextBoolean()) {
            builder.withFirstName(randomFirstName())
                    .withMiddleName(randomMiddleName())
                    .withLastName(randomLastName())
                    .withTitle(randomTitle())
                    .withDateOfBirth(
                            dateUtils.toElasticsearchDateString(
                                    dateUtils.randomDateOfBirthOver18()))
                    .withGender(randomGender())
                    .withNationalInsuranceNumber(nino());
        } else {
            builder.withOrganisationName(randomOrganisationName());
        }
        final String partyType = randomPartyType();
        builder.withAddressLines(RandomAddresses.randomAddress())
                .withPostCode(RandomAddresses.randomPostcode())
                .withPncId(randomPncId())
                .withPartyType(partyType)
                .withAliases(AliasDocumentMother.randomAliasList());

        if (PartyType.DEFENDANT.equals(PartyType.valueOf(partyType))) {
            builder.withArrestSummonsNumber("ASN " + ThreadLocalRandom.current().nextInt(1000, 9999));
            builder.withOffences(asList(defaultOffenceDocument()));
            builder.withProceedingsConcluded(ThreadLocalRandom.current().nextBoolean());
            builder.withRepresentationOrder(defaultRepresentationOrder());
        }

        return builder;

    }

    private static String nino() {
        final StringBuilder sb = new StringBuilder();
        sb.append((char) ThreadLocalRandom.current().nextInt(65, 90));
        sb.append((char) ThreadLocalRandom.current().nextInt(65, 90));
        sb.append(ThreadLocalRandom.current().nextInt(100000, 999999));
        sb.append((char) ThreadLocalRandom.current().nextInt(65, 90));
        return sb.toString();
    }


    private static String randomPartyType() {
        final int index = ThreadLocalRandom.current().nextInt(0, 2);
        return PartyType.values()[index].name();
    }

    private static String randomTitle() {
        return TITLES.get(ThreadLocalRandom.current().nextInt(0, 3));
    }

    private static String randomGender() {
        return ThreadLocalRandom.current().nextBoolean() ? "MALE" : "FEMALE";
    }

    private static String randomPncId() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return PNC_ID_LIST.get(ThreadLocalRandom.current().nextInt(0, 120));
        } else {
            return null;
        }
    }
}
