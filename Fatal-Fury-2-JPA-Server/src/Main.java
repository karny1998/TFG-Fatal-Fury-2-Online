import database.databaseManager;
import database.models.Game;
import database.models.Player;
import database.models.RankedGame;
import server.requestManager;
import server.serverConnection;
import server.serverManager;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static databaseManager dbm = new databaseManager();
    private static serverManager manager = new serverManager(dbm);
    private static ServerSocket serverSocket;
    private static commander com;
    private static Map<InetAddress, clientHandler> threads = new HashMap<>();
    private static boolean  shutdown = false;

    public static void main(String[] args) {
        try{
            serverSocket = new ServerSocket(5555);
            com = new commander(threads,serverSocket);
            com.start();
            while(!shutdown){
                Socket newCon = serverSocket.accept();
                System.out.println("Se ha aceptado una conexion.\n");
                System.out.print("Command: ");
                InetAddress newUser = newCon.getInetAddress();
                serverConnection con = new serverConnection(newCon);
                if(threads.containsKey(newUser)){
                    threads.get(newUser).setCon(con);
                }
                else {
                    //manager.connectUser(newUser,con);
                    clientHandler c = new clientHandler(con, newUser);
                    threads.put(newUser, c);
                    c.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            dbm.close();
            return;
        }
        dbm.close();
    }

    protected static class clientHandler extends Thread{
        private serverConnection con;
        private InetAddress client;
        private boolean stop = false;
        private final Thread thread;
        private int requestId = 1, tramitsId = -1;
        private requestManager rqM;

        public clientHandler(serverConnection con, InetAddress client) {
            System.out.println("Conectado con el usuario: " + client.getHostAddress());
            this.thread = new Thread(this);
            this.con = con;
            this.client = client;
            rqM = new requestManager(requestId,manager,con,client);
        }

        @Override
        public void start(){
            this.thread.start();
        }

        public synchronized void doStop() {
            this.stop = true;
        }

        private synchronized boolean keepRunning() {
            return this.stop == false;
        }

        @Override
        public void run(){
            while(keepRunning()) {
                if(con.isConnected()) {
                    String tramits = con.receive(tramitsId);
                    String request = con.receive(requestId);
                    if (tramits.equals("DISCONNECT")) {
                        threads.remove(client);
                        manager.desconnectUser(client);
                        con.close();
                        doStop();
                    } else {
                        rqM.manageRequest(request);
                    }
                }
                else{
                    doStop();
                }
            }
        }

        public serverConnection getCon() {
            return con;
        }

        public void setCon(serverConnection con) {
            this.con = con;
        }
    }

    protected static class commander extends Thread{

        private Map<InetAddress, clientHandler> threads = new HashMap<>();

        private ServerSocket socket;

        private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        private boolean stop = false;

        private final Thread thread;

        public commander(Map<InetAddress, clientHandler> threads, ServerSocket socket) {
            this.socket = socket;
            this.thread = new Thread(this);
            this.threads = threads;
            System.out.println("Gestión del servidor");
            System.out.println("--------------------");
        }

        @Override
        public void start(){
            this.thread.start();
        }

        public synchronized void doStop() {
            this.stop = true;
        }

        private synchronized boolean keepRunning() {
            return this.stop == false;
        }

        @Override
        public void run(){
            while(keepRunning()) {
                System.out.print("Command: ");
                String cm = "";
                try {
                    cm = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(cm.equals("exit")){
                    for(Map.Entry<InetAddress,clientHandler> c : threads.entrySet()){
                        c.getValue().doStop();
                    }
                    doStop();
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
                else{
                    System.out.println("No se ha reconocido el comando: " + cm +"\n");
                }
            }
        }
    }

    protected static class terminal{
        Process p;
        BufferedWriter w;
        BufferedReader r;

        public terminal(){
            try{
                p = Runtime.getRuntime().exec("cmd /c start cmd.exe");
                w = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            }catch (Exception e){e.printStackTrace();}
        }

        public void write(String msg){
            try {
                w.write(msg + System.lineSeparator());
                w.flush();
            }catch (Exception e){e.printStackTrace();}
        }

        public String readLine(){
            try{
                return r.readLine();
            }catch (Exception e){e.printStackTrace();}
            return "";
        }
    }
}
