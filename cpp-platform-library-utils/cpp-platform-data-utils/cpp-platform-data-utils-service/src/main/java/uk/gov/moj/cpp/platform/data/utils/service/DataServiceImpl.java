package uk.gov.moj.cpp.platform.data.utils.service;

import static java.util.Collections.emptySet;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.moj.cpp.platform.data.utils.persistence.DataTable;
import uk.gov.moj.cpp.platform.data.utils.persistence.repository.DataRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class DataServiceImpl implements DataService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("uuMMdd");
    private static final String CSV_FILE_FORMAT_PATTERN = "%s_%s.csv";
    private static final String ZIP_FILE_FORMAT_PATTERN = "%s_%s.zip";

    @Inject
    private DataRepository dataRepository;

    public DataServiceImpl setDataRepository(final DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        return this;
    }

    @Override
    public DataTable getTableData(final String tableName) {
        return dataRepository.getTableData(tableName, emptySet(), null, null);
    }

    @Override
    public DataTable getTableData(final String tableName, final LocalDate fromDate, final LocalDate toDate) {
        return dataRepository.getTableData(tableName, emptySet(), fromDate, toDate);
    }

    @Override
    public DataTable getTableData(final String tableName, final Set<String> includedColumns, final LocalDate fromDate, final LocalDate toDate) {
        return dataRepository.getTableData(tableName, includedColumns, fromDate, toDate);
    }

    @Override
    public Set<DataTable> getTableData(final Set<String> tableNames) {
        final Set<DataTable> result = new HashSet<>();
        for (final String tableName : tableNames) {
            result.add(dataRepository.getTableData(tableName, emptySet(), null, null));
        }
        return result;
    }

    @Override
    public String streamDataToTempFile(final String tableName, final LocalDate fromDate, final LocalDate toDate) {
        return dataRepository.streamDataToTempFile(tableName, emptySet(), fromDate, toDate);
    }

    @Override
    public Set<DataTable> getTableData(final Set<String> tableNames, final LocalDate fromDate, final LocalDate toDate) {
        final Set<DataTable> result = new HashSet<>();
        for (final String tableName : tableNames) {
            result.add(dataRepository.getTableData(tableName, emptySet(), fromDate, toDate));
        }
        return result;
    }

    @Override
    public Set<DataTable> getTableData(final Map<String, Set<String>> tableNamesAndIncludedColumns, final LocalDate fromDate, final LocalDate toDate) {
        final Set<DataTable> result = new HashSet<>();
        for (final Map.Entry<String, Set<String>> entry : tableNamesAndIncludedColumns.entrySet()) {
            final String tableName = entry.getKey();
            final Set<String> includedColumns = entry.getValue();
            result.add(dataRepository.getTableData(tableName, includedColumns, fromDate, toDate));
        }
        return result;
    }

    @Override
    public String getZipFileName(final String fileNamePrefix) {
        final String localDateStr = new UtcClock().now().format(DATE_FORMAT);
        return String.format(ZIP_FILE_FORMAT_PATTERN, fileNamePrefix, localDateStr);
    }

    @Override
    public String getCSVFileName(final String fileNamePrefix) {
        final String localDateStr = new UtcClock().now().format(DATE_FORMAT);
        return String.format(CSV_FILE_FORMAT_PATTERN, fileNamePrefix, localDateStr);
    }
}
