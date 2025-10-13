package uk.gov.moj.cpp.accesscontrol.assignment.providers;

import org.slf4j.Logger;
import uk.gov.justice.services.core.annotation.FrameworkComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.requester.Requester;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.providers.Provider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.UUID;

import static javax.json.JsonValue.ValueType.NULL;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.messaging.JsonEnvelope.metadataBuilder;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;
import static uk.gov.justice.services.messaging.JsonObjects.jsonReaderFactory;
import static uk.gov.moj.cpp.accesscontrol.drools.constants.AccessControlFrameworkComponent.ACCESS_CONTROL;

@Provider
@ApplicationScoped
public class AssignmentProvider {

    private static final String ASSIGNMENT_QUERY = "assignment.query.assignments";
    private static final String REVIEW_ID_KEY = "reviewId";
    private static final String ASSIGNEE_KEY = "assignee";
    private static final String ASSIGNMENTS_KEY = "assignments";
    private static final String DOMAIN_OBJECT_ID_KEY = "domainObjectId";

    @Inject
    Logger logger;

    @Inject
    @FrameworkComponent(ACCESS_CONTROL)
    Requester requester;

    @Inject
    Enveloper enveloper;


    public boolean isUserAssignedByReviewId(final Action action) {
        final String reviewId = action.envelope().payloadAsJsonObject().getString((REVIEW_ID_KEY));
        return verifyUserAssignedToReview(action, reviewId);
    }

    public boolean isUserAssignedByReviewId(final Action action, final String reviewId) {
        return verifyUserAssignedToReview(action, reviewId);
    }

    private boolean verifyUserAssignedToReview(final Action action, final String reviewId) {
        final String userId = action.userId().get();

        final JsonObject requestPayload = jsonBuilderFactory.createObjectBuilder()
                .add(DOMAIN_OBJECT_ID_KEY, reviewId)
                .build();

        final JsonEnvelope envelope = envelopeFrom(
                metadataBuilder().withId(UUID.randomUUID()).withName(ASSIGNMENT_QUERY),
                requestPayload
        );

        final JsonEnvelope response = requester.requestAsAdmin(envelope);
        JsonObject payload = jsonFromString(response.payload().toString());
        logger.info("assignment.query.assignments: " + payload);

        boolean match = isFound(response) && payload.getJsonArray(ASSIGNMENTS_KEY).getJsonObject(0).getString(ASSIGNEE_KEY)
                .equalsIgnoreCase(userId);
        logger.info(String.format("response for verifyUserAssignedToReview: %s, for userId: %s and reviewId: %s", match, userId, reviewId));
        return match;
    }

    private static JsonObject jsonFromString(String jsonObjectStr) {
        JsonReader jsonReader = jsonReaderFactory.createReader(new StringReader(jsonObjectStr));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        return object;
    }

    private boolean isFound(final JsonEnvelope response) {
        return response.payload().getValueType() != NULL;
    }
}
