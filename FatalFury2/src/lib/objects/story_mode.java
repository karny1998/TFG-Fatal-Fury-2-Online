package lib.objects;

import javafx.util.Pair;
import lib.Enums.GameState;
import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;
import lib.maps.scenary;
import lib.menus.menu;
import lib.menus.menu_generator;

import java.util.Map;

public class story_mode {
    private menu winMenu = menu_generator.generate_story_win();
    private menu loseMenu = menu_generator.generate_story_lose();
    private screenObject loads[];
    private int lvlIa = 1;
    private int score = 0;
    private int stage = 0;
    private Playable_Character charac = Playable_Character.TERRY;
    private fight_controller fight;
    private character_controller player, enemy;
    private GameState state = GameState.NAVIGATION;
    private scenary scene = new scenary(Scenario_type.USA);;
    private hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);;
    private long timeReference = System.currentTimeMillis();
    private Scenario_type scenarys[] = {Scenario_type.CHINA, Scenario_type.CHINA, Scenario_type.CHINA
                                        , Scenario_type.AUSTRALIA, Scenario_type.AUSTRALIA,Scenario_type.AUSTRALIA,
                                        Scenario_type.USA, Scenario_type.USA,Scenario_type.USA};
    private Playable_Character enemies[] = {Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI};

    public story_mode(int lvl){
        this.lvlIa = lvl;
    }

    void loadGame(){}

    void saveGame(){}

    void generateFight(){
        player = new user_controller(charac, 1);
        enemy = new enemy_controller(enemies[stage], 2);
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        state = GameState.FIGHT;
        scene = new scenary(scenarys[stage]);
        fight = new fight_controller(player,enemy,scene);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(true);
    }

    public Pair<Boolean, GameState> getAnimation(Map<Item_Type, screenObject> screenObjects){
        Boolean exit = false;
        //while(ti)

        return new Pair<>(exit, state);
    }
}
