package uk.gov.moj.cpp.platform.data.utils.converter;


import uk.gov.justice.services.common.converter.Converter;
import uk.gov.moj.cpp.platform.data.utils.persistence.DataTable;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.enterprise.inject.Typed;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

@Typed(DataTableToCsvConverter.class)
public class DataTableToCsvConverter implements Converter<DataTable, String> {
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withRecordSeparator("\n").withQuoteMode(QuoteMode.ALL);

    @Override
    public String convert(final DataTable source) {
        final StringWriter stringWriter = new StringWriter();
        try {
            final CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSV_FORMAT.withHeader(source.getColumnNames()));
            for (final List<Object> row : source.getData()) {
                transformRow(row, source.getColumnNames());
                csvPrinter.printRecord(row);
            }
            return stringWriter.toString();
        } catch (IOException e) {
            throw new CsvConversionException("Failed to convert data to CSV ", e);
        }
    }

    /**
     * Override this method if you need to do any bespoke transformation before writting the CSV
     */
    protected void transformRow(final List<Object> row, final String[] columnNames) throws IOException {
        //
    }
}
