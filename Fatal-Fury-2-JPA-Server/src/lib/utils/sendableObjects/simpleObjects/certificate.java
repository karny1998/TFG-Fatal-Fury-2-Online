package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.security.cert.Certificate;

/**
 * The type Certificate.
 */
public class certificate extends sendableObject {
    private static final long serialVersionUID = 7617345688754547711L;
    private Certificate cer;

    /**
     * Instantiates a new Certificate.
     *
     * @param cer the cer
     */
    public certificate(Certificate cer){this.cer = cer;}

    /**
     * Gets cer.
     *
     * @return the cer
     */
    public Certificate getCer() {
        return cer;
    }

    /**
     * Sets cer.
     *
     * @param cer the cer
     */
    public void setCer(Certificate cer) {
        this.cer = cer;
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
