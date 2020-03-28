package lib.objects;

import javafx.util.Pair;
import lib.Enums.GameState;
import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;
import lib.Enums.Selectionable;

import java.util.Map;

public class game_controller {
    private fight_controller fight;
    private scenary scene;
    private menu principal;
    private menu actualMenu;
    private menu escapeMenu;
    private GameState state = GameState.NAVIGATION;

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

    public void getFrame(Map<Item_Type, screenObject> screenObjects){
        controlKey cK = IsKeyPressed.keyPressed();

        if(state == GameState.NAVIGATION){

            screenObject s = actualMenu.getFrame(cK);
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = actualMenu.select();

            if(p.getValue() == Selectionable.START && cK != controlKey.NONE){
                actualMenu = p.getKey();
                actualMenu.updateTime();
            }
            else if(cK == controlKey.ESCAPE){
                actualMenu = actualMenu.getFather();
                actualMenu.updateTime();
            }
            else if(cK == controlKey.ENTER && p.getValue() != Selectionable.NONE){
                if(p.getKey() == null) {
                    switch (p.getValue()) {
                        case PRINCIPAL_EXIT:
                            System.exit(0);
                            break;
                        case GAME_IA:
                            user_controller user = new user_controller(Playable_Character.TERRY);
                            enemy_controller enemy = new enemy_controller(Playable_Character.TERRY);
                            fight = new fight_controller(user, enemy);

                            scene = new scenary();
                            scene.setAnim1(usa.generateAnimation1());
                            scene.setAnim2(usa.generateAnimation2());

                            screenObjects.remove(Item_Type.MENU);
                            state = GameState.FIGHT;
                            break;
                    }
                }
                else{
                    actualMenu = p.getKey();
                    actualMenu.updateTime();
                }
            }
        }
        else if(state == GameState.FIGHT){
            if(cK == controlKey.ESCAPE){
                state = GameState.ESCAPE;
            }
            else {
                screenObjects.remove(Item_Type.MENU);

                fight.getAnimation(screenObjects);
                screenObject ply = scene.getFrame1();
                screenObjects.put(Item_Type.SCENARY_1, ply);
                ply = scene.getFrame2();
                screenObjects.put(Item_Type.SCENARY_2, ply);
            }
        }
        else if (state == GameState.ESCAPE){
            screenObject s = escapeMenu.getFrame(cK);
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = escapeMenu.select();
            if (cK == controlKey.ENTER){
                switch (p.getValue()){
                    case ESCAPE_RESUME:
                        state = GameState.FIGHT;
                        screenObjects.remove(Item_Type.MENU);
                        escapeMenu.updateTime();
                        break;
                    case ESCAPE_BACK:
                        actualMenu = p.getKey();
                        actualMenu.updateTime();
                        state = GameState.NAVIGATION;
                        break;
                    case ESCAPE_EXIT:
                        System.exit(0);
                        break;
                }
            }
        }
    }
}
