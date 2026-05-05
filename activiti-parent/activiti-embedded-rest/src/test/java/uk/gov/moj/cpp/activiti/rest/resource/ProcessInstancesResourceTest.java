package uk.gov.moj.cpp.activiti.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.json.JsonObject;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessInstancesResourceTest {

    @Mock
    private RuntimeService runtimeService;

    @InjectMocks
    private ProcessInstancesResource resource;

    @Test
    void shouldReturnProcessInstancesMatchingKeyAndBusinessKey() {
        final String processDefinitionKey = "pendingMaterialExpiration";
        final String businessKey = "upload-123";

        final ExecutionEntity instance = mock(ExecutionEntity.class);
        when(instance.getId()).thenReturn("proc-inst-id-1");

        final ProcessInstanceQuery query = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(query);
        when(query.processDefinitionKey(processDefinitionKey)).thenReturn(query);
        when(query.processInstanceBusinessKey(businessKey)).thenReturn(query);
        when(query.list()).thenReturn(List.of(instance));

        final JsonObject result = resource.getProcessInstances(processDefinitionKey, businessKey);

        assertThat(result.getJsonArray("data").size(), is(1));
        assertThat(result.getJsonArray("data").getJsonObject(0).getString("id"), is("proc-inst-id-1"));
        assertThat(result.getInt("total"), is(1));
    }

    @Test
    void shouldReturnEmptyDataArrayWhenNoProcessInstancesFound() {
        final ProcessInstanceQuery query = mock(ProcessInstanceQuery.class);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(query);
        when(query.processDefinitionKey("unknown")).thenReturn(query);
        when(query.processInstanceBusinessKey("unknown")).thenReturn(query);
        when(query.list()).thenReturn(List.of());

        final JsonObject result = resource.getProcessInstances("unknown", "unknown");

        assertThat(result.getJsonArray("data").size(), is(0));
        assertThat(result.getInt("total"), is(0));
    }
}
