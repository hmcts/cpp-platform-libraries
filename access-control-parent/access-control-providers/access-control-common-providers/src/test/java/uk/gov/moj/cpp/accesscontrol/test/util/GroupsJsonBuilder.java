package uk.gov.moj.cpp.accesscontrol.test.util;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class GroupsJsonBuilder {

    private List<Group> groups = new ArrayList<>();

    public static GroupsJsonBuilder groupsJson() {
        return new GroupsJsonBuilder();
    }

    public GroupsJsonBuilder withGroups(final List<Group> groups) {
        this.groups = groups;
        return this;
    }

    public String toString() {
        return build().toString();
    }

    public JsonObject build() {
        final JsonObjectBuilder json = Json.createObjectBuilder();
        final JsonArrayBuilder groupsArray = Json.createArrayBuilder();

        for (Group group : groups) {
            groupsArray.add(getGroupJson(group));
        }

        json.add("groups", groupsArray);
        return json.build();
    }

    private JsonObjectBuilder getGroupJson(Group group) {
        return group.getGroupName() != null
                ? Json.createObjectBuilder()
                .add("groupId", group.getGroupId())
                .add("groupName", group.getGroupName())
                : Json.createObjectBuilder()
                .add("groupId", group.getGroupId());
    }
}
