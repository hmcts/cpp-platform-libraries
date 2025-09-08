package uk.gov.moj.cpp.platform.azure.utils;

import static com.google.common.base.Stopwatch.createStarted;
import static com.microsoft.azure.storage.CloudStorageAccount.parse;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;

import com.google.common.base.Stopwatch;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AzureBlobClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureBlobClientService.class);
    private static final String INVALID_URI_ERROR_MSG = "Connection URI parse error";
    private static final String STORAGE_ERROR_MSG = "Error returned from azure service. Http code: %d and error code: %s";
    private CloudBlobContainer container = null;

    public void connect(final String containerName, final String storageConnectionString) {
        try {
            final CloudStorageAccount storageAccount = parse(storageConnectionString);
            final CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            container = blobClient.getContainerReference(containerName);
        } catch (InvalidKeyException ex) {
            throw new AzureBlobClientException("Invalid connection string", ex);
        } catch (URISyntaxException ex) {
            throw new AzureBlobClientException(INVALID_URI_ERROR_MSG, ex);
        } catch (StorageException ex) {
            throw new AzureBlobClientException(format(
                    STORAGE_ERROR_MSG, ex.getHttpStatusCode(), ex.getErrorCode()), ex);
        }

    }

    /**
     * Upload a file to Azure blob storage
     *
     * @param file                File to upload
     * @param fileSize            Size of file to upload
     * @param destinationFileName file name
     * @return void
     * @throws AzureBlobClientException
     */
    public void upload(final JsonObject payload, final String destinationFileName, final String containerName, final String storageConnectionString) {

        try {
            final Stopwatch stopwatch = createStarted();
            LOGGER.info("Connecting to azure blob storage on {}", now());
            connect(containerName, storageConnectionString);
            final CloudBlockBlob fileBlob = container.getBlockBlobReference(destinationFileName);
            LOGGER.info("Uploading {} file to azure blob storage on {}", destinationFileName, now());
            final byte[] bytes = payload.toString().getBytes();
            final long fileLength = bytes.length;
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            fileBlob.upload(byteArrayInputStream, fileLength);
            LOGGER.info(
                    "Total time taken for file upload is : {} : seconds",
                    stopwatch.elapsed(SECONDS));

        } catch (StorageException ex) {
            throw new AzureBlobClientException(format(
                    STORAGE_ERROR_MSG, ex.getHttpStatusCode(), ex.getErrorCode()), ex);
        } catch (URISyntaxException ex) {
            throw new AzureBlobClientException(INVALID_URI_ERROR_MSG, ex);
        } catch (IOException ex) {
            throw new AzureBlobClientException("Error while uploading file to azure blob storage", ex);
        }
    }

}
