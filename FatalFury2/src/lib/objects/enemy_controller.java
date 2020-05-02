package lib.objects;

import lib.Enums.Playable_Character;

// Clase que representa el control de un personaje por IA
public class enemy_controller extends character_controller{

    ia_controller ia = new ia_controller();

    public enemy_controller(Playable_Character ch, int pN){
        super(ch, pN,750,290, 1);
    }

    public enemy_controller(Playable_Character ch, int pN, character rival){
        super(ch,pN,750,290, 1);
        this.rival = rival;
    }

    // Obtener el frame del personaje
    // collides indica si colisiona con el jugador o no
    // En esta función se llamaría a la IA
    // Por ahora se juega aleatoriamente
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        String mov = "";
        if(false){
            return player.getFrame(ia.getMove(), pHurt, eHurt, rival.isAttacking());
        }
        return player.getFrame("", pHurt, eHurt, rival.isAttacking());
    }

    @Override
    void reset() {
        reset(this.player.getCharac(),750,290, 1);
    }

    @Override
    public void stopIA(){ia.stopIA();}

    @Override
    public void setRival(character rival) {
        this.player.setRival(rival);
        this.rival = rival;
        ia = new ia_controller(rival,this.player,0);
    }

    @Override
    public ia_controller getIa() {
        return ia;
    }
}
