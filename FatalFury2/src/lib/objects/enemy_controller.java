package lib.objects;

import lib.Enums.Playable_Character;

// Clase que representa el control de un personaje por IA
public class enemy_controller extends character_controller{

    public enemy_controller(Playable_Character ch){
        super(ch,750,290, 1);
    }

    public enemy_controller(Playable_Character ch, character rival){
        super(ch,750,290, 1);
        this.rival = rival;
    }

    // Obtener el frame del personaje
    // collides indica si colisiona con el jugador o no
    // En esta función se llamaría a la IA
    // Por ahora se juega aleatoriamente
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        String mov = "";
        String array[] = {"A","B", "DE-A"};
        mov = array[rand.nextInt(array.length)];
        if(false){
            this.x = this.player.getX();
            this.y = this.player.getY();

            int rivalX = rival.getHurtbox().getX();
            int rivalW = rival.getHurtbox().getWidth();
            if(rival.getOrientation() == 1 && Math.abs(rivalX - this.player.getHurtbox().getX()-this.player.getHurtbox().getWidth()) > 20
                || rival.getOrientation() == -1 && Math.abs(this.player.getHurtbox().getX() -rivalX-rivalW) > 20){
                if(rival.getOrientation() == 1) {
                    mov = "DE";
                }
                else{
                    mov = "IZ";
                }
            }
            return player.getFrame(mov, pHurt, eHurt, rival.isAttacking());
        }
        return player.getFrame("", pHurt, eHurt, rival.isAttacking());
    }

    @Override
    void reset() {
        reset(this.player.getCharac(),750,290, 1);
    }
}
