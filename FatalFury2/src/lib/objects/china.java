package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Scenario_time;
import lib.Enums.Scenario_type;

import javax.swing.*;

public class china {
    private static String path = "assets/sprites/scenarios/china/";

    // Generar animación del fondo
    public static animation generateAnimation1(Scenario_time time){
        animation a1 = new animation();
        a1.setHasEnd(false);
        screenObject s1, s2;
        String frame1, frame2;
        switch (time) {
            case DAWN:
                frame1 = "dawn";
                frame2 = "dawn";
                break;
            case NIGHT:
                frame1 = "night";
                frame2 = "night";
                break;
            case SUNSET:
                frame1 = "sunset";
                frame2 = "sunset";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + time);
        }
        s1 = new screenObject(0, 0,  1571, 720, new ImageIcon(path+frame1+".png").getImage(), Item_Type.SCENARY_1);
        s2 = new screenObject(0, 0,  1571, 720, new ImageIcon(path+frame2+".png").getImage(), Item_Type.SCENARY_1);
        a1.addFrame(s1,250.0,0,0);
        a1.addFrame(s2,250.0,0,0);
        a1.setSound(null);
        a1.setSoundType(Character_Voices.Win);
        return a1;
    }

    // Generar animación del suelo
    public static animation generateAnimation2(Scenario_time time){
        animation a2 = new animation();
        a2.setHasEnd(false);
        screenObject s1, s2;
        String frame1, frame2;
        switch (time) {
            case DAWN:
                frame1 = "scenario3";
                frame2 = "scenario3";
                break;
            case NIGHT:
                frame1 = "scenario2_1";
                frame2 = "scenario2_2";
                break;
            case SUNSET:
                frame1 = "scenario1_1";
                frame2 = "scenario1_2";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + time);
        }
        s1 = new screenObject(0, 0,  1571, 663, new ImageIcon(path+frame1+".png").getImage(), Item_Type.SCENARY_2);
        s2 = new screenObject(0, 0,  1571, 663, new ImageIcon(path+frame2+".png").getImage(), Item_Type.SCENARY_2);
        a2.addFrame(s1,250.0,0,0);
        a2.addFrame(s2,250.0,0,0);
        a2.setSound(null);
        a2.setSoundType(Character_Voices.Win);
        return a2;
    }
}

