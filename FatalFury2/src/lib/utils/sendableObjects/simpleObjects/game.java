package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.time.LocalDateTime;

/**
 * The type Game.
 */
public class game extends sendableObject {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 7617345688754547712L;
    /**
     * The Id.
     */
    private Integer id;
    /**
     * The Player 1.
     */
    private String player1;
    /**
     * The Player 2.
     */
    private String player2;
    /**
     * The Date.
     */
    private LocalDateTime date;
    /**
     * The Result.
     */
    private int result;
    /**
     * The Ranked.
     */
    protected boolean ranked;
    /**
     * The Winner points.
     */
    private Integer winnerPoints;
    /**
     * The Loser points.
     */
    private Integer loserPoints;
    /**
     * The Character 1.
     */
    private String character1;
    /**
     * The Character 2.
     */
    private String character2;

    /**
     * Instantiates a new Game.
     *
     * @param id           the id
     * @param player1      the player 1
     * @param player2      the player 2
     * @param date         the date
     * @param result       the result
     * @param ranked       the ranked
     * @param winnerPoints the winner points
     * @param loserPoints  the loser points
     * @param character1   the character 1
     * @param character2   the character 2
     */
    public game(Integer id, String player1, String player2, LocalDateTime date, int result, boolean ranked, Integer winnerPoints, Integer loserPoints, String character1, String character2) {
        this.id = id;
        this.player1 = player1;
        this.player2 = player2;
        this.date = date;
        this.result = result;
        this.ranked = ranked;
        this.winnerPoints = winnerPoints;
        this.loserPoints = loserPoints;
        this.character1 = character1;
        this.character2 = character2;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets player 1.
     *
     * @return the player 1
     */
    public String getPlayer1() {
        return player1;
    }

    /**
     * Sets player 1.
     *
     * @param player1 the player 1
     */
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    /**
     * Gets player 2.
     *
     * @return the player 2
     */
    public String getPlayer2() {
        return player2;
    }

    /**
     * Sets player 2.
     *
     * @param player2 the player 2
     */
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public int getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     * Is ranked boolean.
     *
     * @return the boolean
     */
    public boolean isRanked() {
        return ranked;
    }

    /**
     * Sets ranked.
     *
     * @param ranked the ranked
     */
    public void setRanked(boolean ranked) {
        this.ranked = ranked;
    }

    /**
     * Gets winner points.
     *
     * @return the winner points
     */
    public Integer getWinnerPoints() {
        return winnerPoints;
    }

    /**
     * Sets winner points.
     *
     * @param winnerPoints the winner points
     */
    public void setWinnerPoints(Integer winnerPoints) {
        this.winnerPoints = winnerPoints;
    }

    /**
     * Gets loser points.
     *
     * @return the loser points
     */
    public Integer getLoserPoints() {
        return loserPoints;
    }

    /**
     * Sets loser points.
     *
     * @param loserPoints the loser points
     */
    public void setLoserPoints(Integer loserPoints) {
        this.loserPoints = loserPoints;
    }

    /**
     * Gets character 1.
     *
     * @return the character 1
     */
    public String getCharacter1() {
        return character1;
    }

    /**
     * Sets character 1.
     *
     * @param character1 the character 1
     */
    public void setCharacter1(String character1) {
        this.character1 = character1;
    }

    /**
     * Gets character 2.
     *
     * @return the character 2
     */
    public String getCharacter2() {
        return character2;
    }

    /**
     * Sets character 2.
     *
     * @param character2 the character 2
     */
    public void setCharacter2(String character2) {
        this.character2 = character2;
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
