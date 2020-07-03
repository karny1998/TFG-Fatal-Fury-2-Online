package lib.objects.networking;

import lib.utils.Pair;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
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
public class connection {
    /**
     * The Socket.
     */
    private DatagramSocket socket;
    /**
     * The Address.
     */
    private InetAddress address;
    /**
     * The Timeout.
     */
    private int timeout = 50;
    /**
     * The Port send.
     */
    private int portSend,
    /**
     * The Port receive.
     */
    portReceive;
    /**
     * The Buffer to receive.
     */
    private byte[] bufReceive = new byte[256];
    /**
     * The Buffer to send.
     */
    private byte[] bufSend;
    /**
     * The Pending messages.
     */
    private Map<Integer, String> pendingMsgs = new HashMap<>();
    /**
     * The proccess which receives messages constantly.
     */
    private receiver rec;

    /**
     * The Block reception.
     */
    private boolean blockReception = false;

    /**
     * Instantiates a new Connection.
     * Por defecto 50 segundos de timeout, y puerto de envío y recepción el mismo
     *
     * @param ip   the ip
     * @param port the port
     */
    public connection(String ip, int port) {
        this.portSend = port;
        this.portReceive = port;
        try {
            socket = new DatagramSocket(port);
            socket.setSoTimeout(50);
            address = InetAddress.getByName(ip);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        this.rec = new receiver(this);
        this.rec.start();
    }

    /**
     * Instantiates a new Connection.
     *
     * @param ip          the ip
     * @param portReceive the port receive
     * @param portSend    the port send
     * @param timeout     the timeout
     */
    public connection(String ip, int portReceive, int portSend, int timeout) {
        this.portSend = portSend;
        this.portReceive = portReceive;
        this.timeout = timeout;
        try {
            socket = new DatagramSocket(portReceive);
            if(timeout > 0) {
            	socket.setSoTimeout(timeout);
            }
            address = InetAddress.getByName(ip);
        }catch (Exception e){
            System.out.println(e.getMessage());
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
        bufSend = (Integer.toString(id) + ";NR;" + msg).getBytes();
        DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
        try {
            socket.send(packet);
        }catch (Exception e){}
    }

    /**
     * Envía un ack de respuesta al mensaje idenfitifado por id.
     *
     * @param id the id
     */
    public void sendAck(int id){
        bufSend = (Integer.toString(id) + ";NR;ACK").getBytes();
        DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
        try {
            socket.send(packet);
        }catch (Exception e){}
    }

    /**
     * Envía el mensaje msg con identificador id sin confirmar la recepción
     *
     * @param id  the id
     * @param msg the msg
     * @param add the add
     * @param p   the p
     */
    public void send(int id, String msg, InetAddress add, int p){
        bufSend = (Integer.toString(id) + ";NR;" + msg).getBytes();
        DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, add, p);
        try {
            socket.send(packet);
        }catch (Exception e){}
    }

    /**
     * Devuelve el mensaje asociando al identificador id  (cadena vacía si no se ha recibido) string.
     *
     * @param id the id
     * @return the string
     */
    public synchronized String receive(int id){
        if(blockReception){return "";}
        if(pendingMsgs.containsKey(id)){
            String msg = pendingMsgs.get(id);
            pendingMsgs.remove(id);
            return msg;
        }
        try {
            DatagramPacket packet
                    = new DatagramPacket(bufReceive, bufReceive.length);
            socket.receive(packet);
            String msg = new String(packet.getData(), 0, packet.getLength());
            String aux[] = msg.split(";");
            int idM = Integer.parseInt(aux[0]);
            // Si el segundo parámetro es R, es que necesita confirmación de recepción
            boolean reliable = aux[1].equals("R");
            msg = aux[2];
            if(reliable){
                sendAck(idM);
            }
            // Si el mensaje recibido no es el deseado, se guarda
            if(idM == id) {
                return msg;
            }
            else{
                pendingMsgs.put(idM, msg);
                return "";
            }
        }catch (Exception e){
        	return "";
        }
    }

    /**
     * Devuelve el mensaje asociando al identificador id  (cadena vacía si no se ha recibido) string.
     *
     * @param id the id
     * @return the string
     */
    public synchronized Pair<Integer,String> receivePermissive(int id){
        if(blockReception){return new Pair<>(0,"");}
        String msg = receive(id);
        if(msg.equals("") && !pendingMsgs.isEmpty()){
            int maxKey = Collections.max(pendingMsgs.keySet());
            if(maxKey > id){
                msg = pendingMsgs.get(maxKey);
                pendingMsgs.remove(maxKey);
                return new Pair<>(maxKey, msg);
            }
            else{
                return new Pair<>(0,"");
            }
        }
        return new Pair<>(id,msg);
    }

    /**
     * Recibe cualquier mensaje pendiente y lo guarda en pendingMessages.
     */
    public synchronized void receive(){
        if(blockReception){return;}
        try {
            DatagramPacket packet
                    = new DatagramPacket(bufReceive, bufReceive.length);
            socket.receive(packet);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String received = new String(packet.getData(), 0, packet.getLength());
            String aux[] = received.split(";");
            int idM = Integer.parseInt(aux[0]);
            boolean reliable = aux[1].equals("R");
            // Envía la confirmación si corresponde
            if(reliable){
                sendAck(idM);
            }
            pendingMsgs.put(idM, aux[2]);
            System.out.println("Se ha recibido: "+aux[2]);
        }catch (Exception e){}
    }

    /**
     * Envía el mensaje msg con identificador id esperando que confirmen la recepción.
     * Devolverá true si en alguno de los 5 intentos ha recibido confirmación, fasle en caso contrario.
     *
     * @param id  the id
     * @param msg the msg
     * @return the boolean
     */
    public boolean reliableSend(int id, String msg){
        bufSend = (Integer.toString(id) + ";R;" + msg).getBytes();
        DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
        try {
            for(int i = 0; i < 5; ++i){
                socket.send(packet);
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
        }catch (Exception e){}
        return false;
    }

    /**
     * Close the socket.
     */
    public void close(){
        socket.close();
    }

    /**
     * The type Receiver.
     */
    private class receiver extends Thread{
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
        private receiver(connection con) {
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
                try {
                	thread.sleep(1);
				} catch (InterruptedException e) {}
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
            socket.setSoTimeout(timeout);
        }catch (Exception e){}
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

    /*
    public Pair<Pair<InetAddress, Integer>,String> serverReceive(){
        try {
            DatagramPacket packet
                    = new DatagramPacket(bufReceive, bufReceive.length);
            socket.receive(packet);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String received = new String(packet.getData(), 0, packet.getLength());
            String aux[] = received.split(";");
            int idM = Integer.parseInt(aux[0]);
            boolean reliable = aux[1].equals("R");
            received = aux[2];
            if(reliable){
                sendAck(idM);
            }
            return new Pair<>(new Pair<>(address,port),received);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new Pair<>(new Pair<>(null,null),"");
        }
    }
*/
}
