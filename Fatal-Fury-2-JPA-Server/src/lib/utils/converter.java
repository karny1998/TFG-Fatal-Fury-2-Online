package lib.utils;

import database.models.Game;
import database.models.Message;
import database.models.Player;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.simpleObjects.game;
import lib.utils.sendableObjects.simpleObjects.message;
import lib.utils.sendableObjects.simpleObjects.profile;
import lib.utils.sendableObjects.simpleObjects.string;

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

    public static ArrayList<game> castGameList(List<Game> l){
        ArrayList<game>list = new ArrayList<>();
        for(Game m : l){
            list.add(convertGame(m));
        }
        return list;
    }

    public static profile convertPlayerToProfile(Player p, List<Game> lastGames){
        return new profile(p.getUsername(),p.getRankScore(),p.getNormalWins(),p.getNormalLoses(),p.getRankWins(),
                p.getRankLoses(), castGameList(lastGames));
    }

    public static ArrayList<sendableObject> convertStringList(List<String> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(String m : l){
            list.add(new string(m));
        }
        return list;
    }

    public static ArrayList<sendableObject> convertPlayerListToUsernameList(List<Player> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Player p : l){
            list.add(new string(p.getUsername()));
        }
        return list;
    }

    public static ArrayList<sendableObject> convertPlayerListToRanking(List<Player> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Player p : l){
            list.add(new profile(p.getUsername(),p.getRankScore(),0,0,p.getRankWins(), p.getRankLoses(), null));
        }
        return list;
    }
}
