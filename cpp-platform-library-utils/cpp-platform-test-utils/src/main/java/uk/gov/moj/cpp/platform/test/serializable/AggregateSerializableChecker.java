package uk.gov.moj.cpp.platform.test.serializable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import uk.gov.justice.domain.aggregate.Aggregate;

import java.io.Serializable;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;

/**
 * Checks whether {@link Aggregate} classes within a given package are serializable.
 */
public class AggregateSerializableChecker {

    /**
     * Checks whether {@link Aggregate} classes within a given package are serializable.
     *
     * @param packageName - the location to look for aggregate classes. Throws an error if none
     *                    found.
     */
    public void checkAggregatesIn(final String packageName) {
        final List<Class<?>> aggregateClasses = getAggregateClassesWithinPackage(packageName);

        if (aggregateClasses.isEmpty()) {
            fail("No aggregate classes found in package: " + packageName);
        }

        for (final Class aggregate : aggregateClasses) {
            final List<String> nonSerializableFields = isSerializable(aggregate);

            final String results = nonSerializableFields.stream().collect(Collectors.joining(System.lineSeparator() + "- "));
            final String message = "Expected 0 non-serialiazable fields but there are " + nonSerializableFields.size() + " in Class '" + aggregate.getSimpleName() + "'. The following fields are non-serializable:" + System.lineSeparator() + "- " + results;
            assertThat(message, nonSerializableFields.size(), is(0));
        }
    }

    public List<String> isSerializable(final Class aggregate) {

        final Map<String, Collection<Field>> invalidFieldsMap = findInvalidFieldMapForClass(aggregate);

        final List<String> invalidFields = convertMapToErrorFieldStrings(invalidFieldsMap);

        return invalidFields;
    }

    private Map<String, Collection<Field>> findInvalidFieldMapForClass(final Class clazz) {
        return findInvalidFieldMapForClass(clazz, clazz.getSimpleName());
    }

    /**
     * For a given class, recursively examines the fields to check they are serializable.
     *
     * @param clazz     - the class that is to be checked for serialization compatibility.
     * @param className - the friendly class name use to build up the map. This is used to build up
     *                  the object graph for sub-classes.
     * @return a map of all classes that have invalid fields and a list of containing fields.
     */
    private Map<String, Collection<Field>> findInvalidFieldMapForClass(final Class clazz, final String className) {
        final Collection<Field> invalidFields = new ArrayList<>();
        final Map<String, Collection<Field>> invalidFieldsMap = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (!isSpecialField(field)) {
                if (isCollectionField(field)) {
                    final Set<Class<?>> genericClassTypes = getGenericTypeFromCollectionField(field);

                    for (final Class<?> c : genericClassTypes) {

                        if (isNonSerializable(c)) {
                            invalidFields.add(field);
                        }
                        final Map<String, Collection<Field>> invalidFieldMapForClass = findInvalidFieldMapForClass(c, className + "." + field.getName() + "<" + c.getSimpleName() + ">");

                        if (!invalidFieldMapForClass.isEmpty()) {
                            invalidFieldsMap.putAll(invalidFieldMapForClass);
                        }
                    }
                } else {
                    if (regularField(field)) {
                        final List<Class> interfaces = getInterfacesOf(field.getType());

                        if (!interfaces.contains(Serializable.class)) {
                            invalidFields.add(field);
                        }

                        final Map<String, Collection<Field>> invalidFieldMapForClass = findInvalidFieldMapForClass(field.getType());

                        if (!invalidFieldMapForClass.isEmpty()) {
                            invalidFieldsMap.putAll(invalidFieldMapForClass);
                        }
                    }
                }
            }
        }
        if (!invalidFields.isEmpty()) {
            invalidFieldsMap.put(className, invalidFields);
        }
        return invalidFieldsMap;
    }

    private boolean isNonSerializable(final Class<?> c) {
        final List<Class> interfaces = getInterfacesOf(c);
        return !interfaces.contains(Serializable.class);
    }

    /**
     * Skip testing of serialVersionUID and Logger fields
     *
     * @param field - the field to check.
     * @return TRUE if the field is one of the special fields that should be ignored.
     */
    private boolean isSpecialField(final Field field) {
        return field.getType().equals(Logger.class) || "serialVersionUID".equalsIgnoreCase(field.getName()) || isConstant(field) || isTransient(field);
    }

    private boolean isConstant(final Field field) {
        final int modifiers = field.getModifiers();
        return (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }

    /**
     * Non primitive or enum fields that need to be checked.
     *
     * @param field - the field to be checked.
     * @return TRUE if the field needs to be checked for serialization compatibility.
     */
    private boolean regularField(final Field field) {
        return !(isEnum(field) || field.isEnumConstant() || field.getType().isPrimitive());
    }

    private boolean isTransient(final Field field) {
        return Modifier.isTransient(field.getModifiers());
    }

    /**
     * Whether the field is a collection and therefore needs to treated differently to examine the
     * generic type and not the collection class itself.
     *
     * @param field - the field to be checked.
     * @return TRUE if the field is a collection.
     */
    private boolean isCollectionField(final Field field) {
        return Collection.class.isAssignableFrom(field.getType()) || Map.class.equals(field.getType());
    }

    /**
     * Extracts the Generic class type of a collection field.
     *
     * @param field - the collection field to retrieve the generic class from.
     * @return the generic class associated with the collection.
     */
    private Set<Class<?>> getGenericTypeFromCollectionField(final Field field) {
        final Set<Class<?>> genericTypes = getGenericTypes(field.getGenericType());

        return genericTypes;
    }

    private Set<Class<?>> getGenericTypes(final Type type) {
        final Set<Class<?>> genericTypes = new HashSet<>();
        final Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();

        for (final Type actualType : actualTypeArguments) {
            if (actualType instanceof ParameterizedType) {
                genericTypes.addAll(getGenericTypes(actualType));
            } else if (actualType instanceof TypeVariable) {
                AnnotatedType[] declaredAnnotations = ((TypeVariable) actualType).getAnnotatedBounds();
                for (AnnotatedType annotatedType : declaredAnnotations) {
                    genericTypes.add((Class<?>) annotatedType.getType());
                }
            } else {
                genericTypes.add((Class<?>) actualType);
            }
        }
        return genericTypes;
    }

    /**
     * Checks whether a field is a enum field and excluded from serialization checked (all are).
     *
     * @param field - the field to be checked.
     * @return TRUE if the field is an enum.
     */
    private boolean isEnum(final Field field) {
        final Class<?> fieldType = field.getType();
        return fieldType instanceof Class && fieldType.isEnum();
    }

    /**
     * Extracts all interfaces of a class (including it's parent(s)).
     *
     * @param clazz - the class to check.
     * @return a list of interface classes the provided class implements.
     */
    private List<Class> getInterfacesOf(Class clazz) {
        final List<Class> interfaces = new ArrayList<>();
        getClassesAndInterfaces(interfaces, clazz);
        return interfaces;
    }


    private void getClassesAndInterfaces(final List<Class> interfaces, final Class clazz) {
        Class[] clazzInterfaces = clazz.getInterfaces();
        if(Objects.isNull(clazzInterfaces)){
            return;
        }
        for (Class class1 : clazzInterfaces) {
            interfaces.add(class1);
            getClassesAndInterfaces(interfaces, class1);
        }
        Class superclass = clazz.getSuperclass();
        if(Objects.isNull(superclass)){
            return;
        }
        interfaces.add(superclass);
        getClassesAndInterfaces(interfaces, superclass);
    }

    /**
     * Converts the map of class to list of fields into a set of individual results for easier
     * reporting.
     *
     * @param invalidFieldsMap - the map of classes and the associated list of non-serializable
     *                         fields.
     * @return a list of invalid fields, fully qualified with the class name.
     */
    private List<String> convertMapToErrorFieldStrings(final Map<String, Collection<Field>> invalidFieldsMap) {
        final List<String> invalidFields = new ArrayList<>();
        for (Map.Entry entry : invalidFieldsMap.entrySet()) {
            final List<Field> fields = (List<Field>) entry.getValue();
            for (Field f : fields) {
                if (!f.getType().getSimpleName().equalsIgnoreCase("Object")) {
                    invalidFields.add(entry.getKey() + "." + f.getName() + " (Type: " + f.getType().getSimpleName() + ")");
                }
            }
        }

        Collections.sort(invalidFields);
        return invalidFields;
    }

    /**
     * Finds all Aggregate classes within the specified package.
     *
     * @param packageName - the package to examine.
     * @return a list of Aggregate classes found.
     */
    private List<Class<?>> getAggregateClassesWithinPackage(final String packageName) {
        final Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));

        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);

        return allClasses.stream()
                .filter(x -> Arrays.asList(x.getInterfaces()).contains(Aggregate.class))
                .collect(Collectors.toList());
    }
}
