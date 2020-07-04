package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static serverManager manager = new serverManager();
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
                    manager.connectUser(newUser,con);
                    clientHandler c = new clientHandler(con, newUser);
                    threads.put(newUser, c);
                    c.start();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
    }

    protected static class clientHandler extends Thread{

        private serverConnection con;

        private InetAddress client;

        private boolean stop = false;

        private final Thread thread;

        public clientHandler(serverConnection con, InetAddress client) {
            System.out.println("Conectado con el usuario: " + client.getHostAddress());
            this.thread = new Thread(this);
            this.con = con;
            this.client = client;
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

        private void waitConfirm(){
            boolean ok = false;
            while(!ok ){
                ok = con.reliableSend(-2,"WAITING CONFIRM", 200);
            }
        }

        @Override
        public void run(){
            while(keepRunning()) {
                String tramits = con.receive(-1);
                String request = con.receive(1);
                if(tramits.equals("DISCONNECT")){
                    threads.remove(client);
                    manager.desconnectUser(client);
                    doStop();
                }
                else{
                    if(request.equals("SEARCH GAME")){
                        //waitConfirm();
                        manager.searchGame(client);
                    }
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
}
