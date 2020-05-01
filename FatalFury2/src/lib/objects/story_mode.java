package lib.objects;

import lib.Enums.Playable_Character;
import lib.menus.menu;
import lib.menus.menu_generator;

public class story_mode {
    private menu winMenu = menu_generator.generate_story_win();
    private menu loseMenu = menu_generator.generate_story_lose();
    private screenObject loads[];
    private int lvlIa = 1;
    private int score = 0;
    private int stage = 0;
    private Playable_Character charac = Playable_Character.TERRY;

}
