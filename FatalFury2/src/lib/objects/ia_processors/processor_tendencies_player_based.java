package lib.objects.ia_processors;


import lib.Enums.Movement;
import lib.Enums.ia_type;
import lib.objects.character;
import lib.objects.ia_processor;
import lib.objects.russian_roulette;
import lib.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Processor tendencies player based.
 */
// Procesador de la IA que actualiza las probabilidades en base a los movimientos usados por el
// usuario hasta el momento
public class processor_tendencies_player_based extends ia_processor {
    /**
     * The Last size.
     */
// último tamaño de la lista de movimientos
    private int lastSize = 0;

    /**
     * Instantiates a new Processor tendencies player based.
     */
// Contructor por defecto
    public  processor_tendencies_player_based(){super();}

    /**
     * Update probs.
     *
     * @param roulette     the roulette
     * @param type         the type
     * @param pHighAttacks the p high attacks
     * @param pLowAttacks  the p low attacks
     * @param pJump        the p jump
     * @param pSpecial     the p special
     * @param pGetGloser   the p get gloser
     * @param pRunAway     the p run away
     */
// Actualiza la ruleta rusa en base al caracter de la ia, y las probabilidades de los tipos de movimientos
    private void updateProbs(russian_roulette roulette, ia_type type, double pHighAttacks, double pLowAttacks,
                             double pJump, double pSpecial, double pGetGloser, double pRunAway){
        if(roulette.isBasic()){
            List<Pair<Double, Movement>> aux = roulette.getBasicSelectionsOriginal();
            List<Pair<Double, Movement>> list = roulette.getBasicSelections();
            list.clear();
            for(int i = 0; i < aux.size(); ++i){
                Pair<Double, Movement> auxPair = aux.get(i);
                double mul = 1.0;
                if(isHighAttack(auxPair.getValue())){
                    mul += pJump;
                    mul += (pHighAttacks - pLowAttacks);
                }
                else if(isLowAttack(auxPair.getValue())){
                    mul += (pLowAttacks - pHighAttacks);
                }
                else if(isJump(auxPair.getValue())){
                    mul += (pJump + pSpecial);
                }
                else if(isGettingCloser(auxPair.getValue())){
                    if(type == ia_type.AGRESSIVE){
                        mul += (pRunAway - pGetGloser);
                    }
                    else if(type == ia_type.DEFENSIVE){
                        mul -= Math.abs(pGetGloser - pRunAway);
                    }
                }
                else if(isRunAway(auxPair.getValue())){
                    if(type == ia_type.AGRESSIVE){
                        mul -= (pRunAway - pGetGloser);
                    }
                    else if(type == ia_type.DEFENSIVE){
                        mul += Math.abs(pGetGloser - pRunAway);
                    }
                }
                list.add(i, new Pair<>(auxPair.getKey()*mul, auxPair.getValue()));
            }
        }
        else{
            List<Pair<Double, russian_roulette>> aux = roulette.getComplexSelectionOriginal();
            List<Pair<Double, russian_roulette>> list = roulette.getComplexSelection();
            list.clear();
            for(int i = 0; i < aux.size(); ++i){
                Pair<Double, russian_roulette> auxPair = aux.get(i);
                if(auxPair.getValue().getCategory() == Movement.GETTING_CLOSER){
                    if(type == ia_type.AGRESSIVE){
                        list.add(i, new Pair<Double, russian_roulette>(auxPair.getKey()*(1.0 + Math.max(pGetGloser, pRunAway)), auxPair.getValue().clone()));
                    }
                    else if(type == ia_type.DEFENSIVE){
                        list.add(i, new Pair<Double, russian_roulette>(auxPair.getKey()*(1.0 - Math.max(pGetGloser, pRunAway)), auxPair.getValue().clone()));
                    }
                }
                else if(auxPair.getValue().getCategory() == Movement.RUN_AWAY){
                    if(type == ia_type.AGRESSIVE){
                        list.add(i, new Pair<Double, russian_roulette>(auxPair.getKey()*(1.0 - Math.max(pGetGloser, pRunAway)), auxPair.getValue().clone()));
                    }
                    else if(type == ia_type.DEFENSIVE){
                        list.add(i, new Pair<Double, russian_roulette>(auxPair.getKey()*(1.0 + Math.max(pGetGloser, pRunAway)), auxPair.getValue().clone()));
                    }
                }
                else{
                    list.add(i, new Pair<Double, russian_roulette>(auxPair.getKey(), auxPair.getValue().clone()));
                }
                updateProbs(list.get(i).getValue(),type,pHighAttacks, pLowAttacks, pSpecial, pJump, pGetGloser, pRunAway);
            }
        }
    }

    /**
     * Update roulette.
     *
     * @param roulette   the roulette
     * @param type       the type
     * @param weights    the weights
     * @param lvl        the lvl
     * @param player     the player
     * @param enemy      the enemy
     * @param time       the time
     * @param round      the round
     * @param playerWins the player wins
     */
    @Override
    // Actualiza la ruleta en base al tipo de IA y los movimientos ejecutados por el usuario hasta el momento
    public void updateRoulette(russian_roulette roulette, ia_type type[], Double weights[], int lvl, character player,
                               character enemy, int time, int round, int playerWins) {
        // Si es la primera ronda se omite el procesado por ser perjudicial
        if(round == 1){
            return;
        }
        // Si no ha ejecutado ningún nuevo movimiento se omite el procesado
        if(lastSize == player.getExecutedMoves().size()){return;}
        int ind = -1;
        // Se calcula el indice del caracter que le corresponde a la ia en base a type[] y la vida del personaje
        for(int i = type.length-1; i > -1 && ind == -1; --i){
            if((i+1)*100.0/(double)type.length >= (double)enemy.getLife() && i*100.0/(double)type.length <= (double)enemy.getLife()){
                ind = type.length - 1 - i;
            }
        }
        ia_type iat = type[ind];

        lastSize = player.getExecutedMoves().size();
        List<Movement> playerMoves = new ArrayList<>();
        // Movimientos realizador por el jugador omitiendo quedarse de pie y knockback
        for(int i = 0; i < player.getExecutedMoves().size(); ++i){
            Movement m = player.getExecutedMoves().get(i);
            if(m != Movement.STANDING && !isKncockback(m)){
                playerMoves.add(m);
            }
        }

        if(playerMoves.size() == 0){return;}

        // Haya las variables a valorar (probabilidades de tipos de movimientos)
        double pHighAttacks = 0.0, pLowAttacks = 0.0, pJump = 0.0, pSpecial = 0.0,
                pGetGloser = 0.0, pRunAway = 0.0;
        for(int i = 0; i < playerMoves.size(); ++i){
            Movement m = playerMoves.get(i);
            if(isHighAttack(m)){
                pHighAttacks += 1.0;
            }
            else if(isLowAttack(m)){
                pLowAttacks += 1.0;
            }
            else if(isJump(m)){
                pJump += 1.0;
            }
            else if(isSpecial(m)){
                pSpecial += 1.0;
            }
            else if(isGettingCloser(m)){
                pGetGloser += 1.0;
            }
            else if(isRunAway(m)){
                pRunAway += 1.0;
            }
        }
        double attacks = pHighAttacks + pLowAttacks;
        double total = attacks + pJump + pGetGloser + pRunAway;
        double displacement = pGetGloser + pRunAway;
        if(attacks > 0) {
            pHighAttacks /= attacks;
            pLowAttacks /= attacks;
            pSpecial /= attacks;
        }
        if(total > 0) {
            pJump /= total;
        }
        if(displacement > 0) {
            pGetGloser /= displacement;
            pRunAway /= displacement;
        }
        // Actualiza las probabilidades en base al potenciamiento y el caracter correspondiente
        updateProbs(roulette,iat,pHighAttacks, pLowAttacks, pSpecial, pJump, pGetGloser, pRunAway);
        // Normaliza de nuevo las probabilidades
        roulette.fillRoulette();
    }
}
