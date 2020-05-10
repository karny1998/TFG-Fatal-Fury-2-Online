package lib.objects;

import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.menus.character_menu;
import lib.menus.menu;
import lib.menus.menu_generator;
import lib.menus.options;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;
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


// Clase que representa un controlador encargado de gestionar todo el juego
public class game_controller {

    Random ran = new Random();
    boolean debug = false;
    boolean stopMusic = false;
    // Controlador de una pelea
    private fight_controller fight;
    // Escenario (habría que meterlo en fight_controller)
    private scenary scene;
    // Menus
    private menu principal,basicMenu, gameMenu;
    private menu sure;
    private menu difficulty;
    // Menu actual
    private menu actualMenu;
    // Menu al presionar escape en una pelea
    private menu escapeMenu;
    // Menu de selección de mapa
    private menu mapSelection;

    private character_menu charMenu;

    private options optionsMenu;

    private character_controller user;
    private character_controller enemy;
    // Estado del juego
    private GameState state = GameState.OPENING_1;
    // Ranking
    private score ranking = new score(ia_loader.dif.EASY);
    // introducción de nombre
    private ask_for_name name = new ask_for_name();
    // Si es JvJ
    private boolean pvp = false;
    // Limnites del mapa
    hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);
    // Modo historia
    private story_mode story;
    private boolean storyOn;
    private String openings = "/assets/sprites/menu/opening/opening_";
    private long tiempo = System.currentTimeMillis();
    private long timeReference = System.currentTimeMillis();
    private boolean onDemo = false;
    private boolean fromEscape = false;
    private screenObject start = new screenObject(357, 482,  549, 35, new ImageIcon(menu_generator.class.getResource("/assets/sprites/menu/press_start.png")).getImage(), Item_Type.MENU);;
    private ia_loader.dif lvlIa;
    private screenObject how = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource("/assets/sprites/menu/how_to_play.png")).getImage(), Item_Type.MENU);;
    private InputStream fontStream = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");
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

    public game_controller() {
        this.sure = menu_generator.generate_sure();
        this.principal = menu_generator.generate();
        this.basicMenu = principal.getSelectionables().get(Selectionable.START).getMen();
        this.gameMenu = basicMenu.getSelectionables().get(Selectionable.PRINCIPAL_GAME).getMen();
        this.actualMenu = principal;
        this.difficulty = menu_generator.generate_story_difficulty();
        this.escapeMenu = menu_generator.generate_scape();
        this.mapSelection = menu_generator.generate_map_selection();
    }

    public game_controller(menu principal) {
        this.sure = menu_generator.generate_sure();
        this.principal = principal;
        this.actualMenu = principal;
        this.escapeMenu = menu_generator.generate_scape();
        this.difficulty = menu_generator.generate_story_difficulty();
    }

    public game_controller(fight_controller fight, menu principal) {
        this.fight = fight;
        this.principal = principal;
        this.actualMenu = principal;
        this.escapeMenu = menu_generator.generate_scape();
    }

    // Asigna a screenObjects las cosas a mostrar por pantalla
    public void getFrame(Map<Item_Type, screenObject> screenObjects){

        if(debug && state != GameState.FIGHT){
            user = new user_controller(Playable_Character.MAI, 1);
            enemy = new user_controller(Playable_Character.TERRY, 2);
            enemy.setPlayerNum(2);
            enemy.setRival(user.getPlayer());
            enemy.getPlayer().setMapLimit(mapLimit);
            user.setRival(enemy.getPlayer());
            user.getPlayer().setMapLimit(mapLimit);
            state = GameState.FIGHT;
            scene = new scenary(Scenario_type.USA);
            audio_manager.startFight(user.getPlayer().getCharac(), enemy.getPlayer().getCharac(), Scenario_type.USA);
            fight = new fight_controller(user,enemy,scene);
            fight.setMapLimit(mapLimit);
            pvp = true;

            fight.setVsIa(false);
        }

        if(state == GameState.OPENING_1){
            screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(openings + "1.png")).getImage(), Item_Type.MENU);
            screenObjects.put(Item_Type.MENU, s);
            long actual = System.currentTimeMillis();
            if( actual - tiempo > 5000.0){
                state = GameState.OPENING_2;
                tiempo = System.currentTimeMillis();
            }
        }
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
                        case PRINCIPAL_OPTIONS:
                            optionsMenu = new options();
                            actualMenu.updateTime();
                            optionsMenu.updateTime();
                            state = GameState.OPTIONS;
                            break;
                        case PRINCIPAL_RANK:
                            ranking.reloadRanking();
                            state = GameState.RANKING;
                            break;
                        case GAME_MULTIPLAYER:
                            actualMenu.updateTime();
                            charMenu = new character_menu(0);
                            state = GameState.PLAYERS;
                            pvp = true;
                            break;
                        case GAME_IA:
                            actualMenu = difficulty;
                            actualMenu.updateTime();
                            state = GameState.DIFFICULTY;
                            pvp = false;
                            break;
                        case GAME_HISTORY:
                            story = new story_mode();
                            state = GameState.STORY;
                            break;
                        case GAME_HOW:
                            state = GameState.HOW_TO_PLAY;
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
                            System.exit(0);
                            break;
                        case NO:
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
        else if(state == GameState.HOW_TO_PLAY){
            screenObjects.put(Item_Type.MENU, how);
            if(controlListener.getStatus(1, controlListener.ESC_INDEX) && System.currentTimeMillis() - timeReference > 300.0){
                timeReference = System.currentTimeMillis();
                actualMenu = gameMenu;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
            }
        }
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
                        state = GameState.PLAYERS;
                    }
                }
            }
        }
        else if (state == GameState.PLAYERS){

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
        } else if (state == GameState.OPTIONS){
            if (optionsMenu.gestionMenu(screenObjects)){
                state = GameState.NAVIGATION;
            }
        } else if (state == GameState.MAP){

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
        // Si se está peleando
        else if(state == GameState.FIGHT){
            // Si se ha presionado escape se cambia de estado
            if( controlListener.menuInput(1, controlListener.ESC_INDEX) && System.currentTimeMillis()-timeReference>300.0){
                timeReference = System.currentTimeMillis();
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Pause);
                fight.pauseFight();
                fight.getPlayer().getPlayer().stop();
                fight.getEnemy().getPlayer().stop();
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
                        state = GameState.NAVIGATION;
                        actualMenu = gameMenu;
                        principal.updateTime();
                    }
                    timeReference = System.currentTimeMillis();
                    clearInterface(screenObjects);
                }
            }
        }
        // Si se le ha dado a escape durante una pelea
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
        else if (state == GameState.TYPING){
            if (name.gestionMenu(screenObjects)){
                audio_manager.fight.stopMusic(fight_audio.music_indexes.win_theme);
                audio_manager.fight.stopMusic(fight_audio.music_indexes.lose_theme);
                audio_manager.endFight();
                audio_manager.menu.play(menu_audio.indexes.fight_selected);
                fight.getScorePlayer().writeRankScore(name.getName());
                timeReference = System.currentTimeMillis();
                actualMenu = gameMenu;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
                screenObjects.remove(Item_Type.P1_SELECT);
                screenObjects.remove(Item_Type.P2_SELECT);
            }


        }
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

    private void clearInterface(Map<Item_Type, screenObject> screenObjects) {
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

    public void writeDirecly(Graphics2D g){
        if(state == GameState.RANKING){
            ranking.printRanking(g);
        }
        else if(state == GameState.FIGHT || state == GameState.ESCAPE
                || state == GameState.DEMO || state == GameState.SURE && fromEscape) {
            if(debug){
                fight.player.player.getHitbox().drawHitBox(g);
                fight.player.player.getHurtbox().drawHitBox(g);
                fight.enemy.player.getHitbox().drawHitBox(g);
                fight.enemy.player.getHurtbox().drawHitBox(g);
                fight.player.player.getCoverbox().drawHitBox(g);
                fight.enemy.player.getCoverbox().drawHitBox(g);
            }
            if(!storyOn && fight != null) {
                fight.drawHpBarPlayer(g);
                fight.drawHpBarEnemy(g);
            }
            else if(story != null && story.getFight() != null){
                story.getFight().drawHpBarPlayer(g);
                story.getFight().drawHpBarEnemy(g);
            }
        }
        else if(state == GameState.TYPING){
            name.writeName(g);
        } else if(state == GameState.OPTIONS){
                optionsMenu.printOptions(g);
        }
        else if(state == GameState.STORY_FIGHT){
            story.getFight().drawHpBarPlayer(g);
            story.getFight().drawHpBarEnemy(g);
        }
        else if(state == GameState.DIFFICULTY || state == GameState.STORY_DIFFICULTY){
            int i = actualMenu.getSel();
            String s[] = new String[10];
            int t = 0;
            if(state == GameState.STORY_DIFFICULTY){
                i = story.getActualMenu().getSel();
            }
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

    // Getters y setters
    public fight_controller getFight() {
        return fight;
    }

    public void setFight(fight_controller fight) {
        this.fight = fight;
    }

    public menu getPrincipal() {
        return principal;
    }

    public void setPrincipal(menu principal) {
        this.principal = principal;
    }

    public menu getActualMenu() {
        return actualMenu;
    }

    public void setActualMenu(menu actualMenu) {
        this.actualMenu = actualMenu;
    }

    public scenary getScene() {
        return scene;
    }

    public void setScene(scenary scene) {
        this.scene = scene;
    }

    public menu getEscapeMenu() {
        return escapeMenu;
    }

    public void setEscapeMenu(menu escapeMenu) {
        this.escapeMenu = escapeMenu;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
