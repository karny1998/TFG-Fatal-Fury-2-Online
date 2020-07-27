package lib.training;

import lib.Enums.*;
import lib.maps.scenary;
import lib.objects.*;
import lib.sound.audio_manager;
import lib.sound.fight_audio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    String filename = System.getProperty("user.dir") + "/.files/q_learning_stats.xml";

    /**
     * The Agente.
     */
    agent agente;

    /**
     * The Load training.
     */
    private boolean loadTraining = false;

    /**
     * The Random.
     */
    private boolean random = false, /**
     * The Against himself.
     */
    againstHimself = false, /**
     * The Against person.
     */
    againstPerson = false, /**
     * The Training.
     */
    training = true, /**
     * The Use regression.
     */
    useRegression = false;

    /**
     * Instantiates a new Ia vs ia training.
     */
    public IaVsIaTraining() {
        stateCalculator.initialize();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String aux = "";
            do{
                System.out.print("Training (true, false): ");
                aux = in.readLine();
            }while (!aux.equals("true") && !(aux.equals("false")));
            training = Boolean.parseBoolean(aux);
            do{
                System.out.print("Use regression (true, false): ");
                aux = in.readLine();
            }while (!aux.equals("true") && !(aux.equals("false")));
            useRegression = Boolean.parseBoolean(aux);
            do{
                System.out.print("Aleatory fights (true, false): ");
                aux = in.readLine();
            }while (!aux.equals("true") && !(aux.equals("false")));
            random = Boolean.parseBoolean(aux);
            if(!random){
                do{
                    System.out.print("Against himself (true,false): ");
                    aux = in.readLine();
                }while (!aux.equals("true") && !(aux.equals("false")));
                againstHimself = Boolean.parseBoolean(aux);
                if(againstHimself){
                    this.charac = Playable_Character.TERRY;
                    this.pLvl = ia_loader.dif.HARD;
                }
                else {
                    do {
                        System.out.print("Rival (TERRY, MAI, ANDY): ");
                        aux = in.readLine();
                    } while (!aux.equals("TERRY") && !(aux.equals("MAI")) && !(aux.equals("ANDY")));
                    this.charac = Playable_Character.valueOf(aux);

                    do{
                        System.out.print("Against person (true,false): ");
                        aux = in.readLine();
                    }while (!aux.equals("true") && !(aux.equals("false")));
                    againstPerson = Boolean.parseBoolean(aux);

                    if(!againstPerson) {
                        do {
                            System.out.print("Level (EASY, NORMAL, HARD, VERY_HARD): ");
                            aux = in.readLine();
                        } while (!aux.equals("EASY") && !(aux.equals("NORMAL")) && !(aux.equals("HARD")) && !(aux.equals("VERY_HARD")));
                        this.pLvl = ia_loader.dif.valueOf(aux);
                    }
                }
            }
            this.ia = Playable_Character.TERRY;
            this.lvlIa = ia_loader.dif.HARD;

            System.out.print("Iterations (integer): ");
            aux = in.readLine();
            this.times = Integer.parseInt(aux);

            System.out.print("Starting iteration (integer): ");
            aux = in.readLine();
            this.i = Integer.parseInt(aux);

            do{
                System.out.print("Load training (true,false): ");
                aux = in.readLine();
            }while (!aux.equals("true") && !(aux.equals("false")));
            this.loadTraining = Boolean.parseBoolean(aux);
            in.close();
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Generate fight.
     */
    private void generateFight(){
        if (againstPerson) {
            player = new user_controller(charac, 1);
        }
        else if(!againstHimself) {
            if(random) {
                int r = ((int) (Math.random() * 3.0)) % 3;
                switch (r) {
                    case 0:
                        charac = Playable_Character.TERRY;
                        break;
                    case 1:
                        charac = Playable_Character.MAI;
                        break;
                    case 2:
                        charac = Playable_Character.ANDY;
                        break;
                    default:
                        charac = Playable_Character.TERRY;
                        break;
                }
                r = ((int) (Math.random() * 3.0)) % 3;
                switch (r) {
                    case 0:
                        pLvl = ia_loader.dif.NORMAL;
                        break;
                    case 1:
                        pLvl = ia_loader.dif.HARD;
                        break;
                    case 2:
                        pLvl = ia_loader.dif.VERY_HARD;
                        break;
                    default:
                        pLvl = ia_loader.dif.HARD;
                        break;
                }
            }
            player = new enemy_controller(charac, 1);
        }
        if(enemy == null) {
            if(againstHimself) {
                player = new enemy_controller(ia, 1, true, false);
            }
            enemy = new enemy_controller(ia, 2, true, training);
        }
        else{
            if(againstHimself) {
                ((enemy_controller) player).reset();
            }
            enemy.reset();
        }
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        if(!againstPerson) {
            player.getIa().setDif(pLvl);
        }
        enemy.getIa().setDif(lvlIa);
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(Scenario_type.USA);
        fight = new fight_controller(player,enemy,scene);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(true);
        fight.setIaLvl(lvlIa);
        audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
        audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
        double epsilon = 1.0 - (double)i/3000.0;
        agente = enemy.getAgente();
        if(epsilon >= 0.05) {
            enemy.getAgente().setEpsilon(epsilon);
        }
        else{
            enemy.getAgente().setEpsilon(0.05);
        }
        if(loadTraining){
            enemy.getAgente().loadTraining("trainingRegister.txt");
            loadTraining = false;
        }
        if(againstHimself) {
            ((enemy_controller)player).getAgente().setEpsilon(0.0);
        }
        if(!training) {
            enemy.getAgente().setEpsilon(0.0);
        }
        enemy.getAgente().setUseRegression(useRegression);
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
        else if(fight == null){
            generateFight();
            enemy.getPlayer().getStats().getActualFight().setRival(charac);
            enemy.getPlayer().getStats().getActualFight().setLvlRival(pLvl);
        }
        else if(fight.getEnd()){
            enemy.getPlayer().getStats().setFilename(filename);
            enemy.getAgente().writeQTableAndRegister();
            enemy.getAgente().trainRegression();
            enemy.getPlayer().getStats().getActualFight().setAccumulatedReward((int) enemy.getAgente().getAccumulatedReward());
            enemy.getPlayer().getStats().saveUpdatedHistory();
            enemy.getPlayer().getStats().nextFight();
            Fight_Results resultado = fight.getFight_result();
            audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
            ++this.i;
            generateFight();
            enemy.getPlayer().getStats().getActualFight().setRival(charac);
            enemy.getPlayer().getStats().getActualFight().setLvlRival(pLvl);
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
    /*public void setPlayer(character_controller player) {
        this.player = player;
    }*/

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
