package lib.utils;

import database.models.Game;
import database.models.Message;
import database.models.Player;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.simpleObjects.game;
import lib.utils.sendableObjects.simpleObjects.message;
import lib.utils.sendableObjects.simpleObjects.profile;

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

    public static game convertGame(Game g){
        return new game(g.getId(), g.getPlayer1().getUsername(),g.getPlayer2().getUsername(),g.getDate(),g.getResult(),g.isRanked(),g.getWinnerPoints(),g.getLoserPoints(),g.getCharacter1(),g.getCharacter2());
    }

    public static ArrayList<sendableObject> convertCameList(List<Game> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Game m : l){
            list.add(convertGame(m));
        }
        return list;
    }

    public static ArrayList<game> castCameList(List<Game> l){
        ArrayList<game>list = new ArrayList<>();
        for(Game m : l){
            list.add(convertGame(m));
        }
        return list;
    }

    private static profile convertPlayerToProfile(Player p){
        return new profile(p.getUsername(),p.getRankScore(),0,0,0,0,
                castCameList(p.getGamesAsP1()));
    }
}
