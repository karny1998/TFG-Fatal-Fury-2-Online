package lib.objects.networking;

import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.objects.*;
import lib.objects.networking.gui.online_mode_gui;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;

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
    private GameState onlineState = GameState.LOGIN_REGISTER;//GameState.CHARACTER_SELECTION;//
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
    conToClientPort = 55555;

    private online_mode_gui gui;

    private String userLogged = null;

    //Información de la partida en curso
    private String rival = "";
    private Playable_Character char1 = Playable_Character.TERRY, char2 = Playable_Character.TERRY;
    private boolean isRanked = false, isHost = false;
    private int rankPoints = 0;

    /**
     * Instantiates a new Online mode.
     *
     * @param debug the debug
     */
    public online_mode(Screen screen, boolean debug) {
        if(!debug) {
            conToServer = new connection(serverIp, serverPort, 0, false);
            if(!conToServer.isConnected()){
                onlineState = GameState.SERVER_PROBLEM;
            }
            else {
                conToServer.setPortSend(serverPort);
            }
        }
        this.gui = new online_mode_gui(this, screen, onlineState);
    }

    public boolean retryInitialConnection(){
        boolean ok = reconnectToServer();
        gui.clearGui();
        if(ok){
            gui.setOnlineState(GameState.LOGIN_REGISTER);
            return true;
        }
        else{
            gui.setOnlineState(GameState.SERVER_PROBLEM);
            return false;
        }
    }

    public boolean reconnectToServer(){
        conToServer = new connection(serverIp, serverPort, 0, false);
        if(!conToServer.isConnected()){
            return false;
        }
        else {
            conToServer.setPortSend(serverPort);
            return true;
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
        } catch (Exception e) {}
        //conToClient.setBlockReception(false);

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
     * @param isHost the is host
     * @param pC     the p c
     * @param pE     the p e
     * @param sce    the sce
     */
    public void generateFight(boolean isHost, Playable_Character pC, Playable_Character pE, Scenario_type sce, boolean isRanked, String rival){
        this.rankPoints = 0;
        this.char1 = pC;
        this.char2 = pE;
        this.rival = rival;
        this.isRanked = isRanked;
        this.isHost = isHost;
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
        audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
        audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);

        //conToClient.setBlockReception(false);

        //Sincronizar
        boolean syncOk = synchronize(isHost);
        if(!syncOk){
            gui.setOnlineState(GameState.PRINCIPAL_GUI);
            gui.clearGui();
            gui.popUp("Connection lost with the rival.");
            return;
        }

        fight = new online_fight_controller(player, enemy, scene, conToClient, isHost, msgID.toClient.fight, -2);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(false);

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

    private boolean synchronize(boolean isHost){
        if(isHost){
            for(int i = 0; i < 1000 ;++i){
                conToClient.sendStringReliable(msgID.toClient.synchronization, "READY");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    /*e.printStackTrace();*/
                }
                String res = conToClient.receiveString(msgID.toClient.synchronization);
                if( res.equals("READY") || res.equals("ACK")){
                    return true;
                }
            }
            return false;
        }
        else{
            for(int i = 0; i < 1000 ;++i){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    /*e.printStackTrace();*/
                }
                String res = conToClient.receiveString(msgID.toClient.synchronization);
                if(res.equals("READY")){
                    for(int j = 0; j < 10; ++j){
                        conToClient.sendStringReliable(msgID.toClient.synchronization, "READY");
                    }
                    return true;
                }
            }
            return false;
        }
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

                if(((online_fight_controller)fight).connectionLost()){
                    gui.setOnlineState(GameState.PRINCIPAL_GUI);
                    gui.clearGui();
                    gui.principalGUI();
                    gui.popUp("Connection with rival lost. The game will count as tie.");
                    conToServer.sendString(msgID.toServer.request, "REGISTER GAME:" +
                            gui.getUserLogged() + ":" + rival + ":" + char1.toString() + ":" + char2.toString() + ":0:" + isRanked);
                }

                Fight_Results results = fight.getFight_result();
                conToClient.reliableSendString(msgID.toClient.tramits,"GAME ENDED:"+results.toString(), 200);
                int r = 0;
                switch (results){
                    case PLAYER1_WIN:
                        r = 1;
                        break;
                    case PLAYER2_WIN:
                        r = 2;
                        break;
                    default:
                        r = 0;
                        break;
                }

                if(isRanked) {
                    String res = "";
                    if (isHost) {
                        res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request, "REGISTER GAME:" +
                                gui.getUserLogged() + ":" + rival + ":" + char1.toString() + ":" + char2.toString() + ":"
                                + r + ":" + isRanked, 0);
                    }
                    else{
                        do {
                            res = conToServer.receiveString(msgID.toServer.request);
                        }while (!res.contains("GAME REGISTERED"));
                    }
                    rankPoints = Integer.parseInt(res);
                }
                else{
                    if (isHost) {
                        conToServer.sendString(msgID.toServer.request, "REGISTER GAME:" +
                                gui.getUserLogged() + ":" + rival + ":" + char1.toString() + ":" + char2.toString() + ":"
                                + r + ":" + isRanked);
                    }
                }

                audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                fight.getPlayer().stop();
                fight.getEnemy().stop();
                conToClient.close();
                fight = null;
                conToClient = null;
                gui.setOnlineState(GameState.GAME_END);
                gui.clearGui();
                gui.getPrincipal().guiOn();
                audio_manager.endFight();
                audio_manager.menu.play(menu_audio.indexes.menu_theme);
            }
        }
    }

    public boolean generateConToClient(String ip){
        //conToClient = new connection(ip, 5560, 0, true);
        //conToClient.setPortSend(5561);
        //return true;
        conToClient = new connection(ip, conToClientPort, 0, true);
        boolean ok = conToClient.setPortSend(conToClientPort);
        if(!ok){
            gui.setOnlineState(GameState.PRINCIPAL_GUI);
            gui.clearGui();
            gui.principalGUI();
            gui.popUp("Has been a problem establishing the connection.");
        }
        return ok;
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

    public String getRival() {
        return rival;
    }

    public void setRival(String rival) {
        this.rival = rival;
    }

    public Playable_Character getChar1() {
        return char1;
    }

    public void setChar1(Playable_Character char1) {
        this.char1 = char1;
    }

    public Playable_Character getChar2() {
        return char2;
    }

    public void setChar2(Playable_Character char2) {
        this.char2 = char2;
    }

    public boolean isRanked() {
        return isRanked;
    }

    public void setRanked(boolean ranked) {
        isRanked = ranked;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public int getRankPoints() {
        return rankPoints;
    }

    public void setRankPoints(int rankPoints) {
        this.rankPoints = rankPoints;
    }
}
