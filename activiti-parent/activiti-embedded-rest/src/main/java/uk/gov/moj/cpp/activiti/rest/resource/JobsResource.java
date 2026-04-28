package uk.gov.moj.cpp.activiti.rest.resource;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.activiti.engine.ManagementService;
import org.activiti.engine.runtime.Job;

@RequestScoped
@Path("/management/jobs")
public class JobsResource {

    @Inject
    private ManagementService managementService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getJobs(
            @QueryParam("timersOnly") final boolean timersOnly,
            @QueryParam("processInstanceId") final String processInstanceId) {

        final List<Job> jobs = managementService.createJobQuery()
                .timers()
                .processInstanceId(processInstanceId)
                .list();

        final JsonArrayBuilder data = Json.createArrayBuilder();
        for (final Job job : jobs) {
            data.add(Json.createObjectBuilder()
                    .add("id", job.getId()));
        }

        return Json.createObjectBuilder()
                .add("data", data)
                .add("total", jobs.size())
                .build();
    }

    @POST
    @Path("/{jobId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response executeJob(@PathParam("jobId") final String jobId, final JsonObject body) {
        managementService.executeJob(jobId);
        return Response.ok().build();
    }
}
