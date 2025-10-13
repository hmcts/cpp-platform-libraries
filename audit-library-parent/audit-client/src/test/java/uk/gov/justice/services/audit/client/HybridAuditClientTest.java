package uk.gov.justice.services.audit.client;

import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.MetadataBuilder;
import uk.gov.justice.services.messaging.spi.DefaultJsonEnvelopeProvider;
import uk.gov.justice.services.test.utils.core.messaging.JsonEnvelopeBuilder;
import uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil;

import javax.json.JsonObject;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonObjects.jsonBuilderFactory;

@ExtendWith(MockitoExtension.class)
public class HybridAuditClientTest {

    @Mock
    private RemoteAuditClient remoteAuditClient;

    @Mock
    private AzureDataLakeServiceClient azureDataLakeServiceClient;

    @Mock
    private AuditEntryContentCreator auditEntryContentCreator;

    @Mock
    private AuditMetricsRecorder auditMetricsRecorder;

    @Mock
    private Logger logger;

    @InjectMocks
    private HybridAuditClient hybridAuditClient;

    private JsonEnvelope envelope;
    private String component;

    @BeforeEach
    public void setup() {
        final MetadataBuilder metadataBuilder = new DefaultJsonEnvelopeProvider().metadataBuilder().withName("test").withId(randomUUID());
        envelope = JsonEnvelopeBuilder.envelope().with(metadataBuilder).withPayloadOf("Fred Bloggs", "name").build();
        component = "test-component";
    }

    @Nested
    class AuditStorageTest {

        @Test
        public void whenEnabledShouldUploadAuditEntryToAzureStorage() throws Exception {
            final Timer.Sample artemisTimer = mock(Timer.Sample.class);
            final Timer.Sample dataLakeTimer = mock(Timer.Sample.class);
            ReflectionUtil.setField(hybridAuditClient, "auditStorageEnabled", "true");
            final JsonObject auditContentJson = jsonBuilderFactory.createObjectBuilder().add("timestamp", "2024-09-10T01:01:29.238Z").build();
            when(auditEntryContentCreator.create(envelope, component)).thenReturn(auditContentJson);
            when( auditMetricsRecorder.startTimer()).thenReturn(artemisTimer).thenReturn(dataLakeTimer);

            hybridAuditClient.auditEntry(envelope, component);

            verify(azureDataLakeServiceClient).uploadToStorage(argThat(dataLakeFileObject -> {
                assertThat(dataLakeFileObject.getFilePath(), is("2024/09/10"));
                assertTrue(dataLakeFileObject.getFileName().startsWith("2024-09-10T01:01:29.238Z-"));
                assertTrue(dataLakeFileObject.getFileName().endsWith(".json"));
                return true;
            }));
            verify(remoteAuditClient).auditEntry(envelope, component);
            verify(auditMetricsRecorder).recordArtemisSuccessLatency(artemisTimer);
            verify(auditMetricsRecorder).recordDataLakeSyncSuccessLatency(dataLakeTimer);
        }

        @Test
        public void whenArtemisEventSendFailsShouldIgnoreError() throws Exception {
            final Timer.Sample artemisTimer = mock(Timer.Sample.class);
            final Timer.Sample dataLakeTimer = mock(Timer.Sample.class);
            ReflectionUtil.setField(hybridAuditClient, "auditStorageEnabled", "true");
            final JsonObject auditContentJson = jsonBuilderFactory.createObjectBuilder().add("timestamp", "2024-09-10T01:01:29.238Z").build();
            when(auditEntryContentCreator.create(envelope, component)).thenReturn(auditContentJson);
            when( auditMetricsRecorder.startTimer()).thenReturn(artemisTimer).thenReturn(dataLakeTimer);
            doThrow(new RuntimeException()).when(remoteAuditClient).auditEntry(envelope, component);

            hybridAuditClient.auditEntry(envelope, component);

            verify(azureDataLakeServiceClient).uploadToStorage(any());
            verify(logger).error(contains("Failed to send audit entry event for"), any(Exception.class));
            verify(auditMetricsRecorder).recordArtemisFailureLatency(artemisTimer);
            verify(auditMetricsRecorder).recordDataLakeSyncSuccessLatency(dataLakeTimer);
        }

        @Test
        public void whenDisabledShouldNotUploadAuditEntryToAzureStorage() {
            final Timer.Sample artemisTimer = mock(Timer.Sample.class);
            ReflectionUtil.setField(hybridAuditClient, "auditStorageEnabled", "false");
            when( auditMetricsRecorder.startTimer()).thenReturn(artemisTimer);

            hybridAuditClient.auditEntry(envelope, component);

            verifyNoInteractions(azureDataLakeServiceClient);
            verify(remoteAuditClient).auditEntry(envelope, component);
            verify(auditMetricsRecorder).recordArtemisSuccessLatency(artemisTimer);
            verify(auditMetricsRecorder, times(0)).recordDataLakeSyncSuccessLatency(any());
        }

        @Test
        public void shouldLogErrorWhenUploadAuditEntryToAzureStorageFails() throws Exception {
            final Timer.Sample artemisTimer = mock(Timer.Sample.class);
            final Timer.Sample dataLakeTimer = mock(Timer.Sample.class);
            ReflectionUtil.setField(hybridAuditClient, "auditStorageEnabled", "true");
            final JsonObject auditContentJson = jsonBuilderFactory.createObjectBuilder().add("timestamp", "2024-09-10T01:01:29.238Z").build();
            when(auditEntryContentCreator.create(envelope, component)).thenReturn(auditContentJson);
            doThrow(new RuntimeException("Test exception")).when(azureDataLakeServiceClient).uploadToStorage(any(DataLakeFileObject.class));
            when( auditMetricsRecorder.startTimer()).thenReturn(artemisTimer).thenReturn(dataLakeTimer);

            hybridAuditClient.auditEntry(envelope, component);

            verify(logger).error(contains("Error while storing audit event via storage"), any(Throwable.class));
            verify(auditMetricsRecorder).recordArtemisSuccessLatency(artemisTimer);
            verify(auditMetricsRecorder).recordDataLakeSyncFailureLatency(any());
        }
    }
}
