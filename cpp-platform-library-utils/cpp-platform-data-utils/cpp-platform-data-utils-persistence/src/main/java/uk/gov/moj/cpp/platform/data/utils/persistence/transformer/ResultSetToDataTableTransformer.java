package uk.gov.moj.cpp.platform.data.utils.persistence.transformer;

import uk.gov.moj.cpp.platform.data.utils.persistence.DataTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetToDataTableTransformer extends ResultSetTransformer {

    @Override
    public DataTable transform(final String tableName, final ResultSet resultSet) throws SQLException {

        final String[] columnNames = buildColumns(resultSet);
        final List<List<Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            final List<Object> newRow = new ArrayList<>(columnNames.length);
            for (int i = 1; i <= columnNames.length; i++) {
                addColumnValue(resultSet.getObject(i), newRow);
            }
            rows.add(newRow);
        }
        return new DataTable(tableName, columnNames, rows);
    }

}
