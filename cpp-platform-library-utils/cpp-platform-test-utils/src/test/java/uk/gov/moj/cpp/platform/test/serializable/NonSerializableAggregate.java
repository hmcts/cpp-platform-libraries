package uk.gov.moj.cpp.platform.test.serializable;

import uk.gov.justice.domain.aggregate.Aggregate;

import java.util.Optional;

public class NonSerializableAggregate implements Aggregate {

    private Optional<String> nonSerializableField;

    @Override
    public Object apply(final Object event) {
        return null;
    }
}
