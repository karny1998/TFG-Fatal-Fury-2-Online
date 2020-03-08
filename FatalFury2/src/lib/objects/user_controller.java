package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;

import javax.swing.*;

public class user_controller {
    private String charac, path = "assets/sprites/characters/";
    //private state currentState = state.standing1;
    private int x = 350, y = 100;
    private animation anim = new animation();

    public user_controller(Playable_Character ch){
        new IsKeyPressed();
        if(ch == Playable_Character.ANDY){
            charac = "andy";
        }
        else if(ch == Playable_Character.MAI){
            charac = "mai";
        }
        else{
            charac = "terry";
        }
        path = path + charac + "/standing/";
        anim.setHasEnd(false);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(x, y,  600, 600, new ImageIcon(path+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }
    }

    public screenObject getAnimation(){
        controlKey key = IsKeyPressed.keyPressed();
        if(key == controlKey.A){
            x -= 20;
        }
        else if(key == controlKey.D){
            x += 20;
        }
        else if(key == controlKey.W){
            y -= 20;
        }
        else if(key == controlKey.S){
            y += 20;
        }
        return anim.getFrame(x,y,1);
    }
}
