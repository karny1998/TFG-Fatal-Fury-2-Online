package server;

import database.databaseManager;
import database.models.Player;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class serverManager {
    private static databaseManager dbm;
    private Map<InetAddress, serverConnection> connectedUsers = new HashMap<>();
    private List<InetAddress> searchingGameUsers = new ArrayList<>();
    private boolean shutingDown = false;

    public serverManager(databaseManager dbm){
        this.dbm = dbm;
    }

    public synchronized void connectUser(InetAddress add, serverConnection sc){
        connectedUsers.put(add, sc);
    }

    public synchronized void connectUser(InetAddress add, serverConnection sc, String username, String pass){
        Player p = (Player) dbm.findByKey(Player.class, username);
        if(p == null){
            sc.sendString(1,"ERROR:El nombre de usuario no existe.");
        }
        else if(p.getPassword().equals(pass)) {
            connectedUsers.put(add, sc);
            sc.sendString(1,"LOGGED");
        }
        else {
            sc.sendString(1,"ERROR:La contraseña introducirda es incorrecta.");
        }
    }

    public synchronized void desconnectUser(InetAddress add){
        connectedUsers.remove(add);
        searchingGameUsers.remove(add);
    }

    public synchronized boolean searchGame(InetAddress newPlayer){
        serverConnection pCon = connectedUsers.get(newPlayer);
        if(pCon == null){return false;}
        if(searchingGameUsers.size() > 0){
            InetAddress rival = searchingGameUsers.get(0);
            boolean ok = createGameBetweenPalyers(rival, newPlayer);
            int i = 0;
            if(ok) {
                searchingGameUsers.remove(0);
            }
            else {
                searchingGameUsers.add(newPlayer);
                i = 2;
            }
            if(searchingGameUsers.size() > 1){
                while(searchingGameUsers.size() > 1 && i+1 < searchingGameUsers.size()){
                    InetAddress p1 = searchingGameUsers.get(i), p2 = searchingGameUsers.get(i+1);
                    boolean ok2 = createGameBetweenPalyers(p1,p2);
                    if(ok2){
                        searchingGameUsers.remove(i);
                        searchingGameUsers.remove(i+1);
                    }
                    else{
                        i += 2;
                    }
                }
            }
            return ok;
        }
        else{
            searchingGameUsers.add(newPlayer);
        }
        return true;
    }

    protected boolean createGameBetweenPalyers(InetAddress p1, InetAddress p2){
        // Mensaje: eresHost?:direcciónRival
        String msg1 = "SEARCH GAME:true:"+p2.getHostAddress();
        String msg2 = "SEARCH GAME:false:"+p1.getHostAddress();
        boolean okP1 =  connectedUsers.get(p1).reliableSendString(2,msg1,500);
        boolean okP2 =  connectedUsers.get(p2).reliableSendString(2,msg2,500);
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
            return "FRIEND REMOVED";
        }
    }

    public synchronized void stopSearchingGame(InetAddress player){
        searchingGameUsers.remove(player);
    }
}
