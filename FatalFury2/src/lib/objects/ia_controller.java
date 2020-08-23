package lib.objects;

import lib.Enums.Movement;
import lib.Enums.ia_type;
import lib.utils.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * The type Ia controller.
 */
// Clase que encapsula la inteligencia artificial en sí, es decir, toma
// las decisiones de qué movimientos hacer
public class ia_controller {
    /**
     * The Player.
     */
// Jugador
    protected character player;
    /**
     * The Enemy.
     */
// Personaje controlado por la IA
    protected character enemy;
    /**
     * The Mood.
     */
// Caracteres de la IA durante las 4 rondas
    protected ia_type mood[][] = new ia_type[4][];
    /**
     * The Weights.
     */
// Pesos de las variables de la IA durante las 4 rondas
    protected Double weights[][] = new Double[4][5];
    /**
     * The Roulette.
     */
// Ruleta rusa para decidir el movimiento
    protected russian_roulette roulette;
    /**
     * The Processor.
     */
// Procesadores de la IA que alteran las probabilidades según la situación
    protected ia_processor processor[];
    /**
     * The Control.
     */
// Timer de control de referencia
    protected Timer control;
    /**
     * The Movements keys.
     */
// Mapa de combos dado un movimiento
    protected Map<Movement, String> movementsKeys;
    /**
     * The Move.
     */
// Movimiento a ejecutar
    protected String move = "";
    /**
     * The Round.
     */
// Número de ronda
    protected int round = 1;
    /**
     * The P wins.
     */
// Victorias del jugador
    protected int pWins = 0;
    /**
     * The Time.
     */
// Tiempo restante de la ronda
    protected int time = 90;
    /**
     * The Time reference 1.
     */
// Timers de referencia
    protected long timeReference1 = System.currentTimeMillis();
    /**
     * The Time reference 2.
     */
    protected long timeReference2 = System.currentTimeMillis();
    /**
     * The Dif.
     */
// Dificultad de la IA
    protected ia_loader.dif dif = ia_loader.dif.EASY;
    /**
     * The Prob block.
     */
// Probabilidad de bloqueo (al final no se usa)
    protected double probBlock = 0.0;
    /**
     * The Read.
     */
// Si se ha leído o no el movimiento
    protected boolean read = false;

    /**
     * The Stand by.
     */
    private boolean standBy = true;

    /**
     * Instantiates a new Ia controller.
     */
// Contructor por defecto
    public  ia_controller(){}

    /**
     * Instantiates a new Ia controller.
     *
     * @param p   the p
     * @param e   the e
     * @param lvl the lvl
     */
// Contructor que pide el personaje del jugador, el de la ia, y la dificultad de esta última
    public  ia_controller(character p, character e, ia_loader.dif lvl){
        this.dif = lvl;
        this.movementsKeys = e.getMovementsKeys();
        this.round = 1;
        this.player = p;
        this.enemy = e;
        // Carga la IA
        Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>> aux = ia_loader.loadIA(enemy.getCharac(), dif);
        this.processor = aux.getKey().getKey();
        this.roulette = aux.getKey().getValue();
        this.mood = aux.getValue().getKey();
        this.weights = aux.getValue().getValue();
        Timer ia_control = new Timer(1, new ActionListener() {
            @Override
            // Realiza las comprobaciones y gestión de la IA periódicamente
            public void actionPerformed(ActionEvent e) {
                ia_gestion();
            }
        });
        ia_control.start();
        this.control = ia_control;
        switch (lvl){
            case EASY:
                probBlock = 0.25;
                break;
            case NORMAL:
                probBlock = 0.50;
                break;
            case HARD:
                probBlock = 0.75;
                break;
            case VERY_HARD:
                probBlock = 1.0;
                break;
        }
    }

    /**
     * Stop ia.
     */
// Para la gestión de la IA
    public void stopIA(){
        control.stop();
    }

    /**
     * Ia gestion.
     */
// Realiza la gestión de la IA, es decir, analizar la situación, evaluar, y decidir un movimiento
    private void ia_gestion(){
        // Actualiza la ruleta rusa en base a los procesadores
        for(int i = 0; i < processor.length; ++i) {
            processor[i].updateRoulette(roulette, mood[round - 1], weights[round - 1], 4, player, enemy, time, round, pWins);
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
        // Comprobaciones de si ha pasado el tiempo suficiente
        long current = System.currentTimeMillis();
        boolean timeOk = current - timeReference2 > 300.0;
        boolean timeOk2 = current - timeReference2 > 500.0;
        if(timeOk){timeReference1 = current;}
        if(timeOk2){timeReference2 = current;}
        // Si ha pasado el tiempo de margen entre decisiones, o se está saltando y ha pasado el
        // tiempo suficiente para ejecutar un ataque en el aire
        if(timeOk && (enemy.endedMovement() || enemy.getState() == Movement.WALKING
                    || enemy.getState() == Movement.WALKING_BACK || enemy.getState() == Movement.CROUCHED_WALKING
                    || enemy.getState() == Movement.STANDING)
            || enemy.isJumping() && timeOk2){
            Movement m = Movement.STANDING;
            // Mientras se esté a una distancia a la que nunca se le vaya a dar con un ataque normal
            // se sigue girando la ruleta mientras sea ataque normal
            do{
                m = roulette.spinRoulette();
                if (enemy.isJumping() && (m == Movement.SOFT_PUNCH || m == Movement.HARD_PUNCH || m == Movement.HARD_KICK || m == Movement.SOFT_KICK)) {
                    break;
                }
            }while (dis > 175 && processor[0].isAttack(m) && !processor[0].isSpecial(m));
            move = movementsKeys.get(m);
        }
    }

    /**
     * Sets dif.
     *
     * @param dif the dif
     */
// Asigna una nueva dificultad y actualiza las cosas necesarias
    public void setDif(ia_loader.dif dif) {
        this.dif = dif;
        Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>> aux = ia_loader.loadIA(enemy.getCharac(), dif);
        this.processor = aux.getKey().getKey();
        this.roulette = aux.getKey().getValue();
        this.mood = aux.getValue().getKey();
        this.weights = aux.getValue().getValue();
        switch (dif){
            case EASY:
                probBlock = 0.25;
                break;
            case NORMAL:
                probBlock = 0.50;
                break;
            case HARD:
                probBlock = 0.75;
                break;
            case VERY_HARD:
                probBlock = 1.0;
                // Rebalancea los personajes en cuanto a daño y tiempos de ataques
                // para aumentar la dificultad
                /*if(!player.isRebalanced() &&  !enemy.isRebalanced()) {
                    player.rebalance(0.7, 1.0);
                    enemy.rebalance(1.3, 0.7);
                }*/
                break;
        }
    }

    /**
     * Get movement string.
     *
     * @return the string
     */
// Getters y setters
    public String getMovement(){
        return move;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public character getPlayer() {
        return player;
    }

    /**
     * Sets player.
     *
     * @param player the player
     */
    public void setPlayer(character player) {
        this.player = player;
    }

    /**
     * Gets enemy.
     *
     * @return the enemy
     */
    public character getEnemy() {
        return enemy;
    }

    /**
     * Sets enemy.
     *
     * @param enemy the enemy
     */
    public void setEnemy(character enemy) {
        this.enemy = enemy;
    }

    /**
     * Get mood ia type [ ] [ ].
     *
     * @return the ia type [ ] [ ]
     */
    public ia_type[][] getMood() {
        return mood;
    }

    /**
     * Sets mood.
     *
     * @param mood the mood
     */
    public void setMood(ia_type[][] mood) {
        this.mood = mood;
    }

    /**
     * Gets roulette.
     *
     * @return the roulette
     */
    public russian_roulette getRoulette() {
        return roulette;
    }

    /**
     * Sets roulette.
     *
     * @param roulette the roulette
     */
    public void setRoulette(russian_roulette roulette) {
        this.roulette = roulette;
    }

    /**
     * Get processor ia processor [ ].
     *
     * @return the ia processor [ ]
     */
    public ia_processor[] getProcessor() {
        return processor;
    }

    /**
     * Sets processor.
     *
     * @param processor the processor
     */
    public void setProcessor(ia_processor processor[]) {
        this.processor = processor;
    }

    /**
     * Gets control.
     *
     * @return the control
     */
    public Timer getControl() {
        return control;
    }

    /**
     * Sets control.
     *
     * @param control the control
     */
    public void setControl(Timer control) {
        this.control = control;
    }

    /**
     * Gets movements keys.
     *
     * @return the movements keys
     */
    public Map<Movement, String> getMovementsKeys() {
        return movementsKeys;
    }

    /**
     * Sets movements keys.
     *
     * @param movementsKeys the movements keys
     */
    public void setMovementsKeys(Map<Movement, String> movementsKeys) {
        this.movementsKeys = movementsKeys;
    }

    /**
     * Gets move.
     *
     * @return the move
     */
    public String getMove() {
        read = true;
        return move;
    }

    /**
     * Sets move.
     *
     * @param move the move
     */
    public void setMove(String move) {
        this.move = move;
    }

    /**
     * Gets round.
     *
     * @return the round
     */
    public int getRound() {
        return round;
    }

    /**
     * Sets round.
     *
     * @param round the round
     */
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * Get weights double [ ] [ ].
     *
     * @return the double [ ] [ ]
     */
    public Double[][] getWeights() {
        return weights;
    }

    /**
     * Sets weights.
     *
     * @param weights the weights
     */
    public void setWeights(Double[][] weights) {
        this.weights = weights;
    }

    /**
     * Gets wins.
     *
     * @return the wins
     */
    public int getpWins() {
        return pWins;
    }

    /**
     * Sets wins.
     *
     * @param pWins the p wins
     */
    public void setpWins(int pWins) {
        this.pWins = pWins;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Gets time reference 1.
     *
     * @return the time reference 1
     */
    public long getTimeReference1() {
        return timeReference1;
    }

    /**
     * Sets time reference 1.
     *
     * @param timeReference1 the time reference 1
     */
    public void setTimeReference1(long timeReference1) {
        this.timeReference1 = timeReference1;
    }

    /**
     * Gets time reference 2.
     *
     * @return the time reference 2
     */
    public long getTimeReference2() {
        return timeReference2;
    }

    /**
     * Sets time reference 2.
     *
     * @param timeReference2 the time reference 2
     */
    public void setTimeReference2(long timeReference2) {
        this.timeReference2 = timeReference2;
    }

    /**
     * Gets dif.
     *
     * @return the dif
     */
    public ia_loader.dif getDif() {
        return dif;
    }

    /**
     * Gets prob block.
     *
     * @return the prob block
     */
    public double getProbBlock() {
        return probBlock;
    }

    /**
     * Sets prob block.
     *
     * @param probBlock the prob block
     */
    public void setProbBlock(double probBlock) {
        this.probBlock = probBlock;
    }

    /**
     * Is read boolean.
     *
     * @return the boolean
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets read.
     *
     * @param read the read
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Is stand by boolean.
     *
     * @return the boolean
     */
    public boolean isStandBy() {
        return standBy;
    }

    /**
     * Sets stand by.
     *
     * @param standBy the stand by
     */
    public void setStandBy(boolean standBy) {
        this.standBy = standBy;
    }
}
