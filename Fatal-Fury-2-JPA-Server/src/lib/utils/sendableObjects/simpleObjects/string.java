package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

public class string extends sendableObject {
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
}
