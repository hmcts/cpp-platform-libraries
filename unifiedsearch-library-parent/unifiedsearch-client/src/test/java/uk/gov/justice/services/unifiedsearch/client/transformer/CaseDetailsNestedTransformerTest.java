package uk.gov.justice.services.unifiedsearch.client.transformer;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.nullValue;
import static uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsJsonHelper.buildAlias;
import static uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsJsonHelper.buildOffence;
import static uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsJsonHelper.createCaseDetails;
import static uk.gov.justice.services.unifiedsearch.client.transformer.DefenceCounselHelper.assertDefenceCounselWithinHearing;
import static uk.gov.justice.services.unifiedsearch.client.transformer.HearingDayHelper.assertHearingDatesWithinHearingDay;

import uk.gov.justice.services.unifiedsearch.client.domain.CaseDetails;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsNestedTransformerTest {

    @InjectMocks
    private CaseDetailsNestedTransformer caseDetailsNestedTransformer;

    @Mock
    private Logger logger;

    @Test
    public void shouldReplaceWhenPartyIdsMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final boolean proceedingsConcluded1 = false;

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final boolean proceedingsConcluded2 = true;
        final String sourceSystemReference = "L00001";


        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, proceedingsConcluded1, sourceSystemReference );
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, proceedingsConcluded2, sourceSystemReference);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getProceedingsConcluded(), is(proceedingsConcluded2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);
        assertThat(incomingIndexData.getSourceSystemReference(), is(sourceSystemReference));

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getProceedingsConcluded(), is(proceedingsConcluded1));
        assertThat(unifiedSearchIndexData.getSourceSystemReference(), is(sourceSystemReference));
    }

    @Test
    public void shouldMergeAliasesWhenPartyIdsMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";

        final Set<JsonObject> aliasList2 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith"),
                buildAlias(null, "Johno", null, "Smothers"),
                buildAlias("MRS", "Jack", "J", "Smith")));

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, null, null);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, aliasList2, null, null);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(3));

    }

    @Test
    public void shouldAddAliasesWhenPartyIdsMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, null, null);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, null, null, null);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

    }

    @Test
    public void shouldNotDeleteAliasesWhenPartyIdsMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final Set<JsonObject> aliasList2 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith"),
                buildAlias(null, "Johno", null, "Smothers"),
                buildAlias("MRS", "Jack", "J", "Smith")));

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, null, null, null);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, aliasList2, null, null);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(3));

    }

    @Test
    public void shouldAddPartyWhenPartyIdNotMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final boolean proceedingsConcluded1 = false;


        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "2345";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String sourceSystemReference = "L00001";
        final boolean proceedingsConcluded2 = false;


        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, sourceSystemReference);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, proceedingsConcluded2, sourceSystemReference);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(2));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getProceedingsConcluded(), is(proceedingsConcluded2));
        assertThat(unifiedSearchIndexData.getParties().get(1).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(1).getLastName(), is(lastName1));
        assertThat(unifiedSearchIndexData.getParties().get(1).getProceedingsConcluded(), is(proceedingsConcluded1));

    }

    @Test
    public void shouldAddNewPartyWhenExistingPartyIsNull() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String caseType2 = "testCaseType1";

        final String prosecutingAuthority2 = "CC1";
        final String partyId = "2345";
        final String firstName = "Bob1";
        final String lastName = "Barry1";
        final boolean proceedingsConcluded2 = false;
        final String sourceSystemReference = "L00001";


        final JsonObject existingIndex = createCaseDetails(prosecutingAuthority1, caseType1);
        final JsonObject incomingIndex = createCaseDetails(prosecutingAuthority2, caseType2, partyId, firstName, lastName, proceedingsConcluded2, sourceSystemReference);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(existingIndex.toString(), CaseDetails.class);
        final CaseDetails existingIndexData = new ObjectMapper().readValue(incomingIndex.toString(), CaseDetails.class);

        assertThat(incomingIndexData.getHearings(), is(nullValue()));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, existingIndexData);

        assertThat(existingIndexData.getParties().size(), is(1));

        assertThat(existingIndexData.getParties().get(0).getFirstName(), is(firstName));
        assertThat(existingIndexData.getParties().get(0).getLastName(), is(lastName));
        assertThat(existingIndexData.getParties().get(0).getProceedingsConcluded(), is(proceedingsConcluded2));
    }

    @Test
    public void shouldReplaceHearingWhenIdsMatch() throws Exception {


        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final String hearingId1 = "001";
        final List<String> hearingDates1 = asList("2019-01-01", "2019-02-02");
        final String courtId1 = "001";
        final String applicationId1 = "001";
        final String applicationRef1 = "001";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String hearingId2 = "001";
        final List<String> hearingDates2 = asList("2019-03-03", "2019-04-04");
        final String courtId2 = "002";
        final String applicationId2 = "001";
        final String applicationRef2 = "001";

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, hearingId1, hearingDates1, courtId1, applicationId1, applicationRef1, 0);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, hearingId2, hearingDates2, courtId2, applicationId2, applicationRef2, 1);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getHearings().get(0).getCourtId(), is(courtId2));

        assertHearingDatesWithinHearingDay(unifiedSearchIndexData.getHearings().get(0).getHearingDays(), hearingDates2);
        assertDefenceCounselWithinHearing(unifiedSearchIndexData.getHearings().get(0).getDefenceCounsels().get(0), 1);

        caseDetailsNestedTransformer.mergeHearings(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getHearings().size(), is(1));

        assertThat(unifiedSearchIndexData.getHearings().get(0).getCourtId(), is(courtId1));
        assertHearingDatesWithinHearingDay(unifiedSearchIndexData.getHearings().get(0).getHearingDays(), hearingDates1);
        assertDefenceCounselWithinHearing(unifiedSearchIndexData.getHearings().get(0).getDefenceCounsels().get(0), 0);
    }

    @Test
    public void shouldReplaceApplicationWhenIdsMatch() throws Exception {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final String hearingId1 = "001";
        final List<String> hearingDates1 = asList("2019-01-01", "2019-02-02");
        final String courtId1 = "001";
        final String applicationId1 = "001";
        final String applicationRef1 = "001";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String hearingId2 = "001";
        final List<String> hearingDates2 = asList("2019-03-03", "2019-04-04");
        final String courtId2 = "002";
        final String applicationId2 = "001";
        final String applicationRef2 = "002";

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, hearingId1, hearingDates1, courtId1, applicationId1, applicationRef1, 0);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, hearingId2, hearingDates2, courtId2, applicationId2, applicationRef2, 1);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationId(), is(applicationId2));
        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationReference(), is(applicationRef2));
        assertDefenceCounselWithinHearing(unifiedSearchIndexData.getHearings().get(0).getDefenceCounsels().get(0), 1);

        caseDetailsNestedTransformer.mergeApplications(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getApplications().size(), is(1));

        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationId(), is(applicationId1));
        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationReference(), is(applicationRef1));
        assertDefenceCounselWithinHearing(unifiedSearchIndexData.getHearings().get(0).getDefenceCounsels().get(0), 1);
    }

    @Test
    public void shouldAddApplicationWhenIdsDontMatch() throws Exception {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final String hearingId1 = "001";
        final List<String> hearingDates1 = asList("2019-01-01", "2019-02-02");
        final String courtId1 = "001";
        final String applicationId1 = "001";
        final String applicationRef1 = "001";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String hearingId2 = "002";
        final List<String> hearingDates2 = asList("2019-03-03", "2019-04-04");
        final String courtId2 = "002";
        final String applicationId2 = "002";
        final String applicationRef2 = "002";

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, hearingId1, hearingDates1, courtId1, applicationId1, applicationRef1, 0);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, hearingId2, hearingDates2, courtId2, applicationId2, applicationRef2, 1);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationId(), is(applicationId2));
        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationReference(), is(applicationRef2));
        assertDefenceCounselWithinHearing(unifiedSearchIndexData.getHearings().get(0).getDefenceCounsels().get(0), 1);

        caseDetailsNestedTransformer.mergeApplications(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getApplications().size(), is(2));

        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationId(), is(applicationId2));
        assertThat(unifiedSearchIndexData.getApplications().get(0).getApplicationReference(), is(applicationRef2));
        assertThat(unifiedSearchIndexData.getApplications().get(1).getApplicationId(), is(applicationId1));
        assertThat(unifiedSearchIndexData.getApplications().get(1).getApplicationReference(), is(applicationRef1));
        assertDefenceCounselWithinHearing(unifiedSearchIndexData.getHearings().get(0).getDefenceCounsels().get(0), 1);
    }

    @Test
    public void shouldAddNewApplicationWhenExistingApplicationIsNull() throws Exception {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final String hearingId1 = "001";
        final List<String> hearingDates1 = asList("2019-01-01", "2019-02-02");
        final String courtId1 = "001";
        final String applicationId1 = "001";
        final String applicationRef1 = "001";
        final String sourceSystemReference = "L00001";

        final JsonObject incomingIndex = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, hearingId1, hearingDates1, courtId1, applicationId1, applicationRef1, 0);
        final JsonObject existingIndex = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, sourceSystemReference);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(incomingIndex.toString(), CaseDetails.class);
        final CaseDetails existingIndexData = new ObjectMapper().readValue(existingIndex.toString(), CaseDetails.class);

        assertThat(existingIndexData.getApplications(), is(nullValue()));

        caseDetailsNestedTransformer.mergeApplications(incomingIndexData, existingIndexData);

        assertThat(existingIndexData.getApplications().size(), is(1));

        assertThat(existingIndexData.getApplications().get(0).getApplicationId(), is(applicationId1));
        assertThat(existingIndexData.getApplications().get(0).getApplicationReference(), is(applicationRef1));

    }

    @Test
    public void shouldAddNewHearingsWhenExistingHearingsIsNull() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String hearingId2 = "001";
        final List<String> hearingDates2 = asList("2019-03-03", "2019-04-04");
        final String courtId2 = "002";
        final String applicationId2 = "001";
        final String applicationRef2 = "002";
        final String sourceSystemReference = "L00001";

        final JsonObject incomingIndex = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, hearingId2, hearingDates2, courtId2, applicationId2, applicationRef2, 0);
        final JsonObject existingIndex = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, sourceSystemReference);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(incomingIndex.toString(), CaseDetails.class);
        final CaseDetails existingIndexData = new ObjectMapper().readValue(existingIndex.toString(), CaseDetails.class);

        assertThat(existingIndexData.getHearings(), is(nullValue()));

        caseDetailsNestedTransformer.mergeHearings(incomingIndexData, existingIndexData);

        assertThat(existingIndexData.getHearings().size(), is(1));
        assertThat(existingIndexData.getHearings().get(0).getHearingId(), is(hearingId2));
        assertThat(existingIndexData.getHearings().get(0).getCourtId(), is(courtId2));
        assertThat(existingIndexData.getHearings().get(0).getHearingDays().size(), is(2));
        assertThat(existingIndexData.getHearings().get(0).getHearingDays().get(0).getSittingDay(), is("2019-03-03"));
        assertThat(existingIndexData.getHearings().get(0).getHearingDays().get(1).getSittingDay(), is("2019-04-04"));
        assertDefenceCounselWithinHearing(existingIndexData.getHearings().get(0).getDefenceCounsels().get(0), 0);
    }

    @Test
    public void shouldAddHearingWhenIdsDontMatch() throws Exception {


        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final String hearingId1 = "001";
        final List<String> hearingDates1 = asList("2019-01-01", "2019-02-02");
        final String courtId1 = "001";
        final String applicationId1 = "001";
        final String applicationRef1 = "001";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String hearingId2 = "002";
        final List<String> hearingDates2 = asList("2019-03-03", "2019-04-04");
        final String courtId2 = "002";
        final String applicationId2 = "002";
        final String applicationRef2 = "002";

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, hearingId1, hearingDates1, courtId1, applicationId1, applicationRef1, 0);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, hearingId2, hearingDates2, courtId2, applicationId2, applicationRef2, 1);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails existingIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(existingIndexData.getHearings().get(0).getCourtId(), is(courtId2));
        assertHearingDatesWithinHearingDay(existingIndexData.getHearings().get(0).getHearingDays(), hearingDates2);
        assertDefenceCounselWithinHearing(existingIndexData.getHearings().get(0).getDefenceCounsels().get(0), 1);

        caseDetailsNestedTransformer.mergeHearings(incomingIndexData, existingIndexData);

        assertThat(existingIndexData.getHearings().size(), is(2));

        assertThat(existingIndexData.getHearings().get(0).getCourtId(), is(courtId2));
        assertHearingDatesWithinHearingDay(existingIndexData.getHearings().get(0).getHearingDays(), hearingDates2);
        assertThat(existingIndexData.getHearings().get(1).getCourtId(), is(courtId1));
        assertHearingDatesWithinHearingDay(existingIndexData.getHearings().get(1).getHearingDays(), hearingDates1);
        assertDefenceCounselWithinHearing(existingIndexData.getHearings().get(0).getDefenceCounsels().get(0), 1);
        assertDefenceCounselWithinHearing(existingIndexData.getHearings().get(1).getDefenceCounsels().get(0), 0);
    }

    //------------------------------------------------

    // Party with new offences and new LaaRef (?)
    // Party with updated offences
    // Party update with no offences
    // Party with updated LaaRef

    // Party with offences and new LaaRef
    // Party with offences and updated LaaRef

    @Test
    public void shouldAddOffencesWhenPartyIdsMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));
        final String offenceId1 = "offenceId1";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";


        final JsonObject representationOrder = CaseDetailsJsonHelper.buildRepresentationOrder("LAA REF 1", null);
        final JsonObject offenceLaaReference = CaseDetailsJsonHelper.buildLaaReference("OFFENCE LAA REF 1");
        final JsonObject offences = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference, null);

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, representationOrder, asList(offences));
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, null, null, null);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getRepresentationOrder().getApplicationReference(), is("LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceId(), is("offenceId1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getLaaReference().getApplicationReference(), is("OFFENCE LAA REF 1"));




    }

    @Test
    public void shouldAddOffencesAndExistingPartyDataWhenOnlyOffenceChanges() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));
        final String offenceId1 = "offenceId1";
        final String offenceId2 = "offenceId1";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";


        final JsonObject representationOrder = CaseDetailsJsonHelper.buildRepresentationOrder("LAA REF 1", null);
        final JsonObject offenceLaaReference = CaseDetailsJsonHelper.buildLaaReference("OFFENCE LAA REF 1");
        final JsonObject offences = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference, null);
        final JsonObject offences2 = buildOffence(offenceId2, "CODE 2", 1, offenceLaaReference, null);

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, null, null, null, null, asList(offences));
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, aliasList1, representationOrder, asList(offences2));

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getMiddleName(), is("Junior"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getTitle(), is("Mr"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getDateOfBirth(), is("1998-10-22"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getGender(), is("M"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getAddressLines(), is("4 Arnos Grove"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getPostCode(), is("W3 9EF"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getPncId(), is("123"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getArrestSummonsNumber(), is("345"));
        assertThat(unifiedSearchIndexData.getParties().get(0).get_party_type(), is("Defendant"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getNationalInsuranceNumber(), is(""));
        assertThat(unifiedSearchIndexData.getParties().get(0).getProceedingsConcluded(), is(false));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getRepresentationOrder().getApplicationReference(), is("LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceId(), is(offenceId2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceCode(), is("CODE 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getLaaReference().getApplicationReference(), is("OFFENCE LAA REF 1"));
    }

    @Test
    public void shouldAddOffencesAndExistingPartyDataWhenOnlyNewOffenceChange() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));
        final String offenceId1 = "offenceId1";
        final String offenceId2 = "offenceId2";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";


        final JsonObject representationOrder = CaseDetailsJsonHelper.buildRepresentationOrder("LAA REF 1", null);
        final JsonObject offenceLaaReference = CaseDetailsJsonHelper.buildLaaReference("OFFENCE LAA REF 1");
        final JsonObject offences = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference, null);
        final JsonObject offences2 = buildOffence(offenceId2, "CODE 2", 1, offenceLaaReference, null);

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, null, null, null, null, asList(offences));
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, aliasList1, representationOrder, asList(offences2));

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getMiddleName(), is("Junior"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getTitle(), is("Mr"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getDateOfBirth(), is("1998-10-22"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getGender(), is("M"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getAddressLines(), is("4 Arnos Grove"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getPostCode(), is("W3 9EF"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getPncId(), is("123"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getArrestSummonsNumber(), is("345"));
        assertThat(unifiedSearchIndexData.getParties().get(0).get_party_type(), is("Defendant"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getNationalInsuranceNumber(), is(""));
        assertThat(unifiedSearchIndexData.getParties().get(0).getProceedingsConcluded(), is(false));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getRepresentationOrder().getApplicationReference(), is("LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceId(), is(offenceId1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceCode(), is("CODE 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(1).getOffenceId(), is(offenceId2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(1).getOffenceCode(), is("CODE 2"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getLaaReference().getApplicationReference(), is("OFFENCE LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(1).getLaaReference().getApplicationReference(), is("OFFENCE LAA REF 1"));

    }

    @Test
    public void shouldAddOffencesWhenPartyIdsMatchAndOffenceAlreadyExists() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));
        final String offenceId1 = "offenceId1";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String offenceId2 = "offenceId2";


        final JsonObject representationOrder = CaseDetailsJsonHelper.buildRepresentationOrder("LAA REF 1", null);
        final JsonObject offenceLaaReference1 = CaseDetailsJsonHelper.buildLaaReference("OFFENCE 1 LAA REF 1");
        final JsonObject offences1 = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference1, null);

        final JsonObject offenceLaaReference2 = CaseDetailsJsonHelper.buildLaaReference("OFFENCE 2 LAA REF 1");
        final JsonObject offences2 = buildOffence(offenceId2, "CODE 2", 1, offenceLaaReference2, null);

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, representationOrder, asList(offences1));
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, aliasList1, representationOrder, asList(offences2));

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getRepresentationOrder().getApplicationReference(), is("LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceId(), is("offenceId1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceCode(), is("CODE 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(1).getOffenceId(), is("offenceId2"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(1).getOffenceCode(), is("CODE 2"));
    }



    @Test
    public void shouldOverwriteOffencesWhenPartyIdsAndOffenceIdsMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));
        final String offenceId1 = "offenceId1";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";


        final JsonObject representationOrder = CaseDetailsJsonHelper.buildRepresentationOrder("LAA REF 1", null);
        final JsonObject offenceLaaReference1 = CaseDetailsJsonHelper.buildLaaReference("OFFENCE 1 LAA REF 1");
        final JsonObject offences1 = buildOffence(offenceId1, "OVERWRITTEN CODE", 1, offenceLaaReference1, null);

        final JsonObject offenceLaaReference2 = CaseDetailsJsonHelper.buildLaaReference("OFFENCE 2 LAA REF 1");
        final JsonObject offences2 = buildOffence(offenceId1, "ORIGINAL CODE", 1, offenceLaaReference2, null);

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, representationOrder, asList(offences1));
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, aliasList1, representationOrder, asList(offences2));

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getRepresentationOrder().getApplicationReference(), is("LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceId(), is("offenceId1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceCode(), is("OVERWRITTEN CODE"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getVerdict().getOriginatingHearingId(), is(offenceId1));
    }


    @Test
    public void shouldNotDeleteOffencesWhenPartyIdsMatch() throws IOException {

        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final String offenceId1 = "offenceId1";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String offenceId2 = "offenceId2";

        final JsonObject offenceLaaReference2 = CaseDetailsJsonHelper.buildLaaReference("OFFENCE 1 LAA REF 1");
        final List<JsonObject> offences2 = asList(
                buildOffence(offenceId1, "OFFENCE 1 CODE", 1, offenceLaaReference2, null),
                buildOffence(offenceId2, "OFFENCE 2 CODE", 2, offenceLaaReference2, null));

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, null, null, null);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, null, null, offences2);

        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName2));

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(2));

    }

    @Test
    public void shouldMergeCourtOrdersWhenOffenceIdsMatch() throws IOException {
        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));
        final String offenceId1 = "offenceId1";

        final String courtOrderStartDate = LocalDate.of(2021, 1, 20).toString();
        final String courtOrderEndDate = LocalDate.of(2021, 2, 20).toString();
        final String courtOrderDate = LocalDate.of(2021, 1, 8).toString();
        final String courtOrderLabel = "Breach order";
        final String courtOrderId1 = UUID.randomUUID().toString();
        final String courtOrderId2 = UUID.randomUUID().toString();

        final JsonObject representationOrder = CaseDetailsJsonHelper.buildRepresentationOrder("LAA REF 1", null);
        final JsonObject offenceLaaReference = CaseDetailsJsonHelper.buildLaaReference("OFFENCE LAA REF 1");
        final JsonObject offenceCourtOrder1 = CaseDetailsJsonHelper.buildCourtOrder(courtOrderStartDate, courtOrderEndDate, courtOrderDate, courtOrderLabel, courtOrderId1);
        final JsonObject offences = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference, asList(offenceCourtOrder1));

        final JsonObject response = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, representationOrder, asList(offences));
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        final JsonObject offenceCourtOrder2 = CaseDetailsJsonHelper.buildCourtOrder(courtOrderStartDate, courtOrderEndDate, courtOrderDate, courtOrderLabel, courtOrderId2);
        final JsonObject offencesForIndex = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference, asList(offenceCourtOrder2));

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, representationOrder, asList(offencesForIndex));
        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().size(), is(2));

        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().get(0).getId(),
                isOneOf(courtOrderId1, courtOrderId2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().get(0).getOrderingHearingId(),
                isOneOf(offenceCourtOrder1.getString("orderingHearingId"), offenceCourtOrder2.getString("orderingHearingId").trim()));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().get(1).getId(),
                isOneOf(courtOrderId1, courtOrderId2));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().get(1).getOrderingHearingId(),
                isOneOf(offenceCourtOrder1.getString("orderingHearingId"), offenceCourtOrder2.getString("orderingHearingId")));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getRepresentationOrder().getApplicationReference(), is("LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceId(), is("offenceId1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getLaaReference().getApplicationReference(), is("OFFENCE LAA REF 1"));
    }

    @Test
    public void shouldModifyCourtOrderEndDateForExistingRemovedCourtOrder() throws IOException {
        final CaseDetailsNestedTransformer caseDetailsNestedTransformer = new CaseDetailsNestedTransformer();

        final String prosecutingAuthority1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final Set<JsonObject> aliasList1 = new HashSet<>(asList(buildAlias("MR", "John", null, "Smith")));
        final String offenceId1 = "offenceId1";
        final String courtOrderId = UUID.randomUUID().toString();

        final String courtOrderStartDate = LocalDate.of(2021, 1, 20).toString();
        final String courtOrderEndDate = LocalDate.of(2021, 2, 20).toString();
        final String courtOrderDate = LocalDate.of(2021, 1, 8).toString();
        final String courtOrderLabel = "Breach order";
        final String courtOrderEndDateForRemoved = LocalDate.now().toString();

        final JsonObject representationOrder = CaseDetailsJsonHelper.buildRepresentationOrder("LAA REF 1", null);
        final JsonObject offenceLaaReference = CaseDetailsJsonHelper.buildLaaReference("OFFENCE LAA REF 1");
        final JsonObject offenceCourtOrder1 = CaseDetailsJsonHelper.buildCourtOrder(courtOrderStartDate, courtOrderEndDate, courtOrderDate, courtOrderLabel, courtOrderId);
        final JsonObject offences = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference, asList(offenceCourtOrder1));

        final JsonObject response = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, representationOrder, asList(offences));
        final CaseDetails unifiedSearchIndexData = new ObjectMapper().readValue(response.toString(), CaseDetails.class);

        final JsonObject offenceCourtOrder2 = CaseDetailsJsonHelper.buildCourtOrder(courtOrderStartDate, courtOrderEndDateForRemoved, courtOrderDate, courtOrderLabel, courtOrderId);
        final JsonObject offencesForIndex = buildOffence(offenceId1, "CODE 1", 1, offenceLaaReference, asList(offenceCourtOrder2));

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, aliasList1, representationOrder, asList(offencesForIndex));
        final CaseDetails incomingIndexData = new ObjectMapper().readValue(index.toString(), CaseDetails.class);

        caseDetailsNestedTransformer.mergeParties(incomingIndexData, unifiedSearchIndexData);

        assertThat(unifiedSearchIndexData.getParties().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().get(0).getId(), is(courtOrderId));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getCourtOrders().get(0).getEndDate(), is(courtOrderEndDateForRemoved));

        assertThat(unifiedSearchIndexData.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getLastName(), is(lastName1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getAliases().size(), is(1));

        assertThat(unifiedSearchIndexData.getParties().get(0).getRepresentationOrder().getApplicationReference(), is("LAA REF 1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().size(), is(1));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getOffenceId(), is("offenceId1"));
        assertThat(unifiedSearchIndexData.getParties().get(0).getOffences().get(0).getLaaReference().getApplicationReference(), is("OFFENCE LAA REF 1"));
    }

}