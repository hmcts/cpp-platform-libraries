package uk.gov.justice.services.unifiedsearch.client.transformer.cps;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class CpsCaseDetailsJsonHelper {

    public static JsonObject createCaseDetails(final String prosecutor,
                                               final String caseType,
                                               final String partyId,
                                               final String firstName,
                                               final String lastName) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("urn", "caseRef");
        jsonObjectBuilder.add("prosecutor", prosecutor);
        jsonObjectBuilder.add("caseStatusCode", "testCaseStatus");
        jsonObjectBuilder.add("caseType", caseType);

        final JsonArrayBuilder partiesBuilder = createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(buildDefendant(partyId, firstName, lastName, null, null)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                               final String caseType) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("urn", "caseRef");
        jsonObjectBuilder.add("prosecutor", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatusCode", "testCaseStatus");
        jsonObjectBuilder.add("caseType", caseType);

        return jsonObjectBuilder.build();
    }


    public static JsonObject createCaseDetails(final String urn,
                                               final String caseType,
                                               final String Prosecutor,
                                               final String caseStatus,
                                               final String cpsUnitCode,
                                               final String cjsAreaCode) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("urn", urn);
        jsonObjectBuilder.add("caseType", caseType);
        jsonObjectBuilder.add("prosecutor", Prosecutor);
        jsonObjectBuilder.add("caseStatusCode", caseStatus);
        jsonObjectBuilder.add("cpsUnitCode", cpsUnitCode);
        jsonObjectBuilder.add("cjsAreaCodes", createArrayBuilder().
                add(cjsAreaCode).
                build());

        return jsonObjectBuilder.build();
    }


    public static JsonObject createCaseDetails(final String prosecutingAuthority,
                                               final String caseType,
                                               final String partyId,
                                               final String firstName,
                                               final String lastName,
                                               final String hearingId,
                                               final String hearingDateTime,
                                               final String courtRoom) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("urn", "caseRef");
        jsonObjectBuilder.add("prosecutor", prosecutingAuthority);
        jsonObjectBuilder.add("caseStatusCode", "testCaseStatus");
        jsonObjectBuilder.add("caseType", caseType);

        final JsonArrayBuilder partiesBuilder = createArrayBuilder();
        final JsonArrayBuilder hearingsBuilder = createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(buildDefendant(partyId, firstName, lastName, null, null)));
        jsonObjectBuilder.add("hearings", hearingsBuilder.add(buildHearing(hearingId, courtRoom, hearingDateTime)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject createCaseDetails(final String prosecutor,
                                               final String caseType,
                                               final String partyId,
                                               final String firstName,
                                               final String lastName,
                                               final Set<JsonObject> aliases,
                                               final List<JsonObject> offences
    ) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();

        jsonObjectBuilder.add("caseId", "123455fhfgh");
        jsonObjectBuilder.add("urn", "caseRef");
        jsonObjectBuilder.add("prosecutor", prosecutor);
        jsonObjectBuilder.add("caseStatusCode", "testCaseStatus");
        jsonObjectBuilder.add("caseType", caseType);

        final JsonArrayBuilder partiesBuilder = createArrayBuilder();

        jsonObjectBuilder.add("parties", partiesBuilder.add(
                buildDefendant(partyId, firstName, lastName, aliases, offences)));

        return jsonObjectBuilder.build();
    }

    public static JsonObject buildDefendant(final String partyId,
                                            final String firstName,
                                            final String lastName,
                                            final Set<JsonObject> aliases,
                                            final List<JsonObject> offences
    ) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();
        jsonObjectBuilder.add("partyId", partyId);
        if (firstName != null && lastName != null) {
            jsonObjectBuilder.add("firstName", firstName);
            jsonObjectBuilder.add("lastName", lastName);
            jsonObjectBuilder.add("dateOfBirth", "1998-10-22");
            jsonObjectBuilder.add("asn", "W3 9EF");
            jsonObjectBuilder.add("pncId", "123");
            jsonObjectBuilder.add("oicShoulderNumber", "345");
        }

        jsonObjectBuilder.add("_party_type", createArrayBuilder().add("Defendant"));

        if (aliases != null) {
            final JsonArrayBuilder aliasBuilder = createArrayBuilder();
            aliases.forEach(aliasBuilder::add);
            jsonObjectBuilder.add("aliases", aliasBuilder.build());
        }

        if (offences != null) {
            final JsonArrayBuilder offenceBuilder = createArrayBuilder();
            offences.forEach(offenceBuilder::add);
            jsonObjectBuilder.add("offences", offenceBuilder.build());
        }

        return jsonObjectBuilder.build();
    }

    public static JsonObject buildAlias(final String firstName,
                                        final String lastName) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();

        Optional.ofNullable(firstName).map(name -> jsonObjectBuilder.add("firstName", name));
        Optional.ofNullable(lastName).map(name -> jsonObjectBuilder.add("lastName", name));

        return jsonObjectBuilder.build();
    }

    public static JsonObject buildHearing(final String hearingId,
                                          final String courtRoom,
                                          final String hearingDateTime) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();
        jsonObjectBuilder.add("hearingId", hearingId);
        jsonObjectBuilder.add("courtRoom", courtRoom);
        jsonObjectBuilder.add("hearingDateTime", hearingDateTime);
        jsonObjectBuilder.add("hearingType", "HEARING TYPE CODE 1");
        jsonObjectBuilder.add("courtHouse", "WEST ROOM");
        jsonObjectBuilder.add("jurisdiction", "YSTAFELL GORLLEWIN");
        return jsonObjectBuilder.build();
    }

    public static JsonObject buildOffence(final String offenceId, final String offenceCode,
                                          final String custodyTimeLimit) {

        final JsonObjectBuilder jsonObjectBuilder = createObjectBuilder();
        jsonObjectBuilder.add("offenceId", offenceId);
        jsonObjectBuilder.add("offenceCode", offenceCode);
        jsonObjectBuilder.add("custodyTimeLimit", custodyTimeLimit);
        return jsonObjectBuilder.build();
    }

}
