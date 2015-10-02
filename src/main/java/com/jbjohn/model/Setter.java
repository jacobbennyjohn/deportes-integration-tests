package com.jbjohn.model;

import com.jbjohn.model.common.InMap;
import com.jbjohn.utils.Generic;

/**
 */
public class Setter extends InMap {

    private static Object value;

    public static Object setByPath(Object map, String key, Object value) {

        key = Generic.trimPath(key);
        Setter.value = value;
        setOperation(new Setter());

        return setByPath(map, key);
    }

    @Override
    public Object getValue(Object value) {
        return this.value;
    }
}
