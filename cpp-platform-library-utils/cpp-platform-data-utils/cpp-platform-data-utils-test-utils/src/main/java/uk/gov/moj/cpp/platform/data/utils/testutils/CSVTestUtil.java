package uk.gov.moj.cpp.platform.data.utils.testutils;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.moj.cpp.platform.data.utils.testutils.FileUtil.getPayload;

import uk.gov.justice.services.common.util.UtcClock;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVTestUtil {

    private CSVTestUtil() {
    }

    /**
     * Checks tow CSVs
     *
     * @param expectedCsvContent We make sure that each line in this expectedCsvContent is in the
     *                           actualCsvContent no matter whether it's second row or n th row
     * @param excludeColumns     exclude this column from the data set before comparing
     */
    public static void assertCsv(final CSVFormat inputCsvFormat, final String expectedCsvContent, final String actualCsvContent, final String... excludeColumns) throws IOException {
        final CSVFormat csvFormat = inputCsvFormat.withFirstRecordAsHeader();

        final CSVParser expectedCsvParser = CSVParser.parse(expectedCsvContent, csvFormat);
        final Map<String, Integer> expectedHeaders = expectedCsvParser.getHeaderMap();

        final CSVParser actualCsvParser = CSVParser.parse(actualCsvContent, csvFormat);
        final Map<String, Integer> actualHeaders = actualCsvParser.getHeaderMap();

        assertThat(actualHeaders, is(expectedHeaders));

        final List<CSVRecord> expectedCsvRecords = expectedCsvParser.getRecords();
        final List<CSVRecord> actualCsvRecords = actualCsvParser.getRecords();

        final List<String> expectedLines = getLines(expectedCsvRecords, excludeColumns);
        final List<String> actualLines = getLines(actualCsvRecords, excludeColumns);


        /*Check that the actual csv contains all expected line.
        This is to help tests still pass in case more data has been added after the initial expected data was captured
         */
        for (final String expectedLine : expectedLines) {
            assertThat(actualLines, hasItem(expectedLine));
        }
    }


    /**
     * @param unzippedData   the key is the csv file name and the value is the CSV file content
     * @param tableName      table name that maps to this CSV
     * @param excludeColumns column to exclude during the check
     */
    public static void checkCsv(final Map<String, String> unzippedData, final CSVFormat inputCsvFormat, final String tableName, final String... excludeColumns) throws IOException {
        final String expectedResultCsvContent = getExpectedContentForTable(tableName);
        final String actualResultCsvContent = unzippedData.get(getCsvFileName(tableName));

        assertCsv(inputCsvFormat, expectedResultCsvContent, actualResultCsvContent, excludeColumns);
    }

    private static String getExpectedContentForTable(final String tableName) {
        return getPayload("csv/" + tableName + ".csv");
    }

    private static List<String> getLines(final List<CSVRecord> expectedCsvRecords, final String... fieldNameToExclude) {
       return expectedCsvRecords.stream().map(row ->
                recordToLine(row, fieldNameToExclude)
        ).collect(toList());
    }

    private static String recordToLine(final CSVRecord row, final String... fieldNameToExclude) {
        Collection<String> tempResult;
        if (fieldNameToExclude.length == 0) {
            tempResult = row.toMap().values();
        } else {
            final Map<String, String> map = row.toMap();
            for (final String fieldName : fieldNameToExclude) {
                map.remove(fieldName);
            }
            tempResult = map.values();
        }

        return String.join(",", tempResult);
    }

    private static String getCsvFileName(final String tableName) {
        final String datePart = new UtcClock().now().format(ofPattern("uuMMdd"));
        return String.format("%s_%s.csv", tableName, datePart);
    }
}
