package lib.objects;

import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.menus.character_menu;
import lib.menus.menu;
import lib.menus.menu_generator;
import lib.menus.options;
import lib.objects.networking.online_mode;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;
import lib.training.IaVsIaTraining;
import lib.utils.Pair;
import lib.utils.fileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import static lib.Enums.Item_Type.*;
import static lib.Enums.Item_Type.SCORE_FRAME;


/**
 * The type Game controller.
 */
// Clase que representa un controlador principal del juego que se encarga de gestionarlo
public class game_controller {
    /**
     * The Screen.
     */
    Screen screen;
    /**
     * The Ran.
     */
    Random ran = new Random();
    /**
     * The training.
     */
// Modo debug
    boolean training = false;
    /**
     * The Trainer.
     */
    IaVsIaTraining trainer;// = new IaVsIaTraining();
    /**
     * The Debug.
     */
// Modo debug
    boolean debug = false;
    /**
     * The Stop music.
     */
    boolean stopMusic = false;
    /**
     * The Fight.
     */
// Controlador de una pelea
    private fight_controller fight;
    /**
     * The Scene.
     */
// Escenario de una pelea
    private scenary scene;
    /**
     * The Principal.
     */
// Menus
    private menu principal,
    /**
     * The Basic menu.
     */
    basicMenu,
    /**
     * The Game menu.
     */
    gameMenu;
    /**
     * The Sure.
     */
// Menu de salir del juego
    private menu sure;
    /**
     * The Difficulty.
     */
// Menu de selección de dificultad
    private menu difficulty;
    /**
     * The Actual menu.
     */
// Menu actual
    private menu actualMenu;
    /**
     * The Escape menu.
     */
// Menu al presionar escape en una pelea
    private menu escapeMenu;
    /**
     * The Map selection.
     */
// Menu de selección de mapa
    private menu mapSelection;
    /**
     * The Char menu.
     */
// Menu de selección de personaje
    private character_menu charMenu;
    /**
     * The Options menu.
     */
// Menu de opciones del juego
    private options optionsMenu;
    /**
     * The User.
     */
// Controladores de los dos personajes de la pelkea
    private character_controller user;
    /**
     * The Enemy.
     */
    private character_controller enemy;
    /**
     * The State.
     */
// Estado del juego, inicalmente en opening
    private GameState state = GameState.NAVIGATION;
    /**
     * The Ranking.
     */
// Ranking
    private score ranking = new score(ia_loader.dif.EASY);
    /**
     * The Name.
     */
// Instancia de la clase que implementa el pedir el nombre
    private ask_for_name name = new ask_for_name();
    /**
     * The Pvp.
     */
// Si es modo 1P vs 2P
    private boolean pvp = false;
    /**
     * The Map limit.
     */
// Limites del mapa
    hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);
    /**
     * The Story.
     */
// Modo historia
    private story_mode story;
    /**
     * The Story on.
     */
// Es modo historia
    private boolean storyOn;
    /**
     * The Openings.
     */
// Ruta sprites del principio del juego
    private String openings = "/assets/sprites/menu/opening/opening_";
    /**
     * The Tiempo.
     */
// Marcas de tiempo
    private long tiempo = System.currentTimeMillis();
    /**
     * The Time reference.
     */
    private long timeReference = System.currentTimeMillis();
    /**
     * The On demo.
     */
// Está la demo mostrandose
    private boolean onDemo = false;
    /**
     * The From escape.
     */
// Se vuelve del menu escape
    private boolean fromEscape = false;
    /**
     * The Start.
     */
// Sprite press start de la pantalla principal
    private screenObject start = new screenObject(357, 482,  549, 35, new ImageIcon(menu_generator.class.getResource("/assets/sprites/menu/press_start.png")).getImage(), Item_Type.MENU);;
    /**
     * The Lvl ia.
     */
// Cargador de la inteligencia artifical
    private ia_loader.dif lvlIa;
    /**
     * The How.
     */
// Sprite de como jugar
    private screenObject how = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource("/assets/sprites/menu/how_to_play.png")).getImage(), Item_Type.MENU);;
    /**
     * The Font stream.
     */
// Fuente de menus
    private InputStream fontStream = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");
    /**
     * The Font.
     */
    private Font font;
    {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(18f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The Online.
     */
    private online_mode online;// = new online_mode(false);

    /**
     * Instantiates a new Game controller.
     *
     * @param screen the screen
     */
// Constructor del game controller por defecto, se inicializan variables
    public game_controller(Screen screen) {
        this.screen = screen;
        this.sure = menu_generator.generate_sure();
        this.principal = menu_generator.generate();
        this.basicMenu = principal.getSelectionables().get(Selectionable.START).getMen();
        this.gameMenu = basicMenu.getSelectionables().get(Selectionable.PRINCIPAL_GAME).getMen();
        this.actualMenu = principal;
        this.difficulty = menu_generator.generate_story_difficulty();
        this.escapeMenu = menu_generator.generate_scape();
        this.mapSelection = menu_generator.generate_map_selection();
    }

    /**
     * Get frame.
     *
     * @param screenObjects the screen objects
     */
// Asigna a screenObjects las cosas a mostrar por pantalla
    public void getFrame(Map<Item_Type, screenObject> screenObjects){
        if(training){
            fight = trainer.getFight();
            trainer.train(screenObjects);
        }
        else if(state == GameState.ONLINE_MODE){
            online.online_game(screenObjects);
        }
        else if(debug && state != GameState.OPTIONS) {
            optionsMenu = new options();
            actualMenu.updateTime();
            optionsMenu.updateTime();
            state = GameState.OPTIONS;
        }
        // Estado del juego de opening 1 (incial), se muestra un sprite en toda la pantalla
        else if(state == GameState.OPENING_1){
            screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(openings + "1.png")).getImage(), Item_Type.MENU);
            screenObjects.put(Item_Type.MENU, s);
            long actual = System.currentTimeMillis();
            if( actual - tiempo > 5000.0){
                state = GameState.OPENING_2;
                tiempo = System.currentTimeMillis();
            }
        }
        // Estado del juego de opening 2 , se muestra un sprite en toda la pantalla
        else if(state == GameState.OPENING_2){
            screenObjects.remove(Item_Type.MENU);
            screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(openings + "2.png")).getImage(), Item_Type.MENU);
            screenObjects.put(Item_Type.MENU, s);
            long actual = System.currentTimeMillis();
            if( actual - tiempo > 5000.0){
                state = GameState.NAVIGATION;
                tiempo = System.currentTimeMillis();
                timeReference = tiempo;
            }
        }
        // Estado del juego demo, se muestra una pelea aleatoria entre dos personajes controlados por inteligencia artificial
        // Se entra tras estar 10 segundos en el menu principal del juego sin pulsar ninguna tecla
        else if(state == GameState.DEMO){
            if(controlListener.anyKeyPressed()){
                fight.pauseFight();
                state = GameState.NAVIGATION;
                timeReference = System.currentTimeMillis();
                clearInterface(screenObjects);
                actualMenu.updateTime();
                if(onDemo){
                    audio_manager.endFight();
                }
                onDemo = false;
            }
            else{
                if(!onDemo){
                    int x = ran.nextInt(3);
                    switch (x){
                        case 0:
                            user = new enemy_controller(Playable_Character.TERRY, 1);
                            System.out.println("Terry");
                            break;
                        case 1:
                            user = new enemy_controller(Playable_Character.ANDY, 1);
                            System.out.println("Andy");
                            break;
                        case 2:
                            user = new enemy_controller(Playable_Character.MAI, 1);
                            System.out.println("Mai");
                            break;
                    }
                    System.out.println("vs");
                    int y = ran.nextInt(3);
                    switch (y){
                        case 0:
                            enemy = new enemy_controller(Playable_Character.TERRY, 2);
                            System.out.println("Terry");
                            break;
                        case 1:
                            enemy = new enemy_controller(Playable_Character.ANDY, 2);
                            System.out.println("Andy");
                            break;
                        case 2:
                            enemy = new enemy_controller(Playable_Character.MAI, 2);
                            System.out.println("Mai");
                            break;
                    }

                    enemy.setRival(user.getPlayer());
                    enemy.getPlayer().setMapLimit(mapLimit);
                    user.setRival(enemy.getPlayer());
                    user.getPlayer().setMapLimit(mapLimit);

                    System.out.println("in");

                    int z = ran.nextInt(3);
                    switch (z){
                        case 0:
                            scene = new scenary(Scenario_type.CHINA);
                            System.out.println("China");
                            break;
                        case 1:
                            scene = new scenary(Scenario_type.USA);
                            System.out.println("USA");
                            break;
                        case 2:
                            scene = new scenary(Scenario_type.AUSTRALIA);
                            System.out.println("Australia");
                            break;
                    }

                    int a = ran.nextInt(4);
                    switch (a){
                        case 0:
                            user.getIa().setDif(ia_loader.dif.EASY);
                            System.out.println("Easy");
                            break;
                        case 1:
                            user.getIa().setDif(ia_loader.dif.NORMAL);
                            System.out.println("Normal");
                            break;
                        case 2:
                            user.getIa().setDif(ia_loader.dif.HARD);
                            System.out.println("Hard");
                            break;
                        case 3:
                            user.getIa().setDif(ia_loader.dif.VERY_HARD);
                            System.out.println("Very hard");
                            break;
                    }

                    int b = ran.nextInt(4);
                    switch (b){
                        case 0:
                            enemy.getIa().setDif(ia_loader.dif.EASY);
                            System.out.println("Easy");
                            break;
                        case 1:
                            enemy.getIa().setDif(ia_loader.dif.NORMAL);
                            System.out.println("Normal");
                            break;
                        case 2:
                            enemy.getIa().setDif(ia_loader.dif.HARD);
                            System.out.println("Hard");
                            break;
                        case 3:
                            enemy.getIa().setDif(ia_loader.dif.VERY_HARD);
                            System.out.println("Very hard");
                            break;
                    }

                    audio_manager.startFight(user.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scene.getScenario());
                    audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
                    fight = new fight_controller(user,enemy,scene);
                    fight.setMapLimit(mapLimit);
                    fight.setVsIa(true);
                    fight.setIaLvl(enemy.getIa().getDif());
                    onDemo = true;
                    screenObjects.remove(Item_Type.MENU);
                    timeReference = System.currentTimeMillis();
                }
                else{
                    fight.getAnimation(screenObjects);
                    screenObjects.put(Item_Type.MENU, start);
                    if((int)((System.currentTimeMillis() - timeReference)/500.0)%2 == 1){
                        screenObjects.remove(Item_Type.MENU);
                    }
                    if(fight.getEnd()){
                        state = GameState.NAVIGATION;
                        fight.pauseFight();
                        timeReference = System.currentTimeMillis();
                        clearInterface(screenObjects);
                        onDemo = false;
                        actualMenu.updateTime();
                        audio_manager.endFight();
                        audio_manager.menu.play(menu_audio.indexes.menu_theme);
                    }
                }
            }
        }
        // Teecla presionada por el usuario
        // Si se está navegando por los menús
        else if(state == GameState.NAVIGATION){
            if(fight != null){
                fight = null;
                enemy = null;
                user = null;
                System.gc();
            }
            // Cambio al modo demo: han pasado 10 segundos sin presionar nada
            if(actualMenu == principal && System.currentTimeMillis() - timeReference > 10000.0){
                state = GameState.DEMO;
            }
            if(controlListener.menuInput(1, controlListener.ESC_INDEX) && System.currentTimeMillis() - timeReference >= 300.0){
                audio_manager.menu.play(menu_audio.indexes.back);
                if(actualMenu == gameMenu){
                    actualMenu = basicMenu;
                    actualMenu.updateTime();
                }
                timeReference = System.currentTimeMillis();
            }
            screenObject s = actualMenu.getFrame();
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = actualMenu.select();
            // Si el menú es el de start, y se presiona cualquier tecla (de las mapeadas)
            if(p.getValue() == Selectionable.START && controlListener.anyKeyPressed()){
                audio_manager.menu.loop(menu_audio.indexes.menu_theme);
                actualMenu = p.getKey();
                actualMenu.updateTime();
            }
            // Si se presiona enter y ha pasado el tiempo de margen entre menu y menu
            else if( controlListener.menuInput(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE){
                audio_manager.menu.play(menu_audio.indexes.option_selected);
                // La selección no lleva a ningún menú nuevo (p.e. cuando se da a jugar)
                if(p.getKey() == null) {
                    switch (p.getValue()) {
                        // Sale del juego
                        case PRINCIPAL_EXIT:
                            state = GameState.SURE;
                            actualMenu = sure;
                            fromEscape = false;
                            break;
                        // Cambio al menu de opciones del juego
                        case PRINCIPAL_OPTIONS:
                            optionsMenu = new options();
                            actualMenu.updateTime();
                            optionsMenu.updateTime();
                            state = GameState.OPTIONS;
                            break;
                        // Cambio a la pantalla de ranking
                        case PRINCIPAL_RANK:
                            ranking.reloadRanking();
                            state = GameState.RANKING;
                            break;
                        // Cambio al estado de juego 1p vs 2p
                        case GAME_MULTIPLAYER:
                            actualMenu.updateTime();
                            charMenu = new character_menu(0);
                            state = GameState.PLAYERS;
                            pvp = true;
                            break;
                        // Cambio al estado de juego 1p vs IA
                        case GAME_IA:
                            actualMenu = difficulty;
                            actualMenu.updateTime();
                            state = GameState.DIFFICULTY;
                            pvp = false;
                            break;
                        // Cambio al estado de juego historia
                        case GAME_HISTORY:
                            story = new story_mode();
                            state = GameState.STORY;
                            break;
                        // Cambio a la pantalla de como jugar
                        case GAME_HOW:
                            state = GameState.HOW_TO_PLAY;
                            break;
                        case ONLINE:
                            state = GameState.ONLINE_MODE;
                            online = new online_mode(screen,false);
                            break;
                    }
                }
                // La selección lleva a un nuevo menu (actualiza el tiempo de referencia
                // para evitar clicks residuales)
                else{
                    actualMenu = p.getKey();
                    actualMenu.updateTime();
                }
            }
        }
        // Estado del juego SURE, pregunta al usuario si de verdad quiere salir del juego
        else if(state == GameState.SURE){
            screenObject s = actualMenu.getFrame();
            s.setX(317);
            s.setY(217);
            screenObjects.put(Item_Type.SURE, s);
            Pair<menu, Selectionable> p = actualMenu.select();
            if( controlListener.menuInput(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE){
                audio_manager.menu.play(menu_audio.indexes.option_selected);
                // La selección no lleva a ningún menú nuevo (p.e. cuando se da a jugar)
                if(p.getKey() == null) {
                    switch (p.getValue()) {
                        case YES:
                            // Fin del programa
                            System.exit(0);
                            break;
                        case NO:
                            // Volver al menu en el que se encontraba
                            if(fromEscape){
                                state = GameState.ESCAPE;
                                actualMenu = escapeMenu;
                            }
                            else{
                                state = GameState.NAVIGATION;
                                actualMenu = basicMenu;
                            }
                            timeReference = System.currentTimeMillis();
                            actualMenu.updateTime();
                            fromEscape = false;
                            screenObjects.remove(Item_Type.SURE);
                    }
                }
            }
        }
        // Estado del juego how to play, muestra una pantalla con los controles de la pelea
        else if(state == GameState.HOW_TO_PLAY){
            screenObjects.put(Item_Type.MENU, how);
            if(controlListener.getStatus(1, controlListener.ESC_INDEX) && System.currentTimeMillis() - timeReference > 300.0){
                timeReference = System.currentTimeMillis();
                actualMenu = gameMenu;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
            }
        }
        // Estado del juego dificultad, muestra un menu para elegir la dificultad de la pelea e informa de que diferencia
        // un nivel de dificultad de otro
        else if (state == GameState.DIFFICULTY){
            if(controlListener.getStatus(1, controlListener.ESC_INDEX) && System.currentTimeMillis() - timeReference > 300.0){
                timeReference = System.currentTimeMillis();
                actualMenu = gameMenu;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
            }
            else {
                screenObjects.put(Item_Type.MENU, actualMenu.getFrame());
                Pair<menu, Selectionable> p = actualMenu.select();
                if (controlListener.menuInput(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE) {
                    if (p.getKey() == null) {
                        // Ajustar el nivel de la inteligencia artificial segun lo escogido
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
                        actualMenu.updateTime();
                        charMenu = new character_menu(1);
                        // Transición al estado de selección de personaje
                        state = GameState.PLAYERS;
                    }
                }
            }
        }
        // Estado del juego players, muestra el menu de selección de personajes
        else if (state == GameState.PLAYERS){
            // Se ha presionado ESC, volver atrás
            if(controlListener.menuInput(1, controlListener.ESC_INDEX)){
                timeReference = System.currentTimeMillis();
                audio_manager.menu.play(menu_audio.indexes.back);
                actualMenu = gameMenu;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
                screenObjects.remove(Item_Type.P1_SELECT);
                screenObjects.remove(Item_Type.P2_SELECT);
                screenObjects.remove(Item_Type.P1_MUG);
                screenObjects.remove(Item_Type.P2_MUG);
                screenObjects.remove(Item_Type.P1_NAME);
                screenObjects.remove(Item_Type.P2_NAME);
            }
            else {
                Boolean res = charMenu.gestionMenu(screenObjects);
                if (res == true) {
                    user = new user_controller(charMenu.getP1_ch(), 1);
                    if (pvp) {
                        enemy = new user_controller(charMenu.getP2_ch(), 2);
                        enemy.setPlayerNum(2);
                    } else {
                        enemy = new enemy_controller(charMenu.getP2_ch(), 2);
                        enemy.setPlayerNum(2);
                    }
                    enemy.setRival(user.getPlayer());
                    if(!pvp){
                        enemy.getIa().setDif(lvlIa);
                    }
                    enemy.getPlayer().setMapLimit(mapLimit);
                    user.setRival(enemy.getPlayer());
                    user.getPlayer().setMapLimit(mapLimit);
                    actualMenu = mapSelection;
                    actualMenu.updateTime();
                    state = GameState.MAP;
                }
            }
        }
        // Estado del juego options, muestra el menu de opciones (volumen, controles P1, controles P2)
        else if (state == GameState.OPTIONS){
            if (optionsMenu.gestionMenu(screenObjects)){
                state = GameState.NAVIGATION;
            }
        }
        // Estado del juego map, muestra el menu de selección de mapa para la pelea
        else if (state == GameState.MAP){
            // Se ha presionado ESC, volver atrás
            if(controlListener.menuInput(1, controlListener.ESC_INDEX) && System.currentTimeMillis()-timeReference > 300.0){
                audio_manager.menu.play(menu_audio.indexes.back);
                actualMenu = gameMenu;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
                timeReference = System.currentTimeMillis();
            }

            screenObject s = actualMenu.getFrame();
            screenObjects.remove(Item_Type.P1_SELECT);
            screenObjects.remove(Item_Type.P2_SELECT);
            screenObjects.remove(Item_Type.P1_MUG);
            screenObjects.remove(Item_Type.P2_MUG);
            screenObjects.remove(Item_Type.P1_NAME);
            screenObjects.remove(Item_Type.P2_NAME);
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = actualMenu.select();
            // Si se presiona enter y ha pasado el tiempo de margen entre menu y menu
            if( controlListener.menuInput(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE){
                audio_manager.menu.play(menu_audio.indexes.fight_selected);
                // La selección no lleva a ningún menú nuevo (p.e. cuando se da a jugar)
                Scenario_type map = Scenario_type.USA;
                if(p.getKey() == null) {
                    switch (p.getValue()) {
                        // Sale del juego
                        case MAP_USA:
                            scene = new scenary(Scenario_type.USA);
                            map = Scenario_type.USA;
                            break;
                        case MAP_AUS:
                            scene = new scenary(Scenario_type.AUSTRALIA);
                            map = Scenario_type.AUSTRALIA;
                            break;
                        // Inica una partida
                        case MAP_CHI:
                            scene = new scenary(Scenario_type.CHINA);
                            map = Scenario_type.CHINA;
                            break;
                    }

                    audio_manager.startFight(user.getPlayer().getCharac(), enemy.getPlayer().getCharac(), map);
                    fight = new fight_controller(user,enemy,scene);
                    fight.setMapLimit(mapLimit);
                    fight.setVsIa(!pvp);
                    if(!pvp){
                        fight.setIaLvl(enemy.getIa().getDif());
                    }
                    screenObjects.remove(Item_Type.MENU);

                    state = GameState.FIGHT;
                }
                // La selección lleva a un nuevo menu (actualiza el tiempo de referencia
                // para evitar clicks residuales)
                else{
                    actualMenu = p.getKey();
                    actualMenu.updateTime();
                }
            }
        }
        // Estado del juego fight, se está peleando
        else if(state == GameState.FIGHT){
            // Si se ha presionado escape se cambia de estado
            if( controlListener.menuInput(1, controlListener.ESC_INDEX) && System.currentTimeMillis()-timeReference>300.0){
                timeReference = System.currentTimeMillis();
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Pause);
                fight.pauseFight();
                if(fight.getPlayer().getPlayer().getState() != Movement.VICTORY_FIGHT &&
                        fight.getPlayer().getPlayer().getState() != Movement.VICTORY_ROUND &&
                        fight.getPlayer().getPlayer().getState() != Movement.DEFEAT) {
                    fight.getPlayer().getPlayer().stop();
                }
                if(fight.getEnemy().getPlayer().getState() != Movement.VICTORY_FIGHT &&
                        fight.getEnemy().getPlayer().getState() != Movement.VICTORY_ROUND &&
                        fight.getEnemy().getPlayer().getState() != Movement.DEFEAT) {
                    fight.getEnemy().getPlayer().stop();
                }
                state = GameState.ESCAPE;
            }
            // Si se está mostrando la pelea
            else {
                screenObjects.remove(Item_Type.MENU);
                fight.getAnimation(screenObjects);
                if(fight.showIntro  || fight.showOutro){
                    audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                    stopMusic = true;
                } else if(stopMusic){
                    audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
                    stopMusic = false;
                }
                // Ha terminado la pelea
                if (fight.getEnd()) {
                    if(fight.isVsIa()){
                        Fight_Results resultado = fight.getFight_result();
                        audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                        switch (resultado){
                            case UNFINISHED:
                            case PLAYER2_WIN:
                            case TIE:
                                audio_manager.fight.loopMusic(fight_audio.music_indexes.lose_theme);
                                break;
                            case PLAYER1_WIN:
                                audio_manager.fight.loopMusic(fight_audio.music_indexes.win_theme);
                                break;
                        }
                        name = new ask_for_name();

                        state = GameState.TYPING;
                    }
                    else{
                        audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                        audio_manager.endFight();
                        audio_manager.menu.loop(menu_audio.indexes.menu_theme);
                        state = GameState.NAVIGATION;
                        actualMenu = gameMenu;
                        principal.updateTime();
                    }
                    timeReference = System.currentTimeMillis();
                    // Borrar todos los elementos de la interfaz de pelea
                    clearInterface(screenObjects);
                }
            }
        }
        // Estado del juego escape, si se le ha dado a escape durante una pelea
        else if (state == GameState.ESCAPE){
            if(!storyOn) {
                fight.pauseFight();
            }
            screenObject s = escapeMenu.getFrame();
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = escapeMenu.select();
            // Si se ha presionado enter para seleccionar alguna opción
            if ( controlListener.menuInput(1, controlListener.ENT_INDEX) ){
                audio_manager.menu.play(menu_audio.indexes.option_selected);
                switch (p.getValue()){
                    // Retomar la partida
                    case ESCAPE_RESUME:
                        if(!storyOn) {
                            state = GameState.FIGHT;
                            fight.resumeFight();
                        }
                        else{
                            state = GameState.STORY_FIGHT;
                            story.getFight().resumeFight();;
                        }
                        screenObjects.remove(Item_Type.MENU);
                        escapeMenu.updateTime();
                        break;
                    // Volver al menú de juego
                    case ESCAPE_BACK:
                        actualMenu = gameMenu;
                        actualMenu.updateTime();
                        state = GameState.NAVIGATION;
                        clearInterface(screenObjects);
                        audio_manager.endFight();
                        audio_manager.menu.loop(menu_audio.indexes.menu_theme);
                        break;
                    // Salir del juego
                    case ESCAPE_EXIT:
                        state = GameState.SURE;
                        fromEscape = true;
                        actualMenu = sure;
                        actualMenu.updateTime();
                        break;
                }
            }
        }
        else if(state == GameState.RANKING && controlListener.menuInput(1, controlListener.ESC_INDEX) ){
            audio_manager.menu.play(menu_audio.indexes.back);
            state = GameState.NAVIGATION;
        }
        // Estado del juego typing, muestra la introducción del nombre tras una pelea
        else if (state == GameState.TYPING){
            if (name.gestionMenu(screenObjects)){
                audio_manager.fight.stopMusic(fight_audio.music_indexes.win_theme);
                audio_manager.fight.stopMusic(fight_audio.music_indexes.lose_theme);
                audio_manager.endFight();
                audio_manager.menu.play(menu_audio.indexes.fight_selected);
                audio_manager.menu.loop(menu_audio.indexes.menu_theme);
                fight.getScorePlayer().writeRankScore(name.getName());
                timeReference = System.currentTimeMillis();
                actualMenu = gameMenu;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
                screenObjects.remove(Item_Type.P1_SELECT);
                screenObjects.remove(Item_Type.P2_SELECT);
            }
        }
        // Estados del juego relacionados con el modo historia
        else if (state == GameState.STORY || state == GameState.STORY_FIGHT
                || state == GameState.STORY_MENU || state == GameState.STORY_LOADING
                || state == GameState.STORY_END || state == GameState.STORY_DIFFICULTY){
            if(state == GameState.STORY_MENU || state == GameState.STORY_LOADING
                    || state == GameState.STORY_END || state == GameState.STORY_DIFFICULTY){
                clearInterface(screenObjects);
            }
            if(controlListener.menuInput(1, controlListener.ESC_INDEX) ){
                timeReference = System.currentTimeMillis();
                if(state == GameState.STORY_FIGHT) {
                    actualMenu = escapeMenu;
                    actualMenu.updateTime();
                    state = GameState.ESCAPE;
                    storyOn = true;
                    story.getFight().pauseFight();
                    story.getFight().getPlayer().getPlayer().stop();
                    story.getFight().getEnemy().getPlayer().stop();
                }
                else if (state == GameState.STORY_DIFFICULTY){
                    actualMenu = gameMenu;
                    actualMenu.updateTime();
                    state = GameState.NAVIGATION;
                }
            }
            else {
                Pair<Boolean, GameState> aux = story.getAnimation(screenObjects);
                state = aux.getValue();
                if (aux.getKey()) {
                    if(state == GameState.STORY_END){
                        String ruta =  System.getProperty("user.dir") + "/.files";
                        String origen = "/files/";
                        try {
                            fileUtils.copy(origen+"last_game.txt", ruta+"/last_game.txt");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    clearInterface(screenObjects);
                    screenObjects.remove(Item_Type.MENU);
                    state = GameState.NAVIGATION;
                    actualMenu = basicMenu;
                    actualMenu.updateTime();
                    storyOn = false;
                }
            }
        }
    }

    /**
     * Clear interface.
     *
     * @param screenObjects the screen objects
     */
// Borrar todos los elementos de la pantalla que pertenecen a la interfaz de pelea
    private void clearInterface(Map<Item_Type, screenObject> screenObjects) {
        screenObjects.remove(Item_Type.SHADOW_1);
        screenObjects.remove(Item_Type.SHADOW_2);
        screenObjects.remove(Item_Type.TIMER1);
        screenObjects.remove(Item_Type.TIMER2);
        screenObjects.remove(Item_Type.TIMERFRAME);
        screenObjects.remove(Item_Type.HPBAR1);
        screenObjects.remove(Item_Type.HPBAR2);
        screenObjects.remove(Item_Type.NAME1);
        screenObjects.remove(Item_Type.NAME2);
        screenObjects.remove(Item_Type.INDICATOR1);
        screenObjects.remove(Item_Type.INDICATOR2);
        screenObjects.remove(Item_Type.BUBBLE1);
        screenObjects.remove(Item_Type.BUBBLE2);
        screenObjects.remove(Item_Type.BUBBLE3);
        screenObjects.remove(Item_Type.BUBBLE4);
        screenObjects.remove(Item_Type.ANNOUNCEMENT);
        Item_Type[] types = new Item_Type[]{    SCORE_TEXT, SCOREN1, SCOREN2, SCOREN3, SCOREN4, SCOREN5,
                LIFE_TEXT, LIFEN1, LIFEN2, LIFEN3, LIFEN4, LIFEN5,
                TIME_TEXT, TIMEN1, TIMEN2, TIMEN3, TIMEN4, TIMEN5,
                TOTAL_TEXT, TOTALN1, TOTALN2, TOTALN3, TOTALN4, TOTALN5,
                BONUS, SCORE_FRAME };
        for (Item_Type i : types) {
            screenObjects.remove(i);
        }
    }

    /**
     * Write direcly.
     *
     * @param g      the g
     * @param offset the offset
     */
// Escribir directamente sobre el gráfico de la pantalla
    public void writeDirecly(Graphics2D g, int offset){
        if(training){
            trainer.getFight().drawHpBarPlayer(g, offset);
            trainer.getFight().drawHpBarEnemy(g, offset);
        }
        else if(state == GameState.ONLINE_MODE && online.getFight() != null){
            online.getFight().drawHpBarPlayer(g, offset);
            online.getFight().drawHpBarEnemy(g, offset);
            online.getFight().writeDirecly(g,offset);
        }
        else if(state == GameState.RANKING){
            ranking.printRanking(g);
        }
        else if(state == GameState.FIGHT || state == GameState.ESCAPE
                || state == GameState.DEMO || state == GameState.SURE && fromEscape) {
            // Hitboxes, hurtboxes y coverboxes de los personajes para el modo debug
            if(debug){
                fight.player.player.getHitbox().drawHitBox(g);
                fight.player.player.getHurtbox().drawHitBox(g);
                fight.enemy.player.getHitbox().drawHitBox(g);
                fight.enemy.player.getHurtbox().drawHitBox(g);
                fight.player.player.getCoverbox().drawHitBox(g);
                fight.enemy.player.getCoverbox().drawHitBox(g);
            }
            if(!storyOn && fight != null) {
                fight.drawHpBarPlayer(g, offset);
                fight.drawHpBarEnemy(g, offset);
            }
            else if(story != null && story.getFight() != null){
                story.getFight().drawHpBarPlayer(g, offset);
                story.getFight().drawHpBarEnemy(g, offset);
            }
        }
        else if(state == GameState.TYPING){
            name.writeName(g);
        } else if(state == GameState.OPTIONS){
                optionsMenu.printOptions(g);
        }
        else if(state == GameState.STORY_FIGHT){
            fight = story.getFight();
            story.getFight().drawHpBarPlayer(g, offset);
            story.getFight().drawHpBarEnemy(g, offset);
        }
        else if(state == GameState.DIFFICULTY || state == GameState.STORY_DIFFICULTY){
            int i = actualMenu.getSel();
            String s[] = new String[10];
            int t = 0;
            if(state == GameState.STORY_DIFFICULTY){
                i = story.getActualMenu().getSel();
            }
            // Indicadores de dificultad
            switch (i){
                case 0:
                    s[0] = "Low attack freq.";
                    s[1] = "Evaluates";
                    s[2] = "   Time remaining";
                    t = 3;
                    break;
                case 1:
                    s[0] = "Medium attack freq.";
                    s[1] = "Evaluates";
                    s[2] = "   Time remaining";
                    s[3] = "   Player life remaining";
                    t = 4;
                    break;
                case 2:
                    s[0] = "High attack freq.";
                    s[1] = "Evaluates";
                    s[2] = "   Time remaining";
                    s[3] = "   Player life remaining";
                    s[4] = "   IA life remaining";
                    s[5] = "   Round";
                    s[6] = "   Round victory ratio";
                    t = 7;
                    break;
                case 3:
                    s[0] = "Very High attack freq.";
                    s[1] = "Evaluates";
                    s[2] = "   Time remaining";
                    s[3] = "   Player life remaining";
                    s[4] = "   IA life remaining";
                    s[5] = "   Round";
                    s[6] = "   Round victory ratio";
                    s[7] = "   Player tendencies";
                    s[8] = "   during the own game";
                    t = 9;
                    break;
            }
            g.setFont(font);
            g.setColor(Color.YELLOW);
            int y = 135;
            for(int j = 0; j < t; ++j){
                g.drawString(s[j], 425,y + 28*j);
            }
        }
    }

    /**
     * Gets fight.
     *
     * @return the fight
     */
// Getters y setters
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
     * Gets principal.
     *
     * @return the principal
     */
    public menu getPrincipal() {
        return principal;
    }

    /**
     * Sets principal.
     *
     * @param principal the principal
     */
    public void setPrincipal(menu principal) {
        this.principal = principal;
    }

    /**
     * Gets actual menu.
     *
     * @return the actual menu
     */
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
     * Gets escape menu.
     *
     * @return the escape menu
     */
    public menu getEscapeMenu() {
        return escapeMenu;
    }

    /**
     * Sets escape menu.
     *
     * @param escapeMenu the escape menu
     */
    public void setEscapeMenu(menu escapeMenu) {
        this.escapeMenu = escapeMenu;
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
     * Gets ran.
     *
     * @return the ran
     */
    public Random getRan() {
        return ran;
    }

    /**
     * Sets ran.
     *
     * @param ran the ran
     */
    public void setRan(Random ran) {
        this.ran = ran;
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
     * Is stop music boolean.
     *
     * @return the boolean
     */
    public boolean isStopMusic() {
        return stopMusic;
    }

    /**
     * Sets stop music.
     *
     * @param stopMusic the stop music
     */
    public void setStopMusic(boolean stopMusic) {
        this.stopMusic = stopMusic;
    }

    /**
     * Gets basic menu.
     *
     * @return the basic menu
     */
    public menu getBasicMenu() {
        return basicMenu;
    }

    /**
     * Sets basic menu.
     *
     * @param basicMenu the basic menu
     */
    public void setBasicMenu(menu basicMenu) {
        this.basicMenu = basicMenu;
    }

    /**
     * Gets game menu.
     *
     * @return the game menu
     */
    public menu getGameMenu() {
        return gameMenu;
    }

    /**
     * Sets game menu.
     *
     * @param gameMenu the game menu
     */
    public void setGameMenu(menu gameMenu) {
        this.gameMenu = gameMenu;
    }

    /**
     * Gets sure.
     *
     * @return the sure
     */
    public menu getSure() {
        return sure;
    }

    /**
     * Sets sure.
     *
     * @param sure the sure
     */
    public void setSure(menu sure) {
        this.sure = sure;
    }

    /**
     * Gets difficulty.
     *
     * @return the difficulty
     */
    public menu getDifficulty() {
        return difficulty;
    }

    /**
     * Sets difficulty.
     *
     * @param difficulty the difficulty
     */
    public void setDifficulty(menu difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Gets map selection.
     *
     * @return the map selection
     */
    public menu getMapSelection() {
        return mapSelection;
    }

    /**
     * Sets map selection.
     *
     * @param mapSelection the map selection
     */
    public void setMapSelection(menu mapSelection) {
        this.mapSelection = mapSelection;
    }

    /**
     * Gets char menu.
     *
     * @return the char menu
     */
    public character_menu getCharMenu() {
        return charMenu;
    }

    /**
     * Sets char menu.
     *
     * @param charMenu the char menu
     */
    public void setCharMenu(character_menu charMenu) {
        this.charMenu = charMenu;
    }

    /**
     * Gets options menu.
     *
     * @return the options menu
     */
    public options getOptionsMenu() {
        return optionsMenu;
    }

    /**
     * Sets options menu.
     *
     * @param optionsMenu the options menu
     */
    public void setOptionsMenu(options optionsMenu) {
        this.optionsMenu = optionsMenu;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public character_controller getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(character_controller user) {
        this.user = user;
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
     * Gets ranking.
     *
     * @return the ranking
     */
    public score getRanking() {
        return ranking;
    }

    /**
     * Sets ranking.
     *
     * @param ranking the ranking
     */
    public void setRanking(score ranking) {
        this.ranking = ranking;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public ask_for_name getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(ask_for_name name) {
        this.name = name;
    }

    /**
     * Is pvp boolean.
     *
     * @return the boolean
     */
    public boolean isPvp() {
        return pvp;
    }

    /**
     * Sets pvp.
     *
     * @param pvp the pvp
     */
    public void setPvp(boolean pvp) {
        this.pvp = pvp;
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
     * Gets story.
     *
     * @return the story
     */
    public story_mode getStory() {
        return story;
    }

    /**
     * Sets story.
     *
     * @param story the story
     */
    public void setStory(story_mode story) {
        this.story = story;
    }

    /**
     * Is story on boolean.
     *
     * @return the boolean
     */
    public boolean isStoryOn() {
        return storyOn;
    }

    /**
     * Sets story on.
     *
     * @param storyOn the story on
     */
    public void setStoryOn(boolean storyOn) {
        this.storyOn = storyOn;
    }

    /**
     * Gets openings.
     *
     * @return the openings
     */
    public String getOpenings() {
        return openings;
    }

    /**
     * Sets openings.
     *
     * @param openings the openings
     */
    public void setOpenings(String openings) {
        this.openings = openings;
    }

    /**
     * Gets tiempo.
     *
     * @return the tiempo
     */
    public long getTiempo() {
        return tiempo;
    }

    /**
     * Sets tiempo.
     *
     * @param tiempo the tiempo
     */
    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
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
     * Is on demo boolean.
     *
     * @return the boolean
     */
    public boolean isOnDemo() {
        return onDemo;
    }

    /**
     * Sets on demo.
     *
     * @param onDemo the on demo
     */
    public void setOnDemo(boolean onDemo) {
        this.onDemo = onDemo;
    }

    /**
     * Is from escape boolean.
     *
     * @return the boolean
     */
    public boolean isFromEscape() {
        return fromEscape;
    }

    /**
     * Sets from escape.
     *
     * @param fromEscape the from escape
     */
    public void setFromEscape(boolean fromEscape) {
        this.fromEscape = fromEscape;
    }

    /**
     * Gets start.
     *
     * @return the start
     */
    public screenObject getStart() {
        return start;
    }

    /**
     * Sets start.
     *
     * @param start the start
     */
    public void setStart(screenObject start) {
        this.start = start;
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
     * Gets how.
     *
     * @return the how
     */
    public screenObject getHow() {
        return how;
    }

    /**
     * Sets how.
     *
     * @param how the how
     */
    public void setHow(screenObject how) {
        this.how = how;
    }

    /**
     * Gets font stream.
     *
     * @return the font stream
     */
    public InputStream getFontStream() {
        return fontStream;
    }

    /**
     * Sets font stream.
     *
     * @param fontStream the font stream
     */
    public void setFontStream(InputStream fontStream) {
        this.fontStream = fontStream;
    }

    /**
     * Gets font.
     *
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets font.
     *
     * @param font the font
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Gets online.
     *
     * @return the online
     */
    public online_mode getOnline() {
        return online;
    }

    /**
     * Sets online.
     *
     * @param online the online
     */
    public void setOnline(online_mode online) {
        this.online = online;
    }
}
