package lib.objects.networking;

import com.dosse.upnp.UPnP;
import lib.utils.Pair;
import lib.utils.packet;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.simpleObjects.certificate;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.util.Date;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

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

    /**
     * The Pending ac ks.
     */
    protected Map<Integer, String> pendingACKs = new HashMap<>();

    /**
     * The Pending objects.
     */
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
    /**
     * The Notification sm.
     */
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

    /**
     * The Waiter list.
     */
    private List<Pair<Boolean, Boolean>> waiterList = new ArrayList<>();

    /**
     * The Path.
     */
    private String path = System.getProperty("user.dir") + "/.files/certs/";

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
                /*System.out.println("Attempting UPnP port forwarding...");
                if (UPnP.isUPnPAvailable()) { //is UPnP available?
                    if (UPnP.isMappedTCP(port)) { //is the port already mapped?
                        System.out.println("UPnP port forwarding not enabled: port is already mapped");
                    } else if (UPnP.openPortTCP(port)) { //try to map port
                        System.out.println("UPnP port forwarding enabled");
                    } else {
                        System.out.println("UPnP port forwarding failed");
                    }
                } else {
                    System.out.println("UPnP is not available");
                }*/

                /*InetAddress inet = InetAddress.getLocalHost();
                PortMapping desiredMapping = new PortMapping(port,
                        inet.getHostAddress(), PortMapping.Protocol.UDP,
                        "Fatal Fury 2 Online");
                UpnpServiceImpl upnpService = new UpnpServiceImpl(new PortMappingListener(desiredMapping));
                upnpService.getControlPoint().search();*/

                //weupnp();

                this.portReceive = port;
                address = InetAddress.getByName(ip);
                socketUDP = new DatagramSocket(port);
                if (timeout > 0) {
                    socketUDP.setSoTimeout(timeout);
                }
            }
            else{
                //socketTCP = new Socket(ip, port);
                generateTCPSecureSocket(ip,port);
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

    /**
     * Generate tcp secure socket.
     *
     * @param ip   the ip
     * @param port the port
     * @throws Exception the exception
     */
    private void generateTCPSecureSocket(String ip, int port) throws Exception {
        File archivo = new File(path+"ownClientKey.jks");
        System.setProperty("javax.net.ssl.trustStore", path+"clientTrustedCerts.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "clientpass");
        if (!archivo.exists()) {
            firstConnectionToServer(ip, port);
        }
        System.setProperty("javax.net.ssl.keyStore", path+"ownClientKey.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","clientpass");
        //System.setProperty("javax.net.ssl.keyStore", path+"clientKey.jks");
        //System.setProperty("javax.net.ssl.keyStorePassword","clientpass");
        SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socketTCP = clientFactory.createSocket(ip, port);
    }

    /**
     * First connection to server.
     *
     * @param ip   the ip
     * @param port the port
     * @throws Exception the exception
     */
    private void firstConnectionToServer(String ip, int port) throws Exception {
        Certificate cer = selfSign("dc=OwnPlayer");
        writeCertificate(cer);
        System.setProperty("javax.net.ssl.keyStore", path+"clientKey.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","clientpass");

        SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socketTCP = clientFactory.createSocket(ip, port);
        this.portSend = 5555;
        out = new ObjectOutputStream (socketTCP.getOutputStream());
        in = new ObjectInputStream(socketTCP.getInputStream());
        this.rec = new receiver(this);
        this.rec.start();

        sendString(msgID.toServer.request, "FIRST CONNECTION");

        sendObjectWaitingAnswerString(msgID.toServer.request,new certificate(cer), 0);
        try{
			sendString(msgID.toServer.tramits, "DICONNECT");
            do{
                Thread.sleep(100);
            }while(isConnected());
            this.rec.doStop();
            close();
        }catch (Exception e){}
    }

    /**
     * Write certificate.
     *
     * @param cer the cer
     * @throws Exception the exception
     */
    public void writeCertificate(Certificate cer) throws Exception{
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        ks.setCertificateEntry("OwnPlayer", cer);

        File file = new File(path+"ownClientKey.jks");
        OutputStream out = new FileOutputStream(file);
        ks.store(out, "clientpass".toCharArray());
        out.close();

        file = new File(path+"OwnClientPublicKey.cer");
        byte[] buf = cer.getEncoded();
        out = new FileOutputStream(file);
        out.write(buf);
        out.close();
    }

    /**
     * Self sign certificate.
     *
     * @param subjectDN the subject dn
     * @return the certificate
     * @throws Exception the exception
     */
    public static Certificate selfSign(String subjectDN) throws Exception{
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
        gen.initialize(2048, new SecureRandom());
        KeyPair keyPair = gen.generateKeyPair();

        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        X500Name dnName = new X500Name(subjectDN);
        BigInteger certSerialNumber = new BigInteger(Long.toString(now));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();
        String signatureAlgorithm = "SHA256WithRSA";
        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic());
        BasicConstraints basicConstraints = new BasicConstraints(true);
        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints);
        return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
    }

    /**
     * Weupnp.
     */
    public void weupnp(){
        System.out.println("Starting weupnp");

        GatewayDiscover discover = new GatewayDiscover();
        System.out.println("Looking for Gateway Devices");
        try {
            discover.discover();
            GatewayDevice d = discover.getValidGateway();
            if (null != d) {
                System.out.println("Found gateway device.\n{0} ({1})" +
                        new Object[]{d.getModelName(), d.getModelDescription()});
            } else {
                System.out.println("No valid gateway device found.");
                return;
            }
            InetAddress localAddress = d.getLocalAddress();
            System.out.println("Using local address: {0}"+ localAddress);
            String externalIPAddress = d.getExternalIPAddress();
            System.out.println("External address: {0}"+ externalIPAddress);
            System.out.println("Attempting to map port {0}"+ portReceive);
            PortMappingEntry portMapping = new PortMappingEntry();
            System.out.println("Querying device to see if mapping for port {0} already exists"+
                    portReceive);
            if (!d.getSpecificPortMappingEntry(portReceive,"UDP",portMapping)) {
                System.out.println("Port was already mapped. Aborting test.");
            }
            else {
                System.out.println("Sending port mapping request");
                if (!d.addPortMapping(portReceive, portReceive,
                        localAddress.getHostAddress(),"UDP","test")) {
                    System.out.println("Port mapping attempt failed");
                    System.out.println("Test FAILED");
                }
                else {
                    System.out.println("Mapping successful: waiting {0} seconds before removing."+
                        200);
                    /*Thread.sleep(1000*200);
                    d.deletePortMapping(portReceive,"UDP");*/

                    //System.out.println("Port mapping removed");
                    System.out.println("Test SUCCESSFUL");
                }
            }
            System.out.println("Stopping weupnp");
        } catch (Exception e) {}
    }

    /**
     * Send string reliable.
     *
     * @param id  the id
     * @param msg the msg
     */
    public void sendStringReliable(int id, String msg){
        send(id, msg, true, "R");
    }

    /**
     * Send object reliable.
     *
     * @param id  the id
     * @param msg the msg
     */
    public void sendObjectReliable(int id, Object msg){
        send(id, msg, false, "R");
    }

    /**
     * Send string.
     *
     * @param id  the id
     * @param msg the msg
     */
    public void sendString(int id, String msg){
        send(id, msg, true, "NR");
    }

    /**
     * Send object.
     *
     * @param id  the id
     * @param msg the msg
     */
    public void sendObject(int id, Object msg){
        send(id, msg, false, "NR");
    }

    /**
     * Envía el mensaje msg con identificador id sin confirmar la recepción
     *
     * @param id     the id
     * @param msg    the msg
     * @param string the string
     * @param r      the r
     */
    private void send(int id, Object msg, boolean string, String r){
        if(isUDP) {
            bufSend = (Integer.toString(id) + ";"+ r + ";" + (String)msg).getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                socketUDP.send(packet);
            } catch (Exception e) {
                socketTCP = null;
                socketUDP = null;
                /*e.printStackTrace();*/
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
            }catch (Exception e){
                socketTCP = null;
                socketUDP = null;
                //e.printStackTrace();
            }
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
                /*e.printStackTrace();*/
            }
        }
        else{
            try{
                packet p = new packet(id, false, "ACK");
                out.writeObject(p);
            }catch (Exception e){/*e.printStackTrace();*/}
        }
    }

    /**
     * Send hi.
     *
     * @return the boolean
     */
    public boolean sendHi(){
        long timeReference = System.currentTimeMillis();
        if(isUDP) {
            bufSend = ("HI").getBytes();
            DatagramPacket packet = new DatagramPacket(bufSend, bufSend.length, address, portSend);
            try {
                boolean ok = false;
                while (!ok && System.currentTimeMillis()-timeReference < 10000) {
                    //System.out.println("se envia hi");
                    socketUDP.send(packet);
                    Thread.sleep(100);
                    if (receiveACK(msgID.toClient.hi)) {
                        ok = true;
                    }
                }
                return ok;
            } catch (Exception e) {
                /*e.printStackTrace();*/
                return false;
            }
        }
        else{
            try{
                packet p = new packet(0, false, "HI");
                out.writeObject(p);
                return true;
            }catch (Exception e){
                /*e.printStackTrace();*/
                return false;
            }
        }
    }

    /**
     * Receive ack boolean.
     *
     * @param id the id
     * @return the boolean
     */
    public boolean receiveACK(int id){
        boolean ack = pendingACKs.containsKey(id);
        if(ack){
            pendingACKs.remove(id);
        }
        return ack;
    }

    /**
     * Receive string string.
     *
     * @param id the id
     * @return the string
     */
    public String receiveString(int id){
        return (String)receive(id,true);
    }

    /**
     * Receive object object.
     *
     * @param id the id
     * @return the object
     */
    public Object receiveObject(int id){
        return receive(id,false);
    }

    /**
     * Devuelve el mensaje asociando al identificador id  (cadena vacía si no se ha recibido) string.
     *
     * @param id     the id
     * @param string the string
     * @return the object
     */
    private Object receive(int id, boolean string){
        if(blockReception){return "NONE";}
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
            /*e.printStackTrace();*/
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
                // Envía la confirmación si corresponde
                if(reliable){
                    //System.out.println("Se envia ack");
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
                    /*e.printStackTrace();*/
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
                        //System.out.println("Se recibe: " + received.getObject().toString());
                    }
                    else {
                        pendingMsgs.put(received.getId(), received.getMessage());
                        if(received.getId() != msgID.toServer.ping) {
                            System.out.println("Se recibe: " + received.getMessage());
                        }
                        if(received.getId() == msgID.toServer.notification){
                            notificationSM.release();
                        }
                    }
                }catch (Exception e){
                    /*e.printStackTrace();*/
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

    /**
     * Send string waiting answer string string.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the string
     */
    public String sendStringWaitingAnswerString(int id, Object msg, int timeout){
        return (String) sendWaitingAnswer(id, msg, timeout, true, true);
    }

    /**
     * Send object waiting answer string string.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the string
     */
    public String sendObjectWaitingAnswerString(int id, Object msg, int timeout){
        return (String) sendWaitingAnswer(id, msg, timeout, false, true);
    }

    /**
     * Send string waiting answer object object.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the object
     */
    public Object sendStringWaitingAnswerObject(int id, Object msg, int timeout){
        return sendWaitingAnswer(id, msg, timeout, true, false);
    }

    /**
     * Send object waiting answer object object.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the object
     */
    public Object sendObjectWaitingAnswerObject(int id, Object msg, int timeout){
        return sendWaitingAnswer(id, msg, timeout, false, false);
    }

    /**
     * Send waiting answer object.
     *
     * @param id            the id
     * @param msg           the msg
     * @param timeout       the timeout
     * @param sendString    the send string
     * @param receiveString the receive string
     * @return the object
     */
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
                /*e.printStackTrace();*/
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
                    /*e.printStackTrace();*/
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

    /**
     * Reliable send string boolean.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @return the boolean
     */
    public boolean reliableSendString(int id, String msg, int timeout){
        return reliableSend(id, msg, timeout, true);
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
        return reliableSend(id, msg, timeout, false);
    }

    /**
     * Envía el mensaje msg con identificador id esperando que confirmen la recepción.
     * Devolverá true si en alguno de los 5 intentos ha recibido confirmación, fasle en caso contrario.
     *
     * @param id      the id
     * @param msg     the msg
     * @param timeout the timeout
     * @param string  the string
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
                    }catch (Exception e){/*e.printStackTrace();*/}
                }
                Thread.sleep(timeout);
                if(receiveACK(id)) {
                    return true;
                }
            }
            return false;
        }catch (Exception e){/*e.printStackTrace();*/}
        return false;
    }

    /**
     * Receive notifications string.
     *
     * @return the string
     */
    public String receiveNotifications(){
        if (pendingMsgs.containsKey(msgID.toServer.notification)) {
            String aux = pendingMsgs.get(msgID.toServer.notification);
            pendingMsgs.remove(msgID.toServer.notification);
            return aux;
        }
        else{
            try {
                notificationSM.acquire();
            }catch (Exception e){/*e.printStackTrace();*/}
            return receiveNotifications();
        }
    }

    /**
     * Close the socket.
     */
    public void close(){
        if(isUDP) {
            try{
                //socketUDP.disconnect();
                socketUDP.close();
            }catch(Exception e){}
        }
        else{
            try {
                socketTCP.close();
                in.close();
                out.close();
            } catch (IOException e) {
                /*e.printStackTrace();*/
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
        }catch (Exception e){/*e.printStackTrace();*/}
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
     * @return the port send
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
