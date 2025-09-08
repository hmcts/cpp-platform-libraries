package uk.gov.moj.cpp.systemidmapper.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;


public class ResultCodeTest {

    @Test
    public void shouldProduceOKFrom200Code() throws Exception {
        assertThat(ResultCode.valueOf(200), is(ResultCode.OK));
    }

    @Test
    public void shouldProduceConflictFrom409Code() throws Exception {
        assertThat(ResultCode.valueOf(409), is(ResultCode.CONFLICT));
    }

    @Test
    public void shouldThrowExceptionForUnexpectedResponseCode() {

        final IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> ResultCode.valueOf(500));

        assertThat(illegalStateException.getMessage(), is("Unexpected response code: 500"));
    }

}