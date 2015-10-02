package com.jbjohn.model.common;

import com.jbjohn.utils.Generic;

import java.util.*;

/**
 */
public abstract class InMap {

    static InMap operation;

    public static void setOperation(InMap operation) {
        InMap.operation = operation;
    }

    public static Object setByPath(Object map, String key) {

        if (map instanceof HashMap) {
            return setByPath((HashMap<String, Object>) map, key);
        } else if (map instanceof ArrayList)  {
            return setByPath((ArrayList) map, key);
        }
        return map;
    }

    static Object setByPath(HashMap<String, Object> map, String path) {

        List<String> stringList = Generic.getKeyList(path);
        if (stringList.size() > 1) {
            String key = Generic.trimKey(stringList.get(0));
            String newKey = Generic.newKey(stringList);
            if (key.equals("*")) {
                HashMap<String, Object> tempMap = (HashMap<String, Object>) map.clone();
                Iterator it = tempMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    map.put((String) pair.getKey(), setByPath(map.get(pair.getKey()), newKey));
                    it.remove();
                }
            } else {
                if (map.get(key) instanceof HashMap) {
                    map.put(key, setByPath(map.get(key), newKey));
                }
                if (map.get(key) instanceof ArrayList) {
                    map.put(key, setByPath(map.get(key), newKey));
                }
            }
        } else {
            path = Generic.trimKey(path);
            if (path.equals("*")) {
                HashMap<String, Object> tempMap = (HashMap<String, Object>) map.clone();
                Iterator it = tempMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    map.put((String) pair.getKey(), operation.getValue(map.get(pair.getKey())));
                    it.remove();
                }
            } else {
                path = Generic.trimKey(path);
                map.put(path, operation.getValue(map.get(path)));
            }
        }
        return map;
    }

    static Object setByPath(ArrayList map, String path) {

        int index = 0;
        List<String> stringList = Generic.getKeyList(path);
        if (stringList.size() > 1) {
            String key = Generic.trimKey(stringList.get(0));
            String newKey = Generic.newKey(stringList);
            if (key.equals("*")) {
                int counter = 0;
                for (Object values : map) {
                    map.set(counter, setByPath(map.get(counter), newKey));
                    counter++;
                }
            } else {
                if (key.matches("^-?\\d+$")) {
                    index = Integer.parseInt(key);
                }
                if (map.get(index) instanceof HashMap) {
                    map.set(index, setByPath(map.get(index), newKey));
                }
                if (map.get(index) instanceof ArrayList) {
                    map.set(index, setByPath(map.get(index), newKey));
                }
            }
        } else {
            path = Generic.trimKey(path);
            if (path.equals("*")) {
                int counter = 0;
                for (Object values : map) {
                    map.set(counter, operation.getValue(map.get(counter)));
                    counter++;
                }
            } else {
                index = Integer.parseInt(path);
                map.set(index, operation.getValue(map.get(index)));
            }
        }

        return map;
    }

    public abstract Object getValue(Object value);
}
