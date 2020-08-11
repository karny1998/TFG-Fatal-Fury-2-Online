package lib.objects.networking;

import lib.utils.Pair;
import lib.utils.packet;
import lib.utils.sendableObjects.sendableObject;
import org.netlib.lapack.Ssycon;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Conexión mediante sockets UDP con un receptor.
 * Los mensajes al enviarse y recibirse siguen el siguiente patrón:
 * id:reliable:menssage
 * Siendo id el identificador del mensaje,
 * reliable R o NR, según si pide confirmación o no,
 * y message el mensaje a transmitir.
 * El caso de que el mensaje contenga R en reliable, se esperará una confirmación (ACK)
 * por parte del receptor
 */
public class connection {
    /**
     * The Socket.
     */
    protected DatagramSocket socketUDP;

    /**
     * The Socket tcp.
     */
    protected Socket socketTCP;
    /**
     * The Address.
     */
    protected InetAddress address;
    /**
     * The Timeout.
     */
    protected int timeout = 50;
    /**
     * The Port send.
     */
    protected int portSend,
    /**
     * The Port receive.
     */
    portReceive;
    /**
     * The Buffer to receive.
     */
    protected byte[] bufReceive = new byte[65535];
    /**
     * The Buffer to send.
     */
    protected byte[] bufSend;
    /**
     * The Pending messages.
     */
    protected Map<Integer, String> pendingMsgs = new HashMap<>();

    protected Map<Integer, String> pendingACKs = new HashMap<>();

    protected Map<Integer, sendableObject> pendingObjects = new HashMap<>();
    /**
     * The proccess which receives messages constantly.
     */
    protected receiver rec;

    /**
     * The Block reception.
     */
    protected boolean blockReception = false;

    /**
     * The Sm.
     */
    protected Semaphore sm = new Semaphore(1),
            notificationSM = new Semaphore(1);

    /**
     * The Out.
     */
    private ObjectOutputStream out;

    /**
     * The In.
     */
    private ObjectInputStream in;

    /**
     * The Is udp.
     */
    private boolean isUDP = true;

    private List<Pair<Boolean, Boolean>> waiterList = new ArrayList<>();

    /**
     * Instantiates a new Connection.
     */
    public connection() {}

    /**
     * Instantiates a new Connection.
     *
     * @param ip      the ip
     * @param port    the port
     * @param timeout the timeout
     * @param isUDP   the is udp
     */
    public connection(String ip, int port, int timeout, boolean isUDP) {
        this.isUDP = isUDP;
        this.timeout = timeout;
        try {
            if(isUDP) {
                socketUDP = new DatagramSocket(port);
                this.portReceive = port;
                if (timeout > 0) {
                    socketUDP.setSoTimeout(timeout);
                }
                address = InetAddress.getByName(ip);
            }
            else{
                socketTCP = new Socket(ip, port);
                if (timeout > 0) {
                    socketTCP.setSoTimeout(timeout);
                }
                out = new ObjectOutputStream (socketTCP.getOutputStream());
                in = new ObjectInputStream(socketTCP.getInputStream());
            }
            notificationSM.acquire();
        }catch (Exception e){
            e.printStackTrace();
            socketTCP = null;
            socketUDP = null;
        }
        this.rec = new receiver(this);
        this.rec.start();
    }

    public void sendStringReliable(int id, String msg){
        send(id, msg, true, "R");
    }

    public void sendObjectReliable(int id, Object msg){
        send(id, msg, false, "R");
    }

    public void sendString(int id, String msg){
        send(id, msg, true, "NR");
    }

    public void sendObject(int id, Object msg){
        send(id, msg, false, "NR");
    }

    /**
     * Envía el mensaje msg con identificador id sin confirmar la recepción
     *
     * @param id  the id
     * @param msg the msg
     */
    private void send(int id, Object msg, boolean string, String r){
        if(isUDP) {
            bufSend = (Integer.toString(id) + ";"+ r + ";" + (String)msg).getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                socketUDP.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                packet p = null;
                if(string){
                    p = new packet(id, false, (String)msg);
                }
                else {
                    p = new packet(id, false, (sendableObject)msg);
                }
                out.writeObject(p);
            }catch (Exception e){/*e.printStackTrace();*/}
        }
    }

    /**
     * Envía un ack de respuesta al mensaje idenfitifado por id.
     *
     * @param id the id
     */
    public void sendAck(int id){
        if(isUDP) {
            bufSend = (Integer.toString(id) + ";NR;ACK").getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                socketUDP.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            try{
                packet p = new packet(id, false, "ACK");
                out.writeObject(p);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Send hi.
     */
    public boolean sendHi(){
        long timeReference = System.currentTimeMillis();
        if(isUDP) {
            bufSend = ("HI").getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                boolean ok = false;
                while (!ok && System.currentTimeMillis()-timeReference < 5000) {
                    System.out.println("se envia hi");
                    socketUDP.send(packet);
                    Thread.sleep(100);
                    if (receiveACK(msgID.toClient.hi)) {
                        ok = true;
                    }
                }
                return ok;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            try{
                packet p = new packet(0, false, "HI");
                out.writeObject(p);
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean receiveACK(int id){
        boolean ack = pendingACKs.containsKey(id);
        if(ack){
            pendingACKs.remove(id);
        }
        return ack;
    }

    public String receiveString(int id){
        return (String)receive(id,true);
    }

    public Object receiveObject(int id){
        return receive(id,false);
    }

    /**
     * Devuelve el mensaje asociando al identificador id  (cadena vacía si no se ha recibido) string.
     *
     * @param id the id
     * @return the object
     */
    private Object receive(int id, boolean string){
        if(blockReception){return "";}
        try {
            sm.acquire();
            if((string || isUDP) && pendingMsgs.containsKey(id)){
                String msg = pendingMsgs.get(id);
                pendingMsgs.remove(id);
                sm.release();
                return msg;
            }
            else if(!string && pendingObjects.containsKey(id)){
                Object obj = pendingObjects.get(id);
                pendingObjects.remove(id);
                sm.release();
                return  obj;
            }
            sm.release();
            if(string) {
                return "NONE";
            }
            else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sm.release();
        }
        sm.release();
        return "NONE";
    }

    /**
     * Recibe cualquier mensaje pendiente y lo guarda en pendingMessages.
     */
    public void receive(){
        if(blockReception || socketTCP != null && !socketTCP.isConnected()){
            return;
        }
        //if(blockReception || socketTCP != null && !socketTCP.isConnected()){return;}
        try {
            if(isUDP) {
                String received = "";
                DatagramPacket packet
                        = new DatagramPacket(bufReceive, bufReceive.length);
                socketUDP.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                received = new String(packet.getData(), 0, packet.getLength());
                if(received.equals("HI")){
                    sendAck(msgID.toClient.hi);
                    return;
                }
                String aux[] = received.split(";");
                int idM = Integer.parseInt(aux[0]);
                boolean reliable = aux[1].equals("R");
                System.out.println("Se recibe" + received);
                // Envía la confirmación si corresponde
                if(reliable){
                    System.out.println("Se envia ack");
                    sendAck(idM);
                }
                try {
                    sm.acquire();
                    if(aux.length == 2){
                        pendingMsgs.put(idM, "");
                    }
                    else {
                        if(aux[2].equals("ACK")){
                            pendingACKs.put(idM, aux[2]);
                        }
                        else {
                            pendingMsgs.put(idM, aux[2]);
                        }
                    }
                    //System.out.println("Se recibe: " + aux[2]);
                }catch (Exception e){
                    e.printStackTrace();
                    sm.release();
                }
                sm.release();
            }
            else{
                packet received = (packet) in.readObject();
                if(received.isReliable()){
                    sendAck(received.getId());
                }
                try {
                    sm.acquire();
                    if(received.isObject()){
                        pendingObjects.put(received.getId(), received.getObject());
                        System.out.println("Se recibe: " + received.getObject().toString());
                    }
                    else {
                        pendingMsgs.put(received.getId(), received.getMessage());
                        System.out.println("Se recibe: " + received.getMessage());
                        if(received.getId() == msgID.toServer.notification){
                            notificationSM.release();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    sm.release();
                }
                sm.release();
            }

            for(int i = 0; i < waiterList.size(); ++i) {
                synchronized (waiterList.get(i).first) {
                    waiterList.get(i).second = true;
                    waiterList.get(i).first.notify();
                }
            }

        }catch (Exception e){
            /*e.printStackTrace();*/
            socketTCP = null;
            socketUDP = null;
        }
    }

    public String sendStringWaitingAnswerString(int id, Object msg, int timeout){
        return (String) sendWaitingAnswer(id, msg, timeout, true, true);
    }
    public String sendObjectWaitingAnswerString(int id, Object msg, int timeout){
        return (String) sendWaitingAnswer(id, msg, timeout, false, true);
    }
    public Object sendStringWaitingAnswerObject(int id, Object msg, int timeout){
        return sendWaitingAnswer(id, msg, timeout, true, false);
    }
    public Object sendObjectWaitingAnswerObject(int id, Object msg, int timeout){
        return sendWaitingAnswer(id, msg, timeout, false, false);
    }

    private Object sendWaitingAnswer(int id, Object msg, int timeout, boolean sendString, boolean receiveString){
        if(isUDP) {
            try {
                for (int i = 0; i < 10; ++i) {
                    send(id, msg, sendString, "NR");
                    Thread.sleep(timeout);
                    if (receiveString) {
                        String res = receiveString(id);
                        if (res != null && !res.equals("") && !res.equals("NONE")) {
                            return res;
                        }
                    } else {
                        Object res = receiveObject(id);
                        if (res != null) {
                            return res;
                        }
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        else{
            send(id, msg, sendString, "NR");
            boolean error = false;
            long ref = System.currentTimeMillis();
            Pair<Boolean,Boolean> localWaiter =  new Pair<>(new Boolean(true),new Boolean(false));
            synchronized(waiterList) {
                waiterList.add(localWaiter);
            }
            while(!error && !(timeout > 0 && System.currentTimeMillis() - ref > timeout)){
                if (receiveString) {
                    String res = receiveString(id);
                    if (res != null && !res.equals("") && !res.equals("NONE")) {
                        return res;
                    }
                } else {
                    Object res = receiveObject(id);
                    if (res != null) {
                        return res;
                    }
                }
                try {
                    synchronized(localWaiter.first) {
                        while (!localWaiter.second) {
                            localWaiter.first.wait();
                        }
                        waiterList.remove(localWaiter);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    error = true;
                }
            }
        }
        if (receiveString) {
            return "";
        } else {
            return null;
        }
    }

    public boolean reliableSendString(int id, String msg, int timeout){
        return reliableSend(id, msg, timeout, true);
    }

    public boolean reliableSendObject(int id, Object msg, int timeout){
        return reliableSend(id, msg, timeout, false);
    }

    /**
     * Envía el mensaje msg con identificador id esperando que confirmen la recepción.
     * Devolverá true si en alguno de los 5 intentos ha recibido confirmación, fasle en caso contrario.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the boolean
     */
    private boolean reliableSend(int id, Object msg, int timeout, boolean string){
        try {
            pendingACKs.remove(id);
            for(int i = 0; i < 30; ++i){
                if(isUDP) {
                    bufSend = (Integer.toString(id) + ";R;" + (String)msg).getBytes();
                    DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
                    socketUDP.send(packet);
                }
                else{
                    try{
                        packet p = null;
                        if(string){
                            p = new packet(0, true, (String)msg);
                        }
                        else{
                            p = new packet(0, true, (sendableObject)msg);
                        }
                        out.writeObject(p);
                    }catch (Exception e){e.printStackTrace();}
                }
                Thread.sleep(timeout);
                if(receiveACK(id)) {
                    return true;
                }
            }
            return false;
        }catch (Exception e){e.printStackTrace();}
        return false;
    }

    public String receiveNotifications(){
        if (pendingMsgs.containsKey(msgID.toServer.notification)) {
            String aux = pendingMsgs.get(msgID.toServer.notification);
            pendingMsgs.remove(msgID.toServer.notification);
            return aux;
        }
        else{
            try {
                notificationSM.acquire();
            }catch (Exception e){e.printStackTrace();}
            return receiveNotifications();
        }
    }

    /**
     * Close the socket.
     */
    public void close(){
        if(isUDP) {
            socketUDP.disconnect();
            socketUDP.close();
        }
        else{
            try {
                socketTCP.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        rec.doStop();
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public  boolean isConnected(){
        if(isUDP){
            return socketUDP != null;
        }
        else{
            return socketTCP != null;
        }
    }

    /**
     * The type Receiver.
     */
    protected class receiver extends Thread{
        /**
         * The Con.
         */
        private connection con;
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
        public receiver(connection con) {
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

    /**
     * Gets address.
     *
     * @return the address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
        try {
            if(isUDP) {
                socketUDP.setSoTimeout(timeout);
            }
            else{
                socketTCP.setSoTimeout(timeout);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Gets port send.
     *
     * @return the port send
     */
    public int getPortSend() {
        return portSend;
    }

    /**
     * Sets port send.
     *
     * @param portSend the port send
     */
    public boolean setPortSend(int portSend) {
        this.portSend = portSend;
        if(isUDP) {
            socketUDP.connect(address, portSend);
            return sendHi();
        }
        return true;
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
     * Gets pending msgs.
     *
     * @return the pending msgs
     */
    public Map<Integer, String> getPendingMsgs() {
        return pendingMsgs;
    }

    /**
     * Sets pending msgs.
     *
     * @param pendingMsgs the pending msgs
     */
    public void setPendingMsgs(Map<Integer, String> pendingMsgs) {
        this.pendingMsgs = pendingMsgs;
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
}
