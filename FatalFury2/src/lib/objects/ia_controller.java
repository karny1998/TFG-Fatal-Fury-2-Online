package lib.objects;

import lib.Enums.Movement;
import lib.Enums.ia_type;
import lib.utils.Pair;

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
    private int round = 1;
    private int pWins = 0;
    private int time = 90;
    private long timeReference1 = System.currentTimeMillis();
    private long timeReference2 = System.currentTimeMillis();
    private ia_loader.dif dif = ia_loader.dif.EASY;
    private double probBlock = 0.0;
    private int counter = 0;
    private boolean read = false;

    public  ia_controller(){}

    public  ia_controller(character p, character e, ia_loader.dif lvl){
        this.dif = lvl;
        this.movementsKeys = e.getMovementsKeys();
        this.round = 1;
        this.player = p;
        this.enemy = e;
        Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>> aux = ia_loader.loadIA(enemy.getCharac(), dif);
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

    public void stopIA(){
        control.stop();
    }

    private void ia_gestion(){
        for(int i = 0; i < processor.length; ++i) {
            processor[i].updateRoulette(roulette, mood[round - 1], weights[round - 1], 4, player, enemy, time, round, pWins);
        }

        hitBox pHurt = player.getHurtbox();
        hitBox eHurt = enemy.getHurtbox();
        int dis = 0;
        if (pHurt.getX() > eHurt.getX()){
            dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
        }
        else if(pHurt.getX() < eHurt.getX()){
            dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
        }

        long current = System.currentTimeMillis();
        boolean timeOk = current - timeReference2 > 300.0;
        boolean timeOk2 = current - timeReference2 > 500.0;
        if(timeOk){timeReference1 = current;}
        if(timeOk2){timeReference2 = current;}
        if(enemy.endedMovement() && timeOk2
                || timeOk && enemy.getState() == Movement.WALKING
                || timeOk && enemy.getState() == Movement.WALKING_BACK
                || timeOk && enemy.getState() == Movement.CROUCHED_WALKING
                || timeOk2 && enemy.getState() == Movement.STANDING
                || enemy.isJumping() && timeOk2){
            Movement m = Movement.STANDING;
            do{
                m = roulette.spinRoulette();
                if (enemy.isJumping() && (m == Movement.SOFT_PUNCH || m == Movement.HARD_PUNCH || m == Movement.HARD_KICK || m == Movement.SOFT_KICK)) {
                    break;
                }
            }while (dis > 250 && processor[0].isAttack(m) && !processor[0].isSpecial(m));
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
                if(!player.isRebalanced() &&  !enemy.isRebalanced()) {
                    player.rebalance(0.7, 1.0);
                    enemy.rebalance(1.3, 0.7);
                }
                break;
        }
    }
}
