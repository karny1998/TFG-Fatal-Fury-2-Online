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
     * The remaining time of the round
     */
    private int remainingTime = 0;

    /**
     * The Ia victories.
     */
    private int iaVictories = 0;

    /**
     * The Player victories.
     */
    private int playerVictories = 0;

    /**
     * Instantiates a new State.
     *
     * @param life            the life
     * @param playerLife      the player life
     * @param playerState     the player state
     * @param dis             the distance
     * @param round           the round
     * @param remainingTime   the remaining time
     * @param iaVictories     the ia victories
     * @param playerVictories the player victories
     */
    public state(int life, int playerLife, Movement playerState, int dis, int round, int remainingTime, int iaVictories, int playerVictories) {
        this.life = life;
        this.playerLife = playerLife;
        this.playerState = playerState;
        this.dis = dis;
        this.round = round;
        this.remainingTime = remainingTime;
        this.iaVictories = iaVictories;
        this.playerVictories = playerVictories;
        this.stateNum = stateCalculator.calculateNumState(this);
    }

    /**
     * Is round terminal state boolean.
     *
     * @return the boolean
     */
    public boolean isRoundTerminal(){
        return remainingTime == 0  || life == 0 || playerLife == 0;
    }

    /**
     * Is round terminal state boolean.
     *
     * @return the boolean
     */
    public boolean isFightTerminal(){
        return isRoundTerminal() && (round == 4 || iaVictories == 2 || playerVictories == 2);
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

    /**
     * Sets state num.
     *
     * @param stateNum the state num
     */
    public void setStateNum(int stateNum) {
        this.stateNum = stateNum;
    }

    /**
     * Gets remaining time.
     *
     * @return the remaining time
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Sets remaining time.
     *
     * @param remainingTime the remaining time
     */
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * Gets ia victories.
     *
     * @return the ia victories
     */
    public int getIaVictories() {
        return iaVictories;
    }

    /**
     * Sets ia victories.
     *
     * @param iaVictories the ia victories
     */
    public void setIaVictories(int iaVictories) {
        this.iaVictories = iaVictories;
    }

    /**
     * Gets player victories.
     *
     * @return the player victories
     */
    public int getPlayerVictories() {
        return playerVictories;
    }

    /**
     * Sets player victories.
     *
     * @param playerVictories the player victories
     */
    public void setPlayerVictories(int playerVictories) {
        this.playerVictories = playerVictories;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return life +
                "," + playerLife +
                "," + playerState +
                "," + dis +
                "," + round +
                "," + stateNum +
                "," + remainingTime +
                "," + iaVictories +
                "," + playerVictories;
    }

    /**
     * Clone state.
     *
     * @return the state
     */
    public state clone(){
        return new state(life,playerLife,playerState,dis,round,remainingTime,iaVictories,playerVictories);
    }
}
