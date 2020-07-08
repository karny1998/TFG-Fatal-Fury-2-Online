package server;

import java.net.InetAddress;

public class requestManager {
    private serverManager manager;
    private serverConnection con;
    private InetAddress client;
    private int messagesId = 1;

    public requestManager(int msgId, serverManager manager, serverConnection con, InetAddress client){
        this.manager = manager;
        this.con = con;
        this.client = client;
        this.messagesId = msgId;
    }

    public void manageRequest(String request){
        if(request.contains("REGISTER")){
            String aux[] = request.split(":");
            String res = manager.registerUser(aux[1],aux[2],aux[3]);
            if(res.equals("OK")) {
                con.send(messagesId, "REGISTERED");
            }
            else{
                System.out.println("res");
                con.send(messagesId, res);
            }
        }
        else if(request.contains("LOGIN")){
            String aux[] = request.split(":");
            manager.connectUser(client, con, aux[1],aux[2]);
        }
        else if (request.equals("SEARCH GAME")) {
            manager.searchGame(client);
        }
    }
}
