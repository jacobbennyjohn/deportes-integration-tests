package com.univision.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 */
@Document(indexName = "deportes", type = "record")
public class Record {

    private static final Logger LOGGER = LoggerFactory.getLogger(Record.class);

    public enum Status {

        UPDATED("Updated"),
        DELAYED("Delayed"),
        MISSING("Missing");

        String title;
        Status(String title) {
            this.title = title;
        }

        public String toString() {
            return title;
        }
    }

    private String id;

    private String eventId;
    private String fixture;
    private Date   docDate;
    private Status status;
    private Long delayTime;
    private String eventStatus;

    public String getId() {
        return id;
    }

    public void setId() {
        if (eventId != null && fixture != null && docDate != null) {
            String string = eventId + ":" + fixture + ":" + docDate;
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
                md5.update(StandardCharsets.UTF_8.encode(string));
                this.id = String.format("%032x", new BigInteger(1, md5.digest()));
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error("Exception generating record Id", e);
            }
        }
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFixture() {
        return fixture;
    }

    public void setFixture(String fixture) {
        this.fixture = fixture;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date date) {
        this.docDate = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    @Override
    public String toString() {
        return String.format("record:{id:%s, eventId:%s, fixture:%s, date:%s, status:%s, delay:%s, eventStatus:%s}", id, eventId, fixture, docDate, status, delayTime, eventStatus);
    }
}
