package uk.gov.moj.cpp.platform.test.serializable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AggregateSerializableCheckerTest {

    private AggregateSerializableChecker aggregateSerializableChecker = new AggregateSerializableChecker();

    @Test
    public void shouldConfirmClassWithStringMemberVariableIsValid() {
        assertThat(aggregateSerializableChecker.isSerializable(SerializableAggregate.class).size(), is(0));
    }

    @Test
    public void shouldConfirmClassWithOptionalFieldIsNotValid() {
        assertThat(aggregateSerializableChecker.isSerializable(NonSerializableAggregate.class).size(), is(1));
    }

    @Test
    public void shouldConfirmClassWithNonSerializableReferencedObjectIsNotValid() {
        assertThat(aggregateSerializableChecker.isSerializable(AggregateWithNonSerializableObjectField.class).size(), is(2));
    }

    @Test
    public void shouldConfirmClassWithNonSerializableMapIsNotValid() {
        assertThat(aggregateSerializableChecker.isSerializable(NonSerializableMapAggregate.class).size(), is(2));
    }

    @Test
    public void shouldConfirmClassWithSerializableReferencedObjectWhichHasNonSerializableAndSerializableFieldsIsNotValid() {
        assertThat(aggregateSerializableChecker.isSerializable(AggregateWithSerializableClassWithNonSerializableAndSerializableFields.class).size(), is(6));
    }

}
