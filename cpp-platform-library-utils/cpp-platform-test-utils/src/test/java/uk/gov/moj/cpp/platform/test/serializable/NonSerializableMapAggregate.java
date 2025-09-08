package uk.gov.moj.cpp.platform.test.serializable;

import uk.gov.justice.domain.aggregate.Aggregate;

import java.util.List;
import java.util.Map;

public class NonSerializableMapAggregate implements Aggregate {

    private Map<String, List<NonSerializableClass>> nonSerializableMap;

    @Override
    public Object apply(final Object event) {
        return null;
    }
}
