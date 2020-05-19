package lib.objects;

import lib.Enums.Playable_Character;

// Clase que representa el control de un personaje por IA
public class enemy_controller extends character_controller{

    // Controlador de la inteligencia artificial
    ia_controller ia = new ia_controller();

    // Contructor que pide identificador de personaje y numero de personaje
    public enemy_controller(Playable_Character ch, int pN){
        super(ch, pN, 750, 290, 1);
        if(pN == 1){
            this.x = 500;
            this.player.setOrientation(-1);
            this.player.setX(500);
        }
    }

    // Contructor que pide identificador de personaje, numero de personaje  y el rival
    public enemy_controller(Playable_Character ch, int pN, character rival){
        super(ch,pN,750,290, 1);
        this.rival = rival;
    }

    // Obtener el frame del personaje teniendo en cuenta las colisiones de las hurtbox
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        // Si no se está esperando a que se terminen de mostrar los carteles de intro
        // se pide al personaje el frame correspondiente al movimiento decidido por la IA
        if(!standBy){
            return player.getFrame(ia.getMove(), pHurt, eHurt, rival.isAttacking());
        }
        return player.getFrame("", pHurt, eHurt, rival.isAttacking());
    }

    @Override
    // Resetea el personaje en base al número de jugador
    void reset() {
        if(this.playerNum == 1) {
            reset(this.player.getCharac(),500,290, -1);
        }
        else{
            reset(this.player.getCharac(), 750, 290, 1);
        }
    }

    @Override
    // Para la inteligencia artificial
    public void stopIA(){ia.stopIA();}

    @Override
    // Asigna el rival y define la IA
    public void setRival(character rival) {
        this.player.setRival(rival);
        this.rival = rival;
        ia = new ia_controller(rival,this.player,ia_loader.dif.EASY);
    }

    @Override
    // Devuelve el controlador de la IA
    public ia_controller getIa() {
        return ia;
    }
}
