package uk.gov.justice.services.audit.client;

import uk.gov.justice.services.common.configuration.GlobalValue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.file.datalake.DataLakeDirectoryClient;
import com.azure.storage.file.datalake.DataLakeFileClient;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;
import org.slf4j.Logger;

public class AzureDataLakeServiceClient {

    @Inject
    @GlobalValue(key = "azureDataLakeAccountName", defaultValue = "")
    private String accountName;

    @Inject
    @GlobalValue(key = "azureDataLakeAccountKey", defaultValue = "")
    private String accountKey;

    @Inject
    @GlobalValue(key = "azureDataLakeContainerName", defaultValue = "")
    private String containerName;

    @Inject
    @GlobalValue(key = "azureDataLakeContainerType", defaultValue = "dfs")
    private String containerType;

    @Inject
    Logger logger;

    public DataLakeFileSystemClient getDataLakeServiceClient() {

        final StorageSharedKeyCredential sharedKeyCredential = new StorageSharedKeyCredential(accountName, accountKey);

        final DataLakeServiceClientBuilder builder = new DataLakeServiceClientBuilder();

        builder.credential(sharedKeyCredential);

        builder.endpoint("https://" + accountName + "." + containerType + ".core.windows.net");

        final DataLakeServiceClient dataLakeServiceClient = builder.buildClient();

        return dataLakeServiceClient.getFileSystemClient(containerName);
    }

    public void uploadToStorage(final DataLakeFileObject envelopePayloadDLFObject) throws IOException {
        if (envelopePayloadDLFObject == null) {
            logger.error("Invalid input: DataLakeFileObject is null");
            return;
        }

        final DataLakeFileSystemClient fileSystemClient = getDataLakeServiceClient();
        final DataLakeDirectoryClient directoryClient = fileSystemClient.getDirectoryClient(envelopePayloadDLFObject.getFilePath());
        final DataLakeFileClient dataLakeFileClient = directoryClient.getFileClient(envelopePayloadDLFObject.getFileName());

        try (InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(envelopePayloadDLFObject.getBytes()))) {
            final long payloadSize = envelopePayloadDLFObject.getFileSize();
            dataLakeFileClient.upload(inputStream, payloadSize);
        }
    }
}
