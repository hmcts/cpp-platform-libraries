package uk.gov.moj.cpp.activiti.rest.resource;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;

@RequestScoped
@Path("/history/historic-process-instances")
public class HistoricProcessInstancesResource {

    @Inject
    private HistoryService historyService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getHistoricProcessInstances(
            @QueryParam("processDefinitionKey") final String processDefinitionKey,
            @QueryParam("businessKey") final String businessKey) {

        final List<HistoricProcessInstance> instances = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .processInstanceBusinessKey(businessKey)
                .list();

        final JsonArrayBuilder data = Json.createArrayBuilder();
        for (final HistoricProcessInstance instance : instances) {
            final JsonObjectBuilder entry = Json.createObjectBuilder()
                    .add("id", instance.getId());
            if (instance.getDeleteReason() != null) {
                entry.add("deleteReason", instance.getDeleteReason());
            }
            data.add(entry);
        }

        return Json.createObjectBuilder()
                .add("data", data)
                .add("total", instances.size())
                .build();
    }
}
