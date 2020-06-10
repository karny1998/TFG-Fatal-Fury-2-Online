package lib.training;

import lib.Enums.Movement;

/**
 * Estado simplificado (omite cierta información) de un momento de la pelea
 */
public class state {
    /**
     * The Life.
     */
    private int life;
    /**
     * The Player life.
     */
    private int playerLife;
    /**
     * The Player state.
     */
    private Movement playerState;
    /**
     * The Distance between player and ia.
     */
    private int dis;
    /**
     * The Round
     */
    private int round = 0;
    /**
     * The num which represents the state.
     */
    private int stateNum;

    /**
     * Instantiates a new State.
     *
     * @param life        the life
     * @param playerLife  the player life
     * @param playerState the player state
     * @param dis         the distance
     * @param round       the round
     */
    public state(int life, int playerLife, Movement playerState, int dis, int round) {
        this.life = life;
        this.playerLife = playerLife;
        this.playerState = playerState;
        this.dis = dis;
        this.round = round;
        this.stateNum = stateCalculator.calculateNumState(this);
    }

    /**
     * Sets life.
     *
     * @param life the life
     */
    public void setLife(int life) {
        this.life = life;
        this.stateNum = stateCalculator.calculateNumState(this);
    }

    /**
     * Sets player life.
     *
     * @param playerLife the player life
     */
    public void setPlayerLife(int playerLife) {
        this.playerLife = playerLife;
        this.stateNum = stateCalculator.calculateNumState(this);
    }

    /**
     * Sets player state.
     *
     * @param playerState the player state
     */
    public void setPlayerState(Movement playerState) {
        this.playerState = playerState;
        this.stateNum = stateCalculator.calculateNumState(this);
    }

    /**
     * Sets distance.
     *
     * @param dis the distance
     */
    public void setDis(int dis) {
        this.dis = dis;
        this.stateNum = stateCalculator.calculateNumState(this);
    }

    /**
     * Sets round.
     *
     * @param round the round
     */
    public void setRound(int round) {
        this.round = round;
        this.stateNum = stateCalculator.calculateNumState(this);
    }

    /**
     * Gets life.
     *
     * @return the life
     */
    public int getLife() {
        return life;
    }

    /**
     * Gets player life.
     *
     * @return the player life
     */
    public int getPlayerLife() {
        return playerLife;
    }

    /**
     * Gets player state.
     *
     * @return the player state
     */
    public Movement getPlayerState() {
        return playerState;
    }

    /**
     * Gets dis.
     *
     * @return the dis (distance)
     */
    public int getDis() {
        return dis;
    }

    /**
     * Gets state num.
     *
     * @return the state num
     */
    public int getStateNum() {
        return stateNum;
    }

    /**
     * Gets round.
     *
     * @return the round
     */
    public int getRound() {
        return round;
    }
}
