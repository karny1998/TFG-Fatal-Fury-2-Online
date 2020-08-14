package lib.objects;

import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.input.controlListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The type User controller.
 */
// Clase que representa un controlador de interacción entre
// el personaje y el jugador
public class user_controller extends character_controller{
    /**
     * The Inputs.
     */
// Lista de inputs del usuario en los últimos dos segundos
    protected List<String> inputs = new ArrayList<String>();
    /**
     * The Times.
     */
// Tiempos de referencia de cada input
    protected List<Long> times = new ArrayList<Long>();
    /**
     * The Time reference.
     */
// Tiempo de referencia general
    protected long timeReference = System.currentTimeMillis();
    /**
     * The Last key.
     */
// último input introducido
    protected String lastKey = "";
    /**
     * The Mov.
     */
// Movimiento a realizar
    protected String mov = "";

    /**
     * The Input control.
     */
    protected Timer input_control;

    /**
     * Instantiates a new User controller.
     *
     * @param ch the ch
     * @param pN the p n
     */
// Contructor que pide el identificador de personaje y el número de jugador
    public user_controller(Playable_Character ch, int pN){
        super(ch, pN,500,290, -1);
        // Proceso encargado de la gestión de inputs del usuario
        input_control = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputsGestion();
            }
        });
        input_control.start();
    }

    /**
     * Inputs gestion.
     */
    protected void inputsGestion(){
        mov = "";
        if(!standBy) {
            mov = controlListener.getMove(playerNum);
            inputsGestionAux();
        }
    }

    /**
     * Inputs gestion.
     */
// Gestiona la introducción de inputs del usuario y detección de combos
    protected void inputsGestionAux(){
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

    /**
     * Get animation screen object.
     *
     * @param pHurt the p hurt
     * @param eHurt the e hurt
     * @return the screen object
     */
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

    /**
     * Reset.
     */
    @Override
    // Resetea el personaje en base al número de jugador
    void reset() {
        if(playerNum == 1){reset(this.player.getCharac(),500,290, -1);}
        else{reset(this.player.getCharac(),750,290, 1);}
    }

    /**
     * Stops the input gestion.
     */
    @Override
    public void stop(){
        if(input_control != null){
            input_control.stop();
        }
    }

    /**
     * Gets inputs.
     *
     * @return the inputs
     */
// Getters y setters
    public List<String> getInputs() {
        return inputs;
    }

    /**
     * Sets inputs.
     *
     * @param inputs the inputs
     */
    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    /**
     * Gets times.
     *
     * @return the times
     */
    public List<Long> getTimes() {
        return times;
    }

    /**
     * Sets times.
     *
     * @param times the times
     */
    public void setTimes(List<Long> times) {
        this.times = times;
    }

    /**
     * Gets time reference.
     *
     * @return the time reference
     */
    public long getTimeReference() {
        return timeReference;
    }

    /**
     * Sets time reference.
     *
     * @param timeReference the time reference
     */
    public void setTimeReference(long timeReference) {
        this.timeReference = timeReference;
    }

    /**
     * Gets last key.
     *
     * @return the last key
     */
    public String getLastKey() {
        return lastKey;
    }

    /**
     * Sets last key.
     *
     * @param lastKey the last key
     */
    public void setLastKey(String lastKey) {
        this.lastKey = lastKey;
    }

    /**
     * Gets mov.
     *
     * @return the mov
     */
    public String getMov() {
        return mov;
    }

    /**
     * Sets mov.
     *
     * @param mov the mov
     */
    public void setMov(String mov) {
        this.mov = mov;
    }
}

