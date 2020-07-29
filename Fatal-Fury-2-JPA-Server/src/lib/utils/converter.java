package lib.utils;

import database.models.Message;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.simpleObjects.message;

import java.util.ArrayList;
import java.util.List;

public class converter {

    public converter(){}

    public static message convertMessage(Message m){
        return new message(m.getId(),m.getTransmitter().getUsername(), m.getReceiver().getUsername(),m.getDate(),m.getContent());
    }

    public static ArrayList<sendableObject> convertMessageList(List<Message> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Message m : l){
            list.add(convertMessage(m));
        }
        return list;
    }
}
