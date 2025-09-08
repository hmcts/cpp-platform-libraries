package uk.gov.moj.cpp.systemidmapper.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.moj.cpp.systemidmapper.client.ResultCode.CONFLICT;
import static uk.gov.moj.cpp.systemidmapper.client.ResultCode.OK;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class AdditionResponseTest {

    @Test
    public void shouldReturnTrueIfSuccessfulResultCode() {
        final AdditionResponse additionResponse = new AdditionResponse(null, OK, Optional.empty());
        assertThat(additionResponse.isSuccess(), is(true));
    }

    @Test
    public void shouldReturnFalseIfConflictResultCode() {
        final AdditionResponse additionResponse = new AdditionResponse(null, CONFLICT, Optional.empty());
        assertThat(additionResponse.isSuccess(), is(false));
    }
}