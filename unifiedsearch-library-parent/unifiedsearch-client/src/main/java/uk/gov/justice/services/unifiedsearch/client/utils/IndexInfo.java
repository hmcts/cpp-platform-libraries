package uk.gov.justice.services.unifiedsearch.client.utils;

import static uk.gov.justice.services.unifiedsearch.client.utils.IndexFileNameUtils.generateIndexJsonSchemaFilePath;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum IndexInfo {

    CRIME_CASE("crime_case_index", "read.user", "write.user"),
    CPS_CASE("cps_case_index", "cps.read.user", "cps.write.user");

    private static final Map<String, IndexInfo> indexLookup = new ConcurrentHashMap<>();

    static {
        Arrays.stream(IndexInfo.values()).forEach(indexInfo -> {
            indexLookup.put(indexInfo.getIndexName(), indexInfo);
        });
    }

    private String indexName;
    private String indexSchemaFile;
    private String readUser;
    private String writeUser;

    IndexInfo(
            final String indexName,
            final String readUser,
            final String writeUser) {

        this.indexName = indexName;
        this.indexSchemaFile = generateIndexJsonSchemaFilePath(indexName);
        this.readUser = readUser;
        this.writeUser = writeUser;
    }

    public static IndexInfo findByIndexName(final String indexName) {
        return indexLookup.get(indexName);
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexSchemaFile() {
        return indexSchemaFile;
    }

    public String getReadUser() {
        return readUser;
    }

    public String getWriteUser() {
        return writeUser;
    }
}
