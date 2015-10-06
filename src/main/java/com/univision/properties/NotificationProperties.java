package com.univision.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 */
@Component
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private Long ttl;
    private int limit;
    private String title;
    private String description;

    public Long getTtl() {
        return (ttl != null) ? ttl : 30L;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
