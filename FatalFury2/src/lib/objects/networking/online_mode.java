package lib.objects.networking;

import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.objects.*;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Map;

/**
 * The type Online mode.
 */
public class online_mode {
    /**
     * The Online state.
     */
    private GameState onlineState = GameState.PRINCIPAL_GUI;//GameState.ONLINE_MODE;
    /**
     * The Fight.
     */
// La pelea en sí
    private fight_controller fight = null;
    /**
     * The Player.
     */
// Controladores de los personajes
    private online_user_controller player;
    /**
     * The Enemy.
     */
    private online_user_controller enemy = null;
    /**
     * The Scene.
     */
// Escenario
    private scenary scene = new scenary(Scenario_type.USA);;
    /**
     * The Map limit.
     */
// Límites del mapa
    private hitBox mapLimit = new hitBox(0,0,1280,720, box_type.HURTBOX);

    /**
     * The Its me.
     */
    private boolean itsMe = true, /**
     * The Debug.
     */
    debug  = false, /**
     * The Searching.
     */
    searching = false;

    /**
     * The Con to client.
     */
    private connection conToClient, /**
     * The Con to server.
     */
    conToServer;

    /**
     * The Server ip.
     */
    private String serverIp = "127.0.0.1";//"fatalfury2.sytes.net";

    /**
     * The Server port.
     */
    private int serverPort = 5555, /**
     * The Con to client port.
     */
    conToClientPort = 5556;

    private online_mode_gui gui;

    /**
     * Instantiates a new Online mode.
     *
     * @param debug the debug
     */
    public online_mode(Screen screen, boolean debug) {
        this.gui = new online_mode_gui(screen, onlineState);
        this.debug = debug;
        if(debug) {
            /*String ip;
            try (final DatagramSocket socket = new DatagramSocket()) {
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                ip = socket.getLocalAddress().getHostAddress();
                itsMe = ip.equals("192.168.1.3");
            } catch (UnknownHostException | SocketException e) {
                e.printStackTrace();
            }*/
            itsMe = false;
            if (itsMe) {
                System.out.println("ME RECONOCIO");
                conToClient = new connection(serverIp, 3333, 0,true);
                conToClient.setPortSend(3334);
            } else {
                conToClient = new connection(serverIp, 3334, 0,true);
                conToClient.setPortSend(3333);
            }
            System.out.println("Se está usando el puerto: " + conToClient.getPortReceive());
            conToClient.setBlockReception(true);
            System.out.println("Se han creado los sockets");
        }
        else {
            conToServer = new connection(serverIp, serverPort, 0, false);
            conToServer.setPortSend(serverPort);
        }
    }

    /**
     * Generate fight.
     */
    private void generateFight(){
        System.out.println("Se esta creando la pelea");
        if (itsMe) {
            player = new online_user_controller(Playable_Character.TERRY, 1, conToClient, true);
            enemy = new online_user_controller(Playable_Character.TERRY, 2, conToClient, false);
            enemy.setMenssageIdentifier(msgID.toClient.character);
            enemy.setPlayerNum(2);
        } else {
            player = new online_user_controller(Playable_Character.TERRY, 1, conToClient, false);
            enemy = new online_user_controller(Playable_Character.TERRY, 2, conToClient, true);
            enemy.setMenssageIdentifier(msgID.toClient.character);
            enemy.setPlayerNum(2);
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(Scenario_type.USA);
        fight = new online_fight_controller(player, enemy, scene, conToClient, itsMe, msgID.toClient.fight, -2);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(false);
        audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
        audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
        conToClient.setBlockReception(false);

        if (itsMe) {
            boolean ok = false;
            do {
                System.out.println("Se ha enviado el READY");
                ok = conToClient.reliableSendString(0, "READY", 100);
            } while (!ok);
        } else {
            String msg = conToClient.receiveString(0);
            System.out.println("Se esta esperando el ready");
            while (!msg.equals("READY")) {
                msg = conToClient.receiveString(0);
            }
            System.out.println("Se ha recibido el ready");
            for (int i = 0; i < 5; ++i) {
                conToClient.sendAck(0);
            }
        }
        System.out.println("Se ha creado la pelea");
    }

    /**
     * Generate fight.
     *
     * @param ip     the ip
     * @param isHost the is host
     * @param pC     the p c
     * @param pE     the p e
     * @param sce    the sce
     */
    private void generateFight(String ip, boolean isHost, Playable_Character pC, Playable_Character pE, Scenario_type sce){
        System.out.println("Se va a establecer la conexion");
        conToClient = new connection(ip, conToClientPort, 0,true);
        conToClient.setPortSend(conToClientPort);
        conToClient.setBlockReception(true);
        if (isHost) {
            player = new online_user_controller(pC, 1, conToClient, true);
            enemy = new online_user_controller(pE, 2, conToClient, false);
            enemy.setMenssageIdentifier(msgID.toClient.character);
            enemy.setPlayerNum(2);
        } else {
            player = new online_user_controller(pC, 1, conToClient, false);
            enemy = new online_user_controller(pE, 2, conToClient, true);
            enemy.setMenssageIdentifier(msgID.toClient.character);
            enemy.setPlayerNum(2);
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(sce);
        fight = new online_fight_controller(player, enemy, scene, conToClient, isHost, msgID.toClient.fight, -2);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(false);
        audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
        audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
        try {
            Thread.sleep(5000);
        } catch (Exception e) {}
        conToClient.setBlockReception(false);
        if (isHost) {
            boolean ok = false;
            do {
                ok = conToClient.reliableSendString(0, "READY", 100);
            } while (!ok);
        } else {
            String msg = conToClient.receiveString(0);
            while (!msg.equals("READY")) {
                msg = conToClient.receiveString(0);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
        }
    }

    /**
     * The In.
     */
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Commander.
     */
    void commander(){
        String command = "";
        boolean fight = false;
        while(!fight) {
            System.out.print("Command (LOGIN, REGISTER, FIGHT, ADD FRIEND, ACCEPT FRIEND, REJECT FRIEND, SEND MESSAGE, MESSAGES WITH): ");
            String cm = "";
            try {
                cm = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(cm.equals("exit")){
                conToServer.sendString(msgID.toServer.tramits,"DISCONNECT");
                conToServer.close();
                System.exit(0);
            }
            else if(cm.equals("LOGIN")){
                String username = "";
                String password = "";
                try {
                    System.out.print("Username: ");
                    username = in.readLine();
                    System.out.print("Password: ");
                    password = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"LOGIN:"+username+":"+password);
                    Thread.sleep(2000);
                    String res = conToServer.receiveString(msgID.toServer.request);
                    if(res.equals("LOGGED")){
                        System.out.println("Te has logeado");
                    }
                    else{
                        System.out.println(res.split(":")[1]);
                    }
                }catch (Exception e){e.printStackTrace();};
            }
            else if(cm.equals("REGISTER")){
                String username = "";
                String password = "";
                String email = "";
                try {
                    System.out.print("Username: ");
                    username = in.readLine();
                    System.out.print("Password: ");
                    password = in.readLine();
                    System.out.print("Email: ");
                    email = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"REGISTER:"+username+":"+email+":"+password);
                    Thread.sleep(2000);
                    String res = conToServer.receiveString(msgID.toServer.request);
                    if(res.equals("REGISTERED")){
                        System.out.println("Te has registrado");
                    }
                    else if(res.equals("NONE") || res.equals("")){
                        System.out.println("No se ha recibido respuesta");
                    }
                    else{
                        System.out.println(res.split(":")[1]);
                    }
                }catch (Exception e){e.printStackTrace();};

            }
            else if(cm.equals("ADD FRIEND")){
                String username = "";
                String password = "";
                try {
                    System.out.print("Username: ");
                    username = in.readLine();
                    System.out.print("Friend: ");
                    password = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"SEND FRIEND REQUEST:"+username+":"+password);
                }catch (Exception e){e.printStackTrace();};

            }
            else if(cm.equals("ACCEPT FRIEND")){
                String username = "";
                String password = "";
                try {
                    System.out.print("Username: ");
                    username = in.readLine();
                    System.out.print("Friend: ");
                    password = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"ACCEPT FRIEND REQUEST:"+username+":"+password);
                }catch (Exception e){e.printStackTrace();};

            }
            else if(cm.equals("REJECT FRIEND")){
                String username = "";
                String password = "";
                try {
                    System.out.print("Username: ");
                    username = in.readLine();
                    System.out.print("Friend: ");
                    password = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"REJECT FRIEND REQUEST:"+username+":"+password);
                }catch (Exception e){e.printStackTrace();};

            }
            else if(cm.equals("REMOVE FRIEND")){
                String username = "";
                String password = "";
                try {
                    System.out.print("Username: ");
                    username = in.readLine();
                    System.out.print("Friend: ");
                    password = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"REMOVE FRIEND:"+username+":"+password);
                }catch (Exception e){e.printStackTrace();};

            }
            else if(cm.equals("SEND MESSAGE")){
                String username = "";
                String password = "";
                try {
                    System.out.print("Receptor: ");
                    username = in.readLine();
                    System.out.print("Message: ");
                    password = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"SEND MESSAGE:"+username+":"+password);
                }catch (Exception e){e.printStackTrace();};

            }
            else if(cm.equals("MESSAGES WITH")){
                String username = "";
                try {
                    System.out.print("Receptor: ");
                    username = in.readLine();
                    conToServer.sendString(msgID.toServer.request,"MESSAGE HISTORIAL:"+username);
                    Thread.sleep(2000);
                    sendableObjectsList m = (sendableObjectsList) conToServer.receiveObject(msgID.toServer.request);
                    for(sendableObject msg : m.getMsgs()){
                        message ms = (message)msg;
                        System.out.println(ms.getDate() + ": " + ms.getContent());
                    }
                }catch (Exception e){e.printStackTrace();};

            }
            else if(cm.equals("FIGHT")){
                fight = true;
            }
            else{
                System.out.println("No se ha reconocido el comando: " + cm +"\n");
            }
        }
    }

    /**
     * Online game.
     *
     * @param screenObjects the screen objects
     */
    public void online_game(Map<Item_Type, screenObject> screenObjects){
        if(onlineState != GameState.ONLINE_FIGHT && onlineState != GameState.ONLINE_SEARCHING_FIGHT) {
            //commander();
            gui.drawGUI();
        }
        if(controlListener.menuInput(1, controlListener.ESC_INDEX)
            || controlListener.menuInput(2, controlListener.ESC_INDEX)){
            if(conToClient != null){conToClient.close();}
            conToServer.sendString(msgID.toServer.tramits, "DISCONNECT");
            System.exit(0);
        }
        if(debug){
            switch (onlineState) {
                case ONLINE_FIGHT:
                    fight.getAnimation(screenObjects);
                    if (fight.getEnd()) {
                        audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                        fight.getPlayer().stop();
                        fight.getEnemy().stop();
                        conToClient.close();
                        fight = null;
                        conToClient = null;
                        onlineState = GameState.ONLINE_MODE;
                    }
                    break;
                default:
                    generateFight();
                    onlineState = GameState.ONLINE_FIGHT;
            }
        }
        else {
            switch (onlineState) {
                case LOGIN_REGISTER:
                    gui.drawGUI();
                    break;
                case ONLINE_FIGHT:
                    fight.getAnimation(screenObjects);
                    if (fight.getEnd()) {
                        audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                        fight.getPlayer().stop();
                        fight.getEnemy().stop();
                        conToClient.close();
                        fight = null;
                        conToClient = null;
                        onlineState = GameState.ONLINE_MODE;
                    }
                    break;
                case ONLINE_SEARCHING_FIGHT:
                    String msg = conToServer.receiveString(2);
                    if (msg.contains("SEARCH")) {
                        System.out.println("Se ha recibido " + msg);
                    }
                    if (msg.contains("SEARCH GAME")) {
                        System.out.println("Ha sido emparejado con un rival, se va a crear la pelea");
                        String aux[] = msg.split(":");
                        System.out.println("El emparejamiento es: " +msg);
                        generateFight(aux[2], Boolean.parseBoolean(aux[1]), Playable_Character.TERRY, Playable_Character.TERRY, Scenario_type.USA);
                        onlineState = GameState.ONLINE_FIGHT;
                    }
                    break;
                default:
                    gui.drawGUI();
                    break;
            }
        }
    }

    /**
     * The type Receiver.
     */
    protected class tramitter extends Thread{
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
        public tramitter(connection con) {
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
                String msg = con.receiveString(msgID.toServer.notification);
                if(msg.contains("SESSION CLOSED")){
                    System.out.println("Se cierra la sesión por la siguiente razón: " + msg.split(":")[1]);
                    System.exit(0);
                }
                else if(msg.contains("MESSAGE")){
                    System.out.println("Nuevo mensaje de " + msg.split(":")[1]);
                }
            }
        }
    }

    /**
     * Gets fight.
     *
     * @return the fight
     */
    public fight_controller getFight() {
        return fight;
    }

    /**
     * Sets fight.
     *
     * @param fight the fight
     */
    public void setFight(fight_controller fight) {
        this.fight = fight;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public online_user_controller getPlayer() {
        return player;
    }

    /**
     * Sets player.
     *
     * @param player the player
     */
    public void setPlayer(online_user_controller player) {
        this.player = player;
    }

    /**
     * Gets enemy.
     *
     * @return the enemy
     */
    public online_user_controller getEnemy() {
        return enemy;
    }

    /**
     * Sets enemy.
     *
     * @param enemy the enemy
     */
    public void setEnemy(online_user_controller enemy) {
        this.enemy = enemy;
    }

    /**
     * Gets scene.
     *
     * @return the scene
     */
    public scenary getScene() {
        return scene;
    }

    /**
     * Sets scene.
     *
     * @param scene the scene
     */
    public void setScene(scenary scene) {
        this.scene = scene;
    }

    /**
     * Gets map limit.
     *
     * @return the map limit
     */
    public hitBox getMapLimit() {
        return mapLimit;
    }

    /**
     * Sets map limit.
     *
     * @param mapLimit the map limit
     */
    public void setMapLimit(hitBox mapLimit) {
        this.mapLimit = mapLimit;
    }

    /**
     * Is its me boolean.
     *
     * @return the boolean
     */
    public boolean isItsMe() {
        return itsMe;
    }

    /**
     * Sets its me.
     *
     * @param itsMe the its me
     */
    public void setItsMe(boolean itsMe) {
        this.itsMe = itsMe;
    }

    /**
     * Gets con to client.
     *
     * @return the con to client
     */
    public connection getConToClient() {
        return conToClient;
    }

    /**
     * Sets con to client.
     *
     * @param con the con
     */
    public void setConToClient(connection con) {
        this.conToClient = con;
    }
}
