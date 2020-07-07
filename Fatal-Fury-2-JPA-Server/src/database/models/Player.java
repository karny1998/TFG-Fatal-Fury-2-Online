package database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * The type Player.
 */
@Entity(name = "PLAYER")
@Table(name = "player")
public class Player implements Serializable {
    @Id
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "active", nullable = false)
    private boolean active;
    @Column(name = "rankScore", nullable = false)
    private int rankScore;
    @OneToMany(mappedBy = "player1", fetch = FetchType.LAZY)
    private List<Game> gamesAsP1;
    @OneToMany(mappedBy = "player2", fetch = FetchType.LAZY)
    private List<Game> gamesAsP2;
    @OneToMany(mappedBy = "transmitter", fetch = FetchType.LAZY)
    private List<Message> messagesAsTransmitter;
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Message> messagesAsReceiver;
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<Login> logins;
    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    private List<Tournament> participatedTournaments;


    /**
     * Instantiates a new Player.
     */
    public Player() {}

    /**
     * Instantiates a new Player.
     *
     * @param email    the email
     * @param username the username
     * @param password the password
     * @param active   the active
     */
    public Player(String email, String username, String password, boolean active) {
        super();
        this.email = email;
        this.username = username;
        this.password = password;
        this.active = active;
        this.rankScore = 0;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Is active boolean.
     *
     * @return the boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets active.
     *
     * @param active the active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets rank score.
     *
     * @return the rank score
     */
    public int getRankScore() {
        return rankScore;
    }

    /**
     * Sets rank score.
     *
     * @param rankScore the rank score
     */
    public void setRankScore(int rankScore) {
        this.rankScore = rankScore;
    }

    /**
     * Gets games as p 1.
     *
     * @return the games as p 1
     */
    public List<Game> getGamesAsP1() {
        return gamesAsP1;
    }

    /**
     * Sets games as p 1.
     *
     * @param gamesAsP1 the games as p 1
     */
    public void setGamesAsP1(List<Game> gamesAsP1) {
        this.gamesAsP1 = gamesAsP1;
    }

    /**
     * Gets games as p 2.
     *
     * @return the games as p 2
     */
    public List<Game> getGamesAsP2() {
        return gamesAsP2;
    }

    /**
     * Sets games as p 2.
     *
     * @param gamesAsP2 the games as p 2
     */
    public void setGamesAsP2(List<Game> gamesAsP2) {
        this.gamesAsP2 = gamesAsP2;
    }

    /**
     * Gets messages as transmitter.
     *
     * @return the messages as transmitter
     */
    public List<Message> getMessagesAsTransmitter() {
        return messagesAsTransmitter;
    }

    /**
     * Sets messages as transmitter.
     *
     * @param messagesAsTransmitter the messages as transmitter
     */
    public void setMessagesAsTransmitter(List<Message> messagesAsTransmitter) {
        this.messagesAsTransmitter = messagesAsTransmitter;
    }

    /**
     * Gets messages as receiver.
     *
     * @return the messages as receiver
     */
    public List<Message> getMessagesAsReceiver() {
        return messagesAsReceiver;
    }

    /**
     * Sets messages as receiver.
     *
     * @param messagesAsReceiver the messages as receiver
     */
    public void setMessagesAsReceiver(List<Message> messagesAsReceiver) {
        this.messagesAsReceiver = messagesAsReceiver;
    }

    /**
     * Gets logins.
     *
     * @return the logins
     */
    public List<Login> getLogins() {
        return logins;
    }

    /**
     * Sets logins.
     *
     * @param logins the logins
     */
    public void setLogins(List<Login> logins) {
        this.logins = logins;
    }

    /**
     * Gets participated tournaments.
     *
     * @return the participated tournaments
     */
    public List<Tournament> getParticipatedTournaments() {
        return participatedTournaments;
    }

    /**
     * Sets participated tournaments.
     *
     * @param participatedTournaments the participated tournaments
     */
    public void setParticipatedTournaments(List<Tournament> participatedTournaments) {
        this.participatedTournaments = participatedTournaments;
    }
}
