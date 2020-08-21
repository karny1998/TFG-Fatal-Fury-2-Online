package lib.utils;

import lib.utils.sendableObjects.sendableObject;

import java.io.Serializable;

/**
 * Clase contenedora.
 */
public class packet implements Serializable {
    /**
     * The Id.
     */
    private int id;
    /**
     * The Reliable.
     */
    private boolean reliable;
    /**
     * The Is object.
     */
    private boolean isObject;
    /**
     * The Message.
     */
    private String message = null;
    /**
     * The Object.
     */
    private sendableObject object = null;
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 7617345688754547719L;

    /**
     * Instantiates a new Packet.
     *
     * @param id       the id
     * @param reliable the reliable
     * @param message  the message
     */
    public packet(int id, boolean reliable, String message) {
        this.id = id;
        this.reliable = reliable;
        this.message = message;
        this.isObject = false;
    }

    /**
     * Instantiates a new Packet.
     *
     * @param id       the id
     * @param reliable the reliable
     * @param object   the object
     */
    public packet(int id, boolean reliable, sendableObject object) {
        this.id = id;
        this.reliable = reliable;
        this.object = object;
        this.isObject = true;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Is reliable boolean.
     *
     * @return the boolean
     */
    public boolean isReliable() {
        return reliable;
    }

    /**
     * Sets reliable.
     *
     * @param reliable the reliable
     */
    public void setReliable(boolean reliable) {
        this.reliable = reliable;
    }

    /**
     * Is object boolean.
     *
     * @return the boolean
     */
    public boolean isObject() {
        return isObject;
    }

    /**
     * Sets object.
     *
     * @param object the object
     */
    public void setObject(boolean object) {
        isObject = object;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets object.
     *
     * @return the object
     */
    public sendableObject getObject() {
        return object;
    }

    /**
     * Sets object.
     *
     * @param object the object
     */
    public void setObject(sendableObject object) {
        this.object = object;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "packet{" +
                "id=" + id +
                ", reliable=" + reliable +
                ", isObject=" + isObject +
                ", message='" + message + '\'' +
                ", object=" + object +
                '}';
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
