package uk.gov.moj.cpp.systemidmapper.client;

import static uk.gov.moj.cpp.systemidmapper.client.ResultCode.OK;

import java.util.Optional;
import java.util.UUID;

public class AdditionResponse {
    private final UUID mappingId;
    private final ResultCode code;
    private final Optional<String> errorMessage;

    public AdditionResponse(final UUID mappingId, final ResultCode code, final Optional<String> errorMessage) {
        this.mappingId = mappingId;
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public UUID mappingId() {
        return mappingId;
    }

    public ResultCode code() {
        return code;
    }

    public Optional<String> errorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return code == OK;
    }
}
