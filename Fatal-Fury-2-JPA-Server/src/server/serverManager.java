package server;

import database.databaseManager;
import database.models.*;
import lib.utils.Pair;
import lib.utils.converter;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.profile;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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


    public synchronized String connectUser(InetAddress add, serverConnection sc, String username, String pass){
        Player p = (Player) dbm.findByKey(Player.class, username);
        if(p == null){
            return "ERROR:The username doesn't exist.";
        }
        else if(!p.isActive()){
            return "ERROR:The account hasn't been verified.";
        }
        else if(p.getPassword().equals(pass)) {
            Login l = new Login(p);
            dbm.save(l);
            loggedUsers.put(username, sc);
            usersIP.put(username,add);
            return  "LOGGED";
        }
        else {
            return "ERROR:Incorrect password.";
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

    public String registerUser(String username, String email, String password){
        String res = dbm.registerPlayer(username, email, password);
        Player p = (Player) dbm.findByKey(Player.class, username);
        if(res.equals("OK")){
            sendVerificationCode(username,email,p.getCode());
            return "REGISTERED";
        }
        return res;
    }

    public String verifyAccount(String username, int cod){
        Player p = (Player) dbm.findByKey(Player.class, username);
        if (p == null){
            return "ERROR:El jugador solicitante no existe.";
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

    public sendableObjectsList messageBetweenUsersHistorial(String user1, String user2){
        ArrayList<sendableObject> list = converter.convertMessageList(dbm.getUserMessages(user1,user2));
        return new sendableObjectsList(list);
    }

    public synchronized String registerGame(String user1, String user2, String character1, String character2, int result, boolean ranked){
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
                    Pair<Integer,Integer> pts = evaluatePoints(p1,p2,result);
                    int winnerPoints = pts.first, loserPoints = pts.second;

                    RankedGame game = new RankedGame(p1, p2, character1, character2, result, winnerPoints, loserPoints);
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

    private Pair<Integer,Integer> evaluatePoints(Player p1, Player p2, int r){
        int p1P = 0, p2P = 0;
        switch (r){
            case 1:
                p1P = 20;
                p2P = -20;
                break;
            case 2:
                p1P = -20;
                p2P = 20;
                break;
            default:
                break;
        }

        if(p1.getRankScore() > p2.getRankScore()){
            p1P += (p2.getRankScore() - p1.getRankScore())/10;
            p2P += (p1.getRankScore() - p2.getRankScore())/10;
        }
        else if(p1.getRankScore() < p2.getRankScore()){
            p2P += (p2.getRankScore() - p1.getRankScore())/10;
            p1P += (p1.getRankScore() - p2.getRankScore())/10;
        }

        return new Pair<>(p1P, p2P);
    }

    public sendableObjectsList pendingFriendsRequestList(String user){
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return new sendableObjectsList(new ArrayList<>());
        }
        return new sendableObjectsList(converter.convertPlayerListToUsernameList(p.getReceivedFriendRequest()));
    }

    public sendableObjectsList pendingFriendsMessageList(String user){
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return new sendableObjectsList(new ArrayList<>());
        }
        return new sendableObjectsList(converter.convertPlayerListToUsernameList(p.getPending_messages()));
    }

    public String notifyMessagesRead(String receiver, String transmiter){
        Player p1 = (Player) dbm.findByKey(Player.class, receiver);
        if (p1 == null){
            return "ERROR:El jugador receptor no existe.";
        }
        Player p2 = (Player) dbm.findByKey(Player.class, transmiter);
        if (p2 == null){
            return "ERROR:El jugador emisor no existe.";
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

    public String recoverAccount(String user){
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return "ERROR:El jugador receptor no existe.";
        }
        String pass = String.valueOf((int)(Math.random()*999999999));
        try {
            p.setPassword(encrypt(pass));
            dbm.save(p);
            sendRecoverAccount(user, p.getEmail(),pass);
        } catch (Exception e) {
            return "ERROR:Has been a problem.";
        }
        return "RECOVERED";
    }

    public String changePassword(String user, String oldp, String newp) {
        Player p = (Player) dbm.findByKey(Player.class, user);
        if (p == null){
            return "ERROR:El jugador receptor no existe.";
        }
        if (!p.getPassword().equals(oldp)){
            return "ERROR:Incorrect password.";
        }
        String pass = newp;
        try {
            p.setPassword(pass);
            dbm.save(p);
        } catch (Exception e) {
            return "ERROR:Has been a problem.";
        }
        return "CHANGED";
    }

    private void sendRecoverAccount(String user, String cor, String pass){
        String msg = "Hi, " + user + ".<br/> Your password has been changed to : "+ pass
                +"<br/>You can chage your password in your profile." +"<br/>Regards.";
        try {
            sendmail(cor, msg, "Recover Account");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendVerificationCode(String user, String cor, int cod){
        String msg = "Hi, " + user + ".<br/> Your verification code is: "+ cod+"<br/>Thanks for entering our community.";
        try {
            sendmail(cor, msg, "Verification Code");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private void sendmail(String cor, String mensaje, String sub) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
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
        System.out.println(strData);
        strData.replace(":",".");
        return strData;
    }
}
