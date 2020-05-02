package lib.menus;

import lib.Enums.Item_Type;
import lib.Enums.Selectionable;
import lib.objects.animation;
import lib.objects.screenObject;
import lib.objects.selectionable;
import lib.sound.fight_audio;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class menu_generator {
    private static menu start(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.START});

        String path = "/assets/sprites/menu/";
        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon( menu_generator.class.getResource(path  + "start_1.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon( menu_generator.class.getResource(path  + "start_2.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);
        selectionable sel = new selectionable(Selectionable.START,a);

        sel.setMen(principal());

        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        aux.put(Selectionable.START,sel);
        men.setSelectionables(aux);
        return men;
    }

    private static menu principal(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.PRINCIPAL_GAME, Selectionable.PRINCIPAL_RANK, Selectionable.PRINCIPAL_OPTIONS, Selectionable.PRINCIPAL_EXIT});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "/assets/sprites/menu/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_game.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        selectionable sel = new selectionable(Selectionable.PRINCIPAL_GAME,a);
        sel.setMen(game());
        aux.put(Selectionable.PRINCIPAL_GAME,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_ranking.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.PRINCIPAL_RANK,a);
        aux.put(Selectionable.PRINCIPAL_RANK,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_options.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.PRINCIPAL_OPTIONS,a);
        aux.put(Selectionable.PRINCIPAL_OPTIONS,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_exit.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "principal_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.PRINCIPAL_EXIT,a);
        aux.put(Selectionable.PRINCIPAL_EXIT,sel);

        men.setSelectionables(aux);

        return men;
    }

    private static menu game(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.GAME_HISTORY, Selectionable.GAME_MULTIPLAYER, Selectionable.GAME_IA});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "/assets/sprites/menu/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "game_history.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "game_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        selectionable sel = new selectionable(Selectionable.GAME_HISTORY,a);
        aux.put(Selectionable.GAME_HISTORY,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "game_multiplayer.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "game_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.GAME_MULTIPLAYER,a);
        aux.put(Selectionable.GAME_MULTIPLAYER,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "game_ia.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "game_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.GAME_IA,a);
        aux.put(Selectionable.GAME_IA,sel);

        men.setSelectionables(aux);
        return men;
    }

    public static menu generate_scape(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.ESCAPE_RESUME, Selectionable.ESCAPE_BACK, Selectionable.ESCAPE_EXIT});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "/assets/sprites/menu/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "escape_resume.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "escape_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        selectionable sel = new selectionable(Selectionable.ESCAPE_RESUME,a);
        aux.put(Selectionable.ESCAPE_RESUME,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "escape_back.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "escape_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.ESCAPE_BACK,a);
        sel.setMen(game());
        aux.put(Selectionable.ESCAPE_BACK,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "escape_exit.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "escape_basic.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.ESCAPE_EXIT,a);
        aux.put(Selectionable.ESCAPE_EXIT,sel);

        men.setSelectionables(aux);
        men.setX(317);
        men.setY(217);

        return men;
    }


    public static menu generate_story_win(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.WIN_SAVE, Selectionable.WIN_CONTINUE});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "/assets/sprites/menu/story/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "win_menu_save.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "win_menu.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        selectionable sel = new selectionable(Selectionable.WIN_SAVE,a);
        aux.put(Selectionable.WIN_SAVE,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "win_menu_continue.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "win_menu.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.WIN_CONTINUE,a);
        sel.setMen(game());
        aux.put(Selectionable.WIN_CONTINUE,sel);

        men.setSelectionables(aux);

        return men;
    }

    public static menu generate_story_lose(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.LOSE_RETRY, Selectionable.LOSE_EXIT});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "/assets/sprites/menu/story/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "lose_menu_retry.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "lose_menu.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        selectionable sel = new selectionable(Selectionable.LOSE_RETRY,a);
        aux.put(Selectionable.LOSE_RETRY,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "lose_menu_exit.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(menu_generator.class.getResource(path  + "lose_menu.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.LOSE_EXIT,a);
        sel.setMen(game());
        aux.put(Selectionable.LOSE_EXIT,sel);

        men.setSelectionables(aux);

        return men;
    }

    public static menu generate_map_selection(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.MAP_USA, Selectionable.MAP_AUS, Selectionable.MAP_CHI});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "/assets/sprites/menu/map/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "map_usa_ON.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "map_usa_OFF.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        selectionable sel = new selectionable(Selectionable.MAP_USA,a);
        aux.put(Selectionable.MAP_USA,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "map_aus_ON.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "map_aus_OFF.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.MAP_AUS,a);
        aux.put(Selectionable.MAP_AUS,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "map_chi_ON.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource(path  + "map_chi_OFF.png")).getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setHasSound(false);
        a.setSoundType(fight_audio.voice_indexes.Win);

        sel = new selectionable(Selectionable.MAP_CHI,a);
        aux.put(Selectionable.MAP_CHI,sel);

        men.setSelectionables(aux);
        return men;
    }

    public static menu generate(){
        return start();
    }
}