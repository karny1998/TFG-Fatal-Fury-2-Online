package lib.training;

import lib.Enums.*;
import lib.maps.scenary;
import lib.objects.*;
import lib.sound.audio_manager;
import lib.sound.fight_audio;

import java.util.Map;

/**
 * The type Ia vs ia training.
 */
public class IaVsIaTraining {
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
    private enemy_controller enemy = null;
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
     * Iteration
     */
    private int i = 0;
    /**
     * The Charac.
     */
    Playable_Character charac;
    /**
     * The Ia.
     */
    Playable_Character ia;
    /**
     * The P lvl.
     */
    ia_loader.dif pLvl,
    /**
     * The Lvl ia.
     */
    lvlIa;
    /**
     * The Times.
     */
    int times;
    /**
     * The file name
     */
    String filename = "";

    agent agente;

    /**
     * Instantiates a new Ia vs ia training.
     *
     * @param charac the charac
     * @param ia     the ia
     * @param pLvl   the p lvl
     * @param lvlIa  the lvl ia
     * @param times  the times
     */
    public IaVsIaTraining(Playable_Character charac, Playable_Character ia, ia_loader.dif pLvl, ia_loader.dif lvlIa, int times) {
        this.charac = charac;
        this.ia = ia;
        this.pLvl = pLvl;
        this.lvlIa = lvlIa;
        this.times = times;
        filename =  ia.toString() + lvlIa.toString() + "vs" + charac.toString() + pLvl.toString() + ".xml";
        stateCalculator.initialize();
    }

    /**
     * Generate fight.
     */
    private void generateFight(){
        player = new enemy_controller(charac, 1);//new user_controller(charac, 1);
        if(enemy == null) {
            enemy = new enemy_controller(ia, 2, true, true);
        }
        else{
            enemy.reset();
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getIa().setDif(pLvl);//comentar esto para jugadores
        enemy.getIa().setDif(lvlIa);
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(Scenario_type.USA);
        fight = new fight_controller(player,enemy,scene);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(true);
        fight.setIaLvl(lvlIa);
        audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
        audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
    }


    /**
     * Train.
     *
     * @param screenObjects the screen objects
     */
    public void train(Map<Item_Type, screenObject> screenObjects){
        if(i == times){
            System.exit(0);
        }
        else if(fight == null && i == 0){
            generateFight();
        }
        else if(fight.getEnd()){
            //enemy.getPlayer().getStats().setFilename(filename);
            enemy.getAgente().writeQTableAndRegister();
            enemy.getPlayer().getStats().saveUpdatedHistory();
            Fight_Results resultado = fight.getFight_result();
            audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
            generateFight();

            hitBox pHurt = player.getPlayer().getHurtbox();
            hitBox eHurt = enemy.getPlayer().getHurtbox();
            int dis = 0;
            if (pHurt.getX() > eHurt.getX()){
                dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
            }
            else if(pHurt.getX() < eHurt.getX()){
                dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
            }
            enemy.getAgente().restart(new state(100,100,Movement.STANDING,dis,1,90,0,0, false));
            ++i;
        }
        else{
            fight.getAnimation(screenObjects);
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
    public character_controller getPlayer() {
        return player;
    }

    /**
     * Sets player.
     *
     * @param player the player
     */
    public void setPlayer(character_controller player) {
        this.player = player;
    }

    /**
     * Gets enemy.
     *
     * @return the enemy
     */
    public enemy_controller getEnemy() {
        return enemy;
    }

    /**
     * Sets enemy.
     *
     * @param enemy the enemy
     */
    public void setEnemy(enemy_controller enemy) {
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
     * Gets i.
     *
     * @return the i
     */
    public int getI() {
        return i;
    }

    /**
     * Sets i.
     *
     * @param i the
     */
    public void setI(int i) {
        this.i = i;
    }

    /**
     * Gets charac.
     *
     * @return the charac
     */
    public Playable_Character getCharac() {
        return charac;
    }

    /**
     * Sets charac.
     *
     * @param charac the charac
     */
    public void setCharac(Playable_Character charac) {
        this.charac = charac;
    }

    /**
     * Gets ia.
     *
     * @return the ia
     */
    public Playable_Character getIa() {
        return ia;
    }

    /**
     * Sets ia.
     *
     * @param ia the ia
     */
    public void setIa(Playable_Character ia) {
        this.ia = ia;
    }

    /**
     * Gets lvl.
     *
     * @return the lvl
     */
    public ia_loader.dif getpLvl() {
        return pLvl;
    }

    /**
     * Sets lvl.
     *
     * @param pLvl the p lvl
     */
    public void setpLvl(ia_loader.dif pLvl) {
        this.pLvl = pLvl;
    }

    /**
     * Gets lvl ia.
     *
     * @return the lvl ia
     */
    public ia_loader.dif getLvlIa() {
        return lvlIa;
    }

    /**
     * Sets lvl ia.
     *
     * @param lvlIa the lvl ia
     */
    public void setLvlIa(ia_loader.dif lvlIa) {
        this.lvlIa = lvlIa;
    }

    /**
     * Gets times.
     *
     * @return the times
     */
    public int getTimes() {
        return times;
    }

    /**
     * Sets times.
     *
     * @param times the times
     */
    public void setTimes(int times) {
        this.times = times;
    }

    /**
     * Gets filename.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets filename.
     *
     * @param filename the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
