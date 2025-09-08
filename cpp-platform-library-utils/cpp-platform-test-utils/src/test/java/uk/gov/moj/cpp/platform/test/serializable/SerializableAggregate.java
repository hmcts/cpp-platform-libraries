package uk.gov.moj.cpp.platform.test.serializable;

import uk.gov.justice.domain.aggregate.Aggregate;
import uk.gov.justice.services.common.converter.JsonObjectToObjectConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public class SerializableAggregate implements Aggregate {

    private static final long serialVersionUID = 55558266765413272L;
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SerializableAggregate.class);
    private static final Integer integer = 10;
    private static final int anInt = 10;
    private static final DateTimeFormatter ZONE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private String serializableField;
    private Map<String, String> serializableMap;
    private Map<String, List<String>> serializableMapWithList;
    private List<String> serializableList;
    private transient JsonObjectToObjectConverter jsonObjectToObjectConverter;

    @Override
    public Object apply(final Object event) {
        return null;
    }
}
