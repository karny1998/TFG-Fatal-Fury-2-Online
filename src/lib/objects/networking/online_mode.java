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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

public class online_mode {
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

    private boolean itsMe = true;

    private connection con;

    public online_mode() {
        String ip;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
            itsMe = ip.equals("192.168.1.3");
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        itsMe = true;
        if(itsMe){
            System.out.println("ME RECONOCIO");
            con = new connection("2.59.232.179",7777,7777,200);
        }
        else{
            con = new connection("2.152.232.224",7777,7777,200);
        }
        con.setBlockReception(true);
        System.out.println("Se han creado los sockets");
    }

    private void generateFight(){
        System.out.println("Se esta creando la pelea");
        if(itsMe) {
            player = new online_user_controller(Playable_Character.TERRY, 1, con, true);
            enemy = new online_user_controller(Playable_Character.TERRY, 2, con, false);
            enemy.setPlayerNum(2);
        }
        else{
            player = new online_user_controller(Playable_Character.TERRY, 1, con, false);
            enemy = new online_user_controller(Playable_Character.TERRY, 2, con, true);
            enemy.setPlayerNum(2);
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(Scenario_type.USA);
        fight = new fight_controller(player,enemy,scene);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(false);
        audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
        audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
        try {
            Thread.sleep(5000);
        }catch (Exception e){}
        con.setBlockReception(false);
        if(itsMe){
            boolean ok = false;
            do{
                System.out.println("Se ha enviado el READY");
                ok = con.reliableSend(0,"READY");
            }while(!ok);
        }
        else{
            String msg = con.receive(0);
            System.out.println("Se esta esperando el ready");
            while(!msg.equals("READY")){
                msg = con.receive(0);
            }
            System.out.println("Se ha recibido el ready");
            for(int i = 0; i < 5; ++i){
                con.sendAck(0);
            }
        }
        con.setTimeout(10);
        System.out.println("Se ha creado la pelea");
    }

    public void online_game(Map<Item_Type, screenObject> screenObjects){
        if(controlListener.menuInput(1, controlListener.ESC_INDEX)
            || controlListener.menuInput(2, controlListener.ESC_INDEX)){
            con.close();
            System.exit(0);
        }
        else if(fight == null){
            generateFight();
        }
        else if(fight.getEnd()){
            audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
            generateFight();
        }
        else{
            fight.getAnimation(screenObjects);
        }
    }

    public fight_controller getFight() {
        return fight;
    }

    public void setFight(fight_controller fight) {
        this.fight = fight;
    }

    public character_controller getPlayer() {
        return player;
    }

    public void setPlayer(character_controller player) {
        this.player = player;
    }

    public character_controller getEnemy() {
        return enemy;
    }

    public void setEnemy(character_controller enemy) {
        this.enemy = enemy;
    }
}
