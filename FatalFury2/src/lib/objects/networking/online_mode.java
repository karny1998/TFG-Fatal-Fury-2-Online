package lib.objects.networking;

import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.objects.*;
import lib.objects.networking.gui.online_mode_gui;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;
import lib.training.stateCalculator;
import lib.utils.sendableObjects.simpleObjects.qtable;

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
    private character_controller player;
    /**
     * The Enemy.
     */
    private character_controller enemy = null;
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

    /**
     * The Gui.
     */
    private online_mode_gui gui;

    /**
     * The User logged.
     */
    private String userLogged = null;

    /**
     * The Rival.
     */
//Información de la partida en curso
    private String rival = "";
    /**
     * The Char 1.
     */
    private Playable_Character char1 = Playable_Character.TERRY, /**
     * The Char 2.
     */
    char2 = Playable_Character.TERRY;
    /**
     * The Is ranked.
     */
    private boolean isRanked = false, /**
     * The Is host.
     */
    isHost = false;
    /**
     * The Rank points.
     */
    private int rankPoints = 0;
    /**
     * The Result.
     */
    private Fight_Results result;

    private int isVsIA = 0;

    /**
     * Instantiates a new Online mode.
     *
     * @param screen the screen
     * @param debug  the debug
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

    /**
     * Retry initial connection boolean.
     *
     * @return the boolean
     */
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

    /**
     * Reconnect to server boolean.
     *
     * @return the boolean
     */
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
            ((online_user_controller) enemy).setMenssageIdentifier(msgID.toClient.character);
            enemy.setPlayerNum(2);
        } else {
            player = new online_user_controller(Playable_Character.TERRY, 1, conToClient, false);
            enemy = new online_user_controller(Playable_Character.TERRY, 2, conToClient, true);
            ((online_user_controller) enemy).setMenssageIdentifier(msgID.toClient.character);
            enemy.setPlayerNum(2);
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(Scenario_type.USA);
        fight = new online_fight_controller((online_user_controller)player, (online_user_controller)enemy, scene, conToClient, itsMe, msgID.toClient.fight, -2);
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

    public void generateVsIAFight(Playable_Character pC, Scenario_type sce, boolean isGlobal){
        this.isHost = true;
        if(isGlobal){isVsIA = 2;}
        else{isVsIA = 1;}

        Double table[][];
        qtable aux;
        double epsilon = 0.0;
        if(isGlobal){
            aux = (qtable) conToServer.sendStringWaitingAnswerObject(msgID.toServer.request,"GET GLOBAL IA",0);
        }
        else{
            aux = (qtable) conToServer.sendStringWaitingAnswerObject(msgID.toServer.request,"GET OWN IA",0);
        }
        table = aux.getTableDouble();
        epsilon = Double.parseDouble(conToServer.receiveString(msgID.toServer.request).split(":")[1]);

        stateCalculator.initialize();
        this.rankPoints = 0;
        this.char1 = pC;
        this.char2 = Playable_Character.TERRY;
        this.rival = "IA";
        this.isRanked = isRanked;

        player = new user_controller(pC, 1);
        if(enemy == null) {
            enemy = new enemy_controller(char2, 2, true, true);
        }
        else{
            enemy.reset();
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        enemy.getIa().setDif(ia_loader.dif.HARD);
        player.getPlayer().setMapLimit(mapLimit);
        ((enemy_controller)enemy).setRival(player.getPlayer(), userLogged);
        ((enemy_controller)enemy).getAgente().setUser(userLogged);
        ((enemy_controller)enemy).getAgente().setqTable(table);
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);

        scene = new scenary(sce);

        fight = new fight_controller(player,enemy,scene);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(true);
        fight.setIaLvl(ia_loader.dif.HARD);

        audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
        audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);

        ((enemy_controller)enemy).getAgente().setUseRegression(true);
        if(epsilon < 1.0){
            ((enemy_controller)enemy).getAgente().writeQTableAndRegister();
            ((enemy_controller)enemy).getAgente().trainRegression();
        }
        ((enemy_controller)enemy).getAgente().setEpsilon(epsilon);

        gui.getPrincipal().gameOn();
        gui.setOnlineState(GameState.ONLINE_FIGHT);
    }

    /**
     * Generate fight.
     *
     * @param isHost   the is host
     * @param pC       the p c
     * @param pE       the p e
     * @param sce      the sce
     * @param isRanked the is ranked
     * @param rival    the rival
     */
    public void generateFight(boolean isHost, Playable_Character pC, Playable_Character pE, Scenario_type sce, boolean isRanked, String rival){
        isVsIA = 0;
        this.rankPoints = 0;
        this.char1 = pC;
        this.char2 = pE;
        this.rival = rival;
        this.isRanked = isRanked;
        this.isHost = isHost;
        if (isHost) {
            player = new online_user_controller(pC, 1, conToClient, true);
            enemy = new online_user_controller(pE, 2, conToClient, false);
            ((online_user_controller) enemy).setMenssageIdentifier(msgID.toClient.character);
            enemy.setPlayerNum(2);
        } else {
            player = new online_user_controller(pC, 1, conToClient, false);
            enemy = new online_user_controller(pE, 2, conToClient, true);
            ((online_user_controller) enemy).setMenssageIdentifier(msgID.toClient.character);
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

        fight = new online_fight_controller((online_user_controller)player, (online_user_controller)enemy, scene, conToClient, isHost, msgID.toClient.fight, -2);
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

    /**
     * Synchronize boolean.
     *
     * @param isHost the is host
     * @return the boolean
     */
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

                if(isVsIA == 0) {
                    if (((online_fight_controller) fight).connectionLost()) {
                        gui.setOnlineState(GameState.PRINCIPAL_GUI);
                        gui.clearGui();
                        gui.principalGUI();
                        gui.popUp("Connection with rival lost. The game will count as tie.");
                        conToServer.sendString(msgID.toServer.request, "REGISTER GAME:" +
                                gui.getUserLogged() + ":" + rival + ":" + char1.toString() + ":" + char2.toString() + ":0:" + isRanked);
                    }
                }
                else if(isVsIA == 1){
                    conToServer.sendString(msgID.toServer.request, "TRAIN OWN IA");
                    conToServer.sendObject(msgID.toServer.request, new qtable(((enemy_controller)enemy).getAgente().getqTable()));
                }
                else{
                    conToServer.sendString(msgID.toServer.request, "TRAIN GLOBAL IA");
                    conToServer.sendObject(msgID.toServer.request, new qtable(((enemy_controller)enemy).getAgente().getqTable()));
                }

                Fight_Results results = fight.getFight_result();
                this.result =results;
                if(isVsIA == 0 && isHost) {
                    conToClient.reliableSendString(msgID.toClient.tramits, "GAME ENDED:" + results.toString(), 200);
                }
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

                if(isVsIA == 0) {
                    if (isRanked) {
                        String res = "";
                        if (isHost) {
                            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request, "REGISTER GAME:" +
                                    gui.getUserLogged() + ":" + rival + ":" + char1.toString() + ":" + char2.toString() + ":"
                                    + r + ":" + isRanked, 0);
                        } else {
                            do {
                                res = conToServer.receiveString(msgID.toServer.request);
                            } while (!res.contains("GAME REGISTERED"));
                        }
                        rankPoints = Integer.parseInt(res.split(":")[1]);
                    } else {
                        if (isHost) {
                            conToServer.sendString(msgID.toServer.request, "REGISTER GAME:" +
                                    gui.getUserLogged() + ":" + rival + ":" + char1.toString() + ":" + char2.toString() + ":"
                                    + r + ":" + isRanked);
                        }
                    }
                }

                audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                fight.getPlayer().stop();
                fight.getEnemy().stop();
                if(isVsIA == 0 && conToClient.isConnected()) {
                    conToClient.close();
                }
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

    /**
     * Generate con to client boolean.
     *
     * @param ip the ip
     * @return the boolean
     */
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
    public character_controller getPlayer() {
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
    public character_controller getEnemy() {
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

    /**
     * Gets online state.
     *
     * @return the online state
     */
    public GameState getOnlineState() {
        return onlineState;
    }

    /**
     * Sets online state.
     *
     * @param onlineState the online state
     */
    public void setOnlineState(GameState onlineState) {
        this.onlineState = onlineState;
    }

    /**
     * Is debug boolean.
     *
     * @return the boolean
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets debug.
     *
     * @param debug the debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Is searching boolean.
     *
     * @return the boolean
     */
    public boolean isSearching() {
        return searching;
    }

    /**
     * Sets searching.
     *
     * @param searching the searching
     */
    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    /**
     * Gets con to server.
     *
     * @return the con to server
     */
    public connection getConToServer() {
        return conToServer;
    }

    /**
     * Sets con to server.
     *
     * @param conToServer the con to server
     */
    public void setConToServer(connection conToServer) {
        this.conToServer = conToServer;
    }

    /**
     * Gets server ip.
     *
     * @return the server ip
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * Sets server ip.
     *
     * @param serverIp the server ip
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * Gets server port.
     *
     * @return the server port
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets server port.
     *
     * @param serverPort the server port
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Gets con to client port.
     *
     * @return the con to client port
     */
    public int getConToClientPort() {
        return conToClientPort;
    }

    /**
     * Sets con to client port.
     *
     * @param conToClientPort the con to client port
     */
    public void setConToClientPort(int conToClientPort) {
        this.conToClientPort = conToClientPort;
    }

    /**
     * Gets gui.
     *
     * @return the gui
     */
    public online_mode_gui getGui() {
        return gui;
    }

    /**
     * Sets gui.
     *
     * @param gui the gui
     */
    public void setGui(online_mode_gui gui) {
        this.gui = gui;
    }

    /**
     * Gets user logged.
     *
     * @return the user logged
     */
    public String getUserLogged() {
        return userLogged;
    }

    /**
     * Sets user logged.
     *
     * @param userLogged the user logged
     */
    public void setUserLogged(String userLogged) {
        this.userLogged = userLogged;
    }

    /**
     * Gets rival.
     *
     * @return the rival
     */
    public String getRival() {
        return rival;
    }

    /**
     * Sets rival.
     *
     * @param rival the rival
     */
    public void setRival(String rival) {
        this.rival = rival;
    }

    /**
     * Gets char 1.
     *
     * @return the char 1
     */
    public Playable_Character getChar1() {
        return char1;
    }

    /**
     * Sets char 1.
     *
     * @param char1 the char 1
     */
    public void setChar1(Playable_Character char1) {
        this.char1 = char1;
    }

    /**
     * Gets char 2.
     *
     * @return the char 2
     */
    public Playable_Character getChar2() {
        return char2;
    }

    /**
     * Sets char 2.
     *
     * @param char2 the char 2
     */
    public void setChar2(Playable_Character char2) {
        this.char2 = char2;
    }

    /**
     * Is ranked boolean.
     *
     * @return the boolean
     */
    public boolean isRanked() {
        return isRanked;
    }

    /**
     * Sets ranked.
     *
     * @param ranked the ranked
     */
    public void setRanked(boolean ranked) {
        isRanked = ranked;
    }

    /**
     * Is host boolean.
     *
     * @return the boolean
     */
    public boolean isHost() {
        return isHost;
    }

    /**
     * Sets host.
     *
     * @param host the host
     */
    public void setHost(boolean host) {
        isHost = host;
    }

    /**
     * Gets rank points.
     *
     * @return the rank points
     */
    public int getRankPoints() {
        return rankPoints;
    }

    /**
     * Sets rank points.
     *
     * @param rankPoints the rank points
     */
    public void setRankPoints(int rankPoints) {
        this.rankPoints = rankPoints;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public Fight_Results getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(Fight_Results result) {
        this.result = result;
    }
}
