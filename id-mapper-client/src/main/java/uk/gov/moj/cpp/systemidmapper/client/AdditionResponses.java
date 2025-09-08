package uk.gov.moj.cpp.systemidmapper.client;

import java.util.Collections;
import java.util.List;

public class AdditionResponses {
    private final List<SystemIdMappings> systemIdMappings;

    public AdditionResponses(final List<SystemIdMappings> systemIdMappings) {
        this.systemIdMappings = systemIdMappings;
    }

    public List<SystemIdMappings> getSystemIdMappings() {
        return Collections.unmodifiableList(systemIdMappings);
    }
}
