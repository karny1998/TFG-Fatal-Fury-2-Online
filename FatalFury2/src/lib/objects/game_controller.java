package lib.objects;

import javafx.util.Pair;
import lib.Enums.GameState;
import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;
import lib.Enums.Selectionable;
import lib.input.controlListener;
import lib.input.keyBinding;

import java.awt.*;
import java.util.Map;

// Clase que representa un controlador encargado de gestionar todo el juego
public class game_controller {

    boolean debug = false;

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
    // Estado del juego
    private GameState state = GameState.NAVIGATION;
    // Ranking
    private score ranking = new score();
    // introducción de nombre
    private ask_for_name askName = new ask_for_name();

    public game_controller() {
        new IsKeyPressed();
        this.principal = menu_generator.generate();
        this.actualMenu = principal;
        this.escapeMenu = menu_generator.generate_scape();
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
        // Teecla presionada por el usuario
        // Si se está navegando por los menús
        if(state == GameState.NAVIGATION){
            screenObject s = actualMenu.getFrame();
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = actualMenu.select();
            // Si el menú es el de start, y se presiona cualquier tecla (de las mapeadas)
            if(p.getValue() == Selectionable.START && controlListener.anyKeyPressed()){
                actualMenu = p.getKey();
                actualMenu.updateTime();
            }
            // Si se presiona escape (hay que hacer que vuelva al menu anterior)
            else if( controlListener.isPressed(keyBinding.getEscape())){
                //actualMenu = actualMenu.getFather();
                actualMenu.updateTime();
            }
            // Si se presiona enter y ha pasado el tiempo de margen entre menu y menu
            else if(controlListener.isPressed(keyBinding.getEnter()) && p.getValue() != Selectionable.NONE){
                // La selección no lleva a ningún menú nuevo (p.e. cuando se da a jugar)
                if(p.getKey() == null) {
                    switch (p.getValue()) {
                        // Sale del juego
                        case PRINCIPAL_EXIT:
                            System.exit(0);
                            break;
                        case PRINCIPAL_RANK:
                            ranking.reloadRanking();
                            state = GameState.RANKING;
                            break;
                        // Inica una partida
                        case GAME_IA:
                            user_controller user = new user_controller(Playable_Character.TERRY);
                            enemy_controller enemy = new enemy_controller(Playable_Character.TERRY);
                            enemy.setRival(user.getPlayer());
                            fight = new fight_controller(user, enemy);
                            fight.setVsIa(true);

                            scene = new scenary();
                            scene.setAnim1(usa.generateAnimation1());
                            scene.setAnim2(usa.generateAnimation2());

                            screenObjects.remove(Item_Type.MENU);
                            state = GameState.FIGHT;
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
        // Si se está peleando
        else if(state == GameState.FIGHT){
            // Si se ha presionado escape se cambia de estado
            if(controlListener.isPressed(keyBinding.getEscape())){
                state = GameState.ESCAPE;
            }
            // Si se está mostrando la pelea
            else {
                screenObjects.remove(Item_Type.MENU);
                fight.getAnimation(screenObjects);
                if (fight.getEnd()) {
                    if(fight.isVsIa()){
                        askName = new ask_for_name();
                        state = GameState.TYPING;
                    }
                    clearInterface(screenObjects);
                }
                else {
                    screenObject ply = scene.getFrame1();
                    screenObjects.put(Item_Type.SCENARY_1, ply);
                    ply = scene.getFrame2();
                    screenObjects.put(Item_Type.SCENARY_2, ply);
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
            if (controlListener.isPressed(keyBinding.getEnter())){
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
        else if(state == GameState.RANKING && controlListener.isPressed(keyBinding.getEscape()) ){
            state = GameState.NAVIGATION;
        }
        else if (state == GameState.TYPING){
            if(controlListener.isPressed(keyBinding.getEnter())){
                fight.getScorePlayer().writeRankScore(askName.getName());
                actualMenu = principal;
                actualMenu.updateTime();
                state = GameState.NAVIGATION;
            }
            else{
                screenObjects.put(Item_Type.MENU, askName.getAnimation());
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
        else if(state == GameState.FIGHT) {
            if(debug){
                fight.player.player.getHitbox().drawHitBox(g);
                fight.player.player.getHurtbox().drawHitBox(g);
                fight.enemy.player.getHitbox().drawHitBox(g);
                fight.enemy.player.getHurtbox().drawHitBox(g);
            }
            fight.drawHpBarPlayer(g);
            fight.drawHpBarEnemy(g);
        }
        else if(state == GameState.TYPING){
            askName.writeName(g);
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
