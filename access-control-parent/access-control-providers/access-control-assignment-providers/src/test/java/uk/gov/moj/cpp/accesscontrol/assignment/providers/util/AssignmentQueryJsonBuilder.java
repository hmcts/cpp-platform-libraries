package uk.gov.moj.cpp.accesscontrol.assignment.providers.util;

import static java.util.UUID.randomUUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class AssignmentQueryJsonBuilder {

    private static final String KEY_ID = "id";
    public static final String KEY_VERSION = "version";
    public static final String KEY_DOMAIN_OBJECT_ID = "domainObjectId";
    public static final String KEY_ASSIGNMENT_NATURE_TYPE = "assignmentNatureType";
    public static final String KEY_ASSIGNEE = "assignee";
    public static final String KEY_ASSIGNMENTS = "assignments";

    public static final String PRE_CHARGE_REVIEW = "PRE_CHARGE_REVIEW";

    private String assignee;

    public static AssignmentQueryJsonBuilder getReviewJson() {
        return new AssignmentQueryJsonBuilder();
    }

    public AssignmentQueryJsonBuilder withAssignee(final String assignee) {
        this.assignee = assignee;
        return this;
    }

    public String toString() {
        return build().toString();
    }

    public JsonObject build() {
        final JsonObjectBuilder json = Json.createObjectBuilder();
        json.add(KEY_ID, randomUUID().toString());
        json.add(KEY_VERSION, 1);
        json.add(KEY_DOMAIN_OBJECT_ID, randomUUID().toString());
        json.add(KEY_ASSIGNMENT_NATURE_TYPE, PRE_CHARGE_REVIEW);
        json.add(KEY_ASSIGNEE, this.assignee);

        final JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        jsonArrayBuilder.add(json);

        final JsonObjectBuilder wrapper = Json.createObjectBuilder();
        wrapper.add(KEY_ASSIGNMENTS, jsonArrayBuilder);

        return wrapper.build();
    }

}


