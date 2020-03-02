package lib.objects;

import lib.Enums.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

enum state{standing1, standing2, standing3, standing4, walking1, walking2, walking3, walking4}

public class user_controller {
    private String charac, path = "assets/sprites/characters/";
    private state currentState = state.standing1;
    private List<Image> animation;
    private int x = 350, y = 100;

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
        animation = new ArrayList<Image>();;
        for(int i = 1; i <= 3; ++i){
            animation.add(new ImageIcon(path+i+".png").getImage());
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

        if(currentState == state.standing1){
            currentState =  state.standing2;
            return new screenObject(x, y,  600, 600, animation.get(0), Item_Type.PLAYER);
        }
        else if(currentState == state.standing2 || currentState == state.standing4){
            if(currentState == state.standing2){
                currentState =  state.standing3;
            }
            else{
                currentState =  state.standing1;
            }
            return new screenObject(x, y, 600, 600, animation.get(1), Item_Type.PLAYER);
        }
        else if(currentState == state.standing3){
            currentState =  state.standing4;
            return new screenObject(x, y, 600, 600, animation.get(2), Item_Type.PLAYER);
        }
        return new screenObject(x, y, 600, 600, animation.get(0), Item_Type.PLAYER);
    }
}
