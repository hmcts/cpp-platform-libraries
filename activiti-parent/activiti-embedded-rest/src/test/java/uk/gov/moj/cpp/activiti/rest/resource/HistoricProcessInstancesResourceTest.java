package uk.gov.moj.cpp.activiti.rest.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.json.JsonObject;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HistoricProcessInstancesResourceTest {

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private HistoricProcessInstancesResource resource;

    @Test
    void shouldReturnHistoricProcessInstancesMatchingKeyAndBusinessKey() {
        final String processDefinitionKey = "pendingMaterialExpiration";
        final String businessKey = "upload-123";

        final HistoricProcessInstance instance = mock(HistoricProcessInstance.class);
        when(instance.getId()).thenReturn("hist-proc-id-1");
        when(instance.getDeleteReason()).thenReturn(null);

        final HistoricProcessInstanceQuery query = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(query);
        when(query.processDefinitionKey(processDefinitionKey)).thenReturn(query);
        when(query.processInstanceBusinessKey(businessKey)).thenReturn(query);
        when(query.list()).thenReturn(List.of(instance));

        final JsonObject result = resource.getHistoricProcessInstances(processDefinitionKey, businessKey);

        assertThat(result.getJsonArray("data").size(), is(1));
        assertThat(result.getJsonArray("data").getJsonObject(0).getString("id"), is("hist-proc-id-1"));
        assertThat(result.getInt("total"), is(1));
    }

    @Test
    void shouldIncludeDeleteReasonWhenPresent() {
        final HistoricProcessInstance instance = mock(HistoricProcessInstance.class);
        when(instance.getId()).thenReturn("hist-proc-id-2");
        when(instance.getDeleteReason()).thenReturn("cancelled");

        final HistoricProcessInstanceQuery query = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(query);
        when(query.processDefinitionKey("myKey")).thenReturn(query);
        when(query.processInstanceBusinessKey("myBk")).thenReturn(query);
        when(query.list()).thenReturn(List.of(instance));

        final JsonObject result = resource.getHistoricProcessInstances("myKey", "myBk");

        assertThat(result.getJsonArray("data").getJsonObject(0).getString("deleteReason"), is("cancelled"));
    }

    @Test
    void shouldReturnEmptyDataArrayWhenNoHistoricInstancesFound() {
        final HistoricProcessInstanceQuery query = mock(HistoricProcessInstanceQuery.class);
        when(historyService.createHistoricProcessInstanceQuery()).thenReturn(query);
        when(query.processDefinitionKey("unknown")).thenReturn(query);
        when(query.processInstanceBusinessKey("unknown")).thenReturn(query);
        when(query.list()).thenReturn(List.of());

        final JsonObject result = resource.getHistoricProcessInstances("unknown", "unknown");

        assertThat(result.getJsonArray("data").size(), is(0));
        assertThat(result.getInt("total"), is(0));
    }
}
