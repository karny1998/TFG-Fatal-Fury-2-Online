package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.time.LocalDateTime;

public class game extends sendableObject {
    private static final long serialVersionUID = 7617345688754547712L;
    private Integer id;
    private String player1;
    private String player2;
    private LocalDateTime date;
    private int result;
    protected boolean ranked;
    private Integer winnerPoints;
    private Integer loserPoints;
    private String character1;
    private String character2;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public boolean isRanked() {
        return ranked;
    }

    public void setRanked(boolean ranked) {
        this.ranked = ranked;
    }

    public Integer getWinnerPoints() {
        return winnerPoints;
    }

    public void setWinnerPoints(Integer winnerPoints) {
        this.winnerPoints = winnerPoints;
    }

    public Integer getLoserPoints() {
        return loserPoints;
    }

    public void setLoserPoints(Integer loserPoints) {
        this.loserPoints = loserPoints;
    }

    public String getCharacter1() {
        return character1;
    }

    public void setCharacter1(String character1) {
        this.character1 = character1;
    }

    public String getCharacter2() {
        return character2;
    }

    public void setCharacter2(String character2) {
        this.character2 = character2;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
