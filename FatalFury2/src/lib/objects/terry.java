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
        path = path  + "standing/";
        anim = new animation();
        anim.setHasEnd(false);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 260,  400, 400, new ImageIcon(path+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        mov = new movement(Movement.STANDING, anim);
        movs.put(Movement.STANDING,mov);
        combos.put("",Movement.STANDING);
        //ANDANDO
        path = path  + "walking/";
        anim = new animation();
        anim.setHasEnd(false);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 260,  400, 400, new ImageIcon(path+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,10,0);
        }
        mov = new movement(Movement.WALKING, anim);
        movs.put(Movement.WALKING,mov);
        combos.put("",Movement.WALKING);
    }
}
