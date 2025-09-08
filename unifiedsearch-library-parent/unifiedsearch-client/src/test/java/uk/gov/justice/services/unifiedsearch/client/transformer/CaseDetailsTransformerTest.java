package uk.gov.justice.services.unifiedsearch.client.transformer;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;
import static uk.gov.justice.services.unifiedsearch.client.transformer.CaseDetailsJsonHelper.createCaseDetails;
import static uk.gov.justice.services.unifiedsearch.client.transformer.HearingDayHelper.assertHearingDatesWithinHearingDay;

import uk.gov.justice.services.unifiedsearch.client.domain.CaseDetails;

import java.util.List;

import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
public class CaseDetailsTransformerTest {

    @Spy
    private CaseDetailsNestedTransformer caseDetailsNestedTransformer;

    @InjectMocks
    private CaseDetailsTransformer caseDetailsTransformer;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        setField(caseDetailsTransformer, "mapper", new ObjectMapper());
        setField(caseDetailsNestedTransformer, "logger", logger);
    }

    @Test
    public void shouldUpdateExistingParty() {

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
        final String sourceSystemReference = "L00001";

        final JsonObject index = createCaseDetails(prosecutingAuthority1, caseType1, partyId1, firstName1, lastName1, sourceSystemReference);
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, sourceSystemReference);

        final GetResponse getResponse = mock(GetResponse.class);

        when(getResponse.getSourceAsString()).thenReturn(response.toString());

        final CaseDetails caseDetails = caseDetailsTransformer.transform(index, getResponse);

        assertThat(caseDetails.getProsecutingAuthority(), is(prosecutingAuthority1));
        assertThat(caseDetails.get_case_type(), is(caseType1));
        assertThat(caseDetails.getSourceSystemReference(), is(sourceSystemReference));

        assertThat(caseDetails.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(caseDetails.getParties().get(0).getLastName(), is(lastName1));

        assertThat(caseDetails.getParties().get(0).getProceedingsConcluded(), is(proceedingsConcluded1));
    }

    @Test
    public void shouldUpdateExistingPartyHearingAndApplication() {

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
        final JsonObject response = createCaseDetails(prosecutingAuthority2, caseType2, partyId2, firstName2, lastName2, hearingId2, hearingDates2, courtId2, applicationId2, applicationRef2, 0);

        final GetResponse getResponse = mock(GetResponse.class);

        when(getResponse.getSourceAsString()).thenReturn(response.toString());

        final CaseDetails caseDetails = caseDetailsTransformer.transform(index, getResponse);

        assertThat(caseDetails.getProsecutingAuthority(), is(prosecutingAuthority1));
        assertThat(caseDetails.get_case_type(), is(caseType1));

        assertThat(caseDetails.getParties().size(), is(1));
        assertThat(caseDetails.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(caseDetails.getParties().get(0).getLastName(), is(lastName1));

        assertThat(caseDetails.getHearings().size(), is(1));
        assertThat(caseDetails.getHearings().get(0).getCourtId(), is(courtId1));
        assertHearingDatesWithinHearingDay(caseDetails.getHearings().get(0).getHearingDays(), hearingDates1);

        assertThat(caseDetails.getApplications().size(), is(1));
        assertThat(caseDetails.getApplications().get(0).getApplicationId(), is(applicationId1));
        assertThat(caseDetails.getApplications().get(0).getApplicationReference(), is(applicationRef1));
    }

    @Test
    public void shouldUpdateExistingCaseStatus() {

        final String caseReference1 = "caseReference1";
        final String caseType1 = "testCaseType";
        final String prosecutingAuthority1 = "prosecutingAuthority1";
        final String caseStatus1 = "caseStatus1";
        final boolean isSjp1 = true;
        final boolean isMagistrates1 = true;
        final boolean isCrown1 = true;
        final boolean isCharging1 = true;
        final String sjpNoticeServed1 = "sjpNoticeServed1";

        final String prosecutingAuthority2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String caseReference2 = "caseReference2";
        final String caseStatus2 = "caseStatus2";
        final boolean isSjp2 = false;
        final boolean isMagistrates2 = false;
        final boolean isCrown2 = false;
        final boolean isCharging2 = false;
        final String sjpNoticeServed2 = "123";


        final JsonObject index = createCaseDetails(caseReference1, caseType1, prosecutingAuthority1, caseStatus1, isSjp1, isMagistrates1, isCrown1, isCharging1, sjpNoticeServed1);
        final JsonObject response = createCaseDetails(caseReference2, caseType2, prosecutingAuthority2, caseStatus2, isSjp2, isMagistrates2, isCrown2, isCharging2, sjpNoticeServed2);

        final GetResponse getResponse = mock(GetResponse.class);

        when(getResponse.getSourceAsString()).thenReturn(response.toString());

        final CaseDetails caseDetails = caseDetailsTransformer.transform(index, getResponse);

        assertThat(caseDetails.getCaseReference(), is(caseReference1));
        assertThat(caseDetails.get_case_type(), is(caseType1));
        assertThat(caseDetails.getProsecutingAuthority(), is(prosecutingAuthority1));
        assertThat(caseDetails.getCaseStatus(), is(caseStatus1));
        assertThat(caseDetails.get_is_sjp(), is(isSjp1));
        assertThat(caseDetails.get_is_magistrates(), is(isMagistrates1));
        assertThat(caseDetails.get_is_crown(), is(isCrown1));
        assertThat(caseDetails.get_is_charging(), is(isCharging1));
        assertThat(caseDetails.getSjpNoticeServed(), is(sjpNoticeServed1));

    }

}