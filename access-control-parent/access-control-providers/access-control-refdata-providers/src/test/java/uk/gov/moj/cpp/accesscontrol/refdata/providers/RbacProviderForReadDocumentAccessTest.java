
package uk.gov.moj.cpp.accesscontrol.refdata.providers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithDefaults;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;
import static uk.gov.moj.cpp.accesscontrol.refdata.providers.RbacProvider.REFERENCEDATA_QUERY_DOCUMENT_TYPE_ACCESS;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonValue;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class RbacProviderForReadDocumentAccessTest extends RbacProviderBaseTest {

    @Test
    public void userIsNotAllowedToReadWhenRbacDocumentNotDefinedForDocumentType() {

        when(requester.request(any())).thenReturn(null);

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(false));
    }


    @Test
    public void userIsNotAllowedToReadWhenRbacDocumentDefinedForDocumentTypeWithNoUsersAssigned() {

        final Map<String, String> dates = new HashMap<>();

        setUpMockDataForReferenceAndUserContext(readFile("document-type-has-no-access.json", dates));

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(false));
    }

    @Test
    public void userIsNotAllowedWhenEmptyAction() {

        final Map<String, String> dates = new HashMap<>();

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildEmptyAction()), is(false));
    }

    @Test
    public void userIsNotAllowedWhenNullJsonObjectAction() {

        final Map<String, String> dates = new HashMap<>();

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildNullJsonObjectAction()), is(true));
    }


    @Test
    public void userIsNotAllowedToReadWhenRbacDocumentDefinedForDocumentTypeWithEmptyUsersAssigned() {

        final Map<String, String> dates = new HashMap<>();

        setUpMockDataForReferenceAndUserContext(readFile("document-type-has-empty-access.json", dates));

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(false));

    }

    @Test
    public void userIsNotAllowedToReadWhenRbacDocumentDefinedForDocumentTypeWithUserGroupNotMatching() {

        final Map<String, String> dates = new HashMap<>();
        final LocalDate twodaysPrevious = LocalDate.now().minusDays(2);
        final LocalDate twodaysAfter = LocalDate.now().plusDays(2);
        dates.put("VALLIDFROM", twodaysPrevious.toString());
        dates.put("VALLIDTO", twodaysAfter.toString());
        dates.put("GROUP_NAME", "Magistrate");

        setUpMockDataForReferenceAndUserContext(readFile("document-type-has-all-access.json", dates));

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(false));

    }

    @Test
    public void userIsNotAllowedToReadWhenNoUserDefinedForUsers() {

        final Map<String, String> dates = new HashMap<>();
        final LocalDate twodaysPrevious = LocalDate.now().minusDays(2);
        final LocalDate twodaysAfter = LocalDate.now().plusDays(2);
        dates.put("VALLIDFROM", twodaysPrevious.toString());
        dates.put("VALLIDTO", twodaysAfter.toString());
        dates.put("GROUP_NAME", "Listing Officer");


        final JsonEnvelope mockRefDataEnvelope = JsonEnvelope.envelopeFrom(metadataWithRandomUUID(REFERENCEDATA_QUERY_DOCUMENT_TYPE_ACCESS).withUserId(userId.toString()), readFile("document-type-has-all-access.json", dates));

        when(requester.request(any())).thenReturn(mockRefDataEnvelope, JsonEnvelope.envelopeFrom(metadataWithDefaults(), JsonValue.NULL));

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(false));

    }

    @Test
    public void userIsAllowedToReadWhenRbacDocumentFoundForDocumentTypeWithExpiryDateToday() {

        final Map<String, String> dates = new HashMap<>();
        final LocalDate twodaysPrevious = LocalDate.now().minusDays(2);
        final LocalDate now = LocalDate.now();
        dates.put("VALLIDFROM", twodaysPrevious.toString());
        dates.put("VALLIDTO", now.toString());
        dates.put("GROUP_NAME", "Listing Officer");

        setUpMockDataForReferenceAndUserContext(readFile("document-type-has-all-access.json", dates));

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(true));

    }

    @Test
    public void userIsAllowedToReadWhenRbacDocumentFoundForDocumentTypeWithStartDateToday() {

        final Map<String, String> dates = new HashMap<>();
        final LocalDate now = LocalDate.now();
        final LocalDate twodaysAfter = LocalDate.now().plusDays(2);
        dates.put("VALLIDFROM", now.toString());
        dates.put("VALLIDTO", twodaysAfter.toString());
        dates.put("GROUP_NAME", "Listing Officer");

        setUpMockDataForReferenceAndUserContext(readFile("document-type-has-all-access.json", dates));

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(true));

    }


    @Test
    public void userIsAllowedToReadWhenRbacDocumentFoundForDocumentType() {

        final Map<String, String> dates = new HashMap<>();
        final LocalDate twodaysPrevious = LocalDate.now().minusDays(2);
        final LocalDate twodaysAfter = LocalDate.now().plusDays(2);
        dates.put("VALLIDFROM", twodaysPrevious.toString());
        dates.put("VALLIDTO", twodaysAfter.toString());
        dates.put("GROUP_NAME", "Listing Officer");

        setUpMockDataForReferenceAndUserContext(readFile("document-type-has-all-access.json", dates));

        assertThat(rbacProvider.isLoggedInUserAllowedToReadDocument(buildAction()), is(true));

    }

}
