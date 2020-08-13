package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.security.cert.Certificate;

public class certificate extends sendableObject {
    private Certificate cer;

    public certificate(Certificate cer){this.cer = cer;}

    public Certificate getCer() {
        return cer;
    }

    public void setCer(Certificate cer) {
        this.cer = cer;
    }
}
