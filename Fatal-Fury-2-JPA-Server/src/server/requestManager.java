package server;

import lib.utils.sendableObjects.sendableObjectsList;

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
        if(request.contains("REGISTER")){
            String aux[] = request.split(":");
            String res = manager.registerUser(aux[1],aux[2],aux[3]);
            if(res.equals("OK")) {
                con.sendString(msgID.toServer.request, "REGISTERED");
            }
            else{
                con.sendString(msgID.toServer.request, res);
            }
        }
        else if(request.contains("LOGIN")){
            if(userLogged == null) {
                String aux[] = request.split(":");
                if (manager.connectUser(client, con, aux[1], aux[2])) {
                    userLogged = aux[1];
                }
            }
            else{
                con.sendString(1,"E:Estás logeado en otra cuenta");
            }
        }
        else if(request.contains("SIGN OFF")){
            if(userLogged != null) {
                String aux[] = request.split(":");
                userLogged = null;
                con.sendString(msgID.toServer.request, "SINGNED OFF");
            }
            else{
                con.sendString(msgID.toServer.request,"E:No estás logeado en ninguna cuenta");
            }
        }
        else if (request.equals("SEARCH GAME")) {
            manager.searchGame(client);
        }
        else if(request.contains("SEND FRIEND REQUEST")){
            String aux[] = request.split(":");
            String res = manager.friendsRequest(aux[1],aux[2]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("ACCEPT FRIEND REQUEST")){
            String aux[] = request.split(":");
            String res = manager.answerFriendsRequest(aux[1],aux[2], true);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("REJECT FRIEND REQUEST")){
            String aux[] = request.split(":");
            String res = manager.answerFriendsRequest(aux[1],aux[2], false);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("REMOVE FRIEND")){
            String aux[] = request.split(":");
            String res = manager.removeFriend(aux[1],aux[2]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("SEND MESSAGE")){
            String aux[] = request.split(":");
            String res = manager.sendMessage(userLogged, aux[1],aux[2]);
            con.sendString(msgID.toServer.request, res);
        }
        else if(request.contains("MESSAGE HISTORIAL")){
            sendableObjectsList ml = manager.messageBetweenUsersHistorial(userLogged, request.split(":")[1]);
            con.sendObject(msgID.toServer.request,ml);
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
