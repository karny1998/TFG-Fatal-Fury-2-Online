package lib.objects;

import lib.Enums.Item_Type;
import lib.sound.fight_audio;

import javax.swing.*;
import java.awt.*;

/**
 * The type Announcer animation.
 */
public class announcerAnimation {
    /**
     * The Path.
     */
    String path = "/assets/sprites/fight_interface/announcer/";
    /**
     * The Fightanimation.
     */
    private animation fightanimation;
    /**
     * The Fight image.
     */
    Image fight_image;
    /**
     * The Index.
     */
// Listas de coordenadas
    int index;
    /**
     * The W.
     */
// Dimensiones originales de las imagenes
    int w = 540, /**
     * The H.
     */
    h = 148;

    /**
     * Instantiates a new Announcer animation.
     */
    public announcerAnimation() {
        fight_image = new ImageIcon(this.getClass().getResource(path+"fight.png")).getImage();
        fightanimation = generateAnimation();
    }

    /**
     * Generate animation animation.
     *
     * @return the animation
     */
    public animation generateAnimation() {
        animation anim = new animation();
        int newW, newH, x, y;
        for (int m = 10; m > 1; m--) {
            newW = w/m;
            newH = h/m;
            x = 640 - (newW/2);
            y = 360 - (newH/2);
            screenObject s = new screenObject(x,y,newW,newH,fight_image,Item_Type.ANNOUNCEMENT);
            anim.addFrame(s,250.0/m,0,0);
        }
        anim.setHasEnd(false);
        anim.setHasSound(false);
        anim.setSoundType(fight_audio.voice_indexes.Win);
        return anim;
    }

    /**
     * Gets frame.
     *
     * @return the frame
     */
    public screenObject getFrame() {
        return fightanimation.getFrame();
    }

    /**
     * Start.
     */
    public void start() {
        fightanimation.start();
    }
}
