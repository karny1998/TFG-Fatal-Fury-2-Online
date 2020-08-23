package server;

import database.databaseManager;
import database.models.*;
import lib.Enums.Movement;
import lib.training.agent;
import lib.training.state;
import lib.training.stateCalculator;
import lib.utils.Pair;
import lib.utils.converter;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.certificate;
import lib.utils.sendableObjects.simpleObjects.profile;
import lib.utils.sendableObjects.simpleObjects.qtable;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.Query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * The type Server manager.
 */
public class serverManager {
    private static databaseManager dbm;
    private Map<String, serverConnection> loggedUsers = new HashMap<>();
    private Map<String, InetAddress> usersIP = new HashMap<>();
    private List<String> searchingGameUsers = new ArrayList<>();
    private List<String> searchingRankedGameUsers = new ArrayList<>();
    private List<Pair<String,String>> ongoingGames = new ArrayList<>();
    private List<Pair<String,String>> ongoingRankedGames = new ArrayList<>();
    private Map<String, String> pendingFriendsInvitatiosns = new HashMap<>();
    private boolean shuttingDown = false;
    /**
     * The Training global ia.
     */
    public Semaphore trainingGlobalIa = new Semaphore(1);

    /**
     * Instantiates a new Server manager.
     *
     * @param dbm the dbm
     */
    public serverManager(databaseManager dbm){
        stateCalculator.initialize();
        this.dbm = dbm;
    }


    /**
     * Connect user string.
     *
     * @param add      the add
     * @param sc       the sc
     * @param username the username
     * @param pass     the pass
     * @return the string
     */
    public synchronized String connectUser(InetAddress add, serverConnection sc, String username, String pass){
        Player p = (Player) dbm.findByKey(Player.class, username);
        if(p == null){
            return "ERROR:The username doesn't exist.";
        }
        else if(!p.isActive()){
            return "ERROR:The account hasn't been verified.";
        }
        else {
            try {
                if(p.getPassword().equals(encrypt(pass))) {
                    Login l = new Login(p);
                    dbm.save(l);
                    loggedUsers.put(username, sc);
                    usersIP.put(username,add);
                    return  "LOGGED";
                }
                else {
                    return "ERROR:Incorrect password.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "ERROR:Has been some problem.";
            }
        }
    }

    /**
     * Desconnect user.
     *
     * @param us the us
     */
    public synchronized void desconnectUser(String us){
        loggedUsers.remove(us);
        usersIP.remove(us);
        searchingGameUsers.remove(us);
        searchingRankedGameUsers.remove(us);
        pendingFriendsInvitatiosns.remove(us);
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

    /**
     * Search game boolean.
     *
     * @param newPlayer the new player
     * @param ranked    the ranked
     * @return the boolean
     */
    public synchronized boolean searchGame(String newPlayer, boolean ranked){
        if(searchingRankedGameUsers.contains(newPlayer)
            || searchingGameUsers.contains(newPlayer)){
            return true;
        }
        List<String> listAux;
        if(!ranked){
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

    /**
     * Create game between palyers boolean.
     *
     * @param p1     the p 1
     * @param p2     the p 2
     * @param ranked the ranked
     * @return the boolean
     */
    protected boolean createGameBetweenPalyers(String p1, String p2, boolean ranked){
        // Mensaje: eresHost?:direcciónRival
        String msg1 = "SEARCH GAME:true:"+usersIP.get(p2).getHostAddress()+":"+p2;
        String msg2 = "SEARCH GAME:false:"+usersIP.get(p1).getHostAddress()+":"+p1;
        if(ranked){
            msg1 = "SEARCH RANKED GAME:true:"+usersIP.get(p2).getHostAddress()+":"+p2;
            msg2 = "SEARCH RANKED GAME:false:"+usersIP.get(p1).getHostAddress()+":"+p1;
        }
        loggedUsers.get(p1).sendString(msgID.toServer.notification,msg1);
        loggedUsers.get(p2).sendString(msgID.toServer.notification,msg2);
        if(ranked){
            ongoingRankedGames.add(new Pair<>(p1,p2));
        }
        else{
            ongoingGames.add(new Pair<>(p1,p2));
        }
        return  true;
    }

    /**
     * Register user string.
     *
     * @param username the username
     * @param email    the email
     * @param password the password
     * @return the string
     */
    public String registerUser(String username, String email, String password){
        String res = dbm.registerPlayer(username, email, password);
        Player p = (Player) dbm.findByKey(Player.class, username);
        if(res.equals("OK")){
            try {
                sendVerificationCode(username, email, p.getCode());
                return "REGISTERED";
            }catch (Exception e){
                dbm.remove(p);
                return "ERROR:Error sending verification mail.";
            }
        }
        return res;
    }

    /**
     * Verify account string.
     *
     * @param username the username
     * @param cod      the cod
     * @return the string
     */
    public String verifyAccount(String username, int cod){
        Player p = (Player) dbm.findByKey(Player.class, username);
        if (p == null){
            return "ERROR:The player doesn't exist.";
        }
        if(p.getCode() == cod){
            p.setActive(true);
            dbm.save(p);
            return "VERIFIED";
        }
        else{
            return "ERROR:Incorrect code.";
        }
    }

    /**
     * Friends request string.
     *
     * @param user1 the user 1
     * @param user2 the user 2
     * @return the string
     */
    public String friendsRequest(String user1, String user2){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:The player doesn't exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:The player doesn't exist.";
        }

        boolean areFriends = false;
        for(int i = 0; !areFriends && i < p1.getFriendsAsSoliciter().size();++i){
            areFriends = p1.getFriendsAsSoliciter().get(i).getUsername().equals(user2);
        }
        for(int i = 0; !areFriends && i < p1.getFriendsAsReceiver().size();++i){
            areFriends = p1.getFriendsAsReceiver().get(i).getUsername().equals(user2);
        }
        if(areFriends){
            return "ERROR:Both art already friends.";
        }

        areFriends = false;
        for(int i = 0; !areFriends && i < p1.getSentFriendRequest().size();++i){
            areFriends = p1.getSentFriendRequest().get(i).getUsername().equals(user2);
        }
        for(int i = 0; !areFriends && i < p1.getReceivedFriendRequest().size();++i){
            areFriends = p1.getReceivedFriendRequest().get(i).getUsername().equals(user2);
        }
        if(areFriends){
            return "ERROR:You have already sent him a friend request.";
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

    /**
     * Answer friends request string.
     *
     * @param user1 the user 1
     * @param user2 the user 2
     * @param ok    the ok
     * @return the string
     */
    public String answerFriendsRequest(String user1, String user2, boolean ok){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:The player doesn't exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:The player doesn't exist.";
        }

        boolean hasSolicitude = false;
        int sol = 0;
        for(int i = 0; !hasSolicitude && i < p1.getReceivedFriendRequest().size();++i){
            hasSolicitude = p1.getReceivedFriendRequest().get(i).getUsername().equals(user2);
            sol = i;
        }
        if(!hasSolicitude){
            return "ERROR:Problem answering the friend request.";
        }
        p1.getReceivedFriendRequest().remove(sol);
        sol = 0;
        hasSolicitude = false;
        for(int i = 0; !hasSolicitude && i < p2.getSentFriendRequest().size();++i){
            hasSolicitude = p2.getSentFriendRequest().get(i).getUsername().equals(user1);
            sol = i;
        }
        p2.getSentFriendRequest().remove(sol);

        dbm.save(p1);
        dbm.save(p2);

        dbm.refresh(p1);
        dbm.refresh(p2);

        boolean areFriends = false;
        for(int i = 0; !areFriends && i < p1.getFriendsAsSoliciter().size();++i){
            areFriends = p1.getFriendsAsSoliciter().get(i).getUsername().equals(user2);
        }
        for(int i = 0; !areFriends && i < p1.getFriendsAsReceiver().size();++i){
            areFriends = p1.getFriendsAsReceiver().get(i).getUsername().equals(user2);
        }
        if(areFriends){
            return "ERROR:Both are already friends.";
        }

        if(ok) {
            p2.getFriendsAsSoliciter().add(p1);
            p1.getFriendsAsReceiver().add(p2);
            dbm.save(p1);
            dbm.save(p2);
            dbm.refresh(p1);
            dbm.refresh(p2);
            if(loggedUsers.containsKey(user2)){
                loggedUsers.get(user2).sendString(msgID.toServer.notification,"NEW FRIEND:"+user1);
            }
            return "FRIEND REQUEST ACCEPTED";
        }
        dbm.save(p1);
        dbm.save(p2);
        dbm.refresh(p1);
        dbm.refresh(p2);
        return "FRIEND REQUEST REJECTED";
    }

    /**
     * Remove friend string.
     *
     * @param user1 the user 1
     * @param user2 the user 2
     * @return the string
     */
    public String removeFriend(String user1, String user2){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:The player doesn't exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:The player doesn't exist.";
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
            dbm.refresh(p1);
            dbm.refresh(p2);
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
            return "ERROR:You are't friends.";
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
            dbm.refresh(p1);
            dbm.refresh(p2);
            if(loggedUsers.containsKey(user2)){
                loggedUsers.get(user2).sendString(msgID.toServer.notification,"DELETED FRIEND:"+user1);
            }
            return "FRIEND REMOVED";
        }
    }

    /**
     * Stop searching game.
     *
     * @param player the player
     */
    public synchronized void stopSearchingGame(String player){
        searchingRankedGameUsers.remove(player);
        searchingGameUsers.remove(player);
        pendingFriendsInvitatiosns.remove(player);
    }

    /**
     * Send message string.
     *
     * @param transmitter the transmitter
     * @param receiver    the receiver
     * @param msg         the msg
     * @return the string
     */
    public String sendMessage(String transmitter, String receiver, String msg){
        Player p1 = (Player) dbm.findByKey(Player.class, transmitter);
        if (p1 == null){
            return "ERROR:The player does not exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, receiver);
        if (p2 == null){
            return "ERROR:The player does not exist.";
        }
        boolean ok = false;
        for(int i = 0; !ok && i < p1.getPending_messages().size(); ++i){
            if(p1.getPending_messages().get(i).getUsername().equals(receiver)){
                p1.getPending_messages().remove(i);
                dbm.save(p1);
                ok = true;
            }
        }

        Message m = new Message(p1, p2, msg);

        ok = false;
        for(int i = 0; !ok && i < p2.getPending_messages().size(); ++i){
            if(p2.getPending_messages().get(i).getUsername().equals(transmitter)){
                ok = true;
            }
        }
        if(!ok) {
            p2.getPending_messages().add(p1);
            dbm.save(p2);
        }

        dbm.save(m);
        if(loggedUsers.containsKey(receiver)){
            loggedUsers.get(receiver).sendString(msgID.toServer.notification,"MESSAGE RECEIVED:"+transmitter+":"+msg);
        }
        return "MESSAGE SENT";
    }

    /**
     * Message between users historial sendable objects list.
     *
     * @param user1 the user 1
     * @param user2 the user 2
     * @return the sendable objects list
     */
    public sendableObjectsList messageBetweenUsersHistorial(String user1, String user2){
        ArrayList<sendableObject> list = converter.convertMessageList(dbm.getUserMessages(user1,user2));
        return new sendableObjectsList(list);
    }

    /**
     * Register game string.
     *
     * @param user1      the user 1
     * @param user2      the user 2
     * @param character1 the character 1
     * @param character2 the character 2
     * @param result     the result
     * @param ranked     the ranked
     * @return the string
     */
    public synchronized String registerGame(String user1, String user2, String character1, String character2, int result, boolean ranked){
        Player p1 = (Player) dbm.findByKey(Player.class, user1);
        if (p1 == null){
            return "ERROR:The player does not exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, user2);
        if (p2 == null){
            return "ERROR:The player does not exist.";
        }
        try {
            if (ranked) {
                boolean erased = true;
                for(int i = 0; !erased && i < ongoingRankedGames.size(); ++i){
                    if(ongoingRankedGames.get(i).first.equals(user1) || ongoingRankedGames.get(i).second.equals(user1)){
                        ongoingRankedGames.remove(i);
                        erased = true;
                    }
                }
                if(erased) {
                    Pair<Integer,Integer> pts = evaluatePoints(p1,p2,result);
                    int p1pts = pts.first, p2pts = pts.second;

                    RankedGame game = new RankedGame(p1, p2, character1, character2, result, p1pts, p2pts);
                    if(result == 0){
                        p1.addRankedGameResult(true);
                        p2.addRankedGameResult(true);
                    }
                    else if(result == 1){
                        p1.addRankedGameResult(true);
                        p2.addRankedGameResult(false);
                    }
                    else{
                        p1.addRankedGameResult(false);
                        p2.addRankedGameResult(true);
                    }
                    p1.setRankScore(p1.getRankScore() + p1pts);
                    p2.setRankScore(p2.getRankScore() + p2pts);
                    dbm.save(p1);
                    dbm.save(p2);
                    dbm.save(game);

                    loggedUsers.get(user1).sendString(msgID.toServer.request, "GAME REGISTERED:"+p1pts);
                    loggedUsers.get(user2).sendString(msgID.toServer.request, "GAME REGISTERED:"+p2pts);
                }
                else{
                    return "E:Error al registrar la partida.";
                }
            } else {
                boolean erased = true;//false;
                for(int i = 0; !erased && i < ongoingGames.size(); ++i){
                    if(ongoingGames.get(i).first.equals(user1) || ongoingGames.get(i).second.equals(user1)){
                        ongoingGames.remove(i);
                        erased = true;
                    }
                }
                if(erased) {
                    Game game = new Game(p1, p2, character1, character2, result);
                    if(result == 0){
                        p1.addNormalGameResult(true);
                        p2.addNormalGameResult(true);
                    }
                    else if(result == 1){
                        p1.addNormalGameResult(true);
                        p2.addNormalGameResult(false);
                    }
                    else{
                        p1.addNormalGameResult(false);
                        p2.addNormalGameResult(true);
                    }
                    dbm.save(p1);
                    dbm.save(p2);
                    dbm.save(game);
                }
                else{
                    return "E:Error al registrar la partida.";
                }
            }
            return "GAME REGISTERED";
        }catch (Exception e){
            e.printStackTrace();
            return "E:Error al registrar la partida.";
        }
    }

    /**
     * Get user friends sendable objects list.
     *
     * @param user the user
     * @return the sendable objects list
     */
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

    /**
     * Get user profile profile.
     *
     * @param user the user
     * @return the profile
     */
    public profile getUserProfile(String user){
        List<Game> games;
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return null;
        }
        games = dbm.getUserLastGames(user);
        return converter.convertPlayerToProfile(p,games);
    }

    private Pair<Integer,Integer> evaluatePoints(Player p1, Player p2, int r){
        int p1P = 0, p2P = 0;
        int discount = 0;
        if(p1.getRankScore() > p2.getRankScore()){
            discount = (p1.getRankScore() - p2.getRankScore())/10;
            switch (r){
                case 1:
                    p1P = 20 - discount;
                    p2P = -20 + discount;
                    break;
                case 2:
                    p1P = -20 - discount;
                    p2P = 20 + discount;
                    break;
                default:
                    break;
            }
        }
        else if(p1.getRankScore() < p2.getRankScore()){
            discount = (p2.getRankScore() - p1.getRankScore())/10;
            switch (r){
                case 1:
                    p1P = 20 + discount;
                    p2P = -20 - discount;
                    break;
                case 2:
                    p1P = -20 + discount;
                    p2P = 20 - discount;
                    break;
                default:
                    break;
            }
        }
        return new Pair<>(p1P, p2P);
    }

    /**
     * Pending friends request list sendable objects list.
     *
     * @param user the user
     * @return the sendable objects list
     */
    public sendableObjectsList pendingFriendsRequestList(String user){
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return new sendableObjectsList(new ArrayList<>());
        }
        return new sendableObjectsList(converter.convertPlayerListToUsernameList(p.getReceivedFriendRequest()));
    }

    /**
     * Pending friends message list sendable objects list.
     *
     * @param user the user
     * @return the sendable objects list
     */
    public sendableObjectsList pendingFriendsMessageList(String user){
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return new sendableObjectsList(new ArrayList<>());
        }
        return new sendableObjectsList(converter.convertPlayerListToUsernameList(p.getPending_messages()));
    }

    /**
     * Notify messages read string.
     *
     * @param receiver   the receiver
     * @param transmiter the transmiter
     * @return the string
     */
    public String notifyMessagesRead(String receiver, String transmiter){
        Player p1 = (Player) dbm.findByKey(Player.class, receiver);
        if (p1 == null){
            return "ERROR:The player does not exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, transmiter);
        if (p2 == null){
            return "ERROR:The player does not exist.";
        }
        for(int i = 0; i < p1.getPending_messages().size(); ++i){
            if(p1.getPending_messages().get(i).getUsername().equals(transmiter)){
                p1.getPending_messages().remove(i);
                dbm.save(p1);
                return "OK";
            }
        }
        return "OK";
    }

    /**
     * Challenge friend string.
     *
     * @param inviter  the inviter
     * @param receptor the receptor
     * @return the string
     */
    public String challengeFriend(String inviter, String receptor){
        if(!loggedUsers.containsKey(receptor)){
            return "ERROR: The user isnt connected.";
        }
        Player p1 = (Player) dbm.findByKey(Player.class, inviter);
        if (p1 == null){
            return "ERROR:The player does not exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, receptor);
        if (p2 == null){
            return "ERROR:The player does not exist.";
        }
        loggedUsers.get(receptor).sendString(msgID.toServer.notification, "FRIEND CHALLENGE:"+inviter);
        pendingFriendsInvitatiosns.put(inviter, receptor);
        return "CHALLENGE SENT";
    }

    /**
     * Cancel invitation string.
     *
     * @param inviter the inviter
     * @return the string
     */
    public String cancelInvitation(String inviter){
        Player p1 = (Player) dbm.findByKey(Player.class, inviter);
        if (p1 == null){
            return "ERROR:The player does not exist.";
        }
        if(loggedUsers.containsKey(pendingFriendsInvitatiosns.get(inviter))){
            loggedUsers.get(pendingFriendsInvitatiosns.get(inviter)).sendString(msgID.toServer.notification,
                    "FRIEND CHALLENGE CANCELED");
        }
        pendingFriendsInvitatiosns.remove(inviter);
        return "CHALLENGE CANCELLED";
    }

    /**
     * Answer friend challenge string.
     *
     * @param receptor the receptor
     * @param inviter  the inviter
     * @param accepted the accepted
     * @return the string
     */
    public String answerFriendChallenge(String receptor, String inviter, boolean accepted){
        if(!pendingFriendsInvitatiosns.containsKey(inviter) || !loggedUsers.containsKey(inviter)){
            return "ERROR:The invitation has expired.";
        }
        Player p1 = (Player) dbm.findByKey(Player.class, receptor);
        if (p1 == null){
            return "ERROR:The player does not exist.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, inviter);
        if (p2 == null){
            return "ERROR:The player does not exist.";
        }
        if(!accepted){
            loggedUsers.get(inviter).sendString(msgID.toServer.notification, "PLAYER BUSY");
        }
        else{
            createGameBetweenPalyers(inviter, receptor, false);
        }
        return "CHALLENGE ANSWERED";
    }

    /**
     * Load ranking sendable objects list.
     *
     * @return the sendable objects list
     */
    public sendableObjectsList loadRanking(){
        List<Player> list = dbm.getRanking();
        return new sendableObjectsList(converter.convertPlayerListToRanking(list));
    }

    /**
     * Recover account string.
     *
     * @param user the user
     * @return the string
     */
    public String recoverAccount(String user){
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return "ERROR:The player does not exist.";
        }
        String pass = String.valueOf((int)(Math.random()*999999999));
        System.out.println("pass " + pass);
        try {
            p.setPassword(encrypt(pass));
            dbm.save(p);
            dbm.refresh(p);
            sendRecoverAccount(user, p.getEmail(),pass);
        } catch (Exception e) {
            return "ERROR:Has been a problem.";
        }
        return "RECOVERED";
    }

    /**
     * Change password string.
     *
     * @param user the user
     * @param oldp the oldp
     * @param newp the newp
     * @return the string
     */
    public String changePassword(String user, String oldp, String newp) {
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return "ERROR:The player does not exist.";
        }
        try {
            if (!p.getPassword().equals(encrypt(oldp))){
                return "ERROR:Incorrect password.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            p.setPassword(encrypt(newp));
            dbm.save(p);
            dbm.refresh(p);
        } catch (Exception e) {
            return "ERROR:Has been a problem.";
        }
        return "CHANGED";
    }

    private void sendRecoverAccount(String user, String cor, String pass) throws MessagingException{
        String msg = "Hi, " + user + ".<br/> Your password has been changed to : "+ pass
                +"<br/>You can chage your password in your profile." +"<br/>Regards.";
        sendmail(cor, msg, "Recover Account");
    }

    private void sendVerificationCode(String user, String cor, int cod) throws MessagingException{
        String msg = "Hi, " + user + ".<br/> Your verification code is: "+ cod+"<br/>Thanks for joining our community.";
        sendmail(cor, msg, "Verification Code");
    }


    private void sendmail(String cor, String mensaje, String sub) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("fatalfury2online@gmail.com", "199819981998s");
            }
        });
        javax.mail.Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("fatalfury2online@gmail.com", false));

        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(cor));
        msg.setSubject("Fatal Fury 2 Online " + sub);
        msg.setContent(mensaje, "text/html; charset=utf-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    /**
     * Encrypt string.
     *
     * @param strClearText the str clear text
     * @return the string
     * @throws Exception the exception
     */
    public static String encrypt(String strClearText) throws Exception{
        String strData="";
        String strKey = strClearText;
        try {
            SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
            Cipher cipher=Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted=cipher.doFinal(strClearText.getBytes());
            strData=new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        strData.replace(":",".");
        return strData;
    }

    /**
     * Add certificate to keystore string.
     *
     * @param cer the cer
     * @return the string
     */
    public synchronized String addCertificateToKeystore(certificate cer){
        String path = System.getProperty("user.dir") + "/certs/serverTrustedCerts.jks";
        File file = new File(path);
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(file), "servpass".toCharArray());
            ks.setCertificateEntry("OwnPlayer"+((int)Math.random()*10000)+System.currentTimeMillis(), cer.getCer());

            OutputStream out = new FileOutputStream(file);
            ks.store(out, "servpass".toCharArray());
            out.close();
            return "ADDED";
        }catch (Exception e){
            return "ERROR:Problem adding the certificate.";
        }
    }

    /**
     * Get qtable qtable.
     *
     * @param user the user
     * @return the qtable
     */
    public qtable getQtable(String user){
        agent aux = new agent(user);
        return new qtable(aux.getqTable(), "");//,new ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>>());
    }

    /**
     * Train own ia string.
     *
     * @param user  the user
     * @param table the table
     * @return the string
     */
    public String trainOwnIA(String user, qtable table){
        try {
            agent aux = new agent(user);
            aux.setqTable(table.getTableDouble());
            //aux.setTrainingRegister(table.getTransitions());
            aux.writeQTableAndRegister();

            trainGlobalIA(table);

            Player p1 = (Player) dbm.findByKey(Player.class, user);
            if (p1 == null){
                return "ERROR:Has been some problem.";
            }
            p1.setTimesVSglobalIa(p1.getTimesVSglobalIa()+1);
            p1.setTimesVSownlIa(p1.getTimesVSownlIa()+1);
            dbm.save(p1);
            dbm.refresh(p1);
        }catch (Exception e){
            return "ERROR:Has been some problem.";
        }
        return "TRAINED";
    }

    /**
     * Train global ia string.
     *
     * @param table the table
     * @return the string
     */
    public String trainGlobalIA(qtable table){
        try {
            trainingGlobalIa.acquire();
            agent aux = new agent("GLOBAL");
            aux.loadTrainingFromString(table.getTransitions());
            aux.writeQTableAndRegister();
        }catch (Exception e){
            trainingGlobalIa.release();
            return "ERROR:Has been some problem.";
        }
        trainingGlobalIa.release();
        return "TRAINED";
    }

    /**
     * Get player i atimes int.
     *
     * @param user the user
     * @return the int
     */
    public int getPlayerIAtimes(String user){
        Player p1 = (Player) dbm.findByKey(Player.class, user);
        if (p1 == null){
            return 0;
        }
        return p1.getTimesVSownlIa();
    }

    /**
     * Get global ia times int.
     *
     * @return the int
     */
    public int getGlobalIaTimes(){
        return dbm.getGlobalIaTimes();
    }
}
