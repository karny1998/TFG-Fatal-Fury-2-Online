package lib.objects.networking;

import lib.Enums.Playable_Character;
import lib.objects.hitBox;
import lib.objects.screenObject;
import lib.objects.user_controller;
import lib.utils.Pair;

public class online_user_controller extends user_controller {
    private connection con;
    private boolean isLocal = false;
    private int menssageIdentifier = 1;

    public online_user_controller(Playable_Character ch, int pN, connection con, boolean isLocal){
        super(ch, pN);
        this.con = con;
        this.isLocal = isLocal;
        if(!isLocal){
            this.input_control.stop();
        }
    }

    @Override
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        this.x = this.player.getX();
        this.y = this.player.getY();
        if(!isLocal){
            if(!standBy) {
                String newMov = con.receive(menssageIdentifier);
                if (!newMov.equals("NONE")) {
                    mov = newMov;
                }
            }
        }
        else{
            con.send(menssageIdentifier, mov);
        }
        if(rival == null) {
            return player.getFrame(mov, pHurt, eHurt, false);
        }
        else {
            return player.getFrame(mov, pHurt, eHurt, rival.isAttacking());
        }
    }

    public connection getCon() {
        return con;
    }

    public void setCon(connection con) {
        this.con = con;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public int getMenssageIdentifier() {
        return menssageIdentifier;
    }

    public void setMenssageIdentifier(int menssageIdentifier) {
        this.menssageIdentifier = menssageIdentifier;
    }
}
