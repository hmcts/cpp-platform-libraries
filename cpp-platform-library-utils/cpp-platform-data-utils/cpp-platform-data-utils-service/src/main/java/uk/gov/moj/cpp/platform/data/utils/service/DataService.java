package uk.gov.moj.cpp.platform.data.utils.service;


import uk.gov.moj.cpp.platform.data.utils.persistence.DataTable;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * Interface for loading data from DB as DataTable
 */
public interface DataService {

    /**
     * @param tableName the table to get data from. All columns are returned.
     * @return an DataTable representing the selected data
     */
    DataTable getTableData(final String tableName);

    /**
     * @param tableName the table to get data from. All columns are returned.
     * @param fromDate  fetch only items that have changed after this date.
     * @param toDate    fetch only items that have changed before this date.
     * @return an DataTable representing the selected data
     */
    DataTable getTableData(final String tableName, final LocalDate fromDate, final LocalDate toDate);


    /**
     * @param tableName       the table to get data from
     * @param includedColumns columns to include in data. Could be an empty set in which case all
     *                        columns are returned. Note that the order of the column depends the
     *                        type of Set implementation used.
     * @param fromDate        fetch only items that have changed after this date.
     * @param toDate          fetch only items that have changed before this date.
     * @return an DataTable representing the selected data
     */
    DataTable getTableData(final String tableName, final Set<String> includedColumns, final LocalDate fromDate, final LocalDate toDate);

    /**
     * @param tableNames A list of the tables to query. All columns are returned.
     * @return a set of DataTables representing the selected data.
     */
    Set<DataTable> getTableData(final Set<String> tableNames);

    /**
     * @param tableNamesAndIncludedColumns A map containing the tables to query as key and the set
     *                                     of columns to include as value. Columns set could be an
     *                                     empty set in which case all columns are returned.
     * @param from                         fetch only items that have changed after this date.
     * @param to                           fetch only items that have changed before this date.
     * @return a set of DataTables representing the selected data.
     */
    Set<DataTable> getTableData(final Map<String, Set<String>> tableNamesAndIncludedColumns, final LocalDate from, final LocalDate to);

    /**
     * @param tableNames A list of the tables to query. Note that all columns are returned.
     * @param from       fetch only items that have changed after this date.
     * @param to         fetch only items that have changed before this date.
     * @return a set of DataTables representing the selected data.
     */
    Set<DataTable> getTableData(final Set<String> tableNames, final LocalDate from, final LocalDate to);


    /**
     * @param fileNamePrefix File name prefix.
     * @return File name with date concatenated.
     */
    String getZipFileName(final String fileNamePrefix);

    /**
     * @param fileNamePrefix File name prefix.
     * @return File name with date concatenated.
     */
    String getCSVFileName(final String fileNamePrefix);


    /**
     * Stream data to a temp file
     *
     * @param tableName the table to get data from. All columns are returned.
     * @param fromDate  fetch only items that have changed after this date.
     * @param toDate    fetch only items that have changed before this date.
     * @return the absolute path to the temp file containing the CSV data
     */
    String streamDataToTempFile(final String tableName, final LocalDate fromDate, final LocalDate toDate);


}
