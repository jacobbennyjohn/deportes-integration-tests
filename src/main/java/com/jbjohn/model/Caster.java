package com.jbjohn.model;

import com.jbjohn.MapUtil;import com.jbjohn.model.common.InMap;
import com.jbjohn.utils.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class Caster extends InMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMap.class);
    private static Type type = Type.STRING;

    public static Object setByPath(Object map, String key, Type type) {
        Caster.type = type;
        key = Generic.trimPath(key);
        setOperation(new Caster());

        return setByPath(map, key);
    }

    @Override
    public Object getValue(Object value) {
        Object response = value;
        try {
            response = type.getValue(value);
        } catch (Exception e) {
            LOGGER.error("Exception getting value", e);
        }
        return response;
    }

    public enum Type {
        STRING("String") {
            @Override
            Object getValue(Object value) {
                Object response = value;
                if (!(value instanceof String)) {
                    response = String.valueOf(value);
                }
                return response;
            }
        },
        INTEGER("Integer") {
            @Override
            Object getValue(Object value) {
                Object response = value;
                if (!(value instanceof Integer) && (value instanceof String) && value.toString().matches("^-?\\d+$")) {
                    response = Integer.parseInt(value.toString());
                }
                return response;
            }
        },
        BOOLEAN("Boolean") {
            @Override
            Object getValue(Object value) {
                Object response = value;
                if (!(value instanceof Boolean) && (value instanceof String) && (value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false"))) {
                    response = Boolean.parseBoolean(value.toString());
                }
                return response;
            }
        },
        DOUBLE("Double") {
            @Override
            Object getValue(Object value) {
                Object response = value;
                if (!(value instanceof Double) && (value instanceof String) && value.toString().matches("^-?\\d+(?:[.]\\d+)$")) {
                    response = Double.parseDouble(value.toString());
                }
                return response;
            }
        };

        private String type;

        Type(String type) {
            this.type = type;
        }

        abstract Object getValue(Object value);
    }
}
