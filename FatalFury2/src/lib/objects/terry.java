package lib.objects;

import lib.Enums.Animation_type;
import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.sound.Sound;

import javax.swing.*;
import java.util.Map;

public class terry {

    //Burning knuckes
    private String[] special_1_iz_01 = {"AB", "AB-IZ", "IZ-A"};
    private String[] special_1_iz_02 = {"AB", "AB-IZ", "IZ-C"};
    public String[][] special_1_iz = {special_1_iz_01, special_1_iz_02};

    private String[] special_1_de_01 = {"AB", "AB-DE", "DE-A"};
    private String[] special_1_de_02 = {"AB", "AB-DE", "DE-C"};
    public String[][] special_1_de = {special_1_de_01, special_1_de_02};

    //Rising tackle
    private String[] special_2_iz_01 = {"AB", "AR", "A"};
    private String[] special_2_iz_02 = {"AB", "AR", "C"};
    public String[][] special_2_iz = {special_2_iz_01, special_2_iz_02};

    public String[][] special_2_de = special_2_iz;

    //Crack shoot
    private String[] special_3_iz_01 = {"AB", "AB-IZ", "IZ", "AR-IZ", "B"};
    private String[] special_3_iz_02 = {"AB", "AB-IZ", "IZ", "AR-IZ", "D"};
    public String[][] special_3_iz = {special_3_iz_01, special_3_iz_02};

    private String[] special_3_de_01 = {"AB", "AB-DE", "DE", "AR-DE", "B"};
    private String[] special_3_de_02 = {"AB", "AB-DE", "DE", "AR-DE", "D"};
    public String[][] special_3_de = {special_3_de_01, special_3_de_02};

    //Power wave
    private String[] special_4_iz_01 = {"AB", "AB-DE", "DE-A"};
    private String[] special_4_iz_02 = {"AB", "AB-DE", "DE-C"};
    public String[][] special_4_iz = {special_4_iz_01, special_4_iz_02};

    private String[] special_4_de_01 = {"AB", "AB-IZ", "IZ-A"};
    private String[] special_4_de_02 = {"AB", "AB-IZ", "IZ-C"};
    public String[][] special_4_de = {special_4_de_01, special_4_de_02};

    //Power Geyser
    private String[] desperation_move_iz_01 = {"AB", "AB-IZ", "IZ", "AB-IZ", "DE-B-C"};
    public String[][] desperation_move_iz = {desperation_move_iz_01};

    private String[] desperation_move_de_01 = {"AB", "AB-DE", "DE", "AB-DE", "IZ-B-C"};
    public String[][] desperation_move_de = {desperation_move_de_01};





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
        anim.setHurtBox(145,110,220,390);
        //ANDANDO
        anim = new animation();
        anim.setHurtBox(145,110,220,390);
        anim.setHasEnd(true);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "walking/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,-75,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Defeat);
        mov = new movement(Movement.WALKING, anim);
        movs.put(Movement.WALKING,mov);
        combos.put("LEFT",Movement.WALKING);
        //ANDANDO HACIA ATRAS
        anim = new animation();
        anim.setHurtBox(145,110,220,390);
        anim.setHasEnd(true);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "walking_back/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,75,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Defeat);
        mov = new movement(Movement.WALKING_BACK, anim);
        movs.put(Movement.WALKING_BACK,mov);
        combos.put("RIGHT",Movement.WALKING_BACK);
        //AGACHARSE
        anim = new animation();
        anim.setType(Animation_type.HOLDABLE);
        anim.setHurtBox(145,110,220,390);
        anim.setHasEnd(false);
        for(int i = 1; i <= 2; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "crouch/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,90.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hurt_1);
        mov = new movement(Movement.CROUCH, anim);
        movs.put(Movement.CROUCH,mov);
        combos.put("DOWN",Movement.CROUCH);
        // DESAGACHARSE
        anim = new animation();
        anim.setType(Animation_type.ENDABLE);
        anim.setHurtBox(145,110,220,390);
        anim.setHasEnd(true);
        for(int i = 2; i >= 1; --i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "crouch/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,90.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hurt_1);
        mov = new movement(Movement.UNDO_CROUCH, anim);
        movs.put(Movement.UNDO_CROUCH,mov);
        combos.put("ASDDSADASDSA",Movement.UNDO_CROUCH);
        //POÑOTASO DÉBIL
        anim = new animation();
        anim.setHurtBox(145,110,220,390);
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
        anim.setHurtBox(145,110,220,390);
        anim.setHitbox(15, 170, 130,75);
        anim.setHasEnd(true);
        for(int i = 1; i <= 2; ++i){
            screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "hard_punch/"+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hit_2);
        mov = new movement(Movement.HARD_PUNCH, anim);
        mov.setDamage(10);
        movs.put(Movement.HARD_PUNCH,mov);
        combos.put("S",Movement.HARD_PUNCH);
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hit_3);
        //CODASO
        anim = new animation();
        anim.setHurtBox(145,110,220,390);
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
        anim.setHurtBox(145,110,220,390);
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

        // SALTAR VERTICAL
        anim = new animation();
        anim.setHurtBox(145,110,220,390);
        anim.setHasEnd(true);
        screenObject s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "jump/"+1+".png").getImage(), Item_Type.PLAYER);
        anim.addFrame(s,250.0,0,-300);
        s = new screenObject(150, 160,  500, 500, new ImageIcon(path  + "jump/"+2+".png").getImage(), Item_Type.PLAYER);
        anim.addFrame(s,250.0,0,300);
        anim.setSound(sounds);
        anim.setSoundType(Character_Voices.Hurt_2);
        mov = new movement(Movement.NORMAL_JUMP, anim);
        movs.put(Movement.NORMAL_JUMP,mov);
        combos.put("UP",Movement.NORMAL_JUMP);
    }
}
