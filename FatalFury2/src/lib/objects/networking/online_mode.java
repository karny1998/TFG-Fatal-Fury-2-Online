package lib.objects.networking;

import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.objects.*;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;
import lib.training.state;
import lib.training.stateCalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

public class online_mode {
    private GameState onlineState = GameState.ONLINE_MODE;
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

    private boolean itsMe = true, debug  = false, searching = false;

    private connection conToClient, conToServer;

    private String serverIp = "fatalfury2.sytes.net";

    private int serverPort = 5555, conToClientPort = 5556,
            requestID = 1, tramitsClientsID = -2, characterMsgsID = 2, fightMsgsID = 3;

    public online_mode(boolean debug) {
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
            //itsMe = false;
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

    private void generateFight(){
        System.out.println("Se esta creando la pelea");
        if (itsMe) {
            player = new online_user_controller(Playable_Character.TERRY, 1, conToClient, true);
            enemy = new online_user_controller(Playable_Character.TERRY, 2, conToClient, false);
            enemy.setMenssageIdentifier(characterMsgsID);
            enemy.setPlayerNum(2);
        } else {
            player = new online_user_controller(Playable_Character.TERRY, 1, conToClient, false);
            enemy = new online_user_controller(Playable_Character.TERRY, 2, conToClient, true);
            enemy.setMenssageIdentifier(characterMsgsID);
            enemy.setPlayerNum(2);
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(Scenario_type.USA);
        fight = new online_fight_controller(player, enemy, scene, conToClient, itsMe, fightMsgsID, -2);
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
                ok = conToClient.reliableSend(0, "READY", 100);
            } while (!ok);
        } else {
            String msg = conToClient.receive(0);
            System.out.println("Se esta esperando el ready");
            while (!msg.equals("READY")) {
                msg = conToClient.receive(0);
            }
            System.out.println("Se ha recibido el ready");
            for (int i = 0; i < 5; ++i) {
                conToClient.sendAck(0);
            }
        }
        System.out.println("Se ha creado la pelea");
    }

    private void generateFight(String ip, boolean isHost, Playable_Character pC, Playable_Character pE, Scenario_type sce){
        System.out.println("Se va a establecer la conexion");
        conToClient = new connection(ip, conToClientPort, 0,true);
        conToClient.setPortSend(conToClientPort);
        conToClient.setBlockReception(true);
        if (isHost) {
            player = new online_user_controller(pC, 1, conToClient, true);
            enemy = new online_user_controller(pE, 2, conToClient, false);
            enemy.setMenssageIdentifier(characterMsgsID);
            enemy.setPlayerNum(2);
        } else {
            player = new online_user_controller(pC, 1, conToClient, false);
            enemy = new online_user_controller(pE, 2, conToClient, true);
            enemy.setMenssageIdentifier(characterMsgsID);
            enemy.setPlayerNum(2);
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(sce);
        fight = new online_fight_controller(player, enemy, scene, conToClient, isHost, fightMsgsID, -2);
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
                ok = conToClient.reliableSend(0, "READY", 100);
            } while (!ok);
        } else {
            String msg = conToClient.receive(0);
            while (!msg.equals("READY")) {
                msg = conToClient.receive(0);
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
        }
    }

    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    void commander(){
        String command = "";
        boolean fight = false;
        while(!fight) {
            System.out.print("Command (LOGIN, REGISTER, FIGHT): ");
            String cm = "";
            try {
                cm = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(cm.equals("exit")){
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
                    conToServer.send(requestID,"LOGIN:"+username+":"+password);
                    Thread.sleep(2000);
                    String res = conToServer.receive(requestID);
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
                    conToServer.send(requestID,"REGISTER:"+username+":"+email+":"+password);
                    Thread.sleep(2000);
                    String res = conToServer.receive(requestID);
                    if(res.equals("REGISTERED")){
                        System.out.println("Te has registrado");
                    }
                    else{
                        System.out.println(res.split(":")[1]);
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

    public void online_game(Map<Item_Type, screenObject> screenObjects){
        commander();

        if(controlListener.menuInput(1, controlListener.ESC_INDEX)
            || controlListener.menuInput(2, controlListener.ESC_INDEX)){
            if(conToClient != null){conToClient.close();}
            conToServer.send(-1, "DISCONNECT");
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
                    String msg = conToServer.receive(2);
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
                    boolean ok = conToServer.reliableSend(1, "SEARCH GAME", 500);
                    System.out.println("El servidor ha recibido la request: " + ok);
                    if (ok) {
                        System.out.println("Se pasa a buscando partida");
                        onlineState = GameState.ONLINE_SEARCHING_FIGHT;
                    }
                    break;
            }
        }
    }

    public fight_controller getFight() {
        return fight;
    }

    public void setFight(fight_controller fight) {
        this.fight = fight;
    }

    public online_user_controller getPlayer() {
        return player;
    }

    public void setPlayer(online_user_controller player) {
        this.player = player;
    }

    public online_user_controller getEnemy() {
        return enemy;
    }

    public void setEnemy(online_user_controller enemy) {
        this.enemy = enemy;
    }

    public scenary getScene() {
        return scene;
    }

    public void setScene(scenary scene) {
        this.scene = scene;
    }

    public hitBox getMapLimit() {
        return mapLimit;
    }

    public void setMapLimit(hitBox mapLimit) {
        this.mapLimit = mapLimit;
    }

    public boolean isItsMe() {
        return itsMe;
    }

    public void setItsMe(boolean itsMe) {
        this.itsMe = itsMe;
    }

    public connection getConToClient() {
        return conToClient;
    }

    public void setConToClient(connection con) {
        this.conToClient = con;
    }
}
