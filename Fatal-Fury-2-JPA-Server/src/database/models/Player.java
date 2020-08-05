package database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * The type Player.
 */
@Entity(name = "PLAYER")
@Table(name = "player")
public class Player implements Serializable {
    @Id
    @Column(name = "username", nullable = false)
    @Size(min = 3, max = 30, message="ERROR:El nombre tiene que tener entre 3 y 30 caracteres.")
    @Pattern(regexp = "[A-z,0-9,_,-]+", message="ERROR:El nombre solo puede tener letras mayúsculas o minúsculas sin acentuar, números, y los caracteres \"_\" y \"-\".")
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    @Size(min = 3, max = 100, message="ERROR:El correo tiene que tener entre 3 y 100 caracteres.")
    @Pattern(regexp = "[^@]+@[A-z,0-9]+.[A-z]+", message="ERROR:El correo tiene que seguir el patron example@example.example.")
    private String email;
    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 100, message="ERROR:La contraseña tiene que tener entre 8 y 100 caracteres.")
    @Pattern(regexp = "[A-z,0-9,_,-]+", message="ERROR:La contraseña solo puede tener letras mayúsculas o minúsculas sin acentuar, números, y los caracteres \"_\" y \"-\".")
    private String password;
    @Column(name = "active", nullable = false)
    private boolean active;
    @Column(name = "rankScore", nullable = false)
    private int rankScore;
    @OneToMany(mappedBy = "player1", fetch = FetchType.LAZY)
    private List<Game> gamesAsP1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2", fetch = FetchType.LAZY)
    private List<Game> gamesAsP2 = new ArrayList<>();;
    @OneToMany(mappedBy = "transmitter", fetch = FetchType.LAZY)
    private List<Message> messagesAsTransmitter = new ArrayList<>();;
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Message> messagesAsReceiver = new ArrayList<>();;
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<Login> logins = new ArrayList<>();;
    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    private List<Tournament> participatedTournaments = new ArrayList<>();;
    @ManyToMany
    @JoinTable(name = "friends")
    private List<Player> friendsAsSoliciter = new ArrayList<>();;
    @ManyToMany(mappedBy = "friendsAsSoliciter", fetch = FetchType.LAZY)
    private List<Player> friendsAsReceiver = new ArrayList<>();;
    @ManyToMany
    @JoinTable(name = "friend_request")
    private List<Player> sentFriendRequest = new ArrayList<>();;
    @ManyToMany(mappedBy = "sentFriendRequest", fetch = FetchType.LAZY)
    private List<Player> receivedFriendRequest = new ArrayList<>();;

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
    public Player(String username, String email, String password, boolean active) {
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

    public List<Player> getFriendsAsSoliciter() {
        return friendsAsSoliciter;
    }

    public void setFriendsAsSoliciter(List<Player> friendsAsSoliciter) {
        this.friendsAsSoliciter = friendsAsSoliciter;
    }

    public List<Player> getFriendsAsReceiver() {
        return friendsAsReceiver;
    }

    public void setFriendsAsReceiver(List<Player> friendsAsReceiver) {
        this.friendsAsReceiver = friendsAsReceiver;
    }

    public List<Player> getSentFriendRequest() {
        return sentFriendRequest;
    }

    public void setSentFriendRequest(List<Player> sentFriendRequest) {
        this.sentFriendRequest = sentFriendRequest;
    }

    public List<Player> getReceivedFriendRequest() {
        return receivedFriendRequest;
    }

    public void setReceivedFriendRequest(List<Player> receivedFriendRequest) {
        this.receivedFriendRequest = receivedFriendRequest;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", rankScore=" + rankScore +
                '}';
    }
}
