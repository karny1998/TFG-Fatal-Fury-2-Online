package lib.objects;

import lib.Enums.Playable_Character;
import lib.input.controlListener;

import java.util.ArrayList;
import java.util.List;

// Clase que representa un controlador de interacción entre
// el personaje y el jugador
public class user_controller extends character_controller{

    private List<String> inputs = new ArrayList<String>();
    private List<Long> times = new ArrayList<Long>();
    private long timeReference = System.currentTimeMillis();
    private String lastKey = "";
    private String mov = "";

    public user_controller(Playable_Character ch){
        super(ch,500,290, -1);
    }

    void inputsGestion(){
        mov = controlListener.getMove(playerNum);
        long current = System.currentTimeMillis();
        if(!mov.equals(lastKey) && current - timeReference > 100.0){
            if(!mov.equals("")) {
                inputs.add(mov);
                times.add(current);
                timeReference = current;
            }
            lastKey = mov;
        }
        boolean ok = false;
        for(int i = 0; i < inputs.size() && !ok; ++i){
            if(current - times.get(i) < 1000.0){
                ok = true;
            }
            else{
                times.remove(i);
                inputs.remove(i);
                --i;
            }
        }
    }

    // Obtiene el frame del personaje
    // collides indica si colisiona con el enemigo
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        this.x = this.player.getX();
        this.y = this.player.getY();
        mov = "";
        /*if(!standBy){
            mov = controlListener.getMove(1);
        }*/

        if(!standBy){
            inputsGestion();

            //Collections.sort(inputs, Comparator.comparing(String::length));
            boolean end = false;
            for(int i = 0; i < inputs.size() && !end;++i){
                String auxComb = "";
                for(int j = i; j <inputs.size(); ++j){
                    auxComb += (inputs.get(j) + "+");
                }
                if(auxComb.length() > 0){
                    auxComb = auxComb.substring(0,auxComb.length()-1);
                }
                if(this.player.combos.containsKey(auxComb) && auxComb.contains("+")){
                    end = true;
                    mov = auxComb;
                    inputs.clear();
                    times.clear();
                }
            }
        }

        if(rival == null) {
            return player.getFrame(mov, pHurt, eHurt, false);
        }
        else{
            return player.getFrame(mov, pHurt, eHurt, rival.isAttacking());
        }
    }

    @Override
    void reset() {
        if(playerNum == 1){reset(this.player.getCharac(),500,290, -1);}
        else{reset(this.player.getCharac(),750,290, 1);}
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public long getTimeReference() {
        return timeReference;
    }

    public void setTimeReference(long timeReference) {
        this.timeReference = timeReference;
    }

    public String getLastKey() {
        return lastKey;
    }

    public void setLastKey(String lastKey) {
        this.lastKey = lastKey;
    }

    public String getMov() {
        return mov;
    }

    public void setMov(String mov) {
        this.mov = mov;
    }
}

