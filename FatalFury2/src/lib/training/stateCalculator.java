package lib.training;

import lib.Enums.Movement;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase auxiliar para el calculo del número de estado
 */
public class stateCalculator {
    /**
     * The constant lifeScale.
     */
    private static int lifeScale = 5;
    /**
     * The constant distanceScale.
     */
    private static int distanceScale = 100;
    /**
     * The constant idMov.
     */
    private static Map<Movement, Integer> idMov = new HashMap<>();
    /**
     * The constant idAction.
     */
    private static Map<Movement, Integer> idAction = new HashMap<>();
    /**
     * The constant max.
     */
    private static int max = 0;
    /**
     * The constant nActions.
     */
    private static int nActions = 0;

    /**
     * Initialize the idMov map and max.
     */
    public static void initialize(){
        Movement aux[] = new Movement[]{Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH, Movement.HARD_KICK, Movement.STANDING_BLOCK, Movement.CROUCHED_BLOCK,
                Movement.NORMAL_JUMP, Movement.JUMP_ROLL_RIGHT, Movement.CROUCHED_WALKING, Movement.THROW, Movement.STANDING, Movement.WALKING, Movement.WALKING_BACK,
                Movement.CROUCH, Movement.UNDO_CROUCH, Movement.ATTACK_POKE, Movement.THROWN_OUT,
                Movement.SOFT_KNOCKBACK, Movement.MEDIUM_KNOCKBACK, Movement.HARD_KNOCKBACK, Movement.CROUCHED_KNOCKBACK, Movement.STANDING_BLOCK_KNOCKBACK_SOFT,
                Movement.STANDING_BLOCK_KNOCKBACK_HARD, Movement.JUMP_KNOCKBACK, Movement.JUMP_PUNCH_DOWN, Movement.JUMP_ROLL_PUNCH_DOWN, Movement.JUMP_FALL, Movement.JUMP_ROLL_FALL,
                Movement.CROUCHING_HARD_PUNCH, Movement.CROUCHING_SOFT_PUNCH, Movement.HOOK, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK, Movement.DASH, Movement.CROUCHED_BLOCK_KNOCKBACK,
                Movement.CHARGED_PUNCH_A, Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK_DOWN, Movement.JUMP_KICK, Movement.JUMP_ROLL_HARD_PUNCH_DOWN, Movement.JUMP_HARD_PUNCH_DOWN};
        Movement aux2[] = new Movement[]{Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D, Movement.JUMP_ROLL_LEFT, Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C};
        Movement aux3[] = new Movement[]{Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH, Movement.HARD_KICK, Movement.STANDING_BLOCK, Movement.CROUCHED_BLOCK,
                Movement.NORMAL_JUMP, Movement.JUMP_ROLL_RIGHT, Movement.CROUCHED_WALKING, Movement.THROW, Movement.STANDING, Movement.WALKING, Movement.WALKING_BACK,
                Movement.CROUCH, Movement.CROUCHING_HARD_PUNCH, Movement.CROUCHING_SOFT_PUNCH, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK, Movement.DASH,
                Movement.CHARGED_PUNCH_A, Movement.REVERSE_KICK_B,  Movement.JUMP_ROLL_LEFT, Movement.SPIN_PUNCH_C};
        for(int i = 0; i < aux3.length; ++i){
            idAction.put(aux3[i], i);
        }
        nActions = idAction.size();

        for(int i = 0; i < aux.length; ++i){
            idMov.put(aux[i], i);
        }
        int i = idMov.size();
        idMov.put(aux2[0], i);
        idMov.put(aux2[1], i);
        idMov.put(aux2[2], i+1);
        idMov.put(aux2[3], i+1);
        idMov.put(aux2[4], i+2);
        idMov.put(aux2[5], i+2);

        max = (2 + 200/lifeScale) * idMov.size() * (1 + 700/distanceScale) * 4;
    }

    /**
     * Calculate num state int given a state.
     *
     * @param s the s
     * @return the int
     */
    public static int calculateNumState(state s){
        int l = s.getLife()/lifeScale;
        int pL = s.getPlayerLife()/lifeScale;
        int d = s.getDis()/distanceScale;
        int r = s.getRound() - 1;
        int sPid = idMov.get(s.getPlayerState());

        int aux1 = idMov.size();
        int aux2 = (1 + 100/lifeScale) * aux1;
        int aux3 = (1 + 100/lifeScale) * aux2;
        int aux4 = (1 + 700/distanceScale) * aux3;

        int val = r * aux4 + d * aux3 + pL * aux2 + l * aux1 + sPid;

        if (val > max){
            System.out.println("Out of bounds (stateCalculator)");
            return 0;
        }

        return val;
    }

    /**
     * Return the id of the action "m" int.
     *
     * @param m the m
     * @return the int
     */
    public  static int idAction(Movement m){
        return idAction.get(m);
    }

    /**
     * Gets max.
     *
     * @return the max
     */
    public static int getMax() {
        return max;
    }

    /**
     * Gets actions.
     *
     * @return the actions
     */
    public static int getnActions() {
        return nActions;
    }
}
