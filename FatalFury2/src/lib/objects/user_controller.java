package lib.objects;

import lib.Enums.Playable_Character;
import lib.input.controlListener;

// Clase que representa un controlador de interacción entre
// el personaje y el jugador
public class user_controller extends character_controller{

    public user_controller(Playable_Character ch){
        super(ch,500,290, -1);
    }

    // Obtiene el frame del personaje
    // collides indica si colisiona con el enemigo
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        this.x = this.player.getX();
        this.y = this.player.getY();
        String mov = "";
        if(!standBy){mov = controlListener.getMove(1);}
        System.out.println(mov);
        return player.getFrame(mov, pHurt, eHurt);
    }

    @Override
    void reset() {
        reset(this.player.getCharac(),500,290, -1);
    }
}
