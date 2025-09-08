package uk.gov.moj.cpp.platform.data.utils.persistence.repository;

import static java.time.LocalDate.now;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.platform.data.utils.persistence.DataTable;
import uk.gov.moj.cpp.platform.data.utils.persistence.factory.DataQueryFactory;
import uk.gov.moj.cpp.platform.data.utils.persistence.transformer.ResultSetTransformer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class DataRepositoryTest {

    @InjectMocks
    private DataRepository repository;

    @Mock
    private DataSource dataSource;

    @Mock
    private DataQueryFactory dataQueryFactory;

    @Mock
    private ResultSetTransformer resultSetTransformer;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private DataTable dataTable;

    private final String tableName = "table-name";

    @BeforeEach
    public void setup() throws SQLException {
        when(dataQueryFactory.buildQuery(any(), any())).thenReturn("QUERY");
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("QUERY")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(dataQueryFactory.getResultSetTransformer()).thenReturn(resultSetTransformer);
    }

    @Test
    public void shouldGetDataTable() throws IOException, SQLException {

        final LocalDate now = now();
        final OffsetDateTime from = now.atStartOfDay(UTC).toOffsetDateTime();
        final OffsetDateTime to = now.plusDays(1).atStartOfDay(UTC).toOffsetDateTime();

        when(resultSetTransformer.transform(tableName, resultSet)).thenReturn(dataTable);

        final DataTable actual = repository.getTableData(tableName, Collections.emptySet(), now, now);

        assertThat(actual, is(dataTable));
        verify(resultSetTransformer).transform(tableName, resultSet);
        verify(preparedStatement).setFetchSize(0);

        verify(preparedStatement).setObject(1, from);
        verify(preparedStatement).setObject(2, to);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verifyNoMoreInteractions(resultSetTransformer, preparedStatement);
    }

    @Test
    public void shouldStreamDataToTempFile() throws IOException, SQLException {
        final String streamedDataTempFilePath = "/tmp/some-random-file.csv";

        final LocalDate now = now();
        final OffsetDateTime from =  now.atStartOfDay(UTC).toOffsetDateTime();
        final OffsetDateTime to =  now.plusDays(1).atStartOfDay(UTC).toOffsetDateTime();

        when(resultSetTransformer.transform(tableName, resultSet)).thenReturn(streamedDataTempFilePath);

        final String actual = repository.streamDataToTempFile(tableName, Collections.emptySet(), now, now);

        assertThat(actual, is(streamedDataTempFilePath));

        verify(resultSetTransformer).transform(tableName, resultSet);
        verify(preparedStatement).setFetchSize(0);

        verify(preparedStatement).setObject(1, from);
        verify(preparedStatement).setObject(2, to);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verifyNoMoreInteractions(resultSetTransformer, preparedStatement);

    }

}