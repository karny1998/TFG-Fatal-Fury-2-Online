package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Scenario_time;
import lib.Enums.Scenario_type;

import javax.swing.*;

public class usa {
    private static String path = "assets/sprites/scenarios/usa/";

    // Generar animación del fondo
    public static animation generateAnimation1(Scenario_time time){
        animation a1 = new animation();
        a1.setHasEnd(false);
        screenObject s1, s2;
        String frame1, frame2;
        switch (time) {
            case DAWN:
                frame1 = "dawn_1";
                frame2 = "dawn_2";
                break;
            case NIGHT:
                frame1 = "night_1";
                frame2 = "night_2";
                break;
            case SUNSET:
                frame1 = "sunset_1";
                frame2 = "sunset_2";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + time);
        }
        s1= new screenObject(0, 0,  1571, 720, new ImageIcon(path+frame1+".png").getImage(), Item_Type.SCENARY_1);
        s2 = new screenObject(0, 0,  1571, 720, new ImageIcon(path+frame2+".png").getImage(), Item_Type.SCENARY_1);
        a1.addFrame(s1,250.0,0,0);
        a1.addFrame(s2,250.0,0,0);
        a1.setSound(null);
        a1.setSoundType(Character_Voices.Win);
        return a1;
    }

    // Generar animación del ring
    public static animation generateAnimation2(){
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
