package database.models;

import org.hibernate.annotations.DiscriminatorFormula;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The type Game.
 */
@Entity(name = "GAME")
@Table(name = "game")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue(value = "Game")
@DiscriminatorFormula("case when ranked is false then 'Game' else 'RankedGame' end")
public class Game implements Serializable{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "player1", nullable = false)
    private Player player1;
    @ManyToOne
    @JoinColumn(name = "player2", nullable = false)
    private Player player2;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    @Column(name = "result", nullable = false)
    private int result;
    @ManyToOne
    @JoinColumn(name = "tournament", nullable = true)
    private Tournament tournament;
    @Column(name = "ranked", nullable = false)
    protected boolean ranked;
    @Column(name = "winnerPoints")
    private Integer winnerPoints;
    @Column(name = "loserPoints")
    private Integer loserPoints;
    @Column(name = "character1")
    private String character1;
    @Column(name = "character2")
    private String character2;

    /**
     * Instantiates a new Game.
     */
    public Game(){}

    /**
     * Instantiates a new Game.
     *
     * @param player1 the player 1
     * @param player2 the player 2
     * @param result  the result
     */
    public Game(Player player1, Player player2, String character1, String character2, int result){
        this.player1 = player1;
        this.player2 = player2;
        this.date = LocalDateTime.now();
        this.result = result;
        this.ranked = false;
        this.character1 = character1;
        this.character2 = character2;
    }

    /**
     * Instantiates a new Game.
     *
     * @param player1    the player 1
     * @param player2    the player 2
     * @param result     the result
     * @param tournament the tournament
     */
    public Game(Player player1, Player player2, String character1, String character2, int result, Tournament tournament){
        this.player1 = player1;
        this.player2 = player2;
        this.date = LocalDateTime.now();
        this.result = result;
        this.ranked = false;
        this.tournament = tournament;
        this.character1 = character1;
        this.character2 = character2;
    }

    /**
     * Instantiates a new Game.
     *
     * @param player1      the player 1
     * @param player2      the player 2
     * @param result       the result
     * @param winnerPoints the winner points
     * @param loserPoints  the loser points
     */
    public Game(Player player1, Player player2, String character1, String character2, int result, int winnerPoints, int loserPoints){
        this.player1 = player1;
        this.player2 = player2;
        this.date = LocalDateTime.now();
        this.result = result;
        this.ranked = true;
        this.winnerPoints = winnerPoints;
        this.loserPoints = loserPoints;
        this.character1 = character1;
        this.character2 = character2;
    }

    /**
     * Instantiates a new Game.
     *
     * @param player1 the player 1
     * @param player2 the player 2
     * @param date    the date
     * @param result  the result
     */
    public Game(Player player1, Player player2, String character1, String character2, LocalDateTime date, int result){
        this.player1 = player1;
        this.player2 = player2;
        this.date = date;
        this.result = result;
        this.ranked = false;
        this.character1 = character1;
        this.character2 = character2;
    }

    /**
     * Instantiates a new Game.
     *
     * @param player1    the player 1
     * @param player2    the player 2
     * @param date       the date
     * @param result     the result
     * @param tournament the tournament
     */
    public Game(Player player1, Player player2, String character1, String character2, LocalDateTime date, int result, Tournament tournament){
        this.player1 = player1;
        this.player2 = player2;
        this.date = date;
        this.result = result;
        this.ranked = false;
        this.tournament = tournament;
        this.character1 = character1;
        this.character2 = character2;
    }

    /**
     * Instantiates a new Game.
     *
     * @param player1      the player 1
     * @param player2      the player 2
     * @param date         the date
     * @param result       the result
     * @param winnerPoints the winner points
     * @param loserPoints  the loser points
     */
    public Game(Player player1, Player player2, String character1, String character2, LocalDateTime date, int result, int winnerPoints, int loserPoints){
        this.player1 = player1;
        this.player2 = player2;
        this.date = date;
        this.result = result;
        this.ranked = true;
        this.winnerPoints = winnerPoints;
        this.loserPoints = loserPoints;
        this.character1 = character1;
        this.character2 = character2;
    }

    /**
     * Gets player 1.
     *
     * @return the player 1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Sets player 1.
     *
     * @param player1 the player 1
     */
    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    /**
     * Gets player 2.
     *
     * @return the player 2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Sets player 2.
     *
     * @param player2 the player 2
     */
    public void setPlayer2(Player player2) {
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

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
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

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", player1=" + player1.getUsername() +
                ", player2=" + player2.getUsername() +
                ", character1=" + character1 +
                ", character1=" + character2 +
                ", date=" + date +
                ", result=" + result +
                ", tournament=" + tournament.getId() +
                ", ranked=" + ranked +
                ", winnerPoints=" + winnerPoints +
                ", loserPoints=" + loserPoints +
                '}';
    }
}
