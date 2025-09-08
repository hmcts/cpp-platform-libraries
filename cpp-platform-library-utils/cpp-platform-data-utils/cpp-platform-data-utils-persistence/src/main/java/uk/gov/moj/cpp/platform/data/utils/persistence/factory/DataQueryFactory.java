package uk.gov.moj.cpp.platform.data.utils.persistence.factory;

import static java.lang.Integer.max;
import static java.lang.String.format;

import uk.gov.moj.cpp.platform.data.utils.persistence.transformer.ResultSetToDataTableTransformer;
import uk.gov.moj.cpp.platform.data.utils.persistence.transformer.ResultSetTransformer;

import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class DataQueryFactory {
    private static final String ALL_COLUMNS = "*";
    private static final String QUERY_TEMPLATE = "select %s from %s where %s between ? and ?";

    private String lastModifiedColumnName;
    private int fetchSize;
    private ResultSetTransformer resultSetTransformer;


    @Inject
    protected Instance<CustomDataQueryProvider> customDataQueryProviderInstance;

    protected CustomDataQueryProvider customDataQueryProvider;

    @PostConstruct
    protected void initCustomQueryProvider() {
        if (customDataQueryProviderInstance != null && !customDataQueryProviderInstance.isUnsatisfied() && !customDataQueryProviderInstance.isAmbiguous()) {
            customDataQueryProvider = customDataQueryProviderInstance.get();
        }

        /* Set defaults for backward compatibility */
        if (lastModifiedColumnName == null) {
            lastModifiedColumnName = "last_modified_ts";
        }

        if (resultSetTransformer == null) {
            resultSetTransformer = new ResultSetToDataTableTransformer();
        }
    }

    /**
     * @param tableName      The table to build the query for
     * @param includeColumns list of columns to include in the query. If this is em empty set, all
     *                       columns are included. Note that the order of the column depends the
     *                       type of Set implementation used.
     * @return SQL query
     */
    public String buildQuery(final String tableName, final Set<String> includeColumns) {

        if (customDataQueryProvider != null) {
            final Optional<String> customQuery = customDataQueryProvider.getCustomQuery(tableName);
            if (customQuery.isPresent()) {
                return customQuery.get();
            }
        }

        return buildCommonQuery(tableName, includeColumns);
    }

    public DataQueryFactory setLastModifiedColumnName(final String lastModifiedColumnName) {
        this.lastModifiedColumnName = lastModifiedColumnName;
        return this;
    }

    public DataQueryFactory setFetchSize(final int fetchSize) {
        this.fetchSize = max(0, fetchSize);
        return this;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public ResultSetTransformer getResultSetTransformer() {
        return resultSetTransformer;
    }

    public DataQueryFactory setResultSetTransformer(final ResultSetTransformer resultSetTransformer) {
        this.resultSetTransformer = resultSetTransformer;
        return this;
    }

    private String buildCommonQuery(final String tableName, final Set<String> includeColumns) {
        String queryFields = ALL_COLUMNS;
        if (!includeColumns.isEmpty()) {
            queryFields = String.join(",", includeColumns);
        }

        return format(QUERY_TEMPLATE, queryFields, tableName, lastModifiedColumnName);
    }
}
