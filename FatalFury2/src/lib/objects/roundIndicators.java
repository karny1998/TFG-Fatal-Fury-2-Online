package lib.objects;

import lib.Enums.Item_Type;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Round indicators.
 */
public class roundIndicators {
    /**
     * The Path.
     */
    String path = "/assets/sprites/fight_interface/round_points/";
    /**
     * The Player 1 1 no.
     */
    private animation player1_1_no;
    /**
     * The Player 1 1 point.
     */
    private animation player1_1_point;
    /**
     * The Player 1 2 no.
     */
    private animation player1_2_no;
    /**
     * The Player 1 2 point.
     */
    private animation player1_2_point;
    /**
     * The Player 2 1 no.
     */
    private animation player2_1_no;
    /**
     * The Player 2 1 point.
     */
    private animation player2_1_point;
    /**
     * The Player 2 2 no.
     */
    private animation player2_2_no;
    /**
     * The Player 2 2 point.
     */
    private animation player2_2_point;
    /**
     * The Round.
     */
    Image round, /**
     * The Round red.
     */
    round_red, /**
     * The Round no.
     */
    round_no, /**
     * The Round point.
     */
    round_point;
    /**
     * The W.
     */
    int w = 59;
    /**
     * The H.
     */
    int h = 52;

    /**
     * Instantiates a new Round indicators.
     */
    public roundIndicators() {
        round = new ImageIcon(this.getClass().getResource(path+"round.png")).getImage();
        round_red = new ImageIcon(this.getClass().getResource(path+"round_red.png")).getImage();
        round_no = new ImageIcon(this.getClass().getResource(path+"round_no.png")).getImage();
        round_point = new ImageIcon(this.getClass().getResource(path+"round_point.png")).getImage();
        loadAnimations();
        startAnimations();
    }

    /**
     * Load animations.
     */
    public void loadAnimations() {
        player1_1_no = generateAnimation(0,63,false,Item_Type.BUBBLE1);
        player1_1_point = generateAnimation(0,63,true,Item_Type.BUBBLE1);
        player1_2_no = generateAnimation(0+w,63,false,Item_Type.BUBBLE2);
        player1_2_point = generateAnimation(0+w,63,true,Item_Type.BUBBLE2);
        player2_1_no = generateAnimation(1280-2*w,63,false,Item_Type.BUBBLE3);
        player2_1_point = generateAnimation(1280-2*w,63,true,Item_Type.BUBBLE3);
        player2_2_no = generateAnimation(1280-w,63,false,Item_Type.BUBBLE4);
        player2_2_point = generateAnimation(1280-w,63,true,Item_Type.BUBBLE4);
    }

    /**
     * Start animations.
     */
    public void startAnimations() {
        player1_1_no.start();
        player1_1_point.start();
        player1_2_no.start();
        player1_2_point.start();
        player2_1_no.start();
        player2_1_point.start();
        player2_2_no.start();
        player2_2_point.start();
    }

    /**
     * Generate animation animation.
     *
     * @param x     the x
     * @param y     the y
     * @param point the point
     * @param type  the type
     * @return the animation
     */
    public animation generateAnimation(int x, int y, boolean point, Item_Type type) {
        animation anim = new animation();
        screenObject s1, s2;
        anim.setHasEnd(false);
        if (point) {
            s1 = new screenObject(x,y,w,h,round_red,type);
            s2 = new screenObject(x,y,w,h,round_point,type);
        }
        else {
            s1 = new screenObject(x,y,w,h,round,type);
            s2 = new screenObject(x,y,w,h,round_no,type);
        }
        anim.addFrame(s1,250.0,0,0);
        anim.addFrame(s2,250.0,0,0);
        return anim;
    }

    /**
     * Gets frames player 1.
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @return the frames player 1
     */
    public java.util.List<screenObject> getFramesPlayer1(boolean point1, boolean point2) {
        List<screenObject> list = new ArrayList<screenObject>();
        if (point1) { list.add(player1_1_point.getFrame(0,63,1)); }
        else { list.add(player1_1_no.getFrame(0,63,1)); }
        if (point2) { list.add(player1_2_point.getFrame(0+w,63,1)); }
        else { list.add(player1_2_no.getFrame(0+w,63,1)); }
        return list;
    }

    /**
     * Gets frames player 2.
     *
     * @param point1 the point 1
     * @param point2 the point 2
     * @return the frames player 2
     */
    public java.util.List<screenObject> getFramesPlayer2(boolean point1, boolean point2) {
        List<screenObject> list = new ArrayList<screenObject>();
        if (point1) { list.add(player2_1_point.getFrame(1280-2*w,63,1)); }
        else { list.add(player2_1_no.getFrame(1280-2*w,63,1)); }
        if (point2) { list.add(player2_2_point.getFrame(1280-w,63,1)); }
        else { list.add(player2_2_no.getFrame(1280-w,63,1)); }
        return list;
    }


}
