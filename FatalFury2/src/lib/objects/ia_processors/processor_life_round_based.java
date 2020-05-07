package lib.objects.ia_processors;

import lib.utils.Pair;
import lib.Enums.Movement;
import lib.Enums.ia_type;
import lib.objects.character;
import lib.objects.ia_processor;
import lib.objects.russian_roulette;

import java.util.List;

public class processor_life_round_based extends ia_processor {
    private int lastLvl = 0;
    private int lastPlayerLife = 0;
    private int lastIaLife = 0;
    private int lastTime = 0;
    private int lastRound = 0;
    private int lastWins = -1;

    public processor_life_round_based(){super();}

    private void updateProbs(russian_roulette roulette, ia_type type, double empowerment){
        if(roulette.isBasic()){
            double sign = 1.0;
            if(type == ia_type.DEFENSIVE){
                sign = -1.0;
            }
            List<Pair<Double, Movement>> aux = roulette.getBasicSelectionsOriginal();
            List<Pair<Double, Movement>> list = roulette.getBasicSelections();
            list.clear();
            for(int i = 0; i < aux.size(); ++i){
                Pair<Double, Movement> auxPair = aux.get(i);
                if(isAttack(auxPair.getValue()) || isGettingCloser(auxPair.getValue())){
                    list.add(i, new Pair<>(auxPair.getKey()*(1.0 + sign*empowerment), auxPair.getValue()));
                }
                else{
                    list.add(i, new Pair<>(auxPair.getKey()*(1.0 -sign*empowerment), auxPair.getValue()));
                }
            }
        }
        else{
            double sign = 1.0;
            if(type == ia_type.DEFENSIVE){
                sign = -1.0;
            }
            List<Pair<Double, russian_roulette>> aux = roulette.getComplexSelectionOriginal();
            List<Pair<Double, russian_roulette>> list = roulette.getComplexSelection();
            list.clear();
            for(int i = 0; i < aux.size(); ++i){
                Pair<Double, russian_roulette> auxPair = aux.get(i);
                if(auxPair.getValue().getCategory() == Movement.ATTACK
                        || auxPair.getValue().getCategory() == Movement.GETTING_CLOSER){
                    list.add(i, new Pair<>(auxPair.getKey()*(1.0 + sign*empowerment), auxPair.getValue()));
                }
                else if(auxPair.getValue().getCategory() == Movement.DEFENSE
                        || auxPair.getValue().getCategory() == Movement.RUN_AWAY){
                    list.add(i, new Pair<>(auxPair.getKey()*(1.0 -sign*empowerment), auxPair.getValue()));
                }
            }
        }
    }

    @Override
    public void updateRoulette(russian_roulette roulette, ia_type type[], Double weights[], int lvl, character player,
                               character enemy, int time, int round, int playerWins) {
        int ind = -1;
        for(int i = type.length-1; i > -1 && ind == -1; --i){
            if((i+1)*100.0/(double)type.length >= (double)enemy.getLife() && i*100.0/(double)type.length <= (double)enemy.getLife()){
                ind = type.length - 1 - i;
            }
        }
        ia_type iat = type[ind];
        if(lvl == lastLvl && player.getLife() == lastPlayerLife && lastTime == time
                && enemy.getLife() == lastIaLife && round == lastRound && lastWins == playerWins
            || iat == ia_type.BALANCED){
            return;
        }

        lastLvl = lvl;
        lastPlayerLife = player.getLife();
        lastIaLife = enemy.getLife();
        lastRound = round;
        lastTime = time;
        lastWins = playerWins;

        double varTime = (double)time / 90.0;
        double varPlayerLife = player.getLife() / 100.0;
        double varEnemyLife = enemy.getLife() / 100.0;
        double varRound = round / 4.0;
        double varWins = (double)playerWins/(double)round;

        if(weights[0] < 0.0){
            varTime = 1.0 - varTime;
        }
        else if(weights[1] < 0.0){
            varPlayerLife = 1.0 - varPlayerLife;
        }
        else if(weights[2] < 0.0){
            varEnemyLife = 1.0 - varEnemyLife;
        }
        else if(weights[3] < 0.0){
            varRound = 1.0 - varRound;
        }
        else if(weights[4] < 0.0){
            varWins = 1.0 - varWins;
        }
        double empowerment = (Math.abs(weights[0]) * varTime + Math.abs(weights[1]) * varPlayerLife
                + Math.abs(weights[2]) * varEnemyLife + Math.abs(weights[3]) * varRound
                + Math.abs(weights[4]) * varWins) / (4.0/(double)lvl);

        updateProbs(roulette, iat, empowerment);
        roulette.fillRoulette();
    }
}
