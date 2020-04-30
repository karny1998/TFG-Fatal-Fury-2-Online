package lib.sound;

import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;

public class audio_manager {

    public static menu_audio menu;
    public static fight_audio fight;

    private double music_audio;
    private double voice_audio;
    private double sfx_audio;

    enum estado {
        MENUS,
        PELEA
    }

    private static estado actual;

    public audio_manager(){
        menu = new menu_audio();
        menu.update();
        actual = estado.MENUS;
    }

    public static void startFight(Playable_Character p1, Playable_Character p2, Scenario_type map){
        if (actual == estado.MENUS){
            menu.close();
            fight = new fight_audio(p1, p2, map);
            fight.update()
            actual = estado.PELEA;
        }
    }

    public static void endFight(){
        if (actual == estado.PELEA){
            menu = new menu_audio();
            menu.update();
            fight.close();
            actual = estado.MENUS;
        }
    }

    public static void update(){

    }


}
