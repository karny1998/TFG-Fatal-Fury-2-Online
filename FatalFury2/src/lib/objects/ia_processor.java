package lib.objects;

import lib.Enums.Movement;
import lib.Enums.ia_type;

import java.util.Arrays;
import java.util.List;

/**
 * The type Ia processor.
 */
public abstract class ia_processor {
    /**
     * The Attacks.
     */
// Listas con los movimientos clasificados
    private List<Movement> attacks, /**
     * The High attacks.
     */
    highAttacks, /**
     * The Low attacks.
     */
    lowAttacks, /**
     * The Special attacks.
     */
    specialAttacks, /**
     * The Run away.
     */
    runAway;
    /**
     * The Jumps.
     */
    private List<Movement> jumps, /**
     * The Defenses.
     */
    defenses, /**
     * The Get closer.
     */
    getCloser, /**
     * The Knockbacks.
     */
    knockbacks;

    /**
     * Instantiates a new Ia processor.
     */
    public ia_processor(){
        // Se inicializan todas las listas
        Movement array[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.THROW, Movement.ATTACK_POKE,
                Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN, Movement.CHARGED_PUNCH_A,
                Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK, Movement.JUMP_KICK_DOWN, Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK,
                Movement.CROUCHING_SOFT_PUNCH, Movement.CROUCHING_HARD_PUNCH};
        attacks = Arrays.asList(array);
        Movement array2[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.THROW, Movement.ATTACK_POKE,
                Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN, Movement.CHARGED_PUNCH_A,
                Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK, Movement.JUMP_KICK_DOWN, Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C};
        highAttacks = Arrays.asList(array2);
        Movement array3[] = {Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK,
                Movement.CROUCHING_SOFT_PUNCH, Movement.CROUCHING_HARD_PUNCH};
        lowAttacks = Arrays.asList(array3);
        Movement array4[] = {Movement.CHARGED_PUNCH_A, Movement.CHARGED_PUNCH_C,
                Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C,};
        specialAttacks = Arrays.asList(array4);
        Movement array5[] = {Movement.WALKING, Movement.NORMAL_JUMP, Movement.DASH, Movement.JUMP_ROLL_LEFT};
        runAway = Arrays.asList(array5);
        Movement array6[] = {Movement.NORMAL_JUMP, Movement.JUMP_ROLL_RIGHT, Movement.JUMP_ROLL_LEFT};
        jumps = Arrays.asList(array6);
        Movement array7[] = {Movement.WALKING,Movement.CROUCHED_BLOCK};
        defenses = Arrays.asList(array7);
        Movement array8[] = {Movement.WALKING_BACK, Movement.JUMP_ROLL_RIGHT, Movement.CROUCHED_WALKING};
        getCloser = Arrays.asList(array8);
        Movement array9[] = {Movement.HARD_KNOCKBACK, Movement.SOFT_KNOCKBACK, Movement.MEDIUM_KNOCKBACK,
                Movement.CROUCHED_KNOCKBACK, Movement.STANDING_BLOCK_KNOCKBACK_SOFT, Movement.STANDING_BLOCK_KNOCKBACK_HARD,
                Movement.JUMP_KNOCKBACK, Movement.CROUCHED_BLOCK_KNOCKBACK};
        knockbacks = Arrays.asList(array9);
    }

    /**
     * Update roulette.
     *
     * @param roulette   the roulette
     * @param mood       the mood
     * @param weights    the weights
     * @param lvl        the lvl
     * @param player     the player
     * @param enemy      the enemy
     * @param time       the time
     * @param round      the round
     * @param playerWins the player wins
     */
// Actualiza las probabilidades de la ruleta rusa en base al caracter, los pesos...
    public abstract void updateRoulette(russian_roulette roulette, ia_type mood[], Double weights[], int lvl, character player, character enemy, int time, int round, int playerWins);

    /**
     * Is attack boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un ataque
    public boolean isAttack(Movement m){
        return attacks.contains(m);
    }

    /**
     * Is high attack boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un ataque alto
    public boolean isHighAttack(Movement m){
        return highAttacks.contains(m);
    }

    /**
     * Is low attack boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un bajo
    public boolean isLowAttack(Movement m){
        return lowAttacks.contains(m);
    }

    /**
     * Is special boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un ataque especial
    public boolean isSpecial(Movement m){
        return specialAttacks.contains(m);
    }

    /**
     * Is run away boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un movimiento de huida
    public boolean isRunAway(Movement m){
        return runAway.contains(m);
    }

    /**
     * Is jump boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un salto
    public boolean isJump(Movement m){
        return jumps.contains(m);
    }

    /**
     * Is defensive boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un movimiento defensivo
    public boolean isDefensive(Movement m){
        return defenses.contains(m);
    }

    /**
     * Is getting closer boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un movimiento de acercarse
    public boolean isGettingCloser(Movement m){
        return getCloser.contains(m);
    }

    /**
     * Is kncockback boolean.
     *
     * @param m the m
     * @return the boolean
     */
// True si el movimiento es un knockback
    public boolean isKncockback(Movement m){
        return knockbacks.contains(m);
    }
}
