package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Selectionable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class menu_generator {
    private static menu start(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.START});

        String path = "assets/sprites/menu/";
        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "start_1.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "start_2.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);
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
        String path = "assets/sprites/menu/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_game.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        selectionable sel = new selectionable(Selectionable.PRINCIPAL_GAME,a);
        sel.setMen(game());
        aux.put(Selectionable.PRINCIPAL_GAME,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_ranking.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        sel = new selectionable(Selectionable.PRINCIPAL_RANK,a);
        aux.put(Selectionable.PRINCIPAL_RANK,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_options.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        sel = new selectionable(Selectionable.PRINCIPAL_OPTIONS,a);
        aux.put(Selectionable.PRINCIPAL_OPTIONS,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_exit.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "principal_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        sel = new selectionable(Selectionable.PRINCIPAL_EXIT,a);
        aux.put(Selectionable.PRINCIPAL_EXIT,sel);

        men.setSelectionables(aux);

        return men;
    }

    private static menu game(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.GAME_HISTORY, Selectionable.GAME_MULTIPLAYER, Selectionable.GAME_IA});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "assets/sprites/menu/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "game_history.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "game_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        selectionable sel = new selectionable(Selectionable.GAME_HISTORY,a);
        aux.put(Selectionable.GAME_HISTORY,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "game_multiplayer.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "game_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        sel = new selectionable(Selectionable.GAME_MULTIPLAYER,a);
        aux.put(Selectionable.GAME_MULTIPLAYER,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "game_ia.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "game_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        sel = new selectionable(Selectionable.GAME_IA,a);
        aux.put(Selectionable.GAME_IA,sel);

        men.setSelectionables(aux);
        return men;
    }

    public static menu generate_scape(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.ESCAPE_RESUME, Selectionable.ESCAPE_BACK, Selectionable.ESCAPE_EXIT});
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        String path = "assets/sprites/menu/";

        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(317, 217,  646, 287, new ImageIcon(path  + "escape_resume.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(path  + "escape_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        selectionable sel = new selectionable(Selectionable.ESCAPE_RESUME,a);
        aux.put(Selectionable.ESCAPE_RESUME,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(path  + "escape_back.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(path  + "escape_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        sel = new selectionable(Selectionable.ESCAPE_BACK,a);
        sel.setMen(game());
        aux.put(Selectionable.ESCAPE_BACK,sel);

        a = new animation();
        a.setHasEnd(false);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(path  + "escape_exit.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(317, 217,  646, 287, new ImageIcon(path  + "escape_basic.png").getImage(), Item_Type.MENU);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);

        sel = new selectionable(Selectionable.ESCAPE_EXIT,a);
        aux.put(Selectionable.ESCAPE_EXIT,sel);

        men.setSelectionables(aux);
        men.setX(317);
        men.setY(217);

        return men;
    }

    public static menu generate(){
        return start();
    }
}
