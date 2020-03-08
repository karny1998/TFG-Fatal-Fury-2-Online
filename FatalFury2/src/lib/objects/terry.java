package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;

import javax.swing.*;
import java.util.Map;

public class terry {
    public void generateMovs(Map<String, Movement> combos, Map<Movement, movement> movs){
        String path = "assets/sprites/characters/terry/";
        animation anim;
        movement mov;
        //QUIETO
        anim = new animation();
        anim.setHasEnd(false);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 260,  500, 500, new ImageIcon(path  + "standing/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,100.0,0,0);
        }
        mov = new movement(Movement.STANDING, anim);
        movs.put(Movement.STANDING,mov);
        combos.put("",Movement.STANDING);
        //ANDANDO
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 260,  500, 500, new ImageIcon(path  + "walking/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,50.0,50,0);
        }
        mov = new movement(Movement.WALKING, anim);
        movs.put(Movement.WALKING,mov);
        combos.put("LEFT",Movement.WALKING);
        //ANDANDO HACIA ATRAS
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 3; i >= 1; --i){
            screenObject s = new screenObject(150, 260,  500, 500, new ImageIcon(path  + "walking/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,100.0,-50,0);
        }
        mov = new movement(Movement.WALKING_BACK, anim);
        movs.put(Movement.WALKING_BACK,mov);
        combos.put("DER",Movement.WALKING_BACK);
        //AGACHARSE
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 3; i >= 1; --i){
            screenObject s = new screenObject(150, 260,  500, 500, new ImageIcon(path  + "crouch/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,100.0,0,0);
        }
        mov = new movement(Movement.CROUCH, anim);
        movs.put(Movement.CROUCH,mov);
        combos.put("DOWN",Movement.CROUCH);
    }
}
