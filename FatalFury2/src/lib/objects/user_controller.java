package lib.objects;

import lib.Enums.Playable_Character;

public class user_controller {
    private String charac, path = "assets/sprites/characters/";
    character player;

    public character getPlayer() {
        return player;
    }

    public void setPlayer(character player) {
        this.player = player;
    }

    private int x = 500, y = 160;
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
            player = new character(Playable_Character.TERRY);
            player.setX(x);
            player.setY(y);
            player.setOrientation(-1);
        }
    }

    public screenObject getAnimation(boolean collides){
        controlKey key = IsKeyPressed.keyPressed();
        controlKey array1[] = {controlKey.LEFT, controlKey.RIGHT, controlKey.DOWN, controlKey.A, controlKey.S, controlKey.D, controlKey.W};
        String array2[] = {"LEFT", "RIGHT", "DOWN", "A","S", "D", "W"};
        for(int i = 0; i < array1.length; ++i){
            if(array1[i] == key){
                return player.getFrame(array2[i], collides);
            }
        }
        return player.getFrame("", collides);
    }
}
