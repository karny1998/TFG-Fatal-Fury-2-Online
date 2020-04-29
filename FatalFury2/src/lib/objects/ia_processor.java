package lib.objects;

import lib.Enums.Movement;
import lib.Enums.ia_type;

import java.util.Arrays;
import java.util.List;

public abstract class ia_processor {
    public ia_processor(){}

    public abstract void updateRoulette(russian_roulette roulette, ia_type mood[], Double weights[], int lvl, character player, character enemy, int time, int round, int playerWins);

    public boolean isAttack(Movement m){
        Movement array[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.GUARD_ATTACK, Movement.THROW,
                Movement.DESPERATION_MOVE, Movement.ATTACK_POKE, Movement.RANGED_ATTACK,
                Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isHighAttack(Movement m){
        Movement array[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.GUARD_ATTACK, Movement.THROW,
                Movement.DESPERATION_MOVE, Movement.ATTACK_POKE, Movement.RANGED_ATTACK,
                Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isLowAttack(Movement m){
        Movement array[] = {}; // NO ESTÁN METIDOS ATAQUES POR DEBAJO
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isSpecial(Movement m){
        Movement array[] = {Movement.DESPERATION_MOVE, Movement.SPECIAL_1,
                Movement.SPECIAL_2,Movement.SPECIAL_3,}; // NO ESTÁN METIDOS ATAQUES POR DEBAJO
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isRunAway(Movement m){
        Movement array[] = {Movement.WALKING, Movement.NORMAL_JUMP, Movement.GUARD_ATTACK};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isJump(Movement m){
        Movement array[] = {Movement.NORMAL_JUMP, Movement.JUMP_ROLL_RIGHT};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isDefensive(Movement m){
        Movement array[] = {Movement.WALKING, Movement.GUARD_ATTACK, Movement.CROUCHED_BLOCK};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isGettingCloser(Movement m){
        Movement array[] = {Movement.WALKING_BACK, Movement.JUMP_ROLL_RIGHT, Movement.CROUCHED_WALKING};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }

    public boolean isKncockback(Movement m){
        Movement array[] = {Movement.HARD_KNOCKBACK, Movement.SOFT_KNOCKBACK, Movement.MEDIUM_KNOCKBACK,
                Movement.CROUCHED_KNOCKBACK, Movement.STANDING_BLOCK_KNOCKBACK_SOFT, Movement.STANDING_BLOCK_KNOCKBACK_HARD,
                Movement.JUMP_KNOCKBACK};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }
}
