package uk.gov.moj.cpp.accesscontrol.test.util;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonBuilderFactory;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PermissionJsonBuilder {

    private List<Permission> permissionList = new ArrayList<>();

    public static PermissionJsonBuilder permissionsJson() {
        return new PermissionJsonBuilder();
    }

    public PermissionJsonBuilder withPermissions(final List<Permission> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    public String toString() {
        return build().toString();
    }

    public JsonObject build() {
        final JsonObjectBuilder json = getJsonBuilderFactory().createObjectBuilder();
        final JsonArrayBuilder permissionArray = getJsonBuilderFactory().createArrayBuilder();

        for (Permission permission : permissionList) {
            permissionArray.add(getPermissionJson(permission));
        }

        json.add("permissions", permissionArray);
        return json.build();
    }

    private JsonObjectBuilder getPermissionJson(Permission permission) {
        final JsonObjectBuilder jsonObjectBuilder =  getJsonBuilderFactory().createObjectBuilder()
                .add("object", permission.getObject())
                .add("action", permission.getAction());
        if(isNotEmpty(permission.getSource())) {
            jsonObjectBuilder.add("source", permission.getSource());
        }
        if(isNotEmpty(permission.getTarget())) {
            jsonObjectBuilder.add("target", permission.getTarget());
        }
        return jsonObjectBuilder;
    }
}
