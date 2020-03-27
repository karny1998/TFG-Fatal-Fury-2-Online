package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Selectionable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class menu_generator {
    private static menu principal(){
        menu men = new menu();
        men.setOrden(new Selectionable[]{Selectionable.START});

        String path = "assets/sprites/menu/";
        animation a = new animation();
        a.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "start_1.png").getImage(), Item_Type.SCENARY_1);
        a.addFrame(s,500.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "start_2.png").getImage(), Item_Type.SCENARY_1);
        a.addFrame(s,500.00,0,0);
        a.setSound(null);
        a.setSoundType(Character_Voices.Win);
        selectionable sel = new selectionable(Selectionable.START,a);
        Map<Selectionable, selectionable> aux =  new HashMap<Selectionable, selectionable>();
        aux.put(Selectionable.START,sel);
        men.setSelectionables(aux);
        return men;
    }

    public static menu generate(){
        return principal();
    }
}
