package lib.objects;

import lib.Enums.Playable_Character;

// Clase que representa el control de un personaje por IA
public class enemy_controller extends character_controller{

    public enemy_controller(Playable_Character ch){
        super(ch,750,160, 1);
    }

    // Obtener el frame del personaje
    // collides indica si colisiona con el jugador o no
    // En esta función se llamaría a la IA
    // Por ahora se juega aleatoriamente
    public screenObject getAnimation(boolean collides){
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
                return player.getFrame(array2[i], collides);
            }
        }
        return player.getFrame("", collides);
    }
}
