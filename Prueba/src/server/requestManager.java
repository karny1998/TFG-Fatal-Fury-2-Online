package server;

import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.certificate;
import lib.utils.sendableObjects.simpleObjects.profile;
import lib.utils.sendableObjects.simpleObjects.qtable;

import java.net.InetAddress;

/**
 * The type Request manager.
 */
public class requestManager {
    private serverManager manager;
    private serverConnection con;
    private InetAddress client;
    private String userLogged = null;

    /**
     * Instantiates a new Request manager.
     *
     * @param manager the manager
     * @param con     the con
     * @param client  the client
     */
    public requestManager(serverManager manager, serverConnection con, InetAddress client){
        this.manager = manager;
        this.con = con;
        this.client = client;
    }

    /**
     * Manage request.
     *
     * @param request the request
     */
    public void manageRequest(String request){
        try {
            if (request.contains("REGISTER:")) {
                String aux[] = request.split(":");
                String res = manager.registerUser(aux[1], aux[2], aux[3]);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("LOGIN:")) {
                if (userLogged == null ||userLogged.equals(request.split(":")[1])) {
                    String aux[] = request.split(":");
                    String res = manager.connectUser(client, con, aux[1], aux[2]);
                    if (res.equals("LOGGED")) {
                        userLogged = aux[1];
                    }
                    else{
                        userLogged = null;
                    }
                    con.sendString(msgID.toServer.request, res);
                }
                else {
                    con.sendString(1, "ERROR:You are login in other account.");
                }
            }
            else if (request.contains("LOG OFF")) {
                if (userLogged != null) {
                    manager.desconnectUser(userLogged);
                    userLogged = null;
                    con.sendString(msgID.toServer.request, "LOGGED OFF");
                } else {
                    con.sendString(msgID.toServer.request, "ERROR:You are't logged.");
                }
            }
            else if (request.equals("SEARCH GAME")) {
                if (userLogged != null) {
                    manager.searchGame(userLogged, false);
                } else {
                    con.sendString(msgID.toServer.request, "ERROR:You are't logged.");
                }
            }
            else if (request.equals("SEARCH RANKED GAME")) {
                if (userLogged != null) {
                    manager.searchGame(userLogged, true);
                } else {
                    con.sendString(msgID.toServer.request, "ERROR:You are't logged.");
                }
            }
            else if (request.contains("SEND FRIEND REQUEST:")) {
                String aux[] = request.split(":");
                String res = manager.friendsRequest(aux[1], aux[2]);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("ACCEPT FRIEND REQUEST:")) {
                String aux[] = request.split(":");
                String res = manager.answerFriendsRequest(aux[1], aux[2], true);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("REJECT FRIEND REQUEST:")) {
                String aux[] = request.split(":");
                String res = manager.answerFriendsRequest(aux[1], aux[2], false);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("REMOVE FRIEND:")) {
                String aux[] = request.split(":");
                String res = manager.removeFriend(userLogged, aux[1]);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("SEND MESSAGE:")) {
                String aux[] = request.split(":");
                String res = manager.sendMessage(userLogged, aux[1], aux[2]);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("MESSAGE HISTORIAL:")) {
                sendableObjectsList ml = manager.messageBetweenUsersHistorial(userLogged, request.split(":")[1]);
                con.sendObject(msgID.toServer.request, ml);
            }
            else if (request.contains("FRIEND LIST")) {
                sendableObjectsList friends = manager.getUserFriends(userLogged);
                con.sendObject(msgID.toServer.request, friends);
            }
            else if (request.contains("PROFILE:")) {
                profile prof = manager.getUserProfile(request.split(":")[1]);
                con.sendObject(msgID.toServer.request, prof);
            }
            else if (request.contains("REGISTER GAME:")) {
                String aux[] = request.split(":");
                manager.registerGame(aux[1], aux[2], aux[3], aux[4], Integer.parseInt(aux[5]), Boolean.parseBoolean(aux[6]));
            }
            else if (request.contains("FRIENDS REQUESTS")) {
                sendableObjectsList res = manager.pendingFriendsRequestList(userLogged);
                con.sendObject(msgID.toServer.request, res);
            }
            else if (request.contains("FRIENDS PENDING MESSAGES")) {
                sendableObjectsList res = manager.pendingFriendsMessageList(userLogged);
                con.sendObject(msgID.toServer.request, res);
            }
            else if (request.contains("NOTIFY READ MESSAGES")) {
                String aux[] = request.split(":");
                manager.notifyMessagesRead(userLogged, aux[1]);
            }
            else if (request.contains("VERIFY ACCOUNT")) {
                String aux[] = request.split(":");
                String res = manager.verifyAccount(aux[1], Integer.parseInt(aux[2]));
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("RECOVER ACCOUNT")) {
                String aux[] = request.split(":");
                String res = manager.recoverAccount(aux[1]);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("CHANGE PASSWORD")) {
                String aux[] = request.split(":");
                String res = manager.changePassword(userLogged, aux[1], aux[2]);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("CHALLENGE FRIEND")) {
                String aux[] = request.split(":");
                String res = manager.challengeFriend(userLogged, aux[1]);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("CANCEL CHALLENGE")) {
                String res = manager.cancelInvitation(userLogged);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("ANSWER CHALLENGE")) {
                String aux[] = request.split(":");
                String res = manager.answerFriendChallenge(userLogged, aux[1], Boolean.parseBoolean(aux[2]));
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.equals("CANCEL SEARCH GAME")) {
                manager.stopSearchingGame(userLogged);
            }
            else if (request.equals("RANKING")) {
                con.sendObject(msgID.toServer.request, manager.loadRanking());
            }
            else if (request.contains("FIRST CONNECTION")) {
                Object cer;
                do {
                    Thread.sleep(200);
                    cer = con.receiveObject(msgID.toServer.request);
                } while ((cer == null || cer.equals("NONE") || cer.equals("")) && con.isConnected());
                String res = manager.addCertificateToKeystore((certificate) cer);
                con.sendString(msgID.toServer.request, res);
            }
            else if (request.contains("GET OWN IA")) {
                qtable q = manager.getQtable(userLogged);
                con.sendObject(msgID.toServer.request, q);
                con.sendString(msgID.toServer.request,"EPSILON:"+ (1.0-((double)manager.getPlayerIAtimes(userLogged))/50.0));
            }
            else if (request.contains("GET GLOBAL IA")) {
                qtable q = manager.getQtable("GLOBAL");
                con.sendObject(msgID.toServer.request, q);
                con.sendString(msgID.toServer.request,"EPSILON:"+ (1.0-((double)manager.getGlobalIaTimes())/100.0));
            }
            else if (request.contains("TRAIN OWN IA")) {
                Object table;
                do {
                    Thread.sleep(200);
                    table = con.receiveObject(msgID.toServer.request);
                } while ((table == null || table.equals("NONE") || table.equals("")) && con.isConnected());
                manager.trainOwnIA(userLogged, (qtable) table);
            }
            else if (request.contains("TRAIN GLOBAL IA")) {
                Object table;
                do {
                    Thread.sleep(200);
                    table = con.receiveObject(msgID.toServer.request);
                } while ((table == null || table.equals("NONE") || table.equals("")) && con.isConnected());
                manager.trainGlobalIA((qtable) table);
            }
        }catch (Exception e){
            e.printStackTrace();
            if(con.isConnected()) {
                con.sendString(msgID.toServer.request, "ERROR:Has been some problem.");
                con.sendObject(msgID.toServer.request, null);
            }
        }
    }

    /**
     * Is logged boolean.
     *
     * @return the boolean
     */
    public boolean isLogged(){
        return userLogged != null;
    }

    /**
     * Gets manager.
     *
     * @return the manager
     */
    public serverManager getManager() {
        return manager;
    }

    /**
     * Sets manager.
     *
     * @param manager the manager
     */
    public void setManager(serverManager manager) {
        this.manager = manager;
    }

    /**
     * Gets con.
     *
     * @return the con
     */
    public serverConnection getCon() {
        return con;
    }

    /**
     * Sets con.
     *
     * @param con the con
     */
    public void setCon(serverConnection con) {
        this.con = con;
    }

    /**
     * Gets client.
     *
     * @return the client
     */
    public InetAddress getClient() {
        return client;
    }

    /**
     * Sets client.
     *
     * @param client the client
     */
    public void setClient(InetAddress client) {
        this.client = client;
    }

    /**
     * Gets user logged.
     *
     * @return the user logged
     */
    public String getUserLogged() {
        return userLogged;
    }

    /**
     * Sets user logged.
     *
     * @param userLogged the user logged
     */
    public void setUserLogged(String userLogged) {
        this.userLogged = userLogged;
    }
}
