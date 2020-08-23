package server;

import lib.utils.sendableObjects.sendableObject;
import lib.utils.packet;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * The type Server connection.
 */
public class serverConnection{
    /**
     * The Socket.
     */
    protected Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    /**
     * The Port receive.
     */
    protected int portReceive;
    /**
     * The Server pending objects.
     */
    protected Map<Integer, sendableObject> serverPendingObjects = new HashMap<>();
    /**
     * The Server pending messages.
     */
    protected Map<Integer,String> serverPendingMessages = new HashMap<>();
    /**
     * The Rec.
     */
    protected receiver rec;
    /**
     * The Block reception.
     */
    protected  boolean blockReception = false;
    /**
     * The Sm.
     */
    protected Semaphore sm = new Semaphore(1), /**
     * The Request sm.
     */
    requestSM = new Semaphore(1);

    /**
     * Instantiates a new Server connection.
     *
     * @param socket the socket
     */
    public serverConnection(Socket socket) {
        this.socket = socket;
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            requestSM.acquire();
        }catch (Exception e){/*e.printStackTrace();*/}
        this.portReceive = socket.getLocalPort();
        this.rec = new receiver(this);
        this.rec.start();
    }

    /**
     * Send string.
     *
     * @param id  the id
     * @param msg the msg
     */
    public void sendString(int id, Object msg){
        send(id,msg,true);
    }

    /**
     * Send object.
     *
     * @param id  the id
     * @param msg the msg
     */
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
            synchronized (out) {
                out.writeObject(p);
            }
            if(id != 0) {
                System.out.println("Manda " + p.toString());
            }
        }catch (Exception e){
            //e.printStackTrace();
            try {
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            socket = null;
            requestSM.release();
        }
    }

    /**
     * Reliable send string boolean.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the boolean
     */
    public boolean reliableSendString(int id, Object msg, int timeout){
        return reliableSend(id, (String)msg,timeout, true);
    }

    /**
     * Reliable send object boolean.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the boolean
     */
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
                synchronized (out) {
                    out.writeObject(p);
                }
            }catch (Exception e){/*e.printStackTrace();*/}
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                /*e.printStackTrace();*/
            }
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
            synchronized (out) {
                out.writeObject(p);
            }
        }catch (Exception e){/*e.printStackTrace();*/}
    }

    /**
     * Receive string string.
     *
     * @param id the id
     * @return the string
     */
    public String receiveString(int id){
        return (String)receive(id, true);
    }

    /**
     * Receive object object.
     *
     * @param id the id
     * @return the object
     */
    public Object receiveObject(int id){
        return receive(id, false);
    }

    /**
     * Receive object.
     *
     * @param id     the id
     * @param string the string
     * @return the object
     */
    public Object receive(int id, boolean string){
        if(blockReception || socket == null){return "";}
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
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            socket = null;
            sm.release();
            requestSM.release();
        }
        sm.release();
        return "";
    }

    /**
     * Wait for request or tramit.
     */
    public void waitForRequestOrTramit(){
        if (serverPendingMessages.containsKey(msgID.toServer.request)
            || serverPendingMessages.containsKey(msgID.toServer.tramits)
            || serverPendingObjects.containsKey(msgID.toServer.tramits)
            || serverPendingObjects.containsKey(msgID.toServer.request)) {
            return;
        }
        else{
            try {
                requestSM.acquire();
            }catch (Exception e){/*e.printStackTrace();*/}
            if(socket != null) {
                waitForRequestOrTramit();
            }
        }
    }

    /**
     * Receive.
     */
    public void receive(){
        if(blockReception){return;}
        try {
            packet received;
            synchronized (in) {
                received = (packet) in.readObject();
            }
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
                if(received.getId() != msgID.toServer.ping) {
                    System.out.println("Se recibe: " + received.getMessage());
                }
                serverPendingMessages.put(received.getId(), received.getMessage());
            }
            if(received.getId() == msgID.toServer.request || received.getId() == msgID.toServer.tramits){
                requestSM.release();
            }
        }catch (Exception e){
            this.close();
            rec.doStop();
            socket = null;
            /*e.printStackTrace();*/
        }
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected(){return socket != null;}

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

    /**
     * Close.
     */
    public void  close(){
        try {
            in.close();
            out.close();
            rec.doStop();
            if(socket != null) {
                socket.close();
            }
        }catch (Exception e){/*e.printStackTrace();*/}
    }

    /**
     * Gets socket.
     *
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets socket.
     *
     * @param socket the socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Gets out.
     *
     * @return the out
     */
    public ObjectOutputStream getOut() {
        return out;
    }

    /**
     * Sets out.
     *
     * @param out the out
     */
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    /**
     * Gets in.
     *
     * @return the in
     */
    public ObjectInputStream getIn() {
        return in;
    }

    /**
     * Sets in.
     *
     * @param in the in
     */
    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    /**
     * Gets port receive.
     *
     * @return the port receive
     */
    public int getPortReceive() {
        return portReceive;
    }

    /**
     * Sets port receive.
     *
     * @param portReceive the port receive
     */
    public void setPortReceive(int portReceive) {
        this.portReceive = portReceive;
    }

    /**
     * Gets server pending objects.
     *
     * @return the server pending objects
     */
    public Map<Integer, sendableObject> getServerPendingObjects() {
        return serverPendingObjects;
    }

    /**
     * Sets server pending objects.
     *
     * @param serverPendingObjects the server pending objects
     */
    public void setServerPendingObjects(Map<Integer, sendableObject> serverPendingObjects) {
        this.serverPendingObjects = serverPendingObjects;
    }

    /**
     * Gets server pending messages.
     *
     * @return the server pending messages
     */
    public Map<Integer, String> getServerPendingMessages() {
        return serverPendingMessages;
    }

    /**
     * Sets server pending messages.
     *
     * @param serverPendingMessages the server pending messages
     */
    public void setServerPendingMessages(Map<Integer, String> serverPendingMessages) {
        this.serverPendingMessages = serverPendingMessages;
    }

    /**
     * Gets rec.
     *
     * @return the rec
     */
    public receiver getRec() {
        return rec;
    }

    /**
     * Sets rec.
     *
     * @param rec the rec
     */
    public void setRec(receiver rec) {
        this.rec = rec;
    }

    /**
     * Is block reception boolean.
     *
     * @return the boolean
     */
    public boolean isBlockReception() {
        return blockReception;
    }

    /**
     * Sets block reception.
     *
     * @param blockReception the block reception
     */
    public void setBlockReception(boolean blockReception) {
        this.blockReception = blockReception;
    }

    /**
     * Gets sm.
     *
     * @return the sm
     */
    public Semaphore getSm() {
        return sm;
    }

    /**
     * Sets sm.
     *
     * @param sm the sm
     */
    public void setSm(Semaphore sm) {
        this.sm = sm;
    }

    /**
     * Gets request sm.
     *
     * @return the request sm
     */
    public Semaphore getRequestSM() {
        return requestSM;
    }

    /**
     * Sets request sm.
     *
     * @param requestSM the request sm
     */
    public void setRequestSM(Semaphore requestSM) {
        this.requestSM = requestSM;
    }
}
