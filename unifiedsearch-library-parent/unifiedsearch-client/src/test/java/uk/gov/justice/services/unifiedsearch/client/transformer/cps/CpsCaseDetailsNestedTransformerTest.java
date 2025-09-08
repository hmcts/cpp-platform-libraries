package uk.gov.justice.services.unifiedsearch.client.transformer.cps;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static uk.gov.justice.services.unifiedsearch.client.transformer.cps.CpsCaseDetailsJsonHelper.buildAlias;
import static uk.gov.justice.services.unifiedsearch.client.transformer.cps.CpsCaseDetailsJsonHelper.buildOffence;
import static uk.gov.justice.services.unifiedsearch.client.transformer.cps.CpsCaseDetailsJsonHelper.createCaseDetails;

import uk.gov.justice.services.unifiedsearch.client.domain.cps.CaseDetails;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class CpsCaseDetailsNestedTransformerTest {

    private static final String PROSECUTOR1 = "PRA";
    private static final String CASE_TYPE1 = "testCaseType";
    private static final String PARTY_ID1 = "ABC";
    private static final String GIVEN_NAME1 = "Bob";
    private static final String FAMILY_NAME1 = "Barry";
    private static final String HEARING_ID1 = "HEARING_ID1";
    private static final String HEARING_DATE_TIME1 = "2019-01-01";
    private static final String COURT_ROOM1 = "001";
    private static final String OFFENCE_ID1 = "OFFENCE_ID1";
    private static final String OFFENCE_CODE1 = "OFFENCE_CODE1";
    private static final String CUSTODY_TIME_LIMIT1 = "CUSTODY_TIME_LIMIT1";

    private static final String PROSECUTOR2 = "PRB";
    private static final String CASE_TYPE2 = "testCASE_TYPE1";
    private static final String PARTY_ID2 = "XYZ";
    private static final String GIVEN_NAME2 = "John";
    private static final String FAMILY_NAME2 = "Mac";
    private static final String HEARING_ID2 = "HEARING_ID2";
    private static final String COURT_ROOM2 = "002";
    private static final String OFFENCE_ID2 = "OFFENCE_ID2";
    private static final String OFFENCE_CODE2 = "OFFENCE_CODE2";
    private static final String CUSTODY_TIME_LIMIT2 = "CUSTODY_TIME_LIMIT2";

    @Test
    public void shouldReplaceWhenPartyIdsMatch() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1);
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID2, GIVEN_NAME2, FAMILY_NAME2);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(2));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));
    }

    @Test
    public void shouldMergeAliasesWhenPartyIdsMatch() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("John", "Smith")));

        final Set<JsonObject> aliasList2 = new HashSet<>(asList(buildAlias("John", "Smith"),
                buildAlias("Johno", "Smothers"),
                buildAlias("Jack", "Smith")));

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1, aliasList1, null);
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID1, GIVEN_NAME2, FAMILY_NAME2, aliasList2, null);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(3));
    }

    @Test
    public void shouldAddAliasesWhenPartyIdsMatch() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("John", "Smith")));

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1, aliasList1, null);
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID2, GIVEN_NAME2, FAMILY_NAME2, null, null);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));
    }

    @Test
    public void shouldNotDeleteAliasesWhenPartyIdsMatch() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final Set<JsonObject> aliasList2 = new HashSet<>(asList(buildAlias("John", "Smith"),
                buildAlias("Johno", "Smothers"),
                buildAlias("Jack", "Smith")));

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1);
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID2, GIVEN_NAME2, FAMILY_NAME2, aliasList2, null);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(3));
    }

    @Test
    public void shouldAddPartyWhenPartyIdNotMatch() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1);
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID2, GIVEN_NAME2, FAMILY_NAME2);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(2));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(1).getLastName(), is(FAMILY_NAME1));
        assertThat(unifiedSearchIndexData.getParties().get(1).getFirstName(), is(GIVEN_NAME1));
    }

    @Test
    public void shouldAddNewPartyWhenExistingPartyIsNull() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final JsonObject existingIndex = createCaseDetails(PROSECUTOR1, CASE_TYPE1);
        final JsonObject incomingIndex = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(existingIndex.toString(), CaseDetails.class);
        final CaseDetails existingIndexData = new ObjectMapper().readValue(incomingIndex.toString(), CaseDetails.class);

        assertThat(incomingIndexData.getHearings(), is(nullValue()));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, existingIndexData);

        assertThat(existingIndexData.getParties().size(), is(1));

        assertThat(existingIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME1));
        assertThat(existingIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME1));
    }

    @Test
    public void shouldReplaceHearingWhenIdsMatch() throws Exception {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1, HEARING_ID1, HEARING_DATE_TIME1, COURT_ROOM1);
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID2, GIVEN_NAME2, FAMILY_NAME2, HEARING_ID2, HEARING_DATE_TIME1, COURT_ROOM2);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getHearings().get(0).getCourtRoom(), is(COURT_ROOM2));

        cpsCaseDetailsNestedTransformer.mergeHearings(incomingIndexData, unifiedSearchIndexData);
        assertThat(unifiedSearchIndexData.getHearings().size(), is(2));
        assertThat(unifiedSearchIndexData.getHearings().get(0).getCourtRoom(), is(COURT_ROOM2));
        assertThat(unifiedSearchIndexData.getHearings().get(0).getHearingDateTime(), is(HEARING_DATE_TIME1));
    }

    @Test
    public void shouldAddOffencesWhenPartyIdsMatch() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("John", "Smith")));
        final JsonObject offences = buildOffence(OFFENCE_ID1, OFFENCE_CODE1, CUSTODY_TIME_LIMIT1);

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, GIVEN_NAME1, FAMILY_NAME1, aliasList1, asList(offences));
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID2, GIVEN_NAME2, FAMILY_NAME2);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(2));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));
    }

    @Test
    public void shouldAddOffencesAndExistingPartyDataWhenOnlyOffenceChanges() throws IOException {
        final CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer = new CpsCaseDetailsNestedTransformer();

        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("John", "Smith")));

        final JsonObject offences = buildOffence(OFFENCE_ID1, OFFENCE_CODE1, CUSTODY_TIME_LIMIT1);
        final JsonObject offences2 = buildOffence(OFFENCE_ID2, OFFENCE_CODE2, CUSTODY_TIME_LIMIT2);

        final JsonObject index = createCaseDetails(PROSECUTOR1, CASE_TYPE1, PARTY_ID1, null, null, null, asList(offences));
        final JsonObject response = createCaseDetails(PROSECUTOR2, CASE_TYPE2, PARTY_ID2, GIVEN_NAME2, FAMILY_NAME2, aliasList1, asList(offences2));

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));

        cpsCaseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(2));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(GIVEN_NAME2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(FAMILY_NAME2));
    }

}