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

        return player.getFrame(collides);
    }

    @Override
    void reset() {
        reset(this.player.getCharac(),500,160, -1);
    }
}
