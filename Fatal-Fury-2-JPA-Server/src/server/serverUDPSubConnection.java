package server;

import lib.utils.Pair;
import lib.utils.packet;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.simpleObjects.certificate;
import lib.utils.sendableObjects.simpleObjects.string;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

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
public class serverUDPSubConnection {
    /**
     * The Socket.
     */
    protected DatagramSocket socketUDP;
    /**
     * The Buffer to receive.
     */
    protected byte[] bufReceive = new byte[65535];
    /**
     * The Buffer to send.
     */
    protected byte[] bufSend;
    /**
     * The proccess which receives messages constantly.
     */
    protected receiver rec;

    /**
     * The Relation ip port.
     */
    protected Map<String,Integer> relationIpPort = new HashMap<>();

    /**
     * Instantiates a new Connection.
     *
     * @param port the port
     */
    public serverUDPSubConnection(int port) {
        try {
            socketUDP = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.rec = new receiver(this);
        this.rec.start();
    }

    /**
     * Envía el mensaje msg con identificador id sin confirmar la recepción
     *
     * @param id     the id
     * @param msg    the msg
     */
    private void send(int id, String msg, InetAddress address, int portSend, String r){
        bufSend = (Integer.toString(id) + ";"+ r + ";" + (String)msg).getBytes();
        DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
        try {
            socketUDP.send(packet);
        } catch (Exception e) {
            socketUDP = null;
        }
    }

    /**
     * Envía un ack de respuesta al mensaje idenfitifado por id.
     *
     * @param id       the id
     * @param address  the address
     * @param portSend the port send
     */
    public void sendAck(int id, InetAddress address, int portSend){
        bufSend = (Integer.toString(id) + ";NR;ACK").getBytes();
        DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
        try {
            socketUDP.send(packet);
        } catch (Exception e) {}
    }

    /**
     * Recibe cualquier mensaje pendiente y lo guarda en pendingMessages.
     */
    public void receive(){
        try {
            String received = "";
            DatagramPacket packet
                    = new DatagramPacket(bufReceive, bufReceive.length);
            socketUDP.receive(packet);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Recibido: " + received + " de " + address.getHostAddress() + " del puerto " + port);
            if(received.equals("HI")){
                if(!relationIpPort.containsKey(address.getHostAddress())){
                    relationIpPort.put(address.getHostAddress(), port);
                }
                sendAck(msgID.toClient.hi, address, port);
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            try {
                socketUDP.close();
            } catch (Exception ex) {}
            socketUDP = null;
        }
    }

    /**
     * Close the socket.
     */
    public void close(){
        try{
            socketUDP.close();
            if(this.rec != null){
                rec.doStop();
            }
        }catch(Exception e){}
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public  boolean isConnected(){
        return socketUDP != null;
    }

    /**
     * The type Receiver.
     */
    protected class receiver extends Thread{
        /**
         * The Con.
         */
        private serverUDPSubConnection con;
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
        public receiver(serverUDPSubConnection con) {
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
     * Gets socket udp.
     *
     * @return the socket udp
     */
    public DatagramSocket getSocketUDP() {
        return socketUDP;
    }

    /**
     * Sets socket udp.
     *
     * @param socketUDP the socket udp
     */
    public void setSocketUDP(DatagramSocket socketUDP) {
        this.socketUDP = socketUDP;
    }

    /**
     * Get buf receive byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getBufReceive() {
        return bufReceive;
    }

    /**
     * Sets buf receive.
     *
     * @param bufReceive the buf receive
     */
    public void setBufReceive(byte[] bufReceive) {
        this.bufReceive = bufReceive;
    }

    /**
     * Get buf send byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getBufSend() {
        return bufSend;
    }

    /**
     * Sets buf send.
     *
     * @param bufSend the buf send
     */
    public void setBufSend(byte[] bufSend) {
        this.bufSend = bufSend;
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
     * Gets relation ip port.
     *
     * @return the relation ip port
     */
    public Map<String, Integer> getRelationIpPort() {
        return relationIpPort;
    }

    /**
     * Sets relation ip port.
     *
     * @param relationIpPort the relation ip port
     */
    public void setRelationIpPort(Map<String, Integer> relationIpPort) {
        this.relationIpPort = relationIpPort;
    }
}
