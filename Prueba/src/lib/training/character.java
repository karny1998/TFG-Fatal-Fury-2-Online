package lib.training;

import lib.Enums.Movement;
import java.util.*;

/**
 * The type Character.
 */
// Clase que representa un personaje con sus correspondientes movimientos
public class character {
    /**
     * Is attack boolean.
     *
     * @param m the m
     * @return the boolean
     */
    public static boolean isAttack(Movement m){
        Movement array[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.THROW, Movement.ATTACK_POKE,
                Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN, Movement.CHARGED_PUNCH_A,
                Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK, Movement.JUMP_KICK_DOWN, Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK,
                Movement.CROUCHING_SOFT_PUNCH, Movement.CROUCHING_HARD_PUNCH};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
    }
}
