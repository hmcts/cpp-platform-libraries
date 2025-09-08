package uk.gov.moj.cpp.platform.test.utils.reflection;

import uk.gov.moj.cpp.platform.test.exception.TestUtilException;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static void setField(final Object object, final String fieldName, final Object fieldValue) {
        final Field field = getField(object.getClass(), fieldName);

        try {
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (final IllegalAccessException var5) {
            throw new TestUtilException(String.format("Unable to access field '%s' on class %s", fieldName, object.getClass().getName()), var5);
        }
    }

    public static Field getField(final Class<?> aClass, final String fieldName) {
        for (Class currentClass = aClass; currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
            if (hasField(fieldName, currentClass)) {
                try {
                    return currentClass.getDeclaredField(fieldName);
                } catch (final NoSuchFieldException var4) {
                    ;
                }
            }
        }

        throw new TestUtilException(String.format("No field named '%s' found on class %s", fieldName, aClass.getName()));
    }

    private static boolean hasField(final String fieldName, final Class<?> aClass) {
        final Field[] declaredFields = aClass.getDeclaredFields();
        return declaredFields == null ? false : Arrays.stream(declaredFields).anyMatch((field) -> field.getName().equals(fieldName));
    }
}
