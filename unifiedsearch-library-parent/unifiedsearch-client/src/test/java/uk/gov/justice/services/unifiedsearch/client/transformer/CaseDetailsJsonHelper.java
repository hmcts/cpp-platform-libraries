package uk.gov.justice.services.unifiedsearch.client.transformer;

import org.apache.commons.collections.CollectionUtils;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.UUID.randomUUID;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;

public class CaseDetailsJsonHelper {

    public static final List<JsonArray> defendantCounsels = Arrays.asList(jsonBuilderFactory.createArrayBuilder().add(jsonBuilderFactory.createObjectBuilder()
            .add("id", "2ffa8961-58a3-4352-a87d-09aa45f68c7f")
            .add("title", "Mr")
            .add("firstName", "johnny")
            .add("lastName", "robber")
            .add("status","ACTIVE")
            .add("defendants", jsonBuilderFactory.createArrayBuilder().add("2ffa8961-58a3-4352-a87d-09aa45f68c7f").add("2ffa8961-58a3-4352-a87d-09aa45f68c8f"))
            .add("attendanceDays", jsonBuilderFactory.createArrayBuilder().add("2019-01-01"))).build(),jsonBuilderFactory.createArrayBuilder().add(jsonBuilderFactory.createObjectBuilder()
                    .add("id", "2ffa8961-58a3-4352-a87d-09aa45f68c8f")
                    .add("title", "Dr")
                    .add("firstName", "johnn")
                    .add("lastName", "robbery")
                    .add("status","ACTIVE")
                    .add("defendants", jsonBuilderFactory.createArrayBuilder().add("2ffa8961-58a3-4352-a87d-09aa45f78c7f").add("2ffa8961-58a3-4352-a87d-09aa46f68c8f"))
                    .add("attendanceDays", jsonBuilderFactory.createArrayBuilder().add("2019-02-01"))).build());

    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                               final String caseType,
                                               final String partyId,
                                               final String firstName,
                                               final String lastName,
                                               final String sourceSystemReference){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("caseReference", "caseRef");
        jsonObjectBuilder.add("prosecutingAuthority", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatus", "testCaseStatus");
        jsonObjectBuilder.add("_case_type", caseType);
        jsonObjectBuilder.add("_is_sjp", false);
        jsonObjectBuilder.add("_is_magistrates", false);
        jsonObjectBuilder.add("_is_crown", true);
        jsonObjectBuilder.add("_is_charging", true);
        jsonObjectBuilder.add("sjpNoticeServed", "2018-10-22");
        jsonObjectBuilder.add("sourceSystemReference", sourceSystemReference);

        final JsonArrayBuilder partiesBuilder = jsonBuilderFactory.createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(buildDefendant(partyId,firstName,lastName, null, null, null)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                               final String caseType,
                                               final String partyId,
                                               final String firstName,
                                               final String lastName,
                                               final boolean proceedingsConcluded,
                                               final String sourceSystemReference){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("caseReference", "caseRef");
        jsonObjectBuilder.add("prosecutingAuthority", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatus", "testCaseStatus");
        jsonObjectBuilder.add("_case_type", caseType);
        jsonObjectBuilder.add("_is_sjp", false);
        jsonObjectBuilder.add("_is_magistrates", false);
        jsonObjectBuilder.add("_is_crown", true);
        jsonObjectBuilder.add("_is_charging", true);
        jsonObjectBuilder.add("sjpNoticeServed", "2018-10-22");
        jsonObjectBuilder.add("sourceSystemReference", sourceSystemReference);

        final JsonArrayBuilder partiesBuilder = jsonBuilderFactory.createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(buildDefendant(partyId,firstName,lastName, proceedingsConcluded)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                               final String caseType){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("caseReference", "caseRef");
        jsonObjectBuilder.add("prosecutingAuthority", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatus", "testCaseStatus");
        jsonObjectBuilder.add("_case_type", caseType);
        jsonObjectBuilder.add("_is_sjp", false);
        jsonObjectBuilder.add("_is_magistrates", false);
        jsonObjectBuilder.add("_is_crown", true);
        jsonObjectBuilder.add("_is_charging", true);
        jsonObjectBuilder.add("sjpNoticeServed", "2018-10-22");

        return jsonObjectBuilder.build();
    }


    public static JsonObject createCaseDetails(final String caseReference,
                                               final String caseType,
                                               final String prosecutingAuthority,
                                               final String caseStatus,
                                               final boolean isSjp,
                                               final boolean isMagistrates,
                                               final boolean isCrown,
                                               final boolean isCharging,
                                               final String sjpNoticeServed){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("caseReference", caseReference);
        jsonObjectBuilder.add("prosecutingAuthority", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatus", caseStatus);
        jsonObjectBuilder.add("_case_type", caseType);
        jsonObjectBuilder.add("_is_sjp", isSjp);
        jsonObjectBuilder.add("_is_magistrates", isMagistrates);
        jsonObjectBuilder.add("_is_crown", isCrown);
        jsonObjectBuilder.add("_is_charging", isCharging);
        jsonObjectBuilder.add("sjpNoticeServed", sjpNoticeServed);

        return jsonObjectBuilder.build();
    }


    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                               final String caseType,
                                               final String partyId,
                                               final String firstName,
                                               final String lastName,
                                               final String hearingId,
                                               final List<String> hearingDates,
                                               final String courtId,
                                               final String applicationId,
                                               final String applicationReference,
                                               final int defendantCouncelIndex){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("caseReference", "caseRef");
        jsonObjectBuilder.add("prosecutingAuthority", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatus", "testCaseStatus");
        jsonObjectBuilder.add("_case_type", caseType);
        jsonObjectBuilder.add("_is_sjp", false);
        jsonObjectBuilder.add("_is_magistrates", false);
        jsonObjectBuilder.add("_is_crown", true);
        jsonObjectBuilder.add("_is_charging", true);
        jsonObjectBuilder.add("sjpNoticeServed", "2018-10-22");

        final JsonArrayBuilder partiesBuilder = jsonBuilderFactory.createArrayBuilder();
        final JsonArrayBuilder hearingsBuilder = jsonBuilderFactory.createArrayBuilder();
        final JsonArrayBuilder applicationsBuilder = jsonBuilderFactory.createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(buildDefendant(partyId,firstName,lastName, null, null, null)));
        jsonObjectBuilder.add("hearings", hearingsBuilder.add(buildHearing(hearingId,courtId,hearingDates, defendantCouncelIndex)));
        jsonObjectBuilder.add("applications", applicationsBuilder.add(buildApplication(applicationId,applicationReference)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                                    final String caseType,
                                                    final String partyId,
                                                    final String firstName,
                                                    final String lastName,
                                                    final Set<JsonObject> aliases,
                                                    final JsonObject representationOrder,
                                                    final List<JsonObject> offences
    ){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("caseReference", "caseRef");
        jsonObjectBuilder.add("prosecutingAuthority", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatus", "testCaseStatus");
        jsonObjectBuilder.add("_case_type", caseType);
        jsonObjectBuilder.add("_is_sjp", false);
        jsonObjectBuilder.add("_is_magistrates", false);
        jsonObjectBuilder.add("_is_crown", true);
        jsonObjectBuilder.add("_is_charging", true);
        jsonObjectBuilder.add("sjpNoticeServed", "2018-10-22");

        final JsonArrayBuilder partiesBuilder = jsonBuilderFactory.createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(
                buildDefendant(partyId, firstName, lastName, aliases, representationOrder, offences)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                               final String caseType,
                                               final String partyId,
                                               final List<JsonObject> offences
    ){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("caseReference", "caseRef");
        jsonObjectBuilder.add("prosecutingAuthority", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatus", "testCaseStatus");
        jsonObjectBuilder.add("_case_type", caseType);
        jsonObjectBuilder.add("_is_sjp", false);
        jsonObjectBuilder.add("_is_magistrates", false);
        jsonObjectBuilder.add("_is_crown", true);
        jsonObjectBuilder.add("_is_charging", true);
        jsonObjectBuilder.add("sjpNoticeServed", "2018-10-22");

        final JsonArrayBuilder partiesBuilder = jsonBuilderFactory.createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(
                buildDefendant(partyId, offences)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject buildDefendant(final String partyId,
                                            final String firstName,
                                            final String lastName,
                                            final Set<JsonObject> aliases,
                                            final JsonObject representationOrder,
                                            final List<JsonObject> offences
    ){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("partyId", partyId);
        if(firstName != null && lastName!=null){
            jsonObjectBuilder.add("firstName", firstName);
            jsonObjectBuilder.add("middleName", "Junior");
            jsonObjectBuilder.add("lastName", lastName);
            jsonObjectBuilder.add("title", "Mr");
            jsonObjectBuilder.add("dateOfBirth", "1998-10-22");
            jsonObjectBuilder.add("gender", "M");
            jsonObjectBuilder.add("addressLines", "4 Arnos Grove");
            jsonObjectBuilder.add("postCode", "W3 9EF");
            jsonObjectBuilder.add("pncId", "123");
            jsonObjectBuilder.add("arrestSummonsNumber", "345");
            jsonObjectBuilder.add("proceedingsConcluded", false);
        }

        jsonObjectBuilder.add("_party_type", "Defendant");
        jsonObjectBuilder.add("nationalInsuranceNumber", "");

        if (aliases != null) {
            final JsonArrayBuilder aliasBuilder = jsonBuilderFactory.createArrayBuilder();
            aliases.forEach(aliasBuilder::add);
            jsonObjectBuilder.add("aliases", aliasBuilder.build());
        }

        if (representationOrder != null) {
            jsonObjectBuilder.add("representationOrder", representationOrder);
        }

        if (offences != null) {
            final JsonArrayBuilder offenceBuilder = jsonBuilderFactory.createArrayBuilder();
            offences.forEach(offenceBuilder::add);
            jsonObjectBuilder.add("offences", offenceBuilder.build());
        }


        return jsonObjectBuilder.build();
    }


    public static JsonObject buildDefendant(final String partyId,
                                            final String firstName,
                                            final String lastName,
                                            final boolean proceedingsConcluded
    ){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("partyId", partyId);
        jsonObjectBuilder.add("firstName", firstName);
        jsonObjectBuilder.add("middleName", "Junior");
        jsonObjectBuilder.add("lastName", lastName);
        jsonObjectBuilder.add("title", "Mr");
        jsonObjectBuilder.add("dateOfBirth", "1998-10-22");
        jsonObjectBuilder.add("gender", "M");
        jsonObjectBuilder.add("addressLines", "4 Arnos Grove");
        jsonObjectBuilder.add("postCode", "W3 9EF");
        jsonObjectBuilder.add("pncId", "123");
        jsonObjectBuilder.add("arrestSummonsNumber", "345");
        jsonObjectBuilder.add("_party_type", "Defendant");
        jsonObjectBuilder.add("nationalInsuranceNumber", "");
        jsonObjectBuilder.add("proceedingsConcluded", proceedingsConcluded);
        jsonObjectBuilder.add("defendantAddress", defendantAddress());



        return jsonObjectBuilder.build();
    }


    public static JsonObject buildDefendant(final String partyId, final List<JsonObject> offences){

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("partyId", partyId);
        jsonObjectBuilder.add("prosecutionCaseId", "41a6443f-4906-42ce-8ace-a189b7f2f1b9");
        jsonObjectBuilder.add("defendantAddress", defendantAddress());

        if (offences != null) {
            final JsonArrayBuilder offenceBuilder = jsonBuilderFactory.createArrayBuilder();
            offences.forEach(offenceBuilder::add);
            jsonObjectBuilder.add("offences", offenceBuilder.build());
        }
        return jsonObjectBuilder.build();
    }

    public static JsonObject buildAlias(final String title,
                                        final String firstName,
                                        final String middleName,
                                        final String lastName) {

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();

        Optional.ofNullable(title).map(t -> jsonObjectBuilder.add("title", t));
        Optional.ofNullable(firstName).map(name -> jsonObjectBuilder.add("firstName", name));
        Optional.ofNullable(middleName).map(name -> jsonObjectBuilder.add("middleName", name));
        Optional.ofNullable(lastName).map(name -> jsonObjectBuilder.add("lastName", name));

        return jsonObjectBuilder.build();

    }





    public static JsonObject buildHearing(final String hearingId,
                                          final String courtId,
                                          final List<String> hearingDates,
                                          final int defendantCounselIndex) {

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("hearingId", hearingId);
        jsonObjectBuilder.add("courtId", courtId);
        jsonObjectBuilder.add("courtCentreName", courtId + "CENTRE");
        jsonObjectBuilder.add("hearingTypeId", "a1d8338a-857b-4e5c-b1c7-a5cc09663968");
        jsonObjectBuilder.add("hearingTypeLabel", "Pre Sentancing");
        jsonObjectBuilder.add("hearingTypeCode", "HEARING TYPE CODE 1");

        final JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
        final JsonArrayBuilder datesArrayBuilder = jsonBuilderFactory.createArrayBuilder();
        if (hearingDates != null) {
            hearingDates.forEach(hdDate -> {
                jsonArrayBuilder.add(
                        jsonBuilderFactory.createObjectBuilder().add("sittingDay", hdDate)
                );
                datesArrayBuilder.add(hdDate);
            });
        }

        jsonObjectBuilder.add("hearingDates", datesArrayBuilder);
        jsonObjectBuilder.add("hearingDays", jsonArrayBuilder);
        jsonObjectBuilder.add("jurisdictionType", "jurisdictionType");
        jsonObjectBuilder.add("judiciaryTypes", jsonBuilderFactory.createArrayBuilder().add("judiciaryType1").add("judiciaryType2"));
        jsonObjectBuilder.add("isBoxHearing", true);
        jsonObjectBuilder.add("isVirtualBoxHearing", true);
        jsonObjectBuilder.add("courtCentreRoomId", "4ab9c698-f559-48ee-beae-00fb5fe2483e");
        jsonObjectBuilder.add("courtCentreRoomName", "WEST ROOM");
        jsonObjectBuilder.add("courtCentreWelshName", "CANOLFAN LLYS CROWN LLUNDAIN");
        jsonObjectBuilder.add("courtCentreRoomWelshName", "YSTAFELL GORLLEWIN");
        jsonObjectBuilder.add("courtCentreAddress",
                jsonBuilderFactory.createObjectBuilder().add("address1", "123 JUSTICE SQUARE")
                    .add("address2", "LEGAL ROW")
                    .add("address3", "FARRINGDON")
                    .add("address4", "LONDON")
                    .add("postCode", "NE1 6FT")
        );
        jsonObjectBuilder.add("defenceCounsels", defendantCounsels.get(defendantCounselIndex));


        return jsonObjectBuilder.build();
    }


    public static JsonObject buildApplication(final String applicationId,
                                              final String applicationReference) {

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("applicationId", applicationId);
        jsonObjectBuilder.add("applicationReference", applicationReference);
        jsonObjectBuilder.add("applicationType", "APPLICATION_TYPE");
        jsonObjectBuilder.add("receivedDate", "2019-01-01");
        jsonObjectBuilder.add("decisionDate", "2019-02-01");
        jsonObjectBuilder.add("dueDate", "2019-02-03");

        return jsonObjectBuilder.build();
    }

    public static JsonObject buildLaaReference(final String applicationReference) {

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("applicationReference", applicationReference);
        jsonObjectBuilder.add("statusId", randomUUID().toString());
        jsonObjectBuilder.add("statusCode", "LAA STATUS CODE");
        jsonObjectBuilder.add("statusDescription", "LAA STATUS DESC");
        return jsonObjectBuilder.build();
    }

    public static JsonObject buildCourtOrder(final String startDate, final String endDate, final String orderDate, final String label, final String courtOrderId) {

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("startDate", startDate);
        jsonObjectBuilder.add("endDate", endDate);
        jsonObjectBuilder.add("orderDate", orderDate);
        jsonObjectBuilder.add("id", courtOrderId);
        jsonObjectBuilder.add("label", label);
        jsonObjectBuilder.add("canBeSubjectOfBreachProceedings", true);
        jsonObjectBuilder.add("canBeSubjectOfVariationProceedings", false);
        jsonObjectBuilder.add("orderingHearingId", randomUUID().toString());
        return jsonObjectBuilder.build();
    }

    public static JsonObject buildRepresentationOrder(final String applicationReference, final String laaContractNumber) {

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("applicationReference", applicationReference);
        jsonObjectBuilder.add("effectiveFromDate", randomDateInNextThreeMonths());
        jsonObjectBuilder.add("effectiveToDate", randomDateInNextThreeMonths());
        jsonObjectBuilder.add("laaContractNumber",
                laaContractNumber == null ? "LAA CONTRACT NO" : laaContractNumber);

        return jsonObjectBuilder.build();
    }

    public static JsonObject buildOffence(final String offenceId, final String offenceCode,
                                          final int orderIndex,
                                          final JsonObject laaReference,
                                          final List<JsonObject> courtOrders) {

        final JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
        jsonObjectBuilder.add("offenceId", offenceId);
        jsonObjectBuilder.add("offenceCode", offenceCode);
        jsonObjectBuilder.add("offenceTitle", "OFFENCE TITLE");
        jsonObjectBuilder.add("offenceLegislation", "OFFENCE LEGISLATION");
        jsonObjectBuilder.add("proceedingsConcluded", false);
        jsonObjectBuilder.add("arrestDate", randomDateInLastThreeMonths());
        jsonObjectBuilder.add("dateOfInformation", randomDateInLastThreeMonths());
        jsonObjectBuilder.add("endDate", randomDateInLastThreeMonths());
        jsonObjectBuilder.add("startDate", randomDateInLastThreeMonths());
        jsonObjectBuilder.add("chargeDate", randomDateInLastThreeMonths());
        jsonObjectBuilder.add("modeOfTrial", "CROWN");
        jsonObjectBuilder.add("orderIndex", orderIndex);
        if (laaReference != null) {
            jsonObjectBuilder.add("laaReference", laaReference);
        }
        jsonObjectBuilder.add("wording", "OFFENCE WORDING");

        if (CollectionUtils.isNotEmpty(courtOrders)) {
            JsonArrayBuilder arrayBuilder = jsonBuilderFactory.createArrayBuilder();
            courtOrders.forEach(arrayBuilder::add);
            jsonObjectBuilder.add("courtOrders", arrayBuilder);
        }

        jsonObjectBuilder.add("verdict", jsonBuilderFactory.createObjectBuilder().add("originatingHearingId", offenceId)
                .add("verdictDate", "2019-01-01")
                .add("verdictType", jsonBuilderFactory.createObjectBuilder().add("verdictTypeId", offenceId)
                        .add("sequence", 1)
                        .add("categoryType", "categoryType")
                        .add("category", "category")
                        .add("description", "description")));
        return jsonObjectBuilder.build();
    }


    public static final String randomDateInNextThreeMonths() {
        long startDay = LocalDate.now().toEpochDay();
        long endDay = LocalDate.now().plusMonths(3).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return ISO_LOCAL_DATE.format(LocalDate.ofEpochDay(randomDay));
    }

    public static final String randomDateInLastThreeMonths() {
        long endDay = LocalDate.now().toEpochDay();
        long startDay = LocalDate.now().minusMonths(3).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startDay, endDay);
        return ISO_LOCAL_DATE.format(LocalDate.ofEpochDay(randomDay));
    }

    private static JsonObjectBuilder defendantAddress() {
        final JsonObjectBuilder defendantAddress = jsonBuilderFactory.createObjectBuilder();
        defendantAddress.add("address1", "address1");
        defendantAddress.add("address2", "address2");
        defendantAddress.add("address3", "address3");
        defendantAddress.add("address4", "address4");
        defendantAddress.add("address5", "address5");
        defendantAddress.add("postCode", "postCode");
        return defendantAddress;
    }
}
