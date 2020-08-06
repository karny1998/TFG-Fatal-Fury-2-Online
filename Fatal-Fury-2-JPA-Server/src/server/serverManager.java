package server;

import database.databaseManager;
import database.models.*;
import lib.utils.Pair;
import lib.utils.converter;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.message;
import lib.utils.sendableObjects.simpleObjects.profile;

import java.net.InetAddress;
import java.util.*;

public class serverManager {
    private static databaseManager dbm;
    private Map<String, serverConnection> loggedUsers = new HashMap<>();
    private Map<String, InetAddress> usersIP = new HashMap<>();
    private List<String> searchingGameUsers = new ArrayList<>();
    private List<String> searchingRankedGameUsers = new ArrayList<>();
    private List<Pair<String,String>> ongoingGames = new ArrayList<>();
    private List<Pair<String,String>> ongoingRankedGames = new ArrayList<>();
    private boolean shuttingDown = false;

    public serverManager(databaseManager dbm){
        this.dbm = dbm;
    }


    public synchronized boolean connectUser(InetAddress add, serverConnection sc, String username, String pass){
        Player p = (Player) dbm.findByKey(Player.class, username);
        if(p == null){
            sc.sendString(msgID.toServer.request,"ERROR:El nombre de usuario no existe.");
            return false;
        }
        else if(p.getPassword().equals(pass)) {
            Login l = new Login(p);
            dbm.save(l);
            loggedUsers.put(username, sc);
            usersIP.put(username,add);
            sc.sendString(msgID.toServer.request,"LOGGED");
            return  true;
        }
        else {
            sc.sendString(msgID.toServer.request,"ERROR:Incorrect password.");
            return false;
        }
    }

    public synchronized void desconnectUser(String us){
        loggedUsers.remove(us);
        usersIP.remove(us);
        searchingGameUsers.remove(us);
        boolean erased = false;
        for(int i = 0; !erased && i < ongoingGames.size(); ++i){
            if(ongoingGames.get(i).first.equals(us) || ongoingGames.get(i).second.equals(us)){
                ongoingGames.remove(i);
                erased = true;
            }
        }
        erased = false;
        for(int i = 0; !erased && i < ongoingRankedGames.size(); ++i){
            if(ongoingRankedGames.get(i).first.equals(us) || ongoingRankedGames.get(i).second.equals(us)){
                ongoingRankedGames.remove(i);
                erased = true;
            }
        }
    }

    public synchronized boolean searchGame(String newPlayer, boolean ranked){
        List<String> listAux;
        if(ranked){
            listAux = searchingGameUsers;
        }
        else{
            listAux = searchingRankedGameUsers;
        }
        serverConnection pCon = loggedUsers.get(newPlayer);
        if(pCon == null){return false;}
        if(listAux.size() > 0){
            String rival = listAux.get(0);
            boolean ok = createGameBetweenPalyers(rival, newPlayer, ranked);
            int i = 0;
            if(ok) {
                listAux.remove(0);
            }
            else {
                listAux.add(1,newPlayer);
                i = 2;
            }
            if(listAux.size() > 1){
                while(listAux.size() > 1 && i+1 < listAux.size()){
                    String p1 = listAux.get(i), p2 = listAux.get(i+1);
                    boolean ok2 = createGameBetweenPalyers(p1,p2, ranked);
                    if(ok2){
                        listAux.remove(i);
                        listAux.remove(i+1);
                    }
                    else{
                        i += 2;
                    }
                }
            }
            return ok;
        }
        else{
            listAux.add(newPlayer);
        }
        return true;
    }

    protected boolean createGameBetweenPalyers(String p1, String p2, boolean ranked){
        // Mensaje: eresHost?:direcciónRival
        String msg1 = "SEARCH GAME:true:"+usersIP.get(p2).getHostAddress()+":"+p2;
        String msg2 = "SEARCH GAME:false:"+usersIP.get(p1).getHostAddress()+":"+p1;
        boolean okP1 =  loggedUsers.get(p1).reliableSendString(msgID.toServer.tramits,msg1,500);
        boolean okP2 =  loggedUsers.get(p2).reliableSendString(msgID.toServer.tramits,msg2,500);
        if(okP1 && okP2){
            if(ranked){
                ongoingRankedGames.add(new Pair<>(p1,p2));
            }
            else{
                ongoingGames.add(new Pair<>(p1,p2));
            }
        }
        return  okP1 && okP2;
    }

    public String registerUser(String username, String email, String password){
        return dbm.registerPlayer(username, email, password);
    }

    public String friendsRequest(String user1, String user2){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:El jugador solicitante no existe.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:El jugador no existe.";
        }

        boolean areFriends = false;
        for(int i = 0; !areFriends && i < p1.getFriendsAsSoliciter().size();++i){
            areFriends = p1.getFriendsAsSoliciter().get(i).getUsername().equals(user2);
        }
        for(int i = 0; !areFriends && i < p1.getFriendsAsReceiver().size();++i){
            areFriends = p1.getFriendsAsReceiver().get(i).getUsername().equals(user2);
        }
        if(areFriends){
            return "ERROR:Los dos jugadores ya son amigos.";
        }

        areFriends = false;
        for(int i = 0; !areFriends && i < p1.getSentFriendRequest().size();++i){
            areFriends = p1.getSentFriendRequest().get(i).getUsername().equals(user2);
        }
        for(int i = 0; !areFriends && i < p1.getReceivedFriendRequest().size();++i){
            areFriends = p1.getReceivedFriendRequest().get(i).getUsername().equals(user2);
        }
        if(areFriends){
            return "ERROR:Ya se ha enviado la solicitud de amistad.";
        }

        p1.getSentFriendRequest().add(p2);
        p2.getReceivedFriendRequest().add(p1);
        dbm.save(p1);
        dbm.save(p2);

        if(loggedUsers.containsKey(user2)){
            loggedUsers.get(user2).sendString(msgID.toServer.notification, "FRIEND REQUEST:"+user1);
        }

        return "FRIEND REQUEST SENT";
    }

    public String answerFriendsRequest(String user1, String user2, boolean ok){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:El jugador solicitante no existe.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:El jugador no existe.";
        }

        boolean areFriends = false;
        for(int i = 0; !areFriends && i < p1.getFriendsAsSoliciter().size();++i){
            areFriends = p1.getFriendsAsSoliciter().get(i).getUsername().equals(user2);
        }
        for(int i = 0; !areFriends && i < p1.getFriendsAsReceiver().size();++i){
            areFriends = p1.getFriendsAsReceiver().get(i).getUsername().equals(user2);
        }
        if(areFriends){
            return "ERROR:Los dos jugadores ya son amigos.";
        }

        boolean hasSolicitude = false;
        int sol = 0;
        for(int i = 0; !hasSolicitude && i < p1.getReceivedFriendRequest().size();++i){
            hasSolicitude = p1.getReceivedFriendRequest().get(i).getUsername().equals(user2);
            sol = i;
        }
        if(!hasSolicitude){
            return "ERROR:No se ha podido responder a la solicitud de amistad.";
        }
        p1.getReceivedFriendRequest().remove(sol);
        sol = 0;
        hasSolicitude = false;
        for(int i = 0; !hasSolicitude && i < p2.getSentFriendRequest().size();++i){
            hasSolicitude = p2.getSentFriendRequest().get(i).getUsername().equals(user1);
            sol = i;
        }
        p2.getSentFriendRequest().remove(sol);

        if(ok) {
            p2.getFriendsAsSoliciter().add(p1);
            p1.getFriendsAsReceiver().add(p2);
            dbm.save(p1);
            dbm.save(p2);
            if(loggedUsers.containsKey(user2)){
                loggedUsers.get(user2).sendString(msgID.toServer.notification,"NEW FRIEND:"+user1);
            }
            return "FRIEND REQUEST ACCEPTED";
        }
        dbm.save(p1);
        dbm.save(p2);
        return "FRIEND REQUEST REJECTED";
    }

    public String removeFriend(String user1, String user2){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:El jugador solicitante no existe.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:El jugador no existe.";
        }
        boolean areFriends = false;
        int x = 0;
        for(int i = 0; !areFriends && i < p1.getFriendsAsSoliciter().size();++i){
            areFriends = p1.getFriendsAsSoliciter().get(i).getUsername().equals(user2);
            x = i;
        }
        if(areFriends){
            p1.getFriendsAsSoliciter().remove(x);
            x = 0;
            for(int i = 0; !areFriends && i < p2.getFriendsAsReceiver().size();++i){
                areFriends = p2.getFriendsAsReceiver().get(i).getUsername().equals(user1);
                x = i;
            }
            p2.getFriendsAsReceiver().remove(x);
            dbm.save(p1);
            dbm.save(p2);
            if(loggedUsers.containsKey(user2)){
                loggedUsers.get(user2).sendString(msgID.toServer.notification,"DELETED FRIEND:"+user1);
            }
            return "FRIEND REMOVED";
        }
        for(int i = 0; !areFriends && i < p1.getFriendsAsReceiver().size();++i){
            areFriends = p1.getFriendsAsReceiver().get(i).getUsername().equals(user2);
            x = i;
        }
        if(!areFriends){
            return "ERROR:Los dos jugadores no son amigos.";
        }
        else{
            p1.getFriendsAsReceiver().remove(x);
            x = 0;
            for(int i = 0; !areFriends && i < p2.getFriendsAsSoliciter().size();++i){
                areFriends = p2.getFriendsAsSoliciter().get(i).getUsername().equals(user1);
                x = i;
            }
            p2.getFriendsAsSoliciter().remove(x);
            dbm.save(p1);
            dbm.save(p2);
            if(loggedUsers.containsKey(user2)){
                loggedUsers.get(user2).sendString(msgID.toServer.notification,"DELETED FRIEND:"+user1);
            }
            return "FRIEND REMOVED";
        }
    }

    public synchronized void stopSearchingGame(InetAddress player){
        searchingGameUsers.remove(player);
    }

    public String sendMessage(String transmitter, String receiver, String msg){
        Player p1 = (Player) dbm.findByKey(Player.class, transmitter);
        if (p1 == null){
            return "ERROR:El jugador emisor no existe.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, receiver);
        if (p2 == null){
            return "ERROR:El jugador receptor no existe.";
        }
        Message m = new Message(p1, p2, msg);
        dbm.save(m);
        if(loggedUsers.containsKey(receiver)){
            loggedUsers.get(receiver).sendString(msgID.toServer.notification,"MESSAGE RECEIVED:"+transmitter+":"+msg);
        }
        return "MESSAGE SENT";
    }

    public sendableObjectsList messageBetweenUsersHistorial(String user1, String user2){
        ArrayList<sendableObject> list = converter.convertMessageList(dbm.getUserMessages(user1,user2));
        return new sendableObjectsList(list);
    }

    public String registerGame(String user1, String user2, String character1, String character2, int result, boolean ranked){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:El jugador emisor no existe.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:El jugador receptor no existe.";
        }
        try {
            if (ranked) {
                boolean erased = false;
                for(int i = 0; !erased && i < ongoingRankedGames.size(); ++i){
                    if(ongoingRankedGames.get(i).first.equals(user1) || ongoingRankedGames.get(i).second.equals(user1)){
                        ongoingRankedGames.remove(i);
                        erased = true;
                    }
                }
                if(erased) {
                    int winnerPoints = 0, loserPoints = 0;
                    // EVALUAR PUNTOS
                    RankedGame game = new RankedGame(p1, p2, character1, character2, result, winnerPoints, loserPoints);
                    p1.setRankScore(p1.getRankScore() + winnerPoints);
                    p2.setRankScore(p2.getRankScore() + winnerPoints);
                    dbm.save(p1);
                    dbm.save(p2);
                    dbm.save(game);
                }
                else{
                    return "E:Error al registrar la partida.";
                }
            } else {
                boolean erased = false;
                for(int i = 0; !erased && i < ongoingGames.size(); ++i){
                    if(ongoingGames.get(i).first.equals(user1) || ongoingGames.get(i).second.equals(user1)){
                        ongoingGames.remove(i);
                        erased = true;
                    }
                }
                if(erased) {
                    Game game = new RankedGame(p1, p2, character1, character2, result);
                    dbm.save(game);
                }
                else{
                    return "E:Error al registrar la partida.";
                }
            }
            return "GAME REGISTERED";
        }catch (Exception e){return "E:Error al registrar la partida.";}
    }

    public sendableObjectsList getUserFriends(String user){
        Player p = (Player) dbm.findByKey(Player.class, user);
        sendableObjectsList friends = new sendableObjectsList(new ArrayList<>());
        if(p != null){
            List<String> aux = new ArrayList<>();
            for(Player pAux : p.getFriendsAsReceiver()){
                aux.add(pAux.getUsername());
            }
            for(Player pAux : p.getFriendsAsSoliciter()){
                aux.add(pAux.getUsername());
            }
            if(aux.size() > 0) {
                friends = new sendableObjectsList(converter.convertStringList(aux));
            }
        }
        return friends;
    }

    public profile getUserProfile(String user){
        List<Game> games;
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return null;
        }
        games = dbm.getUserLastGames(user);
        return converter.convertPlayerToProfile(p,games);
    }
}
