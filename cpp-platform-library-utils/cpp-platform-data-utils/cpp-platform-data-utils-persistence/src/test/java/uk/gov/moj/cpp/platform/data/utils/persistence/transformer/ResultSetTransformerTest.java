package uk.gov.moj.cpp.platform.data.utils.persistence.transformer;

import static java.time.OffsetDateTime.parse;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResultSetTransformerTest {

    @Spy
    private ResultSetTransformer resultSetTransformer;

    @Test
    public void shouldFormatZonedDateTimeColumnValue() throws Exception {
        List<Object> row = new ArrayList<>();
        final String expectedIsoDateTimeString = "2018-07-27T21:06:21.738Z";
        final Instant instant = parse(expectedIsoDateTimeString).toInstant();
        final Timestamp timeStamp = Timestamp.from(instant);

        resultSetTransformer.addColumnValue(timeStamp, row);

        final String actualFormattedDate = (String) row.get(0);
        assertThat(actualFormattedDate, is(expectedIsoDateTimeString));
    }

    @Test
    public void shouldReturnSameStringIfColumnValueIsString() throws Exception {
        List<Object> row = new ArrayList<>();
        final String expectedString = "some string";

        resultSetTransformer.addColumnValue(expectedString, row);

        final String actualFormattedDate = (String) row.get(0);
        assertThat(actualFormattedDate, is(expectedString));
    }

    @Test
    public void shouldReturnNullIfColumnValueIsNull() throws Exception {
        List<Object> row = new ArrayList<>();
        resultSetTransformer.addColumnValue(null, row);
        assertThat(row.get(0), is(nullValue()));
    }
}
