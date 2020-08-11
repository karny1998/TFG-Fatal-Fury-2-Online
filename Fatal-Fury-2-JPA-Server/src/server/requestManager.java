package server;

import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.profile;

import java.net.InetAddress;

public class requestManager {
    private serverManager manager;
    private serverConnection con;
    private InetAddress client;
    private String userLogged = null;

    public requestManager(serverManager manager, serverConnection con, InetAddress client){
        this.manager = manager;
        this.con = con;
        this.client = client;
    }

    public void manageRequest(String request){
        if(request.contains("REGISTER:")){
            String aux[] = request.split(":");
            String res = manager.registerUser(aux[1],aux[2],aux[3]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("LOGIN:")){
            if(userLogged == null) {
                String aux[] = request.split(":");
                String res = manager.connectUser(client, con, aux[1], aux[2]);
                if (res.equals("LOGGED")) {
                    userLogged = aux[1];
                }
                con.sendString(msgID.toServer.request,res);
            }
            else{
                con.sendString(1,"E:Estás logeado en otra cuenta");
            }
        }
        else if(request.contains("LOG OFF")){
            if(userLogged != null) {
                manager.desconnectUser(userLogged);
                userLogged = null;
                con.sendString(msgID.toServer.request, "LOGGED OFF");
            }
            else{
                con.sendString(msgID.toServer.request,"E:No estás logeado en ninguna cuenta");
            }
        }
        else if (request.equals("SEARCH GAME")) {
            if(userLogged != null) {
                manager.searchGame(userLogged, false);
            }
            else{
                con.sendString(msgID.toServer.request,"E:No estás logeado en ninguna cuenta");
            }
        }
        else if (request.equals("SEARCH RANKED GAME")) {
            if(userLogged != null) {
                manager.searchGame(userLogged, true);
            }
            else{
                con.sendString(msgID.toServer.request,"E:No estás logeado en ninguna cuenta");
            }
        }
        else if(request.contains("SEND FRIEND REQUEST:")){
            String aux[] = request.split(":");
            String res = manager.friendsRequest(aux[1],aux[2]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("ACCEPT FRIEND REQUEST:")){
            String aux[] = request.split(":");
            String res = manager.answerFriendsRequest(aux[1],aux[2], true);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("REJECT FRIEND REQUEST:")){
            String aux[] = request.split(":");
            String res = manager.answerFriendsRequest(aux[1],aux[2], false);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("REMOVE FRIEND:")){
            String aux[] = request.split(":");
            String res = manager.removeFriend(userLogged,aux[1]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("SEND MESSAGE:")){
            String aux[] = request.split(":");
            String res = manager.sendMessage(userLogged, aux[1],aux[2]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("MESSAGE HISTORIAL:")){
            sendableObjectsList ml = manager.messageBetweenUsersHistorial(userLogged, request.split(":")[1]);
            con.sendObject(msgID.toServer.request,ml);
        }
        else if(request.contains("FRIEND LIST")){
            sendableObjectsList friends = manager.getUserFriends(userLogged);
            con.sendObject(msgID.toServer.request,friends);
        }
        else if(request.contains("PROFILE:")){
            profile prof = manager.getUserProfile(request.split(":")[1]);
            con.sendObject(msgID.toServer.request,prof);
        }
        else if(request.contains("REGISTER GAME:")){
            String aux[] = request.split(":");
            String res = manager.registerGame(aux[1], aux[2], aux[3], aux[4], Integer.parseInt(aux[5]), Boolean.parseBoolean(aux[6]));
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("FRIENDS REQUESTS")){
            sendableObjectsList res = manager.pendingFriendsRequestList(userLogged);
            con.sendObject(msgID.toServer.request, res);
        }
        else if(request.contains("FRIENDS PENDING MESSAGES")){
            sendableObjectsList res = manager.pendingFriendsMessageList(userLogged);
            con.sendObject(msgID.toServer.request, res);
        }
        else if(request.contains("NOTIFY READ MESSAGES")){
            String aux[] = request.split(":");
            manager.notifyMessagesRead(userLogged, aux[1]);
        }
        else if(request.contains("VERIFY ACCOUNT")){
            String aux[] = request.split(":");
            String res = manager.verifyAccount(aux[1], Integer.parseInt(aux[2]));
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("RECOVER ACCOUNT")){
            String aux[] = request.split(":");
            String res = manager.recoverAccount(aux[1]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("CHANGE PASSWORD")){
            String aux[] = request.split(":");
            String res = manager.changePassword(userLogged,aux[1], aux[2]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("CHALLENGE FRIEND")){
            String aux[] = request.split(":");
            String res = manager.challengeFriend(userLogged,aux[1]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("CANCEL CHALLENGE")){
            String res = manager.cancelInvitation(userLogged);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("ANSWER CHALLENGE")){
            String aux[] = request.split(":");
            String res = manager.answerFriendChallenge(userLogged, aux[1],Boolean.parseBoolean(aux[2]));
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.equals("CALCEL SEARCH GAME")){
            manager.stopSearchingGame(userLogged);
        }
        else if(request.equals("RANKING")){
            con.sendObject(msgID.toServer.request, manager.loadRanking());
        }
    }

    public boolean isLogged(){
        return userLogged != null;
    }

    public serverManager getManager() {
        return manager;
    }

    public void setManager(serverManager manager) {
        this.manager = manager;
    }

    public serverConnection getCon() {
        return con;
    }

    public void setCon(serverConnection con) {
        this.con = con;
    }

    public InetAddress getClient() {
        return client;
    }

    public void setClient(InetAddress client) {
        this.client = client;
    }

    public String getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(String userLogged) {
        this.userLogged = userLogged;
    }
}
