package lib.objects;

import lib.Enums.Movement;
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
    // Lista de inputs del usuario en los últimos dos segundos
    private List<String> inputs = new ArrayList<String>();
    // Tiempos de referencia de cada input
    private List<Long> times = new ArrayList<Long>();
    // Tiempo de referencia general
    private long timeReference = System.currentTimeMillis();
    // último input introducido
    private String lastKey = "";
    // Movimiento a realizar
    private String mov = "";

    // Contructor que pide el identificador de personaje y el número de jugador
    public user_controller(Playable_Character ch, int pN){
        super(ch, pN,500,290, -1);
        // Proceso encargado de la gestión de inputs del usuario
        Timer inptut_control = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputsGestion();
            }
        });
        inptut_control.start();
    }

    // Gestiona la introducción de inputs del usuario y detección de combos
    void inputsGestion(){
        mov = "";
        // Si no se está en standBy se anañizan los inputs
        if(!standBy) {
            mov = controlListener.getMove(playerNum);
            // Se cambia derecha por izquierda según la orientación
            if(mov.contains("DE") && player.getOrientation() == 1){
                mov = mov.replace("DE", "IZ");
            }
            else if(mov.contains("IZ") && player.getOrientation() == 1){
                mov =  mov.replace("IZ", "DE");
            }

            long current = System.currentTimeMillis();
            // Se actualiza el movimiento al último input en caso de haber pasado el tiempo suficiente
            if(current - timeReference < 100.0){
                mov = lastKey;
            }
            // Si el movimiento es distinto al último input y ha pasado el tiempo de referencia, y no es la cadena vacía
            // se añade a la lista de inputs
            if (!mov.equals(lastKey) && current - timeReference > 100.0) {
                if (!mov.equals("")) {
                    inputs.add(mov);
                    times.add(current);
                    timeReference = current;
                }
                lastKey = mov;
            }
            boolean ok = false;
            // Se eliminan de la lista todos los inputs que tengan como tiempo de introducción hace más de 2 segundos
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
            // Comprueba si se corresponde con algún combo de más largo a más corto
            for (int i = 0; i < inputs.size() && !end; ++i) {
                String auxComb = "";
                // Genera una posible cadena de combo
                for (int j = i; j < inputs.size(); ++j) {
                    auxComb += (inputs.get(j) + "+");
                }
                if (auxComb.length() > 0) {
                    auxComb = auxComb.substring(0, auxComb.length() - 1);
                }
                // Si dicho combo existe se ejecuta, y se vacian los inputs y tiempos
                if (this.player.getCombos().containsKey(auxComb) && auxComb.contains("+")) {
                    // En caso de que el combo sea el dash, solo se ejecuta si ha sido en menos de 1 sec
                    if(this.player.getCombos().get(auxComb) != Movement.DASH ||
                            this.player.getCombos().get(auxComb) == Movement.DASH &&
                            times.get(i+1) - times.get(i) <= 1000.0) {
                        end = true;
                        mov = auxComb;
                        inputs.clear();
                        times.clear();
                    }
                }
            }
        }
    }

    // Obtiene el frame del personaje, teniendo en cuanta las colisiones entre los personajes
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        this.x = this.player.getX();
        this.y = this.player.getY();
        if(rival == null) {
            return player.getFrame(mov, pHurt, eHurt, false);
        }
        else {
            return player.getFrame(mov, pHurt, eHurt, rival.isAttacking());
        }
    }

    @Override
    // Resetea el personaje en base al número de jugador
    void reset() {
        if(playerNum == 1){reset(this.player.getCharac(),500,290, -1);}
        else{reset(this.player.getCharac(),750,290, 1);}
    }

    // Getters y setters
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

