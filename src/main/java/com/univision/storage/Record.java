package com.univision.storage;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.GeneratedValue;
import java.util.Date;

/**
 */
@Document(indexName = "deportes", type = "record")
public class Record {

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

    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String eventId;
    private String fixture;
    private Date   docDate;
    private Status status;
    private Long delayTime;

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return String.format("Record[id=%s, eventId=%s, fixture=%s, date=%s, status=%s, delay=%s]", id, eventId, fixture, docDate, status, delayTime);
    }
}
