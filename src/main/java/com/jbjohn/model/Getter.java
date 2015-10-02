package com.jbjohn.model;

import com.jbjohn.utils.Generic;
import com.jbjohn.utils.Predicate;

import java.util.*;
/**
 */
public class Getter {

    private static Object search(Object map, String key) {
        String predicate = Generic.getPredicate(key);

        if (map instanceof HashMap) {
            return search((HashMap<String, Object>) map, key);
        }
        if (map instanceof ArrayList) {
            return search((ArrayList) map, key, predicate);
        }
        if (map instanceof Collection) {
            search((Collection<Object>) map);
        }
        return map;
    }

    private static Object search(HashMap<String, Object> map, String key) {
        if (key.equals("*")) {
            return map.values();
        }
        return map.get(key);
    }

    private static Object search(ArrayList map, String key, String predicate) {
        if (key.equals("*")) {
            return map;
        }
        if (!predicate.equals("")) {
            return Predicate.process(map, predicate);
        }
        int index = Generic.getIntValue(key, -1);
        if (index > -1) {
            return map.get(index);
        }
        ArrayList response = new ArrayList();
        for (Object item : map) {
            Map<String, Object> itemMap = (Map<String, Object>) item;
            if (itemMap.containsKey(key)) {
                response.add(itemMap.get(key));
            }
        }
        return response;
    }

    private static Object search(Collection map) {
        ArrayList<Object> response = new ArrayList<Object>();
        for (Object item : map) {
            response.add(item);
        }
        return response.get(0);
    }

    public static Object searchByPath(Object map, String path) {

        path = Generic.trimPath(path);

        List<String> stringList = Generic.getKeyList(path);
        if (stringList.size() > 1) {
            String key = Generic.trimKey(stringList.get(0));
            map = search(map, key);
            path = Generic.newKey(stringList);
        } else {
            path = Generic.trimKey(path);
            return search(map, path);
        }
        return searchByPath(map, path);
    }
}
