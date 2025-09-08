package uk.gov.moj.cpp.platform.data.utils.persistence.repository;

import static java.lang.String.format;
import static java.time.ZoneOffset.UTC;

import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;
import uk.gov.justice.services.jdbc.persistence.ViewStoreJdbcDataSourceProvider;
import uk.gov.moj.cpp.platform.data.utils.persistence.DataTable;
import uk.gov.moj.cpp.platform.data.utils.persistence.factory.DataQueryFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;


@ApplicationScoped
public class DataRepository {

    @Inject
    private ViewStoreJdbcDataSourceProvider viewStoreJdbcDataSourceProvider;

    @Inject
    private DataQueryFactory dataQueryFactory;

    private DataSource dataSource;

    @PostConstruct
    private void initialiseDataSource() {
        dataSource = viewStoreJdbcDataSourceProvider.getDataSource();
    }

    public DataRepository setDataQueryFactory(final DataQueryFactory dataQueryFactory) {
        this.dataQueryFactory = dataQueryFactory;
        return this;
    }

    public DataRepository setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    protected Object getData(final String tableName, final Set<String> includeColumns, final LocalDate fromDate, final LocalDate toDate) {

        final String query = dataQueryFactory.buildQuery(tableName, includeColumns);

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setFetchSize(this.dataQueryFactory.getFetchSize());

            // pg JDBC does not support passing ZonedDateTime: https://jdbc.postgresql.org/documentation/head/8-date-time.html
            ps.setObject(1, fromDate.atStartOfDay(UTC).toOffsetDateTime());
            ps.setObject(2, toDate.plusDays(1).atStartOfDay(UTC).toOffsetDateTime());

            try (final ResultSet resultSet = ps.executeQuery()) {
                return dataQueryFactory.getResultSetTransformer().transform(tableName, resultSet);
            }

        } catch (SQLException | IOException e) {
            throw new JdbcRepositoryException(format("Exception while getting table data for query: %s", query), e);
        }
    }

    public DataTable getTableData(final String tableName, final Set<String> includeColumns, final LocalDate fromDate, final LocalDate toDate) {
        return ((DataTable) getData(tableName, includeColumns, fromDate, toDate));
    }

    public String streamDataToTempFile(final String tableName, final Set<String> includeColumns, final LocalDate fromDate, final LocalDate toDate) {
        return ((String) getData(tableName, includeColumns, fromDate, toDate));
    }
}
