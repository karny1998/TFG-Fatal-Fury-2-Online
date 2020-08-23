package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.time.LocalDateTime;

/**
 * The type Message.
 */
public class message extends sendableObject {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 7617345688754547713L;
    /**
     * The Id.
     */
    private Integer id;
    /**
     * The Transmitter.
     */
    private String transmitter;
    /**
     * The Receiver.
     */
    private String receiver;
    /**
     * The Date.
     */
    private LocalDateTime date;
    /**
     * The Content.
     */
    private String content;

    /**
     * Instantiates a new Message.
     *
     * @param id          the id
     * @param transmitter the transmitter
     * @param receiver    the receiver
     * @param content     the content
     */
    public message(Integer id, String transmitter, String receiver, String content) {
        this.id = id;
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.content = content;
        this.date = LocalDateTime.now();
    }

    /**
     * Instantiates a new Message.
     *
     * @param id          the id
     * @param transmitter the transmitter
     * @param receiver    the receiver
     * @param date        the date
     * @param content     the content
     */
    public message(Integer id, String transmitter, String receiver, LocalDateTime date, String content) {
        this.id = id;
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.date = date;
        this.content = content;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets transmitter.
     *
     * @return the transmitter
     */
    public String getTransmitter() {
        return transmitter;
    }

    /**
     * Sets transmitter.
     *
     * @param transmitter the transmitter
     */
    public void setTransmitter(String transmitter) {
        this.transmitter = transmitter;
    }

    /**
     * Gets receiver.
     *
     * @return the receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Sets receiver.
     *
     * @param receiver the receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets serial version uid.
     *
     * @return the serial version uid
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
