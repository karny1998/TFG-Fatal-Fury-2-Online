package lib.objects;

import lib.Enums.Playable_Character;

import java.util.Random;

public class enemy_controller {
    private String charac, path = "assets/sprites/characters/";
    character player;
    private int x = 750, y = 160;
    private animation anim = new animation();
    private Random rand = new Random();
    private int posAprox = 0;

    public enemy_controller(Playable_Character ch){
        new IsKeyPressed();
        if(ch == Playable_Character.ANDY){
            charac = "andy";
        }
        else if(ch == Playable_Character.MAI){
            charac = "mai";
        }
        else{
            charac = "terry";
            player = new character(Playable_Character.TERRY);
            player.setX(x);
            player.setY(y);
            player.setOrientation(1);
        }
    }

    public character getPlayer() {
        return player;
    }

    public screenObject getAnimation(){
        controlKey array1[] = {controlKey.LEFT, controlKey.RIGHT, controlKey.DOWN, controlKey.A, controlKey.S, controlKey.D, controlKey.W};
        controlKey key =  array1[rand.nextInt(array1.length)];
        if (key == controlKey.LEFT){++posAprox;}
        else if (key == controlKey.RIGHT){--posAprox;}
        if (posAprox > 10){
            key = controlKey.RIGHT;
            --posAprox;
        }
        else if (posAprox < 0){
            key = controlKey.LEFT;
            ++posAprox;
        }
        String array2[] = {"LEFT", "RIGHT", "DOWN", "A","S", "D", "W"};
        for(int i = 0; i < array1.length; ++i){
            if(array1[i] == key){
                return player.getFrame(array2[i]);
            }
        }
        return player.getFrame("");
    }
}
