package uk.gov.moj.cpp.unifiedsearch.test.util.constant;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.cps.CaseDocument;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum IndexInfo {

    CRIME_CASE("ELASTICSEARCH_ADMIN_USERNAME", "devadmin", "ELASTICSEARCH_ADMIN_PASSWORD", "Devadminpass", "ELASTICSEARCH_WRITER_USERNAME", "dev2", "ELASTICSEARCH_WRITER_PASSWORD", "Dev2pass", "crime_case_index", uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.CaseDocument.class),
    CPS_CASE("CPSSEARCH_ADMIN_USERNAME", "devadmin", "CPSSEARCH_ADMIN_PASSWORD", "Devadminpass", "CPSSEARCH_WRITER_USERNAME", "cpsdev2", "CPSSEARCH_WRITER_PASSWORD", "cpsDev2pass", "cps_case_index", CaseDocument.class);

    private static final Map<String, IndexInfo> lookup = new ConcurrentHashMap<>();

    private static final Map<Class, IndexInfo> ingesterClassLookup = new ConcurrentHashMap<>();

    static {
        Arrays.stream(IndexInfo.values()).forEach(indexInfo -> {
            lookup.put(indexInfo.getIndexName(), indexInfo);
            ingesterClassLookup.put(indexInfo.getIngesterClazz(), indexInfo);
        });
    }

    private String adminUserNameKey;

    private String defaultAdminUserName;

    private String adminPasswordKey;

    private String defaultAdminPassword;

    private String writerUserNameKey;

    private String defaultWriterUserName;

    private String writerPasswordKey;

    private String defaultWriterPassword;

    private String indexName;

    private String indexSourceFile;

    private Class ingesterClazz;

    @SuppressWarnings("squid:S107")
    IndexInfo(final String adminUserNameKey,
              final String defaultAdminUserName,
              final String adminPasswordKey,
              final String defaultAdminPassword,
              final String writerUserNameKey,
              final String defaultWriterUserName,
              final String writerPasswordKey,
              final String defaultWriterPassword,
              final String indexName,
              final Class ingesterClazz) {
        this.adminUserNameKey = adminUserNameKey;
        this.defaultAdminUserName = defaultAdminUserName;
        this.adminPasswordKey = adminPasswordKey;
        this.defaultAdminPassword = defaultAdminPassword;
        this.writerUserNameKey = writerUserNameKey;
        this.defaultWriterUserName = defaultWriterUserName;
        this.writerPasswordKey = writerPasswordKey;
        this.defaultWriterPassword = defaultWriterPassword;
        this.indexName = indexName;
        this.indexSourceFile = indexName + ".json";
        this.ingesterClazz = ingesterClazz;
    }

    public String getAdminUserNameKey() {
        return adminUserNameKey;
    }

    public String getDefaultAdminUserName() {
        return defaultAdminUserName;
    }

    public String getAdminPasswordKey() {
        return adminPasswordKey;
    }

    public String getDefaultAdminPassword() {
        return defaultAdminPassword;
    }

    public String getWriterUserNameKey() {
        return writerUserNameKey;
    }

    public String getDefaultWriterUserName() {
        return defaultWriterUserName;
    }

    public String getWriterPasswordKey() {
        return writerPasswordKey;
    }

    public String getDefaultWriterPassword() {
        return defaultWriterPassword;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexSourceFile() {
        return indexSourceFile;
    }

    public Class getIngesterClazz() {
        return ingesterClazz;
    }

    public static IndexInfo findByIndexName(final String indexName) {
        return lookup.get(indexName);
    }

    public static IndexInfo findByIngesterClazz(final Object object) {
        return ingesterClassLookup.get(object.getClass());
    }
}
