package lib.utils;

import lib.objects.networking.sendableObjects.sendableObject;

import java.io.Serializable;

public class packet implements Serializable {
    private int id;
    private boolean reliable;
    private boolean isObject;
    private String message = null;
    //private sendableObject object = null;

    public packet(int id, boolean reliable, String message) {
        this.id = id;
        this.reliable = reliable;
        this.message = message;
        this.isObject = false;
    }

    /*public packet(int id, boolean reliable, sendableObject object) {
        this.id = id;
        this.reliable = reliable;
        this.object = object;
        this.isObject = true;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReliable() {
        return reliable;
    }

    public void setReliable(boolean reliable) {
        this.reliable = reliable;
    }

    public boolean isObject() {
        return isObject;
    }

    public void setObject(boolean object) {
        isObject = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /*public sendableObject getObject() {
        return object;
    }

    public void setObject(sendableObject object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "packet{" +
                "id=" + id +
                ", reliable=" + reliable +
                ", isObject=" + isObject +
                ", message='" + message + '\'' +
                ", object=" + object +
                '}';
    }*/
}
