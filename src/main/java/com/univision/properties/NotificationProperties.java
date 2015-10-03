package com.univision.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 */
@Component
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private Long ttl;

    public Long getTtl() {
        return (ttl != null) ? ttl : 30L;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
