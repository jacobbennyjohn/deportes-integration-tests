package com.univision.storage;

import org.springframework.data.elasticsearch.annotations.Document;

/**
 * Created by jbjohn on 10/5/15.
 */
@Document(indexName = "deportes", type = "information")
public class Information {

    private String id;
    private String type;

    private String key;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("information:{id:%s, type:%s, key:%s, value:%s}", id, type, key, value);
    }
}
