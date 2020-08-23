package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

/**
 * The type String.
 */
public class string extends sendableObject {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 7617345688754547716L;
    /**
     * The Content.
     */
    private String content;

    /**
     * Instantiates a new String.
     *
     * @param content the content
     */
    public string(String content) {
        this.content = content;
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
