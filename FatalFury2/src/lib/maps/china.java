package lib.maps;

import lib.Enums.Item_Type;
import lib.Enums.Scenario_time;
import lib.objects.animation;
import lib.objects.screenObject;
import lib.sound.fight_audio;

import javax.swing.*;

/**
 * The type China.
 */
public class china {
    /**
     * The constant path.
     */
    private static String path = "/assets/sprites/scenarios/china/";

    /**
     * Generate animation 1 animation.
     *
     * @param time the time
     * @return the animation
     */
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
        s1 = new screenObject(0, 0,  1571, 720, new ImageIcon( china.class.getResource(path+frame1+".png")).getImage(), Item_Type.SCENARY_1);
        s2 = new screenObject(0, 0,  1571, 720, new ImageIcon( china.class.getResource(path+frame2+".png")).getImage(), Item_Type.SCENARY_1);
        a1.addFrame(s1,250.0,0,0);
        a1.addFrame(s2,250.0,0,0);
        a1.setHasSound(false);
        a1.setSoundType(fight_audio.voice_indexes.Win);
        return a1;
    }

    /**
     * Generate animation 2 animation.
     *
     * @param time the time
     * @return the animation
     */
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
        s1 = new screenObject(0, 0,  1571, 663, new ImageIcon( china.class.getResource(path+frame1+".png")).getImage(), Item_Type.SCENARY_2);
        s2 = new screenObject(0, 0,  1571, 663, new ImageIcon( china.class.getResource(path+frame2+".png")).getImage(), Item_Type.SCENARY_2);
        a2.addFrame(s1,250.0,0,0);
        a2.addFrame(s2,250.0,0,0);
        a2.setHasSound(false);
        a2.setSoundType(fight_audio.voice_indexes.Win);
        return a2;
    }
}

