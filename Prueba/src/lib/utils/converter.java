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

/**
 * The type Converter.
 */
public class converter {

    /**
     * Instantiates a new Converter.
     */
    public converter(){}

    /**
     * Convert message message.
     *
     * @param m the m
     * @return the message
     */
    public static message convertMessage(Message m){
        return new message(m.getId(),m.getTransmitter().getUsername(), m.getReceiver().getUsername(),m.getDate(),m.getContent());
    }

    /**
     * Convert message list array list.
     *
     * @param l the l
     * @return the array list
     */
    public static ArrayList<sendableObject> convertMessageList(List<Message> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Message m : l){
            list.add(convertMessage(m));
        }
        return list;
    }

    /**
     * Convert game game.
     *
     * @param g the g
     * @return the game
     */
    public static game convertGame(Game g){
        return new game(g.getId(), g.getPlayer1().getUsername(),g.getPlayer2().getUsername(),g.getDate(),g.getResult(),g.isRanked(),g.getWinnerPoints(),g.getLoserPoints(),g.getCharacter1(),g.getCharacter2());
    }

    /**
     * Convert came list array list.
     *
     * @param l the l
     * @return the array list
     */
    public static ArrayList<sendableObject> convertCameList(List<Game> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Game m : l){
            list.add(convertGame(m));
        }
        return list;
    }

    /**
     * Cast game list array list.
     *
     * @param l the l
     * @return the array list
     */
    public static ArrayList<game> castGameList(List<Game> l){
        ArrayList<game>list = new ArrayList<>();
        for(Game m : l){
            list.add(convertGame(m));
        }
        return list;
    }

    /**
     * Convert player to profile profile.
     *
     * @param p         the p
     * @param lastGames the last games
     * @return the profile
     */
    public static profile convertPlayerToProfile(Player p, List<Game> lastGames){
        return new profile(p.getUsername(),p.getRankScore(),p.getNormalWins(),p.getNormalLoses(),p.getRankWins(),
                p.getRankLoses(), castGameList(lastGames));
    }

    /**
     * Convert string list array list.
     *
     * @param l the l
     * @return the array list
     */
    public static ArrayList<sendableObject> convertStringList(List<String> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(String m : l){
            list.add(new string(m));
        }
        return list;
    }

    /**
     * Convert player list to username list array list.
     *
     * @param l the l
     * @return the array list
     */
    public static ArrayList<sendableObject> convertPlayerListToUsernameList(List<Player> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Player p : l){
            list.add(new string(p.getUsername()));
        }
        return list;
    }

    /**
     * Convert player list to ranking array list.
     *
     * @param l the l
     * @return the array list
     */
    public static ArrayList<sendableObject> convertPlayerListToRanking(List<Player> l){
        ArrayList<sendableObject>list = new ArrayList<>();
        for(Player p : l){
            list.add(new profile(p.getUsername(),p.getRankScore(),0,0,p.getRankWins(), p.getRankLoses(), null));
        }
        return list;
    }
}
