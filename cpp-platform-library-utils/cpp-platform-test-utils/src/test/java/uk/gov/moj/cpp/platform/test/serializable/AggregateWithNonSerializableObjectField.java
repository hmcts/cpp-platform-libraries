package uk.gov.moj.cpp.platform.test.serializable;

import uk.gov.justice.domain.aggregate.Aggregate;

public class AggregateWithNonSerializableObjectField implements Aggregate {

    private NonSerializableClass nonSerializableClass;

    @Override
    public Object apply(final Object event) {
        return null;
    }
}
