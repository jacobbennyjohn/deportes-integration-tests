package com.univision.storage;

import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Map;

/**
 */
@Document(indexName = "deportes", type = "information")
public class Information {

    private String id;
    private String type;

    private Map<String, String> map;

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

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return String.format("information:{id:%s, type:%s, map:%s}", id, type, map);
    }
}
