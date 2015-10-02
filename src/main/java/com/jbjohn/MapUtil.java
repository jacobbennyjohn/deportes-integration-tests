package com.jbjohn;

import com.jbjohn.model.Caster;
import com.jbjohn.model.Getter;
import com.jbjohn.model.Setter;

/**
 */
public class MapUtil {

    public static Object get(Object map, String key) {
        Object response = null;
        try {
            response = Getter.searchByPath(map, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Object set(Object map, String key, Object value) {
        Object response = null;
        try {
            response = Setter.setByPath(map, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Object parse(Object map, String key, Caster.Type type) {
        Object response = null;
        try {
            response = Caster.setByPath(map, key, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
