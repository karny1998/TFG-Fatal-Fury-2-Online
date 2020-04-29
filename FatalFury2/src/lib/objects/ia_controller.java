package lib.objects;

import javafx.util.Pair;
import lib.Enums.Movement;
import lib.Enums.ia_type;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ia_controller {
    private character player;
    private character enemy;
    private ia_type mood[][] = new ia_type[4][];
    private Double weights[][] = new Double[4][5];
    private russian_roulette roulette;
    private ia_processor processor[];
    private Timer control;
    private Map<Movement, String> movementsKeys;
    private String move = "";
    private int lvl = 1;
    private int round = 1;
    private int pWins = 0;
    private int time = 90;
    private long timeReference = System.currentTimeMillis();

    public  ia_controller(){}

    public  ia_controller(character p, character e, int lvl){
        this.movementsKeys = e.getMovementsKeys();
        this.round = 1;
        this.lvl = lvl;
        this.player = p;
        this.enemy = e;
        Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>> aux = ia_loader.loadIA(enemy.getCharac());
        this.processor = aux.getKey().getKey();
        this.roulette = aux.getKey().getValue();
        this.mood = aux.getValue().getKey();
        this.weights = aux.getValue().getValue();
        Timer ia_control = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ia_gestion();
            }
        });
        ia_control.start();
        this.control = ia_control;
    }

    public void stopIA(){
        control.stop();
    }

    private void ia_gestion(){
        for(int i = 0; i < processor.length; ++i) {
            processor[i].updateRoulette(roulette, mood[round - 1], weights[round - 1], lvl, player, enemy, time, round, pWins);
        }
        long current = System.currentTimeMillis();
        boolean timeOk = current - timeReference > 200.0;
        boolean timeOk2 = current - timeReference > 800.0;
        if(timeOk){timeReference = current;}
        if(enemy.endedMovement() || timeOk && enemy.getState() == Movement.WALKING || timeOk && enemy.getState() == Movement.WALKING_BACK
                || timeOk && enemy.getState() == Movement.CROUCHED_WALKING || enemy.isJumping() && timeOk2
                || !enemy.getMovements().get(enemy.getState()).hasEnd()){
            Movement m = roulette.spinRoulette();
            move = movementsKeys.get(m);
        }
    }

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
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
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

    public long getTimeReference() {
        return timeReference;
    }

    public void setTimeReference(long timeReference) {
        this.timeReference = timeReference;
    }
}
