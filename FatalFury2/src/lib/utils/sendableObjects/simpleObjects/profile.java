package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.util.ArrayList;

/**
 * The type Profile.
 */
public class profile extends sendableObject {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 7617345688754547717L;
    /**
     * The User.
     */
    private String user;
    /**
     * The Points.
     */
    private int points;
    /**
     * The Normal wins.
     */
    private int normalWins, /**
     * The Normal loses.
     */
    normalLoses, /**
     * The Ranked wins.
     */
    rankedWins, /**
     * The Ranked loses.
     */
    rankedLoses;
    /**
     * The Games.
     */
    private ArrayList<game> games;

    /**
     * Instantiates a new Profile.
     *
     * @param user        the user
     * @param points      the points
     * @param normalWins  the normal wins
     * @param normalLoses the normal loses
     * @param rankedWins  the ranked wins
     * @param rankedLoses the ranked loses
     * @param games       the games
     */
    public profile(String user, int points, int normalWins, int normalLoses, int rankedWins, int rankedLoses, ArrayList<game> games) {
        this.user = user;
        this.points = points;
        this.normalWins = normalWins;
        this.normalLoses = normalLoses;
        this.rankedWins = rankedWins;
        this.rankedLoses = rankedLoses;
        this.games = games;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets points.
     *
     * @return the points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets points.
     *
     * @param points the points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Gets normal wins.
     *
     * @return the normal wins
     */
    public int getNormalWins() {
        return normalWins;
    }

    /**
     * Sets normal wins.
     *
     * @param normalWins the normal wins
     */
    public void setNormalWins(int normalWins) {
        this.normalWins = normalWins;
    }

    /**
     * Gets normal loses.
     *
     * @return the normal loses
     */
    public int getNormalLoses() {
        return normalLoses;
    }

    /**
     * Sets normal loses.
     *
     * @param normalLoses the normal loses
     */
    public void setNormalLoses(int normalLoses) {
        this.normalLoses = normalLoses;
    }

    /**
     * Gets ranked wins.
     *
     * @return the ranked wins
     */
    public int getRankedWins() {
        return rankedWins;
    }

    /**
     * Sets ranked wins.
     *
     * @param rankedWins the ranked wins
     */
    public void setRankedWins(int rankedWins) {
        this.rankedWins = rankedWins;
    }

    /**
     * Gets ranked loses.
     *
     * @return the ranked loses
     */
    public int getRankedLoses() {
        return rankedLoses;
    }

    /**
     * Sets ranked loses.
     *
     * @param rankedLoses the ranked loses
     */
    public void setRankedLoses(int rankedLoses) {
        this.rankedLoses = rankedLoses;
    }

    /**
     * Gets games.
     *
     * @return the games
     */
    public ArrayList<game> getGames() {
        return games;
    }

    /**
     * Sets games.
     *
     * @param games the games
     */
    public void setGames(ArrayList<game> games) {
        this.games = games;
    }

    /**
     * Gets serial version uid.
     *
     * @return the serial version uid
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
