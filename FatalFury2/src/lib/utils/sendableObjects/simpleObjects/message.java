package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.time.LocalDateTime;

public class message extends sendableObject {
    private Integer id;
    private String transmitter;
    private String receiver;
    private LocalDateTime date;
    private String content;

    public message(Integer id, String transmitter, String receiver, String content) {
        this.id = id;
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.content = content;
        this.date = LocalDateTime.now();
    }

    public message(Integer id, String transmitter, String receiver, LocalDateTime date, String content) {
        this.id = id;
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.date = date;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransmitter() {
        return transmitter;
    }

    public void setTransmitter(String transmitter) {
        this.transmitter = transmitter;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
