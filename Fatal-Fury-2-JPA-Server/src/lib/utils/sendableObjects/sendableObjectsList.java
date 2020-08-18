package lib.utils.sendableObjects;

import database.models.Message;

import java.util.ArrayList;
import java.util.List;

public class sendableObjectsList extends sendableObject {
    private static final long serialVersionUID = 7617345688754547715L;
    private ArrayList<sendableObject> msgs;

    public sendableObjectsList(ArrayList<sendableObject> l){
        if(l != null && !l.isEmpty()) {
            msgs = l;
        }
        else{
            msgs = new ArrayList<>();
        }
    }

    public ArrayList<sendableObject> getMsgs() {
        return msgs;
    }

    public void setMsgs(ArrayList<sendableObject> msgs) {
        this.msgs = msgs;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
