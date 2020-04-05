package lib.objects;

import lib.Enums.Playable_Character;

// Clase que representa un controlador de interacción entre
// el personaje y el jugador
public class user_controller extends character_controller{

    public user_controller(Playable_Character ch){
        super(ch,500,160, -1);
    }

    // Obtiene el frame del personaje
    // collides indica si colisiona con el enemigo
    public screenObject getAnimation(boolean collides){
        this.x = this.player.getX();
        this.y = this.player.getY();
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
