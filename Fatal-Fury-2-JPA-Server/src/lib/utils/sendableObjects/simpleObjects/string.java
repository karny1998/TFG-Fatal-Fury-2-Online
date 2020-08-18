package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

public class string extends sendableObject {
    private static final long serialVersionUID = 7617345688754547716L;
    private String content;

    public string(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
