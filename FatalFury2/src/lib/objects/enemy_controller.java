package lib.objects;

import lib.Enums.Playable_Character;

// Clase que representa el control de un personaje por IA
public class enemy_controller extends character_controller{
    private character rival;

    public enemy_controller(Playable_Character ch){
        super(ch,750,160, 1);
    }

    public enemy_controller(Playable_Character ch, character rival){
        super(ch,750,160, 1);
        this.rival = rival;
    }

    // Obtener el frame del personaje
    // collides indica si colisiona con el jugador o no
    // En esta función se llamaría a la IA
    // Por ahora se juega aleatoriamente
    public screenObject getAnimation(boolean collides){
        this.x = this.player.getX();
        this.y = this.player.getY();

        controlKey array1[] = {controlKey.LEFT, controlKey.RIGHT, controlKey.DOWN, controlKey.A, controlKey.S, controlKey.D, controlKey.W};
        controlKey key =  array1[rand.nextInt(array1.length)];

        int rivalX = rival.getHurtbox().getX();
        int rivalW = rival.getHurtbox().getWidth();
        if(rival.getOrientation() == 1 && Math.abs(rivalX - this.player.getHurtbox().getX()) > 100
            || rival.getOrientation() == -1 && Math.abs(rivalX+rivalW - this.player.getHurtbox().getX()) > 100){
            key = controlKey.LEFT;
        }

        String array2[] = {"LEFT", "RIGHT", "DOWN", "A","S", "D", "W"};
        for(int i = 0; i < array1.length; ++i){
            if(array1[i] == key){
                return player.getFrame(array2[i], collides);
            }
        }
        return player.getFrame("", collides);
    }

    public character getRival() {
        return rival;
    }

    public void setRival(character rival) {
        this.rival = rival;
    }
}
