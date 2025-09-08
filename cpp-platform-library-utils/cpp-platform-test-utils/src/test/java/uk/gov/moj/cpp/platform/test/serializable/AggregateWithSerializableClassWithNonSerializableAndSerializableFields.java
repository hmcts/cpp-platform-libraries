package uk.gov.moj.cpp.platform.test.serializable;

import uk.gov.justice.domain.aggregate.Aggregate;

import java.util.List;
import java.util.Map;

public class AggregateWithSerializableClassWithNonSerializableAndSerializableFields implements Aggregate {

    private SerializableClassWithNonSerializableField serializableClassWithNonSerializableField;
    private SerializableClassWithNonSerializableField serializableClassWithNonSerializableField2;
    private NonSerializableClass nonSerializableClass;
    private List<SerializableClassWithNonSerializableField> serializableClassWithNonSerializableFieldList;
    private Map<String, SerializableClassWithNonSerializableField> serializableClassWithNonSerializableFieldMap;
    private Map<String, List<SerializableClassWithNonSerializableField>> serializableClassWithNonSerializableFieldMap2;
    private Map<String, List<SerializableAggregate>> stringListMap;
    private List<SerializableAggregate> serializableAggregates;
    private String regularStringField;

    @Override
    public Object apply(final Object event) {
        return null;
    }
}
