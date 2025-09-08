package uk.gov.moj.cpp.unifiedsearch.test.util.ingest;

public class ElasticSearchTestHostProvider {

    public static final String ELASTICSEARCH_URI = "ELASTICSEARCH_URI";

    private ElasticSearchTestHostProvider() {
    }

    public static String getUri() {
        return System.getProperty(ELASTICSEARCH_URI, "http://localhost:9200");
    }

    public static String getAdminUserName(final String adminUserNameKey, final String defaultAdminUserName) {
        return System.getProperty(adminUserNameKey, defaultAdminUserName);
    }

    public static String getAdminPassword(final String adminPasswordKey, final String defaultAdminPassword) {
        return System.getProperty(adminPasswordKey, defaultAdminPassword);
    }

    public static String getWriterName(final String writerUserNameKey, final String defaultWriterUserName) {
        return System.getProperty(writerUserNameKey, defaultWriterUserName);
    }

    public static String getWriterPassword(final String writerPasswordKey, final String defaultWriterPassword) {
        return System.getProperty(writerPasswordKey, defaultWriterPassword);
    }
}