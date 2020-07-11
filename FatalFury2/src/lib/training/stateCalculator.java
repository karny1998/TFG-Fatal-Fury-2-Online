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
     * The inverse of idAction
     */
    private static Map<Integer, Movement> actionByID = new HashMap<>();
    /**
     * The constant max.
     */
    private static int max = 0;
    /**
     * The constant nActions.
     */
    private static int nActions = 0;

    private static boolean simple = true;

    /**
     * Initialize the idMov map and max.
     */
    public static void initialize(){
        Movement aux[] = new Movement[]{Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH, Movement.HARD_KICK, Movement.STANDING_BLOCK, Movement.CROUCHED_BLOCK,
                Movement.NORMAL_JUMP, Movement.JUMP_ROLL_RIGHT, Movement.CROUCHED_WALKING, Movement.THROW, Movement.STANDING, Movement.WALKING, Movement.WALKING_BACK,
                Movement.CROUCH, Movement.UNDO_CROUCH, Movement.ATTACK_POKE, Movement.THROWN_OUT,Movement.JUMP_ROLL_LEFT,
                Movement.SOFT_KNOCKBACK, Movement.MEDIUM_KNOCKBACK, Movement.HARD_KNOCKBACK, Movement.CROUCHED_KNOCKBACK, Movement.STANDING_BLOCK_KNOCKBACK_SOFT,
                Movement.STANDING_BLOCK_KNOCKBACK_HARD, Movement.JUMP_KNOCKBACK, Movement.JUMP_PUNCH_DOWN, Movement.JUMP_ROLL_PUNCH_DOWN, Movement.JUMP_FALL, Movement.JUMP_ROLL_FALL,
                Movement.CROUCHING_HARD_PUNCH, Movement.CROUCHING_SOFT_PUNCH, Movement.HOOK, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK, Movement.DASH, Movement.CROUCHED_BLOCK_KNOCKBACK,
                 Movement.JUMP_KICK_DOWN, Movement.JUMP_KICK, Movement.JUMP_ROLL_HARD_PUNCH_DOWN, Movement.JUMP_HARD_PUNCH_DOWN, Movement.VICTORY_ROUND, Movement.VICTORY_FIGHT, Movement.DEFEAT};
        Movement aux2[] = new Movement[]{Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D, Movement.CHARGED_PUNCH_A, Movement.CHARGED_PUNCH_C, Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C};
        Movement aux3[] = new Movement[]{Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH, Movement.HARD_KICK, Movement.CROUCHED_BLOCK,
                Movement.NORMAL_JUMP, Movement.JUMP_ROLL_RIGHT, Movement.CROUCHED_WALKING, Movement.THROW, Movement.STANDING, Movement.WALKING, Movement.WALKING_BACK,
                Movement.CROUCH, Movement.CROUCHING_HARD_PUNCH, Movement.CROUCHING_SOFT_PUNCH, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK, Movement.DASH,
                Movement.CHARGED_PUNCH_A, Movement.REVERSE_KICK_B,  Movement.JUMP_ROLL_LEFT, Movement.SPIN_PUNCH_C};
        for(int i = 0; i < aux3.length; ++i){
            idAction.put(aux3[i], i);
            actionByID.put(i, aux3[i]);
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

        if(!simple) {
            max = (1 + 100 / lifeScale) * (1 + 100 / lifeScale) * idMov.size() * (1 + 700 / distanceScale) * 4 * 2 * 2;
        }
        else{
            max = 2 * 3 * 3 * idMov.size() * 3;
        }
    }

    /**
     * Calculate num state int given a state.
     *
     * @param s the s
     * @return the int
     */
    public static int calculateNumState(state s){
        int r = s.getRound() - 1;
        int w = s.getPlayerVictories();
        int j = 0;
        if(s.isJumping()){j = 1;}
        int sPid = idMov.get(s.getPlayerState());

        int val = 0;
        if(!simple){
            int l = s.getLife()/lifeScale;
            int pL = s.getPlayerLife()/lifeScale;
            int d = s.getDis()/distanceScale;

            int aux1 = idMov.size();
            int aux2 = (1 + 100/lifeScale) * aux1;
            int aux3 = (1 + 100/lifeScale) * aux2;
            int aux4 = (1 + 700/distanceScale) * aux3;
            int aux5 = 4 * aux4;
            int aux6 = 2 * aux5;
            val = j*aux6 + w * aux5 + r * aux4 + d * aux3 + pL * aux2 + l * aux1 + sPid;
        }
        else {
            int l = s.getLife();
            int pL = s.getPlayerLife();
            int d = s.getDis();

            int val1 = 0, val2 = 0, val3 = 0;
            if(l >= 70){val1 = 2;}
            else if(l >= 30){val1 = 1;}
            else{val1 = 0;}

            if(pL >= 70){val2 = 2;}
            else if(pL >= 30){val2 = 1;}
            else{val2 = 0;}

            if(d >= 400){val3 = 2;}
            else if(d >= 120){val3 = 1;}
            else{val3 = 0;}

            int aux1 = idMov.size();
            int aux2 = 3 * aux1;
            int aux3 = 3 * aux2;
            int aux4 = 2 * aux3;
            val = j * aux4 + val3 * aux3 + val2 * aux2 + val1 * aux1 + sPid;
        }

        if (val > max){
            System.out.println("Out of bounds (stateCalculator) " + s.toString());
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
     * Return the id of the action "m" int.
     *
     * @param m the m
     * @return the int
     */
    public  static Movement actionById(int m){
        return actionByID.get(m);
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

    /**
     * Gets life scale.
     *
     * @return the life scale
     */
    public static int getLifeScale() {
        return lifeScale;
    }

    /**
     * Sets life scale.
     *
     * @param lifeScale the life scale
     */
    public static void setLifeScale(int lifeScale) {
        stateCalculator.lifeScale = lifeScale;
    }

    /**
     * Gets distance scale.
     *
     * @return the distance scale
     */
    public static int getDistanceScale() {
        return distanceScale;
    }

    /**
     * Sets distance scale.
     *
     * @param distanceScale the distance scale
     */
    public static void setDistanceScale(int distanceScale) {
        stateCalculator.distanceScale = distanceScale;
    }

    /**
     * Gets id mov.
     *
     * @return the id mov
     */
    public static Map<Movement, Integer> getIdMov() {
        return idMov;
    }

    /**
     * Sets id mov.
     *
     * @param idMov the id mov
     */
    public static void setIdMov(Map<Movement, Integer> idMov) {
        stateCalculator.idMov = idMov;
    }

    /**
     * Gets id action.
     *
     * @return the id action
     */
    public static Map<Movement, Integer> getIdAction() {
        return idAction;
    }

    /**
     * Sets id action.
     *
     * @param idAction the id action
     */
    public static void setIdAction(Map<Movement, Integer> idAction) {
        stateCalculator.idAction = idAction;
    }

    /**
     * Gets action by id.
     *
     * @return the action by id
     */
    public static Map<Integer, Movement> getActionByID() {
        return actionByID;
    }

    /**
     * Sets action by id.
     *
     * @param actionByID the action by id
     */
    public static void setActionByID(Map<Integer, Movement> actionByID) {
        stateCalculator.actionByID = actionByID;
    }

    /**
     * Sets max.
     *
     * @param max the max
     */
    public static void setMax(int max) {
        stateCalculator.max = max;
    }

    /**
     * Sets actions.
     *
     * @param nActions the n actions
     */
    public static void setnActions(int nActions) {
        stateCalculator.nActions = nActions;
    }
}
