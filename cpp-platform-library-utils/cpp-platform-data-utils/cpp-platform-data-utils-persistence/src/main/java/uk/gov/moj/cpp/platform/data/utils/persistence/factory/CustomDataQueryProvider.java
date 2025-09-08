package uk.gov.moj.cpp.platform.data.utils.persistence.factory;

import java.util.Optional;

/**
 * In case the {@link DataQueryFactory } is not enough and a custom query are needed, just implement
 * this interface and the class will be automatically be picked up and used to override the default
 * query when needed.
 *
 * A sample implementation is the CustomDataQueryProviderImpl in mireportdata
 *
 */
public interface CustomDataQueryProvider {

    /**
     * Gets a custom query query to use for fetching data instead. Note that you can have multiple
     * queries defined in an implementation, and the one returned will depend on the @tableName
     * parameter.
     *
     * @param tableName the table name or key identifying the custom query.
     * @return an optional query string
     */
    Optional<String> getCustomQuery(String tableName);
}
