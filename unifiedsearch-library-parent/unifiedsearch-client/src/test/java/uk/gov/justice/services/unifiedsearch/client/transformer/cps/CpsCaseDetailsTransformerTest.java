package uk.gov.justice.services.unifiedsearch.client.transformer.cps;

import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.setField;
import static uk.gov.justice.services.unifiedsearch.client.transformer.cps.CpsCaseDetailsJsonHelper.createCaseDetails;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.unifiedsearch.client.domain.cps.CaseDetails;

import javax.json.JsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CpsCaseDetailsTransformerTest {

    @Spy
    private CpsCaseDetailsNestedTransformer cpsCaseDetailsNestedTransformer;

    @InjectMocks
    private CpsCaseDetailsTransformer cpsCaseDetailsTransformer;

    @BeforeEach
    public void setup() {
        setField(cpsCaseDetailsTransformer, "mapper", new ObjectMapper());
    }

    @Test
    public void shouldUpdateExistingParty() {

        final String prosecutor1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";

        final String prosecutor2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";

        final JsonObject index = createCaseDetails(prosecutor1, caseType1, partyId1, firstName1, lastName1);
        final JsonObject response = createCaseDetails(prosecutor2, caseType2, partyId2, firstName2, lastName2);

        final GetResponse getResponse = mock(GetResponse.class);

        when(getResponse.getSourceAsString()).thenReturn(response.toString());

        final CaseDetails caseDetails = cpsCaseDetailsTransformer.transform(index, getResponse);

        assertThat(caseDetails.getProsecutor(), is(prosecutor1));
        assertThat(caseDetails.getCaseType(), is(caseType1));

        assertThat(caseDetails.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(caseDetails.getParties().get(0).getLastName(), is(lastName1));
    }

    @Test
    public void shouldUpdateExistingPartyHearing() {

        final String prosecutor1 = "CC";
        final String caseType1 = "testCaseType";
        final String partyId1 = "123";
        final String firstName1 = "Bob";
        final String lastName1 = "Barry";
        final String hearingId1 = "001";
        final String hearingDateTime1 = "2019-01-01";
        final String courtRoom1 = "001";

        final String prosecutor2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String partyId2 = "123";
        final String firstName2 = "Bob1";
        final String lastName2 = "Barry1";
        final String hearingId2 = "001";
        final String hearingDateTime2 = "2019-03-03";
        final String courtRoom2 = "002";

        final JsonObject index = createCaseDetails(prosecutor1, caseType1, partyId1, firstName1, lastName1, hearingId1, hearingDateTime1, courtRoom1);
        final JsonObject response = createCaseDetails(prosecutor2, caseType2, partyId2, firstName2, lastName2, hearingId2, hearingDateTime2, courtRoom2);

        final GetResponse getResponse = mock(GetResponse.class);

        when(getResponse.getSourceAsString()).thenReturn(response.toString());

        final CaseDetails caseDetails = cpsCaseDetailsTransformer.transform(index, getResponse);

        assertThat(caseDetails.getProsecutor(), is(prosecutor1));
        assertThat(caseDetails.getCaseType(), is(caseType1));

        assertThat(caseDetails.getParties().size(), is(1));
        assertThat(caseDetails.getParties().get(0).getFirstName(), is(firstName1));
        assertThat(caseDetails.getParties().get(0).getLastName(), is(lastName1));

        assertThat(caseDetails.getHearings().size(), is(1));
        assertThat(caseDetails.getHearings().get(0).getCourtRoom(), is(courtRoom1));
    }

    @Test
    public void shouldUpdateExistingCaseStatus() {

        final String urn1 = "urn";
        final String caseType1 = "testCaseType";
        final String prosecutor1 = "prosecutingAuthority1";
        final String caseStatus1 = "caseStatus1";
        final String cpsUnitCode1 = "cps unit 1";
        final String cjsAreaCode1 = "cjs Area 1";

        final String urn2 = "urn";
        final String prosecutor2 = "CC1";
        final String caseType2 = "testCaseType1";
        final String caseStatus2 = "caseStatus2";
        final String cpsUnitCode2 = "cps unit 2";
        final String cjsAreaCode2 = "cjs Area 2";


        final JsonObject index = createCaseDetails(urn1, caseType1, prosecutor1, caseStatus1, cpsUnitCode1, cjsAreaCode1);
        final JsonObject response = createCaseDetails(urn1, caseType2, prosecutor2, caseStatus2, cpsUnitCode2, cjsAreaCode2);

        final GetResponse getResponse = mock(GetResponse.class);

        when(getResponse.getSourceAsString()).thenReturn(response.toString());

        final CaseDetails caseDetails = cpsCaseDetailsTransformer.transform(index, getResponse);

        assertThat(caseDetails.getUrn(), is(urn1));
        assertThat(caseDetails.getCaseType(), is(caseType1));
        assertThat(caseDetails.getProsecutor(), is(prosecutor1));
        assertThat(caseDetails.getCaseStatusCode(), is(caseStatus1));
        assertThat(caseDetails.getCpsUnitCode(), is(cpsUnitCode1));
        assertThat(caseDetails.getCjsAreaCodes(), is(of(cjsAreaCode1)));
    }

}