package lib.objects;

import lib.Enums.Movement;
import lib.Enums.ia_type;
import lib.utils.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

// Clase que encapsula la inteligencia artificial en sí, es decir, toma
// las decisiones de qué movimientos hacer
public class ia_controller {
    // Jugador
    private character player;
    // Personaje controlado por la IA
    private character enemy;
    // Caracteres de la IA durante las 4 rondas
    private ia_type mood[][] = new ia_type[4][];
    // Pesos de las variables de la IA durante las 4 rondas
    private Double weights[][] = new Double[4][5];
    // Ruleta rusa para decidir el movimiento
    private russian_roulette roulette;
    // Procesadores de la IA que alteran las probabilidades según la situación
    private ia_processor processor[];
    // Timer de control de referencia
    private Timer control;
    // Mapa de combos dado un movimiento
    private Map<Movement, String> movementsKeys;
    // Movimiento a ejecutar
    private String move = "";
    // Número de ronda
    private int round = 1;
    // Victorias del jugador
    private int pWins = 0;
    // Tiempo restante de la ronda
    private int time = 90;
    // Timers de referencia
    private long timeReference1 = System.currentTimeMillis();
    private long timeReference2 = System.currentTimeMillis();
    // Dificultad de la IA
    private ia_loader.dif dif = ia_loader.dif.EASY;
    // Probabilidad de bloqueo (al final no se usa)
    private double probBlock = 0.0;
    // Si se ha leído o no el movimiento
    private boolean read = false;

    // Contructor por defecto
    public  ia_controller(){}

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

    // Para la gestión de la IA
    public void stopIA(){
        control.stop();
    }

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
            }while (dis > 250 && processor[0].isAttack(m) && !processor[0].isSpecial(m));
            move = movementsKeys.get(m);
        }
    }

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
                if(!player.isRebalanced() &&  !enemy.isRebalanced()) {
                    player.rebalance(0.7, 1.0);
                    enemy.rebalance(1.3, 0.7);
                }
                break;
        }
    }

    // Getters y setters
    public String getMovement(){
        return move;
    }

    public character getPlayer() {
        return player;
    }

    public void setPlayer(character player) {
        this.player = player;
    }

    public character getEnemy() {
        return enemy;
    }

    public void setEnemy(character enemy) {
        this.enemy = enemy;
    }

    public ia_type[][] getMood() {
        return mood;
    }

    public void setMood(ia_type[][] mood) {
        this.mood = mood;
    }

    public russian_roulette getRoulette() {
        return roulette;
    }

    public void setRoulette(russian_roulette roulette) {
        this.roulette = roulette;
    }

    public ia_processor[] getProcessor() {
        return processor;
    }

    public void setProcessor(ia_processor processor[]) {
        this.processor = processor;
    }

    public Timer getControl() {
        return control;
    }

    public void setControl(Timer control) {
        this.control = control;
    }

    public Map<Movement, String> getMovementsKeys() {
        return movementsKeys;
    }

    public void setMovementsKeys(Map<Movement, String> movementsKeys) {
        this.movementsKeys = movementsKeys;
    }

    public String getMove() {
        read = true;
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Double[][] getWeights() {
        return weights;
    }

    public void setWeights(Double[][] weights) {
        this.weights = weights;
    }

    public int getpWins() {
        return pWins;
    }

    public void setpWins(int pWins) {
        this.pWins = pWins;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getTimeReference1() {
        return timeReference1;
    }

    public void setTimeReference1(long timeReference1) {
        this.timeReference1 = timeReference1;
    }

    public long getTimeReference2() {
        return timeReference2;
    }

    public void setTimeReference2(long timeReference2) {
        this.timeReference2 = timeReference2;
    }

    public ia_loader.dif getDif() {
        return dif;
    }
}
