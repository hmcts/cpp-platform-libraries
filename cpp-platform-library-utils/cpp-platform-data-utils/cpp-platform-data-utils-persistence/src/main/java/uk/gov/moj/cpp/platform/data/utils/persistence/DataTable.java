package uk.gov.moj.cpp.platform.data.utils.persistence;


import java.util.List;

public class DataTable {
    private final String name;
    private final List<List<Object>> data;
    private final String[] columnNames;

    public DataTable(final String name, final String[] columns, final List<List<Object>> data) {
        this.name = name;
        this.data = data;
        this.columnNames = columns;
    }

    public String getName() {
        return name;
    }

    public List<List<Object>> getData() {
        return data;
    }

    public String[] getColumnNames() {
        return columnNames;
    }
}
