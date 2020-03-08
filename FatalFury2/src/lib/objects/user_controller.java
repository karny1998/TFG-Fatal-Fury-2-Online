package lib.objects;

import lib.Enums.Playable_Character;

public class user_controller {
    private String charac, path = "assets/sprites/characters/";
    character player;
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
            player = new character(Playable_Character.TERRY);
        }
        /*path = path + charac + "/standing/";
        anim.setHasEnd(false);
        for(int i = 1; i <= 3; ++i){
            screenObject s = new screenObject(x, y,  600, 600, new ImageIcon(path+i+".png").getImage(), Item_Type.PLAYER);
            anim.addFrame(s,150.0,0,0);
        }*/
    }

    public screenObject getAnimation(){
        controlKey key = IsKeyPressed.keyPressed();
        if(key == controlKey.){
            return player.getFrame("LEFT");
        }
        else if(key == controlKey.D){
            return player.getFrame("DER");
        }
        else if(key == controlKey.D){
            return player.getFrame("DOWN");
        }
        return player.getFrame("");
    }
}
