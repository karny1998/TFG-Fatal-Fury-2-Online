package lib.utils.sendableObjects;

import java.util.ArrayList;

/**
 * The type Sendable objects list.
 */
public class sendableObjectsList extends sendableObject {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 7617345688754547715L;
    /**
     * The Msgs.
     */
    private ArrayList<sendableObject> msgs;

    /**
     * Instantiates a new Sendable objects list.
     *
     * @param l the l
     */
    public sendableObjectsList(ArrayList<sendableObject> l){
        if(l != null && !l.isEmpty()) {
            msgs = l;
        }
        else{
            msgs = new ArrayList<>();
        }
    }

    /**
     * Gets msgs.
     *
     * @return the msgs
     */
    public ArrayList<sendableObject> getMsgs() {
        return msgs;
    }

    /**
     * Sets msgs.
     *
     * @param msgs the msgs
     */
    public void setMsgs(ArrayList<sendableObject> msgs) {
        this.msgs = msgs;
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
