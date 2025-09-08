package uk.gov.moj.cpp.platform.test.serializable;


import java.io.Serializable;

public class SerializableClassWithNonSerializableField implements Serializable {

    private NonSerializableClass nonSerializableClass;

}
