package lib.maps;

import lib.Enums.Item_Type;
import lib.Enums.Scenario_time;
import lib.objects.animation;
import lib.objects.screenObject;
import lib.sound.fight_audio;

import javax.swing.*;

/**
 * The type Australia.
 */
public class australia {
    /**
     * The constant path.
     */
    private static String path = "/assets/sprites/scenarios/australia/";

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
                frame1 = "sunset2_1";
                frame2 = "sunset2_2";
                break;
            case NIGHT:
                frame1 = "sunset3_1";
                frame2 = "sunset3_2";
                break;
            case SUNSET:
                frame1 = "sunset1_1";
                frame2 = "sunset1_2";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + time);
        }
        s1 = new screenObject(0, 0,  1571, 368, new ImageIcon( australia.class.getResource(path+frame1+".png")).getImage(), Item_Type.SCENARY_1);
        s2 = new screenObject(0, 0,  1571, 368, new ImageIcon( australia.class.getResource(path+frame2+".png")).getImage(), Item_Type.SCENARY_1);
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
                frame1 = "back1_1";
                frame2 = "back1_2";
                break;
            case NIGHT:
                frame1 = "back3_1";
                frame2 = "back3_2";
                break;
            case SUNSET:
                frame1 = "back2_1";
                frame2 = "back2_2";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + time);
        }
        s1 = new screenObject(0, 0,  1571, 640, new ImageIcon( australia.class.getResource(path+frame1+".png")).getImage(), Item_Type.SCENARY_2);
        s2 = new screenObject(0, 0,  1571, 640, new ImageIcon( australia.class.getResource(path+frame2+".png")).getImage(), Item_Type.SCENARY_2);
        a2.addFrame(s1,250.0,0,0);
        a2.addFrame(s2,250.0,0,0);
        a2.setHasSound(false);
        a2.setSoundType(fight_audio.voice_indexes.Win);
        return a2;
    }
}

