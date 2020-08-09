package lib.objects.networking;

import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.objects.*;
import lib.objects.networking.gui.online_mode_gui;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.message;

import javax.security.sasl.SaslServer;
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
    private GameState onlineState = GameState.LOGIN_REGISTER;//GameState.ONLINE_MODE;
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
    private String serverIp = "fatalfury2.sytes.net";//"127.0.0.1";//

    /**
     * The Server port.
     */
    private int serverPort = 5555, /**
     * The Con to client port.
     */
    conToClientPort = 5556;

    private online_mode_gui gui;

    private String userLogged = null;

    /**
     * Instantiates a new Online mode.
     *
     * @param debug the debug
     */
    public online_mode(Screen screen, boolean debug) {
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
        this.gui = new online_mode_gui(this, screen, onlineState);
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
    public void generateFight(String ip, boolean isHost, Playable_Character pC, Playable_Character pE, Scenario_type sce){
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
        gui.getPrincipal().gameOn();
        gui.setOnlineState(GameState.ONLINE_FIGHT);
    }

    /**
     * Online game.
     *
     * @param screenObjects the screen objects
     */
    public void online_game(Map<Item_Type, screenObject> screenObjects){
        if(onlineState != GameState.ONLINE_FIGHT) {
            gui.drawGUI();
        }
        else{
            screenObjects.remove(Item_Type.MENU);
            fight.getAnimation(screenObjects);
            if (fight.getEnd()) {
                audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                fight.getPlayer().stop();
                fight.getEnemy().stop();
                conToClient.close();
                fight = null;
                conToClient = null;
                onlineState = GameState.PRINCIPAL_GUI;
                gui.getPrincipal().getGui();
                audio_manager.endFight();
                audio_manager.menu.play(menu_audio.indexes.menu_theme);
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

    public GameState getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(GameState onlineState) {
        this.onlineState = onlineState;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isSearching() {
        return searching;
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public connection getConToServer() {
        return conToServer;
    }

    public void setConToServer(connection conToServer) {
        this.conToServer = conToServer;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getConToClientPort() {
        return conToClientPort;
    }

    public void setConToClientPort(int conToClientPort) {
        this.conToClientPort = conToClientPort;
    }

    public online_mode_gui getGui() {
        return gui;
    }

    public void setGui(online_mode_gui gui) {
        this.gui = gui;
    }

    public String getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(String userLogged) {
        this.userLogged = userLogged;
    }
}
