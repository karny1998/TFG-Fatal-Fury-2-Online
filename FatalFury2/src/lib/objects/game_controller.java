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
    private GameState state = GameState.NAVIGATION;

    public game_controller() {
        new IsKeyPressed();
        principal = menu_generator.generate();
        actualMenu = principal;
    }

    public game_controller(menu principal) {
        this.principal = principal;
        this.actualMenu = principal;
    }

    public game_controller(fight_controller fight, menu principal) {
        this.fight = fight;
        this.principal = principal;
        this.actualMenu = principal;
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
        if(state == GameState.NAVIGATION){

            controlKey cK = IsKeyPressed.keyPressed();
            screenObject s = actualMenu.getFrame(cK);
            screenObjects.put(Item_Type.MENU, s);
            Pair<menu, Selectionable> p = actualMenu.select();

            if(p.getValue() == Selectionable.START && cK != controlKey.NONE){
                user_controller user = new user_controller(Playable_Character.TERRY);
                enemy_controller enemy = new enemy_controller(Playable_Character.TERRY);
                fight = new fight_controller(user, enemy);

                scene = new scenary();
                scene.setAnim1(usa.generateAnimation1());
                scene.setAnim2(usa.generateAnimation2());

                state = GameState.FIGHT;

                screenObjects.remove(Item_Type.MENU);
            }
            else if(cK == controlKey.ENTER){
                if(p.getKey() == null) {
                    switch (p.getValue()) {
                        case GAME_IA:
                            user_controller user = new user_controller(Playable_Character.TERRY);
                            enemy_controller enemy = new enemy_controller(Playable_Character.TERRY);
                            fight = new fight_controller(user, enemy);

                            scene = new scenary();
                            scene.setAnim1(usa.generateAnimation1());
                            scene.setAnim2(usa.generateAnimation2());

                            screenObjects.remove(Item_Type.MENU);
                            state = GameState.FIGHT;
                    }
                }
                else{
                    actualMenu = p.getKey();
                }
            }
        }
        else if(state == GameState.FIGHT){
            fight.getAnimation(screenObjects);
            screenObject ply = scene.getFrame1();
            screenObjects.put(Item_Type.SCENARY_1, ply);
            ply = scene.getFrame2();
            screenObjects.put(Item_Type.SCENARY_2, ply);
        }
    }
}
