package com.jbjohn.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 */
public class Predicate {

    public enum Operator {
        DEFAULT("  ") {
            @Override
            ArrayList apply(Map<String, Object> request, String key, String value, ArrayList response) {
                return response;
            }
        },
        EQUALS("==") {
            @Override
            ArrayList apply(Map<String, Object> request, String key, String value, ArrayList response) {
                if (request.get(key).equals(value)) {
                    response.add(request);
                }
                return response;
            }
        },
        GREATER_THAN(">") {
            @Override
            ArrayList apply(Map<String, Object> request, String key, String value, ArrayList response) {
                int valueInt = Generic.getIntValue((String) request.get(key), -1);
                int predicateInt = Generic.getIntValue(value, -1);
                if ((valueInt > predicateInt) && (valueInt > -1)) {
                    response.add(request);
                }
                return response;
            }
        },
        LESSER_THAN("<") {
            @Override
            ArrayList apply(Map<String, Object> request, String key, String value, ArrayList response) {
                int valueInt = Generic.getIntValue((String) request.get(key), -1);
                int predicateInt = Generic.getIntValue(value, -1);
                if ((valueInt < predicateInt) && (valueInt > -1)) {
                    response.add(request);
                }
                return response;
            }
        },
        NOT_EQUALS("!=") {
            @Override
            ArrayList apply(Map<String, Object> request, String key, String value, ArrayList response) {
                if (!request.get(key).equals(value)) {
                    response.add(request);
                }
                return response;
            }
        };

        private String operation;

        Operator(String operation) {
            this.operation = operation;
        }

        String getOperator() {
            return operation;
        }

        abstract ArrayList apply(Map<String, Object> request, String key, String value, ArrayList response);
    }

    private static Operator operator;
    private static String predicateKey = "";
    private static String predicateValue = "";

    public static Object process(Object map, String predicate) {

        for (Operator operator : Operator.values()) check(operator, predicate);

        if (map instanceof ArrayList) {
            ArrayList request = (ArrayList) map;
            ArrayList response = new ArrayList();
            for (Object item : request) apply(operator, item, response);
            return response;
        }

        return map;
    }

    static void check(Operator operator, String predicate) {
        if (predicate.contains(operator.getOperator())) {
            List<String> predicateList = Arrays.asList(predicate.split(operator.getOperator()));
            predicateKey = predicateList.get(0);
            predicateValue = predicateList.get(1);
            Predicate.operator = operator;
        }
    }

    static ArrayList apply(Operator operator, Object request, ArrayList response) {
        Map<String, Object> itemMap = (Map<String, Object>) request;
        if (itemMap.containsKey(predicateKey)) {
            operator.apply(itemMap, predicateKey, predicateValue, response);
        }
        return response;
    }
}
