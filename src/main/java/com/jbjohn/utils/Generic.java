package com.jbjohn.utils;

import java.util.Arrays;
import java.util.List;

/**
 */
public class Generic {

    public static String trimPath(String path) {
        if (path.startsWith("$.")) {
            path = path.substring(2);
        }
        return path;
    }

    public static String trimKey(String key) {
        if (key.startsWith("[") && key.endsWith("]")) {
            key = key.replace("[", "").replace("]", "");
        }
        return key;
    }

    public static String newKey(List<String> stringList) {
        String newKey = "";
        int counter = 0;
        for (String string : stringList) {
            counter++;
            if (counter < 2) {
                continue;
            }
            if (newKey.length() > 1) {
                newKey += ".";
            }
            newKey += string;
        }
        return newKey;
    }

    public static List<String> getKeyList(String path) {
        return Arrays.asList(path.split("\\."));
    }

    public static String getPredicate(String key) {
        String predicate = "";
        if (key.startsWith("?")) {
            predicate = key.substring(1);
        }
        return predicate;
    }

    public static int getIntValue(String string, int defaultValue) {
        if (string.matches("^-?\\d+$")) {
            return Integer.parseInt(string);
        }
        return defaultValue;
    }

    public static int getIntValue(String string) {
        return getIntValue(string, 0);
    }
}
