package lib.objects;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

enum character{terry, mai, andy}
enum state{standing1, standing2, standing3, standing4, walking1, walking2, walking3, walking4}

public class user_controller {
    private String charac, path = "assets\\sprites\\characters\\";
    private Screen screen;
    private state currentState = state.standing1;
    private List<Image> animation;

    public user_controller(character ch){
        if(ch == character.andy){
            charac = "andy";
        }
        else if(ch == character.mai){
            charac = "mai";
        }
        else{
            charac = "terry";
        }
        path = path + charac + "\\standing\\";
        animation = new ArrayList<Image>();;
        for(int i = 1; i <= 3; ++i){
            animation.add(new ImageIcon(path+i+".png").getImage());
        }
    }

    public screenObject getAnimation(){
        if(currentState == state.standing1){
            currentState =  state.standing2;
            return new screenObject(500, 500, animation.get(0));
        }
        else if(currentState == state.standing2 || currentState == state.standing4){
            if(currentState == state.standing2){
                currentState =  state.standing3;
            }
            else{
                currentState =  state.standing1;
            }
            return new screenObject(200, 200, animation.get(1));
        }
        else if(currentState == state.standing3){
            currentState =  state.standing4;
            return new screenObject(500, 500, animation.get(2));
        }
        return new screenObject(500, 500, animation.get(0));
    }
}
