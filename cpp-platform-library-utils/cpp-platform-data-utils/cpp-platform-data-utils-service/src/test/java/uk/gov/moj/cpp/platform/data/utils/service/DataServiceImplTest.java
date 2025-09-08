package uk.gov.moj.cpp.platform.data.utils.service;


import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.moj.cpp.platform.data.utils.persistence.DataTable;
import uk.gov.moj.cpp.platform.data.utils.persistence.repository.DataRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DataServiceImplTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private DataServiceImpl dataService;
    private LocalDate toDate;
    private LocalDate fromDate;
    private String tableName1;
    private String tableName2;
    private String tableName3;

    private DataTable dataTable1;
    private DataTable dataTable2;
    private DataTable dataTable3;


    @BeforeEach
    public void setUp() throws Exception {
        fromDate = now();
        toDate = now();
        tableName1 = "tableName1";
        tableName2 = "tableName2";
        tableName3 = "tableName3";

        dataTable1 = new DataTable(tableName1, new String[0], emptyList());
        dataTable2 = new DataTable(tableName2, new String[0], emptyList());
        dataTable3 = new DataTable(tableName3, new String[0], emptyList());
    }

    @Test
    public void testGetTableData() throws Exception {
        when(dataRepository.getTableData(eq(tableName1), anySet(), any(), any())).thenReturn(dataTable1);

        final DataTable actual = dataService.getTableData(tableName1, fromDate, toDate);

        verify(dataRepository).getTableData(tableName1, emptySet(), fromDate, toDate);
        assertThat(actual, equalTo(dataTable1));
    }

    @Test
    public void testGetTableDataWithoutDateRange() throws Exception {
        when(dataRepository.getTableData(eq(tableName3), anySet(), any(), any())).thenReturn(dataTable3);

        final DataTable actual = dataService.getTableData(tableName3);

        verify(dataRepository).getTableData(tableName3, emptySet(), null, null);
        assertThat(actual, equalTo(dataTable3));
    }

    @Test
    public void testGetTableDataWithColumnNames() throws Exception {
        final Set<String> columns = newHashSet("column1", "column2");
        when(dataRepository.getTableData(eq(tableName1), anySet(), any(), any())).thenReturn(dataTable1);

        final DataTable actual = dataService.getTableData(tableName1, columns, fromDate, toDate);

        verify(dataRepository).getTableData(tableName1, columns, fromDate, toDate);
        assertThat(actual, equalTo(dataTable1));
    }

    @Test
    public void testGetTableDataWithMultipleTables() throws Exception {
        final Set<String> tables = newHashSet(tableName1, tableName2);
        when(dataRepository.getTableData(eq(tableName1), anySet(), any(), any())).thenReturn(dataTable1);
        when(dataRepository.getTableData(eq(tableName2), anySet(), any(), any())).thenReturn(dataTable2);

        final Set<DataTable> actual = dataService.getTableData(tables, fromDate, toDate);

        verify(dataRepository).getTableData(tableName1, emptySet(), fromDate, toDate);
        verify(dataRepository).getTableData(tableName2, emptySet(), fromDate, toDate);
        assertThat(actual, equalTo(ImmutableSet.of(dataTable1, dataTable2)));
    }

    @Test
    public void testGetTableDataWithMultipleTablesAndColumns() throws Exception {
        final HashSet<String> columnSet1 = newHashSet("column1", "column2");
        final HashSet<String> columnSet2 = newHashSet("column3", "column4");
        final Map<String, Set<String>> tablesAndColumns = ImmutableMap.of(tableName1, columnSet1, tableName2, columnSet2);
        when(dataRepository.getTableData(eq(tableName1), anySet(), any(), any())).thenReturn(dataTable1);
        when(dataRepository.getTableData(eq(tableName2), anySet(), any(), any())).thenReturn(dataTable2);

        final Set<DataTable> actual = dataService.getTableData(tablesAndColumns, fromDate, toDate);

        verify(dataRepository).getTableData(tableName1, columnSet1, fromDate, toDate);
        verify(dataRepository).getTableData(tableName2, columnSet2, fromDate, toDate);
        assertThat(actual, equalTo(ImmutableSet.of(dataTable1, dataTable2)));
    }

    @Test
    public void testGetTableDataWithMultipleTablesWithoutDateRange() throws Exception {
        final Set<String> tables = newHashSet(tableName1, tableName2, tableName3);
        when(dataRepository.getTableData(eq(tableName1), anySet(), any(), any())).thenReturn(dataTable1);
        when(dataRepository.getTableData(eq(tableName2), anySet(), any(), any())).thenReturn(dataTable2);
        when(dataRepository.getTableData(eq(tableName3), anySet(), any(), any())).thenReturn(dataTable3);

        final Set<DataTable> actual = dataService.getTableData(tables);

        verify(dataRepository).getTableData(tableName1, emptySet(), null, null);
        verify(dataRepository).getTableData(tableName2, emptySet(), null, null);
        verify(dataRepository).getTableData(tableName3, emptySet(), null, null);
        assertThat(actual, equalTo(ImmutableSet.of(dataTable1, dataTable2, dataTable3)));
    }

    @Test
    public void testGetZipFileName() throws Exception {
        final String expected = format("%s_%s.zip", "zip_file_name_prefix", getFileNameDatePart());

        final String actual = dataService.getZipFileName("zip_file_name_prefix");

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testGetCSVFileName() throws Exception {
        final String expected = format("%s_%s.csv", "csv_file_name_prefix", getFileNameDatePart());

        final String actual = dataService.getCSVFileName("csv_file_name_prefix");

        assertThat(actual, equalTo(expected));
    }

    private String getFileNameDatePart() {
        return now(UTC).format(ofPattern("uuMMdd"));
    }
}