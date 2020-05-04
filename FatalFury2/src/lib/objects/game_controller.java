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

import java.awt.*;
import java.util.Map;

// Clase que representa un controlador encargado de gestionar todo el juego
public class game_controller {

    boolean debug = false;
    boolean stopMusic = false;
    // Controlador de una pelea
    private fight_controller fight;
    // Escenario (habría que meterlo en fight_controller)
    private scenary scene;
    // Menu principal
    private menu principal;
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
    private GameState state = GameState.NAVIGATION;
    // Ranking
    private score ranking = new score();
    // introducción de nombre
    private ask_for_name askName = new ask_for_name();
    // Si es JvJ
    private boolean pvp = false;
    // Limnites del mapa
    hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);
    // Modo historia
    private story_mode story;

    public game_controller() {
        this.principal = menu_generator.generate();
        this.actualMenu = principal;
        this.escapeMenu = menu_generator.generate_scape();
        this.mapSelection = menu_generator.generate_map_selection();

    }

    public game_controller(menu principal) {
        this.principal = principal;
        this.actualMenu = principal;
        this.escapeMenu = menu_generator.generate_scape();
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
            user = new user_controller(Playable_Character.TERRY, 1);
            enemy = new enemy_controller(Playable_Character.TERRY, 2);
            enemy.setRival(user.getPlayer());
            enemy.getPlayer().setMapLimit(mapLimit);
            user.setRival(enemy.getPlayer());
            user.getPlayer().setMapLimit(mapLimit);
            state = GameState.FIGHT;
            scene = new scenary(Scenario_type.USA);
            audio_manager.startFight(user.getPlayer().getCharac(), enemy.getPlayer().getCharac(), Scenario_type.USA);
            fight = new fight_controller(user,enemy,scene);
            fight.setMapLimit(mapLimit);
            fight.setVsIa(true);
            //state = GameState.OPTIONS;
        }

        // Teecla presionada por el usuario
        // Si se está navegando por los menús
        if(state == GameState.NAVIGATION){
            screenObject s = actualMenu.getFrame();
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = actualMenu.select();
            // Si el menú es el de start, y se presiona cualquier tecla (de las mapeadas)
            if(p.getValue() == Selectionable.START && controlListener.anyKeyPressed()){
                audio_manager.menu.loop(menu_audio.indexes.menu_theme);
                actualMenu = p.getKey();
                actualMenu.updateTime();
            }
            // Si se presiona escape (hay que hacer que vuelva al menu anterior)
            else if( controlListener.menuInput(1, controlListener.ESC_INDEX) ){
                audio_manager.menu.play(menu_audio.indexes.back);
                //actualMenu = actualMenu.getFather();
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
                            System.exit(0);
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
                            actualMenu.updateTime();
                            charMenu = new character_menu(1);
                            state = GameState.PLAYERS;
                            pvp = false;
                            break;
                        case GAME_HISTORY:
                            story = new story_mode();
                            state = GameState.STORY;
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
        } else if (state == GameState.PLAYERS){
            screenObject s = actualMenu.getFrame();
            screenObjects.put(Item_Type.MENU, s);
            Boolean res = charMenu.gestionMenu(screenObjects);
            if (res == true){
                user = new user_controller(charMenu.getP1_ch(),1);
                if(pvp) {
                    enemy = new user_controller(charMenu.getP2_ch(), 2);
                    enemy.setPlayerNum(2);
                }
                else {
                    enemy = new enemy_controller(charMenu.getP2_ch(), 2);
                }
                enemy.setRival(user.getPlayer());
                enemy.getPlayer().setMapLimit(mapLimit);
                user.setRival(enemy.getPlayer());
                user.getPlayer().setMapLimit(mapLimit);
                actualMenu = mapSelection;
                actualMenu.updateTime();
                state = GameState.MAP;
            }
        } else if (state == GameState.OPTIONS){
            screenObject s = actualMenu.getFrame();
            screenObjects.put(Item_Type.MENU, s);

            if (optionsMenu.gestionMenu(screenObjects)){
                state = GameState.NAVIGATION;
            }
        } else if (state == GameState.MAP){

            screenObject s = actualMenu.getFrame();
            screenObjects.remove(Item_Type.P1_SELECT);
            screenObjects.remove(Item_Type.P2_SELECT);
            screenObjects.remove(Item_Type.P1_MUG);
            screenObjects.remove(Item_Type.P2_MUG);
            screenObjects.remove(Item_Type.P1_NAME);
            screenObjects.remove(Item_Type.P2_NAME);
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = actualMenu.select();
            // Si se presiona escape (hay que hacer que vuelva al menu anterior)
            if( controlListener.menuInput(1, controlListener.ESC_INDEX) ){
                audio_manager.menu.play(menu_audio.indexes.back);
                //actualMenu = actualMenu.getFather();
                actualMenu.updateTime();
            }
            // Si se presiona enter y ha pasado el tiempo de margen entre menu y menu
            else if( controlListener.menuInput(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE){
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
                    //user_controller user = new user_controller(Playable_Character.TERRY);
                    //enemy_controller enemy = new enemy_controller(Playable_Character.TERRY);
                    //enemy.setRival(user.getPlayer());

                    audio_manager.startFight(user.getPlayer().getCharac(), enemy.getPlayer().getCharac(), map);
                    fight = new fight_controller(user,enemy,scene);
                    fight.setMapLimit(mapLimit);
                    fight.setVsIa(!pvp);
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
            if( controlListener.menuInput(1, controlListener.ESC_INDEX) ){
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Pause);
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
                        askName = new ask_for_name();
                        state = GameState.TYPING;
                    }
                    else{
                        state = GameState.NAVIGATION;
                        actualMenu = principal;
                        principal.updateTime();
                    }
                    clearInterface(screenObjects);
                }
            }
        }
        // Si se le ha dado a escape durante una pelea
        else if (state == GameState.ESCAPE){
            fight.pauseFight();
            screenObject s = escapeMenu.getFrame();
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = escapeMenu.select();
            // Si se ha presionado enter para seleccionar alguna opción
            if ( controlListener.menuInput(1, controlListener.ENT_INDEX) ){
                audio_manager.menu.play(menu_audio.indexes.option_selected);
                switch (p.getValue()){
                    // Retomar la partida
                    case ESCAPE_RESUME:
                        state = GameState.FIGHT;
                        screenObjects.remove(Item_Type.MENU);
                        escapeMenu.updateTime();
                        fight.resumeFight();
                        break;
                    // Volver al menú de juego
                    case ESCAPE_BACK:
                        actualMenu = p.getKey();
                        actualMenu.updateTime();
                        state = GameState.NAVIGATION;
                        clearInterface(screenObjects);
                        break;
                    // Salir del juego
                    case ESCAPE_EXIT:
                        System.exit(0);
                        break;
                }
            }
        }
        else if(state == GameState.RANKING && controlListener.menuInput(1, controlListener.ESC_INDEX) ){
            audio_manager.menu.play(menu_audio.indexes.back);
            state = GameState.NAVIGATION;
        }
        else if (state == GameState.TYPING){
            if( controlListener.menuInput(1, controlListener.ENT_INDEX) ){
                audio_manager.menu.play(menu_audio.indexes.fight_selected);
                fight.getScorePlayer().writeRankScore(askName.getName());
                actualMenu = principal;
                actualMenu.updateTime();
                audio_manager.endFight();
                state = GameState.NAVIGATION;
            }
            else{
                screenObjects.put(Item_Type.MENU, askName.getAnimation());
            }
        }
        else if (state == GameState.STORY || state == GameState.STORY_FIGHT
                || state == GameState.STORY_MENU || state == GameState.STORY_LOADING
                || state == GameState.STORY_END || state == GameState.STORY_DIFFICULTY){
            if(state == GameState.STORY_MENU || state == GameState.STORY_LOADING
                    || state == GameState.STORY_END || state == GameState.STORY_DIFFICULTY){
                clearInterface(screenObjects);
            }
            Pair<Boolean, GameState> aux = story.getAnimation(screenObjects);
            state = aux.getValue();
            if(aux.getKey()){
                clearInterface(screenObjects);
                screenObjects.remove(Item_Type.MENU);
                state = GameState.NAVIGATION;
                actualMenu = principal;
                actualMenu.updateTime();
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
    }

    public void writeDirecly(Graphics2D g){
        if(state == GameState.RANKING){
            ranking.printRanking(g);
        }
        else if(state == GameState.FIGHT || state == GameState.ESCAPE) {
            if(debug){
                fight.player.player.getHitbox().drawHitBox(g);
                fight.player.player.getHurtbox().drawHitBox(g);
                fight.enemy.player.getHitbox().drawHitBox(g);
                fight.enemy.player.getHurtbox().drawHitBox(g);
                fight.player.player.getCoverbox().drawHitBox(g);
                fight.enemy.player.getCoverbox().drawHitBox(g);
            }
            fight.drawHpBarPlayer(g);
            fight.drawHpBarEnemy(g);
        }
        else if(state == GameState.TYPING){
            askName.writeName(g);
        } else if(state == GameState.OPTIONS){
                optionsMenu.printOptions(g);
        }
        else if(state == GameState.STORY_FIGHT){
            story.getFight().drawHpBarPlayer(g);
            story.getFight().drawHpBarEnemy(g);
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
