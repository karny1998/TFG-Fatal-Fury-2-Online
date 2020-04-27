package lib.objects;

import javafx.util.Pair;
import lib.Enums.Movement;
import lib.Enums.moods;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ia_controller {
    private character player;
    private character enemy;
    private moods mood[];
    private russian_roulette roulette;
    private ia_processor processor;
    private Timer control;
    private Map<Movement, String> movementsKeys;
    private String move = "";
    private int lvl = 1;
    private int round = 1;
    private long timeReference = System.currentTimeMillis();

    public  ia_controller(){}

    public  ia_controller(character p, character e, int lvl){
        this.movementsKeys = e.getMovementsKeys();
        this.round = 1;
        this.lvl = lvl;
        moods mod[] = new moods[4];
        this.player = p;
        this.enemy = e;
        Pair<ia_processor, russian_roulette> aux = ia_loader.loadIA(enemy.getCharac(), mod);
        this.processor = aux.getKey();
        this.roulette = aux.getValue();
        this.mood = mod;
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
        processor.updateRoulette(roulette, lvl, player, enemy, 90, round);
        long current = System.currentTimeMillis();
        boolean timeOk = current - timeReference > 200.0;
        if(timeOk){timeReference = current;}
        if(enemy.endedMovement() || timeOk && enemy.getState() == Movement.WALKING || timeOk && enemy.getState() == Movement.WALKING_BACK
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

    public moods[] getMood() {
        return mood;
    }

    public void setMood(moods[] mood) {
        this.mood = mood;
    }

    public russian_roulette getRoulette() {
        return roulette;
    }

    public void setRoulette(russian_roulette roulette) {
        this.roulette = roulette;
    }

    public ia_processor getProcessor() {
        return processor;
    }

    public void setProcessor(ia_processor processor) {
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
}
