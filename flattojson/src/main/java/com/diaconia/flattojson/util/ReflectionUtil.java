package com.diaconia.flattojson.util;
import java.lang.reflect.Field;

public class ReflectionUtil {

    public boolean hasProperty(Object obj, String propertyName) {
        Class<?> objClass = obj.getClass();
        for (Field field : objClass.getDeclaredFields()) {
            if (field.getName().equals(propertyName)) {
                return true;
            }
        }
        return false;
    }
}
