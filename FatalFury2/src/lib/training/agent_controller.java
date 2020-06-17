package lib.training;

import lib.Enums.Movement;
import lib.Enums.ia_type;
import lib.objects.*;
import lib.utils.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.MarshalledObject;
import java.util.Map;

/**
 * The type Ia controller.
 */
public class agent_controller extends ia_controller{
    /**
     * The Control.
     */
// Timer de control de referencia
    private Timer ia_control;

    /**
     * The Ia training.
     */
    private Timer ia_training;

    /**
     * The Agente.
     */
    private agent agente;

    /**
     * The Action executed.
     */
    private boolean actionExecuted = false;

    /**
     * The Previous state.
     */
    private Movement previousState = Movement.STANDING;

    /**
     * The Training.
     */
    private boolean training = false;

    /**
     * The Move agent.
     */
    private String moveAgent = "";

    private long timeReferenceAgent = System.currentTimeMillis();

    private boolean actionInExecution = false;

    private Movement actionToExecute = Movement.STANDING;

    private Movement previousAction = Movement.STANDING;

    private int previousRound = 1;


    /**
     * Instantiates a new Ia controller.
     */
    public agent_controller(){}

    /**
     * Contructor que pide el personaje del jugador, el de la ia, y la dificultad de esta última
     *
     * @param p        the p
     * @param e        the e
     * @param lvl      the lvl
     * @param training the training
     */
    public  agent_controller(character p, character e, ia_loader.dif lvl, boolean training){
        super(p,e,lvl);
        this.movementsKeys = e.getMovementsKeys();
        this.training = training;
        hitBox pHurt = player.getHurtbox();
        hitBox eHurt = enemy.getHurtbox();
        int dis = 0;
        if (pHurt.getX() > eHurt.getX()){
            dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
        }
        else if(pHurt.getX() < eHurt.getX()){
            dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
        }

        state s = new state(100,100,player.getState(),dis,1,90,0,0);

        this.agente = new agent(s,0.99,0.75,0.25);

        if(training){
            ia_training = new Timer(1, new ActionListener() {
                @Override
                // Realiza las comprobaciones y gestión de la IA periódicamente
                public void actionPerformed(ActionEvent e) {
                    agente.train_Q_Learning();
                }
            });
            ia_training.start();
        }

        ia_control = new Timer(1, new ActionListener() {
            @Override
            // Realiza las comprobaciones y gestión de la IA periódicamente
            public void actionPerformed(ActionEvent e) {
                agent_gestion();
            }
        });
        ia_control.start();
        timeReference1 = System.currentTimeMillis();
    }

    /**
     * Para la gestión de la IA
     */
    public void stopIA(){
        if(training){
            ia_training.stop();
        }
        ia_control.stop();
    }

    /**
     * Ia gestion.
     */
// Realiza la gestión de la IA, es decir, analizar la situación, evaluar, y decidir un movimiento
    private void agent_gestion(){

        if(isStandBy()){
            previousAction = Movement.STANDING;
            previousState = Movement.STANDING;
            actionToExecute = agente.getActionToExecute();
            moveAgent = movementsKeys.get(actionToExecute);
            actionExecuted = false;
            actionInExecution = false;
            return;
        }

        hitBox pHurt = player.getHurtbox();
        hitBox eHurt = enemy.getHurtbox();
        // Distancia entre los personajes
        int dis = 0;
        if (pHurt.getX() > eHurt.getX()){
            dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
        }
        else if(pHurt.getX() < eHurt.getX()){
            dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
        }

        long actual = System.currentTimeMillis();

        if(!training) {
            state s = new state(enemy.getLife(), player.getLife(), player.getState(), dis, round, time, round - pWins - 1, pWins);
            Movement m = agente.selectAction(s);
            actionToExecute = agente.selectAction(s);
            moveAgent = movementsKeys.get(m);
            if(m == Movement.NONE){
                actionToExecute = enemy.getCombos().get(move);
                moveAgent = move;
            }
            return;
        }

        Movement actualState = enemy.getState();

        if(!actionInExecution && !actionExecuted){
            if(actionToExecute == Movement.THROW && dis >= 10){
                actionInExecution = true;
                actionExecuted = true;
            }
            else {
                actionInExecution = previousState != actualState && actualState == actionToExecute
                        || previousState == actionToExecute && actualState == actionToExecute
                        || previousState == Movement.STANDING && actualState == Movement.STANDING && Movement.STANDING == actionToExecute;
                if (actionInExecution && !(actionToExecute == Movement.WALKING || actionToExecute == Movement.WALKING_BACK)) {
                    moveAgent = "";
                }
            }
        }
        else if(!actionExecuted){
            if(actionToExecute == Movement.WALKING || actionToExecute == Movement.WALKING_BACK){
                actionExecuted = System.currentTimeMillis() - timeReferenceAgent > 300.0;
            }
            else {
                actionExecuted = (previousState != actualState && previousState == actionToExecute)
                        || actionToExecute == Movement.STANDING
                        || actionToExecute == Movement.THROW && dis >= 10 && player.getState() != Movement.THROWN_OUT;
            }
            if(actionExecuted){
                previousAction = actionToExecute;
            }
        }
        previousState = actualState;

        if(actionExecuted){
            actionExecuted = false;
            actionInExecution = false;

            state s = new state(enemy.getLife(), player.getLife(), player.getState(), dis, round, time, round - pWins - 1, pWins);
            agente.notifyResult(s);

            Movement m = agente.getActionToExecute();
            timeReferenceAgent = System.currentTimeMillis();

            if(m == Movement.NONE){
                actionToExecute = enemy.getCombos().get(move);
                moveAgent = move;
            }
            else {
                actionToExecute = m;
                moveAgent = movementsKeys.get(m);
            }
        }
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    @Override
    public String getMove() {
        read = true;
        return moveAgent;
    }

    /**
     * Gets ia control.
     *
     * @return the ia control
     */
    public Timer getIa_control() {
        return ia_control;
    }

    /**
     * Sets ia control.
     *
     * @param ia_control the ia control
     */
    public void setIa_control(Timer ia_control) {
        this.ia_control = ia_control;
    }

    /**
     * Gets ia training.
     *
     * @return the ia training
     */
    public Timer getIa_training() {
        return ia_training;
    }

    /**
     * Sets ia training.
     *
     * @param ia_training the ia training
     */
    public void setIa_training(Timer ia_training) {
        this.ia_training = ia_training;
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
     * Is action executed boolean.
     *
     * @return the boolean
     */
    public boolean isActionExecuted() {
        return actionExecuted;
    }

    /**
     * Sets action executed.
     *
     * @param actionExecuted the action executed
     */
    public void setActionExecuted(boolean actionExecuted) {
        this.actionExecuted = actionExecuted;
    }

    /**
     * Gets previous state.
     *
     * @return the previous state
     */
    public Movement getPreviousState() {
        return previousState;
    }

    /**
     * Sets previous state.
     *
     * @param previousState the previous state
     */
    public void setPreviousState(Movement previousState) {
        this.previousState = previousState;
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
     * Gets move agent.
     *
     * @return the move agent
     */
    public String getMoveAgent() {
        return moveAgent;
    }

    /**
     * Sets move agent.
     *
     * @param moveAgent the move agent
     */
    public void setMoveAgent(String moveAgent) {
        this.moveAgent = moveAgent;
    }
}

