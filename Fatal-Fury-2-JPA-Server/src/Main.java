import database.databaseManager;
import database.models.Game;
import database.models.Player;
import database.models.RankedGame;
import lib.utils.sendableObjects.simpleObjects.certificate;
import server.msgID;
import server.requestManager;
import server.serverConnection;
import server.serverManager;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Main.
 */
public class Main {
    private static databaseManager dbm = new databaseManager();
    private static serverManager manager = new serverManager(dbm);
    private static ServerSocket serverSocket;
    private static commander com;
    private static Map<InetAddress, clientHandler> threads = new HashMap<>();
    private static Map<String, clientHandler> threadsByUser = new HashMap<>();
    private static boolean  shutdown = false;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        try{
            //serverSocket = new ServerSocket(5555);
            serverSocket = generateTCPSecureServerSocket(5555);
            /*com = new commander(threads,serverSocket);
            com.start();*/
            while(!shutdown){
                Socket newCon = serverSocket.accept();
                System.out.println("Se ha aceptado una conexion.\n");
                System.out.print("Command: ");
                InetAddress newUser = newCon.getInetAddress();
                serverConnection con = new serverConnection(newCon);
                if(threads.containsKey(newUser)){
                    threadsByUser.remove(threads.get(newUser).getRqM().getUserLogged());
                    manager.desconnectUser(threads.get(newUser).getRqM().getUserLogged());
                    threads.get(newUser).doStop();
                    threads.remove(newUser);
                }
                clientHandler c = new clientHandler(con, newUser);
                threads.put(newUser, c);
                c.start();
            }
            manager.getUdpConnection().close();
        }catch (Exception e){
            e.printStackTrace();
            dbm.close();
            return;
        }
        dbm.close();
    }

    private static ServerSocket generateTCPSecureServerSocket(int port) throws IOException {
        String path = System.getProperty("user.dir") + "/certs/";
        System.setProperty("javax.net.ssl.keyStore", path+"serverKey.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","servpass");
        System.setProperty("javax.net.ssl.trustStore", path+"serverTrustedCerts.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "servpass");
        SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        return serverFactory.createServerSocket(port);
    }

    /**
     * The type Client handler.
     */
    protected static class clientHandler extends Thread{
        private serverConnection con;
        private InetAddress client;
        private boolean stop = false;
        private final Thread thread;
        private requestManager rqM;
        private boolean logged = false;
        private boolean forzedClose = false;
        private clientPinger cp;

        /**
         * Instantiates a new Client handler.
         *
         * @param con    the con
         * @param client the client
         */
        public clientHandler(serverConnection con, InetAddress client) {
            System.out.println("Conectado con el usuario: " + client.getHostAddress());
            this.thread = new Thread(this);
            this.con = con;
            this.client = client;
            rqM = new requestManager(manager,con,client);
            this.cp = new clientPinger(con);
            this.cp.start();
        }

        @Override
        public void start(){
            this.thread.start();
        }

        /**
         * Do stop.
         */
        public synchronized void doStop() {
            if(con.isConnected()) {
                con.sendString(msgID.toServer.notification, "SERVER CLOSED");
            }
            threads.remove(client);
            threadsByUser.remove(rqM.getUserLogged());
            con.close();
            manager.desconnectUser(rqM.getUserLogged());
            cp.doStop();
            this.stop = true;
        }

        /**
         * Do stop.
         *
         * @param msg the msg
         */
        public synchronized void doStop(String msg) {
            try {
                con.sendString(msgID.toServer.notification, "SERVER CLOSED");
            }catch (Exception e){}
            threads.remove(client);
            threadsByUser.remove(rqM.getUserLogged());
            con.sendString(msgID.toServer.notification,msg);
            con.close();
            cp.doStop();
            this.stop = true;
        }

        private synchronized boolean keepRunning() {
            return this.stop == false;
        }

        @Override
        public void run(){
            while(keepRunning()) {
                try {
                    if (forzedClose) {
                        threads.remove(client);
                        manager.desconnectUser(rqM.getUserLogged());
                        con.close();
                        doStop();
                    } else if (con.isConnected()) {
                        con.waitForRequestOrTramit();
                        String tramits = con.receiveString(msgID.toServer.tramits);
                        String request = con.receiveString(msgID.toServer.request);
                        if (tramits.equals("DISCONNECT") || forzedClose) {
                            threads.remove(client);
                            manager.desconnectUser(rqM.getUserLogged());
                            con.close();
                            cp.doStop();
                            doStop();
                        } else {
                            if(request.contains("LOG OFF")){
                                threadsByUser.remove(rqM.getUserLogged());
                            }
                            rqM.manageRequest(request);
                            if (!logged && rqM.isLogged()) {
                                logged = true;
                                String aux = "";
                                if (threadsByUser.containsKey(rqM.getUserLogged())) {
                                    aux = threadsByUser.get(rqM.getUserLogged()).getRqM().getCon().getSocket().getInetAddress().getHostAddress();
                                }
                                if (threadsByUser.containsKey(rqM.getUserLogged()) && !con.getSocket().getInetAddress().getHostAddress().equals(aux)) {
                                    rqM = threadsByUser.get(rqM.getUserLogged()).getRqM();
                                    rqM.setCon(con);
                                    threadsByUser.get(rqM.getUserLogged()).doStop("SESSION CLOSED:Se ha iniciado sesión desde otro ordenador.");
                                }
                                threadsByUser.put(rqM.getUserLogged(), this);
                            } else if (logged && !rqM.isLogged()) {
                                logged = false;
                            }
                        }
                    } else {
                        doStop();
                    }
                }catch (Exception e){
                    con.sendString(msgID.toServer.request, "ERROR:Has been a problem.");
                }
            }
        }

        /**
         * The type Client pinger.
         */
        protected class clientPinger extends Thread{
            private serverConnection con;
            private boolean stop = false;
            private final Thread thread;
            private long timeReference = System.currentTimeMillis();

            /**
             * Instantiates a new Client pinger.
             *
             * @param con the con
             */
            public clientPinger(serverConnection con) {
                this.con = con;
                this.thread = new Thread(this);
            }

            @Override
            public void start(){
                this.thread.start();
            }

            /**
             * Do stop.
             */
            public synchronized void doStop() {
                this.stop = true;
            }

            private synchronized boolean keepRunning() {
                return this.stop == false;
            }

            @Override
            public void run(){
                while(keepRunning()) {
                    try{
                        con.sendString(msgID.toServer.ping, "PING");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String p = con.receiveString(msgID.toServer.ping);
                    if(p.equals("PING")){
                        timeReference = System.currentTimeMillis();
                    }
                    else{
                        if(System.currentTimeMillis() - timeReference > 10000){
                            clientHandler.this.setForzedClose(true);
                            doStop();
                        }
                    }
                }
            }
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
            this.con.close();
            this.rqM.setCon(con);
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
         * Is stop boolean.
         *
         * @return the boolean
         */
        public boolean isStop() {
            return stop;
        }

        /**
         * Sets stop.
         *
         * @param stop the stop
         */
        public void setStop(boolean stop) {
            this.stop = stop;
        }

        /**
         * Gets thread.
         *
         * @return the thread
         */
        public Thread getThread() {
            return thread;
        }

        /**
         * Gets rq m.
         *
         * @return the rq m
         */
        public requestManager getRqM() {
            return rqM;
        }

        /**
         * Sets rq m.
         *
         * @param rqM the rq m
         */
        public void setRqM(requestManager rqM) {
            this.rqM = rqM;
        }

        /**
         * Is logged boolean.
         *
         * @return the boolean
         */
        public boolean isLogged() {
            return logged;
        }

        /**
         * Sets logged.
         *
         * @param logged the logged
         */
        public void setLogged(boolean logged) {
            this.logged = logged;
        }

        /**
         * Is forzed close boolean.
         *
         * @return the boolean
         */
        public boolean isForzedClose() {
            return forzedClose;
        }

        /**
         * Sets forzed close.
         *
         * @param forzedClose the forzed close
         */
        public void setForzedClose(boolean forzedClose) {
            this.forzedClose = forzedClose;
        }
    }

    /**
     * The type Commander.
     */
    protected static class commander extends Thread{

        private Map<InetAddress, clientHandler> threads = new HashMap<>();

        private ServerSocket socket;

        private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        private boolean stop = false;

        private final Thread thread;

        /**
         * Instantiates a new Commander.
         *
         * @param threads the threads
         * @param socket  the socket
         */
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

        /**
         * Do stop.
         */
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
                    try {
                        for(Map.Entry<InetAddress,clientHandler> c : threads.entrySet()){
                            try {
                                c.getValue().doStop();
                            }catch (Exception e){}
                        }
                    }catch (Exception e){}
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

    /**
     * The type Terminal.
     */
    protected static class terminal{
        /**
         * The P.
         */
        Process p;
        /**
         * The W.
         */
        BufferedWriter w;
        /**
         * The R.
         */
        BufferedReader r;

        /**
         * Instantiates a new Terminal.
         */
        public terminal(){
            try{
                p = Runtime.getRuntime().exec("cmd /c start cmd.exe");
                w = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            }catch (Exception e){e.printStackTrace();}
        }

        /**
         * Write.
         *
         * @param msg the msg
         */
        public void write(String msg){
            try {
                w.write(msg + System.lineSeparator());
                w.flush();
            }catch (Exception e){e.printStackTrace();}
        }

        /**
         * Read line string.
         *
         * @return the string
         */
        public String readLine(){
            try{
                return r.readLine();
            }catch (Exception e){e.printStackTrace();}
            return "";
        }
    }
}
