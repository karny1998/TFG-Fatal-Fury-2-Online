package lib.objects;

import lib.sound.menu_audio;
import lib.utils.Pair;
import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.menus.menu;
import lib.menus.menu_generator;
import lib.sound.audio_manager;
import lib.sound.fight_audio;

import javax.swing.*;
import java.io.*;
import java.util.Map;

/**
 * The type Story mode.
 */
public class story_mode {
    /**
     * The Actual menu.
     */
// Menú actual del modo historia
    private menu actualMenu;
    /**
     * The Win menu.
     */
// Menu de victoria
    private menu winMenu = menu_generator.generate_story_win();
    /**
     * The Lose menu.
     */
// Menú de derrota
    private menu loseMenu = menu_generator.generate_story_lose();
    /**
     * The Difficulty.
     */
// Menú de dificultad
    private menu difficulty = menu_generator.generate_story_difficulty();
    /**
     * The Loads.
     */
// Pantallas de transición
    private screenObject loads[];
    /**
     * The Ends.
     */
// Pantallas del final del modo hisotria
    private screenObject ends[];
    /**
     * The Lvl ia.
     */
// Dificultad
    private ia_loader.dif lvlIa = ia_loader.dif.EASY;
    /**
     * The Score.
     */
// Score
    private int score = 0;
    /**
     * The Stage.
     */
// Nivel del modo historia en el que se está
    private int stage = 0;
    /**
     * The Charac.
     */
// Personaje que se juega
    private Playable_Character charac = Playable_Character.TERRY;
    /**
     * The Fight.
     */
// La pelea en sí
    private fight_controller fight;
    /**
     * The Player.
     */
// Controladores de los personajes
    private character_controller player, /**
     * The Enemy.
     */
    enemy;
    /**
     * The State.
     */
// Estado del modo historia, por defecto en selección de dificultad
    private GameState state = GameState.STORY_DIFFICULTY;
    /**
     * The Scene.
     */
// Escenario
    private scenary scene = new scenary(Scenario_type.USA);;
    /**
     * The Map limit.
     */
// Límites del mapa
    private hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);;
    /**
     * The Time reference.
     */
// Tiempo de referencia
    private long timeReference = System.currentTimeMillis();
    /**
     * The Playing.
     */
// Si se está jugando
    private boolean playing = false;
    /**
     * The Scenarys.
     */
// Orden de escenarios
    private Scenario_type scenarys[] = {Scenario_type.CHINA, Scenario_type.CHINA, Scenario_type.CHINA
                                        , Scenario_type.AUSTRALIA, Scenario_type.AUSTRALIA,Scenario_type.AUSTRALIA,
                                        Scenario_type.USA, Scenario_type.USA,Scenario_type.USA};
    /**
     * The Enemies.
     */
// Orden de enemigos
    private Playable_Character enemies[] = {Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI};
    /**
     * The Audio enemies.
     */
// Audios de los personajes
    private menu_audio.indexes audioEnemies[] = { menu_audio.indexes.Terry, menu_audio.indexes.Andy,menu_audio.indexes.Mai,
                                                    menu_audio.indexes.Terry,menu_audio.indexes.Andy,menu_audio.indexes.Mai,
                                                    menu_audio.indexes.Terry,menu_audio.indexes.Andy,menu_audio.indexes.Mai};
    /**
     * The Stop music.
     */
// Si hay que parar la música
    private boolean stopMusic = false;

    /**
     * Instantiates a new Story mode.
     */
// Constructor por defecto
    public story_mode(){
        // Carga la partida si la hay
        loadGame();
        // Carga las pantallas de transición
        loadLoadScreens();
    }

    /**
     * Instantiates a new Story mode.
     *
     * @param lvl the lvl
     */
// Contructor que pide la dificultad
    public story_mode(ia_loader.dif lvl){
        this.lvlIa = lvl;
        loadLoadScreens();
    }

    /**
     * Load game.
     */
// Carga la partida si existe
    void loadGame(){
        String path =  System.getProperty("user.dir") + "/.files/last_game.txt";
        boolean newGame = true;
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            if ((aux = b.readLine()) != null) {
                lvlIa = ia_loader.dif.valueOf(aux);
                if ((aux = b.readLine()) != null) {
                    stage = Integer.valueOf(aux);
                    newGame = false;
                }
                else{
                    newGame = true;
                }
            }
            else{
                newGame = true;
            }
            b.close();
            if(!newGame) {
                state = GameState.STORY_LOADING;
                audio_manager.endFight();
            }
            else{
                state = GameState.STORY_DIFFICULTY;
                actualMenu = difficulty;
                actualMenu.updateTime();
            }
        }
        catch (Exception e){
            state = GameState.STORY_DIFFICULTY;
            actualMenu = difficulty;
            actualMenu.updateTime();
        }
    }

    /**
     * Save game.
     */
// Guarda la partida en su estado actual
    void saveGame(){
        String path =  System.getProperty("user.dir") + "/.files/last_game.txt";
        File f= new File(path);
        f.delete();
        f= new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(lvlIa.toString()+"\n");
            bw.write(Integer.toString(stage));
            bw.close();
        }
        catch (Exception e){}
    }

    /**
     * Load load screens.
     */
// Carga las pantallas de transición y final
    void loadLoadScreens(){
        String path =  "/assets/sprites/menu/story/story_";
        loads = new screenObject[9];
        for(int i = 1; i < 10; ++i){
            loads[i-1] = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource(path  + i + ".png")).getImage(), Item_Type.MENU);
        }
        path =  "/assets/sprites/menu/story/end_story_";
        ends = new screenObject[3];
        for(int i = 1; i < 4; ++i){
            ends[i-1] = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource(path  + i + ".png")).getImage(), Item_Type.MENU);
        }
    }

    /**
     * Generate fight.
     */
// Crea la pelea del nivel actual
    void generateFight(){
        player = new user_controller(charac, 1);
        enemy = new enemy_controller(enemies[stage], 2);
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        enemy.getIa().setDif(lvlIa);
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(scenarys[stage]);
        fight = new fight_controller(player,enemy,scene);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(true);
    }

    /**
     * Get animation pair.
     *
     * @param screenObjects the screen objects
     * @return the pair
     */
// Obtiene el frame correspondiente al momento actual en el modo historia
    public Pair<Boolean, GameState> getAnimation(Map<Item_Type, screenObject> screenObjects){
        Boolean exit = false;
        long current = System.currentTimeMillis();
        // Selección de dificultad
        if(state == GameState.STORY_DIFFICULTY){
            screenObjects.put(Item_Type.MENU,actualMenu.getFrame());
            Pair<menu, Selectionable> p = actualMenu.select();
            if(controlListener.getStatus(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE) {
                if (p.getKey() == null) {
                    switch (p.getValue()) {
                        case EASY:
                            lvlIa = ia_loader.dif.EASY;
                            break;
                        case NORMAL:
                            lvlIa = ia_loader.dif.NORMAL;
                            break;
                        case HARD:
                            lvlIa = ia_loader.dif.HARD;
                            break;
                        case VERY_HARD:
                            lvlIa = ia_loader.dif.VERY_HARD;
                            break;
                    }
                    audio_manager.menu.play(menu_audio.indexes.option_selected);
                    audio_manager.menu.stop(menu_audio.indexes.menu_theme);
                    state = GameState.STORY_LOADING;
                    timeReference = current;
                }
            }
        }
        // Pantalla de transición
        else if(state == GameState.STORY_LOADING){
            if(current - timeReference < 3000.0 && (!playing || current - timeReference < 300.0)){
                screenObjects.put(Item_Type.MENU, loads[stage]);
                playing = true;
            }
            else if(current - timeReference < 3000.0 && current - timeReference > 200.0 && playing){
                audio_manager.menu.stop(menu_audio.indexes.menu_theme);
                audio_manager.menu.play(menu_audio.indexes.Terry);
                try {
                    Thread.sleep(1000);
                    audio_manager.menu.play(menu_audio.indexes.Versus);
                    Thread.sleep(1000);
                    audio_manager.menu.play(audioEnemies[stage]);
                    Thread.sleep(1000);
                }catch (Exception e){}
                playing = false;
            }
            else{
                generateFight();
                audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scenarys[stage]);
                audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
                state = GameState.STORY_FIGHT;
                generateFight();
                screenObjects.remove(Item_Type.MENU);
                fight.getAnimation(screenObjects);
            }
        }

        // En una pelea
        else if(state == GameState.STORY_FIGHT){
            fight.getAnimation(screenObjects);
            if(fight.showIntro  || fight.showOutro){
                audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                stopMusic = true;
            } else if(stopMusic){
                audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
                stopMusic = false;
            }
            // Si la pelea ha terminado, se muestra el menú correspondiente
            if (fight.getEnd()) {
                Fight_Results resultado = fight.getFight_result();
                audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                switch (resultado){
                    case UNFINISHED:
                    case PLAYER2_WIN:
                    case TIE:
                        audio_manager.fight.loopMusic(fight_audio.music_indexes.lose_theme);
                        actualMenu = loseMenu;
                        actualMenu.updateTime();
                        break;
                    case PLAYER1_WIN:
                        audio_manager.fight.loopMusic(fight_audio.music_indexes.win_theme);
                        actualMenu = winMenu;
                        actualMenu.updateTime();
                        break;
                }
                state = GameState.STORY_MENU;
                if(actualMenu == winMenu && stage == 8){
                    state = GameState.STORY_END;
                    timeReference = current;
                }
            }
        }
        // En menú de victoria o derrota
        else if(state == GameState.STORY_MENU){
            screenObjects.put(Item_Type.MENU,actualMenu.getFrame());
            Pair<menu, Selectionable> p = actualMenu.select();
            if(controlListener.getStatus(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE) {
                if (p.getKey() == null) {
                    switch (p.getValue()) {
                        case LOSE_EXIT:
                            exit = true;
                            audio_manager.endFight();
                            audio_manager.menu.loop(menu_audio.indexes.menu_theme);
                            break;
                        case LOSE_RETRY:
                            state = GameState.STORY_LOADING;
                            audio_manager.endFight();
                            timeReference = System.currentTimeMillis();
                            break;
                        case WIN_CONTINUE:
                            ++stage;
                            saveGame();
                            state = GameState.STORY_LOADING;
                            audio_manager.endFight();
                            timeReference = System.currentTimeMillis();
                            break;
                        case WIN_SAVE:
                            ++stage;
                            saveGame();
                            exit = true;
                            audio_manager.endFight();
                            audio_manager.menu.loop(menu_audio.indexes.menu_theme);
                            break;
                    }

                }
            }
        }
        // En el final del modo historia
        else if(state == GameState.STORY_END){
            if(current - timeReference < 4000.0){
                screenObjects.put(Item_Type.MENU, ends[0]);
            }
            else if(current - timeReference > 12000.0){
                exit = true;
                audio_manager.fight.stopMusic(fight_audio.music_indexes.win_theme);
                audio_manager.fight.stopMusic(fight_audio.music_indexes.lose_theme);
                audio_manager.endFight();
                audio_manager.menu.play(menu_audio.indexes.menu_theme);
            }
            else{
                double aux = current - timeReference - 2000.0;
                int aux2 = (int)(aux / 200.0) % 2;
                screenObjects.put(Item_Type.MENU, ends[aux2+1]);
            }

        }
        return new Pair<>(exit, state);
    }

    /**
     * Gets actual menu.
     *
     * @return the actual menu
     */
// Getters y setters
    public menu getActualMenu() {
        return actualMenu;
    }

    /**
     * Sets actual menu.
     *
     * @param actualMenu the actual menu
     */
    public void setActualMenu(menu actualMenu) {
        this.actualMenu = actualMenu;
    }

    /**
     * Gets win menu.
     *
     * @return the win menu
     */
    public menu getWinMenu() {
        return winMenu;
    }

    /**
     * Sets win menu.
     *
     * @param winMenu the win menu
     */
    public void setWinMenu(menu winMenu) {
        this.winMenu = winMenu;
    }

    /**
     * Gets lose menu.
     *
     * @return the lose menu
     */
    public menu getLoseMenu() {
        return loseMenu;
    }

    /**
     * Sets lose menu.
     *
     * @param loseMenu the lose menu
     */
    public void setLoseMenu(menu loseMenu) {
        this.loseMenu = loseMenu;
    }

    /**
     * Get loads screen object [ ].
     *
     * @return the screen object [ ]
     */
    public screenObject[] getLoads() {
        return loads;
    }

    /**
     * Sets loads.
     *
     * @param loads the loads
     */
    public void setLoads(screenObject[] loads) {
        this.loads = loads;
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
     * Gets score.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets score.
     *
     * @param score the score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets stage.
     *
     * @return the stage
     */
    public int getStage() {
        return stage;
    }

    /**
     * Sets stage.
     *
     * @param stage the stage
     */
    public void setStage(int stage) {
        this.stage = stage;
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
    public character_controller getEnemy() {
        return enemy;
    }

    /**
     * Sets enemy.
     *
     * @param enemy the enemy
     */
    public void setEnemy(character_controller enemy) {
        this.enemy = enemy;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(GameState state) {
        this.state = state;
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
     * Gets time reference.
     *
     * @return the time reference
     */
    public long getTimeReference() {
        return timeReference;
    }

    /**
     * Sets time reference.
     *
     * @param timeReference the time reference
     */
    public void setTimeReference(long timeReference) {
        this.timeReference = timeReference;
    }

    /**
     * Get scenarys scenario type [ ].
     *
     * @return the scenario type [ ]
     */
    public Scenario_type[] getScenarys() {
        return scenarys;
    }

    /**
     * Sets scenarys.
     *
     * @param scenarys the scenarys
     */
    public void setScenarys(Scenario_type[] scenarys) {
        this.scenarys = scenarys;
    }

    /**
     * Get enemies playable character [ ].
     *
     * @return the playable character [ ]
     */
    public Playable_Character[] getEnemies() {
        return enemies;
    }

    /**
     * Sets enemies.
     *
     * @param enemies the enemies
     */
    public void setEnemies(Playable_Character[] enemies) {
        this.enemies = enemies;
    }
}
