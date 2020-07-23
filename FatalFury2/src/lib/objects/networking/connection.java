package lib.objects.networking;

import lib.utils.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Collections;
import java.util.HashMap;
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
    /**
     * The proccess which receives messages constantly.
     */
    protected receiver rec;

    /**
     * The Block reception.
     */
    protected boolean blockReception = false;

    protected Semaphore sm = new Semaphore(1);

    private PrintWriter out;

    private BufferedReader in;

    boolean isUDP = true;

    public connection() {}

    /**
     * Instantiates a new Connection.
     * Por defecto 50 segundos de timeout, y puerto de envío y recepción el mismo
     *
     * @param ip   the ip
     */
    public connection(String ip, int port, boolean isUDP) {
        this.isUDP = isUDP;
        try {
            if(isUDP) {
                socketUDP = new DatagramSocket(port);
                this.portReceive = port;
                socketUDP.setSoTimeout(50);
                address = InetAddress.getByName(ip);
            }
            else{
                socketTCP = new Socket(ip, port);
                socketTCP.setSoTimeout(50);
                out = new PrintWriter(socketTCP.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.rec = new receiver(this);
        this.rec.start();
    }

    /**
     * Instantiates a new Connection.
     *
     * @param ip          the ip
     * @param timeout     the timeout
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
                out = new PrintWriter(socketTCP.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socketTCP.getInputStream()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        this.rec = new receiver(this);
        this.rec.start();
    }

    /**
     * Envía el mensaje msg con identificador id sin confirmar la recepción
     *
     * @param id  the id
     * @param msg the msg
     */
    public void send(int id, String msg){
        if(isUDP) {
            bufSend = (Integer.toString(id) + ";NR;" + msg).getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                socketUDP.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            out.println(Integer.toString(id) + ";NR;" + msg);
        }
    }

    // Tener cuidao con esta
    public void sendThroughServer(int id, InetAddress dest, String msg){
        if(isUDP) {
            bufSend = (dest.getHostAddress() + "/" + Integer.toString(id) + ";NR;" + msg).getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                socketUDP.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            out.println(dest.getHostAddress() + "/" + Integer.toString(id) + ";NR;" + msg);
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
            out.println(Integer.toString(id) + ";NR;ACK");
        }
    }

    public void sendHi(){
        if(isUDP) {
            bufSend = ("HI").getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                boolean ok = false;
                while (!ok) {
                    socketUDP.send(packet);
                    Thread.sleep(50);
                    String ack = receive(-1);
                    if (ack.equals("ACK")) {
                        ok = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            out.println("HI");
        }
    }

    /**
     * Devuelve el mensaje asociando al identificador id  (cadena vacía si no se ha recibido) string.
     *
     * @param id the id
     * @return the string
     */
    public String receive(int id){
        if(blockReception){return "";}
        try {
            sm.acquire();
            if(pendingMsgs.containsKey(id)){
                String msg = pendingMsgs.get(id);
                pendingMsgs.remove(id);
                sm.release();
                return msg;
            }
            sm.release();
            return "NONE";
        } catch (Exception e) {
            e.printStackTrace();
            sm.release();
        }
        sm.release();
        return "";
    }

    /**
     * Devuelve el mensaje asociando al identificador id  (cadena vacía si no se ha recibido) string.
     *
     * @param id the id
     * @return the string
     */
    public Pair<Integer,String> receivePermissive(int id){
        if(blockReception){return new Pair<>(0,"");}
        String msg = receive(id);
        try {
            sm.acquire();
            if (msg.equals("") && !pendingMsgs.isEmpty()) {
                int maxKey = Collections.max(pendingMsgs.keySet());
                if (maxKey > id) {
                    msg = pendingMsgs.get(maxKey);
                    pendingMsgs.remove(maxKey);
                    sm.release();
                    return new Pair<>(maxKey, msg);
                } else {
                    sm.release();
                    return new Pair<>(0, "");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            sm.release();
        }
        sm.release();
        return new Pair<>(id,msg);
    }

    /**
     * Recibe cualquier mensaje pendiente y lo guarda en pendingMessages.
     */
    public void receive(){
        if(blockReception || socketUDP != null && !socketUDP.isConnected()){return;}
        if(blockReception || socketUDP != null && !socketUDP.isConnected()){return;}
        try {
            String received = "";
            if(isUDP) {
                DatagramPacket packet
                        = new DatagramPacket(bufReceive, bufReceive.length);
                socketUDP.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                received = new String(packet.getData(), 0, packet.getLength());
            }
            else{
                received = in.readLine();
            }
            if(received.equals("HI")){
                sendAck(-1);
                return;
            }
            String aux[] = received.split(";");
            int idM = Integer.parseInt(aux[0]);
            boolean reliable = aux[1].equals("R");
            // Envía la confirmación si corresponde
            if(reliable){
                sendAck(idM);
            }
            try {
                sm.acquire();
                if(aux.length == 2){
                    pendingMsgs.put(idM, "");
                }
                else {
                    pendingMsgs.put(idM, aux[2]);
                }
                System.out.println("Se recibe: " + aux[2]);
            }catch (Exception e){
                e.printStackTrace();
                sm.release();
            }
            sm.release();
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Envía el mensaje msg con identificador id esperando que confirmen la recepción.
     * Devolverá true si en alguno de los 5 intentos ha recibido confirmación, fasle en caso contrario.
     *
     * @param id  the id
     * @param msg the msg
     * @return the boolean
     */
    public boolean reliableSend(int id, String msg, int timeout){
        bufSend = (Integer.toString(id) + ";R;" + msg).getBytes();
        DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
        try {
            for(int i = 0; i < 10; ++i){
                if(isUDP) {
                    socketUDP.send(packet);
                }
                else{
                    out.println(Integer.toString(id) + ";R;" + msg);
                }
                Thread.sleep(timeout);
                String ack = receive(id);
                if(!ack.equals("")) {
                    if (ack.equals("ACK")){
                        return true;
                    }
                    else{
                        pendingMsgs.put(id, ack);
                    }
                }
            }
            return false;
        }catch (Exception e){e.printStackTrace();}
        return false;
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

    public  boolean isConnected(){
        if(isUDP){
            return socketUDP.isConnected();
        }
        else{
            return socketTCP.isConnected();
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
    public void setPortSend(int portSend) {
        this.portSend = portSend;
        if(isUDP) {
            socketUDP.connect(address, portSend);
            sendHi();
        }
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
