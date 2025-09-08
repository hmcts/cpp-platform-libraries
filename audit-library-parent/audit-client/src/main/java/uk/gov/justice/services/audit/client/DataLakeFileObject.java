package uk.gov.justice.services.audit.client;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.json.JsonObject;

public class DataLakeFileObject {


    private final String fileName;

    private final String filePath;

    private final byte[] bytes;

    private final long fileSize;

    public DataLakeFileObject(final JsonObject jsonObject) {
        final String timeStamp = getTimeStamp(jsonObject);
        this.fileName = timeStamp + "-" + UUID.randomUUID() + ".json";
        this.filePath = getFilePath(timeStamp);
        this.bytes = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
        this.fileSize = bytes.length;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getFileSize() {
        return fileSize;
    }

    private String getTimeStamp(final JsonObject jsonObject) {
        if (jsonObject == null || !jsonObject.containsKey("timestamp")) {
            return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        }
        return jsonObject.getString("timestamp");
    }

    private String getFilePath(final String timeStamp) {
        return timeStamp.split("T")[0].replace("-", "/");
    }
}
