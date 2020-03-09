package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.sound.Sound;

import javax.swing.*;
import java.util.Map;

public class terry {
    public void generateMovs(Map<String, Movement> combos, Map<Movement, movement> movs, Sound sounds){
        String path = "assets/sprites/characters/terry/";
        animation anim;
        movement mov;
        //QUIETO
        anim = new animation();
        anim.setHasEnd(false);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "standing/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Win);
        mov = new movement(Movement.STANDING, anim);
        movs.put(Movement.STANDING,mov);
        combos.put("",Movement.STANDING);
        //ANDANDO
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "walking/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,-50,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Defeat);
        mov = new movement(Movement.WALKING, anim);
        movs.put(Movement.WALKING,mov);
        combos.put("LEFT",Movement.WALKING);
        //ANDANDO HACIA ATRAS
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 3; i >= 1; --i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "walking/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,50,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Defeat);
        mov = new movement(Movement.WALKING_BACK, anim);
        movs.put(Movement.WALKING_BACK,mov);
        combos.put("RIGHT",Movement.WALKING_BACK);
        //AGACHARSE
        anim = new animation();
        anim.setHasEnd(false);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "crouch/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hurt_1);
        mov = new movement(Movement.CROUCH, anim);
        movs.put(Movement.CROUCH,mov);
        combos.put("DOWN",Movement.CROUCH);
        //POÑOTASO DÉBIL
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 1; i <= 2; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "soft_punch/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hit_1);
        mov = new movement(Movement.SOFT_PUNCH, anim);
        movs.put(Movement.SOFT_PUNCH,mov);
        combos.put("A",Movement.SOFT_PUNCH);
        //POÑOTASO FUERTE
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 1; i <= 2; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "hard_punch/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hit_2);
        mov = new movement(Movement.HARD_PUNCH, anim);
        movs.put(Movement.HARD_PUNCH,mov);
        combos.put("S",Movement.HARD_PUNCH);
        //CODASO
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hit_3);
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 1; i <= 2; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "attack_poke/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hit_3);
        mov = new movement(Movement.ATTACK_POKE, anim);
        movs.put(Movement.ATTACK_POKE,mov);
        combos.put("D",Movement.ATTACK_POKE);
        //VOLTERETA
        anim = new animation();
        anim.setHasEnd(true);
        for(int i = 1; i <= 4; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "roll_front/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hurt_2);
        mov = new movement(Movement.ROLL_FRONT, anim);
        movs.put(Movement.ROLL_FRONT,mov);
        combos.put("W",Movement.ROLL_FRONT);
    }
}
