package uk.gov.moj.cpp.activiti.rest.resource;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

@RequestScoped
@Path("/runtime/process-instances")
public class ProcessInstancesResource {

    @Inject
    private RuntimeService runtimeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProcessInstances(
            @QueryParam("processDefinitionKey") final String processDefinitionKey,
            @QueryParam("businessKey") final String businessKey) {

        final List<ProcessInstance> instances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .processInstanceBusinessKey(businessKey)
                .list();

        final JsonArrayBuilder data = Json.createArrayBuilder();
        for (final ProcessInstance instance : instances) {
            data.add(Json.createObjectBuilder()
                    .add("id", instance.getId())
                    .add("processDefinitionKey", processDefinitionKey));
        }

        return Json.createObjectBuilder()
                .add("data", data)
                .add("total", instances.size())
                .build();
    }
}
