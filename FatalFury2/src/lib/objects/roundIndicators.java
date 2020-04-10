package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class roundIndicators {
    String path = "assets/sprites/fight_interface/round_points/";
    private animation player1_1_no;
    private animation player1_1_point;
    private animation player1_2_no;
    private animation player1_2_point;
    private animation player2_1_no;
    private animation player2_1_point;
    private animation player2_2_no;
    private animation player2_2_point;
    Image round, round_red, round_no, round_point;
    int w = 59;
    int h = 52;

    public roundIndicators() {
        round = new ImageIcon(path+"round.png").getImage();
        round_red = new ImageIcon(path+"round_red.png").getImage();
        round_no = new ImageIcon(path+"round_no.png").getImage();
        round_point = new ImageIcon(path+"round_point.png").getImage();
        loadAnimations();
        startAnimations();
    }

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
        anim.setSound(null);
        anim.setSoundType(Character_Voices.Win);
        return anim;
    }

    public java.util.List<screenObject> getFramesPlayer1(boolean point1, boolean point2) {
        List<screenObject> list = new ArrayList<screenObject>();
        if (point1) { list.add(player1_1_point.getFrame(0,63,1)); }
        else { list.add(player1_1_no.getFrame(0,63,1)); }
        if (point2) { list.add(player1_2_point.getFrame(0+w,63,1)); }
        else { list.add(player1_2_no.getFrame(0+w,63,1)); }
        return list;
    }

    public java.util.List<screenObject> getFramesPlayer2(boolean point1, boolean point2) {
        List<screenObject> list = new ArrayList<screenObject>();
        if (point1) { list.add(player2_1_point.getFrame(1280-2*w,63,1)); }
        else { list.add(player2_1_no.getFrame(1280-2*w,63,1)); }
        if (point2) { list.add(player2_2_point.getFrame(1280-w,63,1)); }
        else { list.add(player2_2_no.getFrame(1280-w,63,1)); }
        return list;
    }


}
