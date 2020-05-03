package lib.objects;

import lib.Enums.Playable_Character;
import lib.input.controlListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public user_controller(Playable_Character ch, int pN){
        super(ch, pN,500,290, -1);
        Timer inptut_control = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputsGestion();
            }
        });
        inptut_control.start();
    }

    void inputsGestion(){
        mov = "";
        if(!standBy) {
            mov = controlListener.getMove(playerNum);

            if(mov.contains("DE") && player.getOrientation() == 1){
                mov = mov.replace("DE", "IZ");
            }
            else if(mov.contains("IZ") && player.getOrientation() == 1){
                mov =  mov.replace("IZ", "DE");
            }

            long current = System.currentTimeMillis();
            if(current - timeReference < 100.0){
                mov = lastKey;
            }
            if (!mov.equals(lastKey) && current - timeReference > 100.0) {
                if (!mov.equals("")) {
                    inputs.add(mov);
                    times.add(current);
                    timeReference = current;
                }
                lastKey = mov;
            }
            boolean ok = false;
            for (int i = 0; i < inputs.size() && !ok; ++i) {
                if (current - times.get(i) < 2000.0) {
                    ok = true;
                } else {
                    times.remove(i);
                    inputs.remove(i);
                    --i;
                }
            }

            boolean end = false;
            for (int i = 0; i < inputs.size() && !end; ++i) {
                String auxComb = "";
                for (int j = i; j < inputs.size(); ++j) {
                    auxComb += (inputs.get(j) + "+");
                }
                if (auxComb.length() > 0) {
                    auxComb = auxComb.substring(0, auxComb.length() - 1);
                }
                if (this.player.getCombos().containsKey(auxComb) && auxComb.contains("+")) {
                    end = true;
                    mov = auxComb;
                    inputs.clear();
                    times.clear();
                }
            }


            /*else if (mov.contains("-") && !mov.contains("+") && !this.player.combos.containsKey(mov)) {
                String aux = mov;
                boolean ok2 = false;
                for (int i = 0; i < mov.length() && !ok2; ++i) {
                    if (aux.charAt(i) == '-') {
                        aux = aux.substring(0, i) + '+' + aux.substring(i + 1);
                        if (this.player.combos.containsKey(aux) && this.player.state != this.player.combos.get(aux)) {
                            ok2 = true;
                            inputs.clear();
                            times.clear();
                        } else {
                            aux = mov;
                        }
                    }
                }
                if (ok2) {
                    mov = aux;
                }
            }*/
        }
    }

    // Obtiene el frame del personaje
    // collides indica si colisiona con el enemigo
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        this.x = this.player.getX();
        this.y = this.player.getY();

        if(rival == null) {
            return player.getFrame(mov, pHurt, eHurt, false);
        }
        else {
            /*
            if (!inputs.isEmpty()) {
                System.out.println(inputs);
            }*/
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

