package server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class serverManager {
    private Map<InetAddress, serverConnection> connectedUsers = new HashMap<>();
    private List<InetAddress> searchingGameUsers = new ArrayList<>();
    private boolean shutingDown = false;

    public serverManager(){}

    public synchronized void connectUser(InetAddress add, serverConnection sc){
        connectedUsers.put(add, sc);
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
        boolean okP1 =  connectedUsers.get(p1).reliableSend(2,msg1,500);
        boolean okP2 =  connectedUsers.get(p2).reliableSend(2,msg2,500);
        return  okP1 && okP2;
    }

    public synchronized void stopSearchingGame(InetAddress player){
        searchingGameUsers.remove(player);
    }
}
