package com.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class serverConnection{
    protected Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    protected int portReceive;
    protected Map<Integer,String> serverPendingMessages = new HashMap<>();
    protected receiver rec;
    protected  boolean blockReception = false;
    protected Semaphore sm = new Semaphore(1);

    public serverConnection(Socket socket) {
        this.socket = socket;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (Exception e){e.printStackTrace();}
        this.portReceive = socket.getLocalPort();
        this.rec = new receiver(this);
        this.rec.start();
    }

    public void send(int id, String msg){
        out.println(Integer.toString(id) + ";NR;"+msg);
    }

    public boolean reliableSend(int id, String msg, int timeout){
        boolean ok = false;
        for(int i = 0; i < 10 && !ok; ++i){
            out.println(Integer.toString(id) + ";R;"+msg);
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(serverPendingMessages);
            String rec = receive(id);
            if(rec.equals("ACK")){
                ok = true;
            }
        }
        return ok;
    }

    /**
     * Envía un ack de respuesta al mensaje idenfitifado por id.
     *
     * @param id the id
     */
    public void sendAck(int id){
        out.println(Integer.toString(id) + ";NR;ACK");
    }

    public String receive(int id){
        if(blockReception){return "";}
        try {
            sm.acquire();
            if(serverPendingMessages.containsKey(id)){
                String msg = serverPendingMessages.get(id);
                serverPendingMessages.remove(id);
                sm.release();
                return msg;
            }
            sm.release();
            return "NONE";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            sm.release();
        }
        sm.release();
        return "";
    }

    public void receive(){
        if(blockReception){return;}
        try {
            String received = in.readLine();
            System.out.println("Se ha recibido: " + received);
            if(received.equals("HI")){
                sendAck(-1);
                return;
            }
            String aux[] = received.split(";");
            boolean reliable = aux[1].equals("R");
            if(reliable){
                sendAck(Integer.parseInt(aux[0]));
            }
            serverPendingMessages.put(Integer.parseInt(aux[0]), aux[2]);
        }catch (Exception e){}
    }

    /**
     * The type Receiver.
     */
    protected class receiver extends Thread{
        /**
         * The Con.
         */
        private serverConnection con;
        /**
         * The Stop.
         */
        private boolean stop = false;
        /**
         * The Thread.
         */
        private final Thread thread;

        /**
         * Instantiates a new Receiver.
         *
         * @param con the con
         */
        public receiver(serverConnection con) {
            this.thread = new Thread(this);
            this.con = con;
        }

        /**
         * Start.
         */
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

        /**
         * Keep running boolean.
         *
         * @return the boolean
         */
        private synchronized boolean keepRunning() {
            return this.stop == false;
        }

        /**
         * Run.
         */
        @Override
        public void run(){
            while(keepRunning()) {
                con.receive();
            }
        }
    }

    public void  close(){
        try {
            socket.close();
            in.close();
            out.close();
            rec.doStop();
        }catch (Exception e){e.printStackTrace();}
    }
}
