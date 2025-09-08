package uk.gov.moj.cpp.accesscontrol.progression.test.util;


import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class ProgressionSearchCaseByMaterialIdJsonResponseBuilder {

    private UUID caseId;
    private String prosecutingAuthority;

    private ProgressionSearchCaseByMaterialIdJsonResponseBuilder() {
        this.caseId = UUID.randomUUID();
        this.prosecutingAuthority = "";
    }

    public static ProgressionSearchCaseByMaterialIdJsonResponseBuilder aSearchCaseByMaterialIdJsonResponse() {
        return new ProgressionSearchCaseByMaterialIdJsonResponseBuilder();
    }

    public ProgressionSearchCaseByMaterialIdJsonResponseBuilder withProsecutingAuthority(String prosecutingAuthority) {
        this.prosecutingAuthority = prosecutingAuthority;
        return this;
    }

    public JsonObject build() {
        final JsonObjectBuilder json = Json.createObjectBuilder();

        json.add("caseId", caseId.toString());
        json.add("prosecutingAuthority", prosecutingAuthority);

        return json.build();
    }
}
