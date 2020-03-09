package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;

import javax.swing.*;

public class usa {
    public static animation generateAnimation1(){
        String path = "assets/sprites/scenarios/usa/";
        animation a1 = new animation();
        a1.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1571, 720, new ImageIcon(path  + "night_1.png").getImage(), Item_Type.SCENARY_1);
        a1.addFrame(s,250.0,0,0);
        s = new screenObject(0, 0,  1571, 720, new ImageIcon(path  + "night_2.png").getImage(), Item_Type.SCENARY_1);
        a1.addFrame(s,250.0,0,0);
        a1.setSound(null);
        a1.setSoundType(Character_Voices.Win);
        return a1;
    }
    public static animation generateAnimation2(){
        String path = "assets/sprites/scenarios/usa/";
        animation a2 = new animation();
        a2.setHasEnd(false);
        screenObject s = new screenObject(0, 0,  1571, 322, new ImageIcon(path  + "ring_blue_1.png").getImage(), Item_Type.SCENARY_2);
        a2.addFrame(s,100.0,0,0);
        s = new screenObject(0, 0,  1571, 322, new ImageIcon(path  + "ring_blue_2.png").getImage(), Item_Type.SCENARY_2);
        a2.addFrame(s,100.0,0,0);
        a2.setSound(null);
        a2.setSoundType(Character_Voices.Win);
        return a2;
    }
}
