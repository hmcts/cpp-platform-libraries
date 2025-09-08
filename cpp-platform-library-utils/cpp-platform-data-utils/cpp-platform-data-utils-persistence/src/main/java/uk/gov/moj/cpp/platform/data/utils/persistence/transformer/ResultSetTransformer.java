package uk.gov.moj.cpp.platform.data.utils.persistence.transformer;

import static uk.gov.justice.services.common.converter.ZonedDateTimes.fromSqlTimestamp;

import uk.gov.justice.services.common.converter.ZonedDateTimes;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public abstract class ResultSetTransformer {

    public abstract Object transform(final String tableName, final ResultSet resultSet) throws SQLException, IOException;

    protected String[] buildColumns(final ResultSet resultSet) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int numberOfColumns = metaData.getColumnCount();
        final String[] columnNames = new String[numberOfColumns];
        for (int i = 0; i < numberOfColumns; i++) {
            columnNames[i] = metaData.getColumnLabel(i + 1).toLowerCase();
        }

        return columnNames;
    }

    protected void addColumnValue(final Object columnData, final List<Object> newRow) {
        if (columnData instanceof Timestamp) {
            final Timestamp timestamp = (Timestamp) columnData;
            newRow.add(ZonedDateTimes.toString(fromSqlTimestamp(timestamp)));
        } else {
            newRow.add(columnData);
        }
    }
}
