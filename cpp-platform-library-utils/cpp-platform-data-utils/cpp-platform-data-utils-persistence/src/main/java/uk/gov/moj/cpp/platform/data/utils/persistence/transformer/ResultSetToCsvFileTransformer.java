package uk.gov.moj.cpp.platform.data.utils.persistence.transformer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetToCsvFileTransformer extends ResultSetTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetToCsvFileTransformer.class);

    public static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withRecordSeparator("\n").withQuoteMode(QuoteMode.ALL);
    private static final long COUNTER_MODULUS = 10000L;

    public String transform(final String tableName, final ResultSet resultSet) throws SQLException, IOException {

        final String[] columnNames = buildColumns(resultSet);

        final String prefix = "hmctsmi-" + tableName + "-";
        final File targetTempFile = File.createTempFile(prefix, ".csv");
        final String targetTempFilePath = targetTempFile.getAbsolutePath();
        targetTempFile.deleteOnExit();

        LOGGER.info("Starting CSV Export from table {} to file {}", tableName, targetTempFilePath);

        try (final FileWriter fileWriter = new FileWriter(targetTempFile);
             final Writer output = new BufferedWriter(fileWriter);
             final CSVPrinter csvPrinter = new CSVPrinter(output, CSV_FORMAT.withHeader(columnNames))) {

            long counter = 0;
            while (resultSet.next()) {
                final List<Object> newRow = new ArrayList<>(columnNames.length);
                for (int i = 1; i <= columnNames.length; i++) {
                    addColumnValue(resultSet.getObject(i), newRow);
                }

                csvPrinter.printRecord(newRow);
                if (counter % COUNTER_MODULUS == 0) {
                    LOGGER.info("CSV Export Progress: Exported {} rows from table {} to {} ...", counter, tableName, targetTempFilePath);
                }

                counter++;
            }

            LOGGER.info("CSV Export Success: Finished Exporting a total of {} rows from table {} to {}", counter, tableName, targetTempFilePath);
        }

        return targetTempFilePath;
    }

}
