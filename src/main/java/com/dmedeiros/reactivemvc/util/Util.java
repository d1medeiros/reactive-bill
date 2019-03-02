package com.dmedeiros.reactivemvc.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

public class Util {

    public static boolean containsInClassFields(String s, Class<?> c) {
        return List.of(getFieldName(c)).contains(s);
    }

    public static String[] getFieldName(Class<?> c) {
        Field[] declaredFields = c.getDeclaredFields();
        return Stream.of(declaredFields)
                .map(Field::getName)
                .toArray(String[]::new);
    }
}
