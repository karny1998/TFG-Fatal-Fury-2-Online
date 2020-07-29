package server;

import lib.utils.sendableObjects.sendableObject;
import lib.utils.packet;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class serverConnection{
    protected Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    protected int portReceive;
    protected Map<Integer, sendableObject> serverPendingObjects = new HashMap<>();
    protected Map<Integer,String> serverPendingMessages = new HashMap<>();
    protected receiver rec;
    protected  boolean blockReception = false;
    protected Semaphore sm = new Semaphore(1);

    public serverConnection(Socket socket) {
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }catch (Exception e){e.printStackTrace();}
        this.portReceive = socket.getLocalPort();
        this.rec = new receiver(this);
        this.rec.start();
    }

    public void sendString(int id, Object msg){
        send(id,msg,true);
    }

    public void sendObject(int id, Object msg){
        send(id,msg,false);
    }

    private void send(int id, Object msg, boolean string){
        try{
            packet p = null;
            if(string){
                p = new packet(id, false, (String)msg);
            }
            else{
                p = new packet(id, false, (sendableObject)msg);
            }
            out.writeObject(p);
        }catch (Exception e){e.printStackTrace();}
    }

    public boolean reliableSendString(int id, Object msg, int timeout){
        return reliableSend(id, (String)msg,timeout, true);
    }

    public boolean reliableSendObject(int id, Object msg, int timeout){
        return reliableSend(id, (String)msg,timeout, false);
    }

    private boolean reliableSend(int id, Object msg, int timeout, boolean string){
        boolean ok = false;
        for(int i = 0; i < 10 && !ok; ++i){
            try{
                packet p = null;
                if(string){
                    p = new packet(id, true, (String)msg);
                }
                else {
                    p = new packet(id, true, (sendableObject)msg);
                }
                out.writeObject(p);
            }catch (Exception e){e.printStackTrace();}
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(serverPendingMessages);
            String rec = receiveString(id);
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
        try{
            packet p = new packet(id, false, "ACK");
            out.writeObject(p);
        }catch (Exception e){e.printStackTrace();}
    }

    public String receiveString(int id){
        return (String)receive(id, true);
    }

    public Object receiveObject(int id){
        return receive(id, false);
    }

    public Object receive(int id, boolean string){
        if(blockReception || !socket.isConnected()){return "";}
        try {
            sm.acquire();
            Object msg;
            if(string && serverPendingMessages.containsKey(id)){
                msg = serverPendingMessages.get(id);
                serverPendingMessages.remove(id);
                sm.release();
                return msg;
            }
            else if(!string && serverPendingObjects.containsKey(id)){
                msg = serverPendingObjects.get(id);
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
            packet received = (packet) in.readObject();
            System.out.println("Se ha recibido: " + received.toString());
            if(!received.isObject() && received.getMessage().equals("HI")){
                sendAck(-1);
                return;
            }
            if(received.isReliable()){
                sendAck(received.getId());
            }
            if(received.isObject()){
                serverPendingObjects.put(received.getId(), received.getObject());
            }
            else {
                serverPendingMessages.put(received.getId(), received.getMessage());
            }
        }catch (Exception e){
            this.close();
            rec.doStop();
            /*e.printStackTrace();*/
        }
    }

    public boolean isConnected(){return socket.isConnected();}

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
                if(!con.isConnected()){
                    doStop();
                    con.close();
                }
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
