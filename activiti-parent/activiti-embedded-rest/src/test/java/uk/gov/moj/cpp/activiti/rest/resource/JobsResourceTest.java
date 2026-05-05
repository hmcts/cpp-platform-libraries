package uk.gov.moj.cpp.activiti.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

import org.activiti.engine.ManagementService;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobsResourceTest {

    @Mock
    private ManagementService managementService;

    @InjectMocks
    private JobsResource resource;

    @Test
    void shouldReturnJobsMatchingProcessInstanceId() {
        final String processInstanceId = "proc-inst-id-1";

        final Job job = mock(Job.class);
        when(job.getId()).thenReturn("job-id-1");

        final JobQuery query = mock(JobQuery.class);
        when(managementService.createJobQuery()).thenReturn(query);
        when(query.timers()).thenReturn(query);
        when(query.processInstanceId(processInstanceId)).thenReturn(query);
        when(query.list()).thenReturn(List.of(job));

        final JsonObject result = resource.getJobs(true, processInstanceId);

        assertThat(result.getJsonArray("data").size(), is(1));
        assertThat(result.getJsonArray("data").getJsonObject(0).getString("id"), is("job-id-1"));
        assertThat(result.getInt("total"), is(1));
    }

    @Test
    void shouldReturnEmptyDataArrayWhenNoJobsFound() {
        final JobQuery query = mock(JobQuery.class);
        when(managementService.createJobQuery()).thenReturn(query);
        when(query.timers()).thenReturn(query);
        when(query.processInstanceId("unknown")).thenReturn(query);
        when(query.list()).thenReturn(List.of());

        final JsonObject result = resource.getJobs(true, "unknown");

        assertThat(result.getJsonArray("data").size(), is(0));
        assertThat(result.getInt("total"), is(0));
    }

    @Test
    void shouldExecuteJobAndReturnOkResponse() {
        final String jobId = "job-id-1";

        final Response response = resource.executeJob(jobId, Json.createObjectBuilder().build());

        verify(managementService).executeJob(jobId);
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }
}
