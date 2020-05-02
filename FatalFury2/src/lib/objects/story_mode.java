package lib.objects;

import lib.Enums.GameState;
import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;
import lib.maps.scenary;
import lib.menus.menu;
import lib.menus.menu_generator;
import lib.utils.Pair;

import javax.swing.*;
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
    private GameState state = GameState.STORY_LOADING;
    private scenary scene = new scenary(Scenario_type.USA);;
    private hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);;
    private long timeReference = System.currentTimeMillis();
    private Scenario_type scenarys[] = {Scenario_type.CHINA, Scenario_type.CHINA, Scenario_type.CHINA
                                        , Scenario_type.AUSTRALIA, Scenario_type.AUSTRALIA,Scenario_type.AUSTRALIA,
                                        Scenario_type.USA, Scenario_type.USA,Scenario_type.USA};
    private Playable_Character enemies[] = {Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI};

    public story_mode(){
        loadGame();
        loadLoadScreens();
    }

    public story_mode(int lvl){
        this.lvlIa = lvl;
        loadLoadScreens();
    }

    void loadGame(){}

    void saveGame(){}

    void loadLoadScreens(){
        String path =  "assets/sprites/menu/story/story_";
        for(int i = 1; i < 10; ++i){
            loads[i-1] = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + i + ".png").getImage(), Item_Type.MENU);
        }
    }

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
        if(state == GameState.STORY_LOADING){
            long current = System.currentTimeMillis();
            if(current - timeReference < 2000.0){
                screenObjects.put(Item_Type.MENU, loads[stage]);
            }
            else{
                state = GameState.STORY_FIGHT;
                generateFight();
                screenObjects.remove(Item_Type.MENU);
                fight.getAnimation(screenObjects);
            }
        }
        /*else if(state == GameState.STORY_FIGHT){
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
                state = GameState.STORY_MENU;
            }
        }*/

        return new Pair<>(exit, state);
    }
}
