package com.jbjohn;

import com.jbjohn.model.Caster;
import com.jbjohn.model.Getter;
import com.jbjohn.model.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class MapUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapUtil.class);

    public static Object get(Object map, String key) {
        Object response = null;
        try {
            response = Getter.searchByPath(map, key);
        } catch (Exception e) {
            LOGGER.error("Exception in Get", e);
        }
        return response;
    }

    public static Object set(Object map, String key, Object value) {
        Object response = null;
        try {
            response = Setter.setByPath(map, key, value);
        } catch (Exception e) {
            LOGGER.error("Exception in Set", e);
        }
        return response;
    }

    public static Object parse(Object map, String key, Caster.Type type) {
        Object response = null;
        try {
            response = Caster.setByPath(map, key, type);
        } catch (Exception e) {
            LOGGER.error("Exception in Parse", e);
        }
        return response;
    }
}
