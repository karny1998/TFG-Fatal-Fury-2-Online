package lib.objects;

import lib.Enums.Playable_Character;
import lib.training.agent;
import lib.training.agent_controller;

import javax.swing.*;

/**
 * The type Enemy controller.
 */
// Clase que representa el control de un personaje por IA
public class enemy_controller extends character_controller{

    /**
     * The Agent ia.
     */
    protected boolean agentIa = false;

    /**
     * The Agent ia created.
     */
    protected boolean agentIaCreated = false;

    /**
     * The Training.
     */
    private boolean training = false;

    /**
     * The Agente.
     */
    private agent agente = null;

    /**
     * The Ia.
     */
// Controlador de la inteligencia artificial
    ia_controller ia = new ia_controller();

    /**
     * Instantiates a new Enemy controller.
     *
     * @param ch the ch
     * @param pN the p n
     */
    public enemy_controller(Playable_Character ch, int pN){
        super(ch, pN, 750, 290, 1);
        if(pN == 1){
            this.x = 500;
            this.player.setOrientation(-1);
            this.player.setX(500);
        }
    }

    /**
     * Instantiates a new Enemy controller.
     *
     * @param ch       the ch
     * @param pN       the p n
     * @param agentIa  the agent Ia
     * @param training the training
     */
    public enemy_controller(Playable_Character ch, int pN, boolean agentIa, boolean training){
        super(ch, pN, 750, 290, 1);
        this.agentIa = agentIa;
        this.training = training;
        if(pN == 1){
            this.x = 500;
            this.player.setOrientation(-1);
            this.player.setX(500);
        }
    }

    /**
     * Instantiates a new Enemy controller.
     *
     * @param ch    the ch
     * @param pN    the p n
     * @param rival the rival
     */
// Contructor que pide identificador de personaje, numero de personaje  y el rival
    public enemy_controller(Playable_Character ch, int pN, character rival){
        super(ch,pN,750,290, 1);
        this.rival = rival;
    }

    /**
     * Get animation screen object.
     *
     * @param pHurt the p hurt
     * @param eHurt the e hurt
     * @return the screen object
     */
// Obtener el frame del personaje teniendo en cuenta las colisiones de las hurtbox
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        // Si no se está esperando a que se terminen de mostrar los carteles de intro
        // se pide al personaje el frame correspondiente al movimiento decidido por la IA
        if(!standBy){
            return player.getFrame(ia.getMove(), pHurt, eHurt, rival.isAttacking());
        }
        return player.getFrame("", pHurt, eHurt, rival.isAttacking());
    }

    /**
     * Reset.
     */
    @Override
    // Resetea el personaje en base al número de jugador
    public void reset() {
        if(this.playerNum == 1) {
            reset(this.player.getCharac(),500,290, -1);
        }
        else{
            reset(this.player.getCharac(), 750, 290, 1);
        }
    }

    /**
     * Stop ia.
     */
    @Override
    // Para la inteligencia artificial
    public void stopIA(){ia.stopIA();}

    /**
     * Sets rival.
     *
     * @param rival the rival
     */
    @Override
    // Asigna el rival y define la IA
    public void setRival(character rival) {
        this.player.setRival(rival);
        this.rival = rival;
        if(agentIa && !agentIaCreated){
            agent_controller aux = new agent_controller(rival, this.player,ia_loader.dif.HARD, training);
            ia = aux;
            agente = aux.getAgente();
            agentIaCreated = true;
        }
        else if(agentIaCreated){
            ia.setPlayer(rival);
        }
        else {
            if(!agentIa){
                ia = new ia_controller(rival, this.player, ia_loader.dif.EASY);
            }
        }
    }

    /**
     * Gets ia.
     *
     * @return the ia
     */
    @Override
    // Devuelve el controlador de la IA
    public ia_controller getIa() {
        return ia;
    }

    /**
     * Stops the ia
     */
    @Override
    public void stop(){
        ia.stopIA();
    }

    /**
     * Is agent ia boolean.
     *
     * @return the boolean
     */
    public boolean isAgentIa() {
        return agentIa;
    }

    /**
     * Sets agent ia.
     *
     * @param agentIa the agent ia
     */
    public void setAgentIa(boolean agentIa) {
        this.agentIa = agentIa;
    }

    /**
     * Is training boolean.
     *
     * @return the boolean
     */
    public boolean isTraining() {
        return training;
    }

    /**
     * Sets training.
     *
     * @param training the training
     */
    public void setTraining(boolean training) {
        this.training = training;
    }

    /**
     * Gets agente.
     *
     * @return the agente
     */
    public agent getAgente() {
        return agente;
    }

    /**
     * Sets agente.
     *
     * @param agente the agente
     */
    public void setAgente(agent agente) {
        this.agente = agente;
    }

    /**
     * Sets ia.
     *
     * @param ia the ia
     */
    public void setIa(ia_controller ia) {
        this.ia = ia;
    }

    /**
     * Start stand by.
     */
    @Override
    public void startStandBy(){
        this.standBy = true;
        this.ia.setStandBy(true);
    }

    /**
     * End stand by.
     */
    @Override
    public void endStandBy(){
        this.standBy = false;
        this.ia.setStandBy(false);
    }
}
