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
    @Size(min = 3, max = 10, message="ERROR:Username must be between 3 and 10 chars.")
    @Pattern(regexp = "[A-z,0-9,_,-]+", message="ERROR:Username can only contain uppercase and lowercase letters, numbers, \"_\" and  \"-\"")
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    @Size(min = 3, max = 100, message="ERROR:Email must be between 3 and 100 chars.")
    @Pattern(regexp = "[^@]+@[A-z,0-9]+.[A-z]+", message="ERROR:Email must be like example@example.example")
    private String email;
    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 100, message="ERROR:Password must be between 8 and 100 chars.")
    private String password;
    @Column(name = "active", nullable = false)
    private boolean active = false;
    @Column(name = "rankScore", nullable = false)
    private int rankScore = 0;
    @Column(name = "normalWins", nullable = false)
    private int normalWins = 0;
    @Column(name = "normalLoses", nullable = false)
    private int normalLoses = 0;
    @Column(name = "rankWins", nullable = false)
    private int rankWins = 0;
    @Column(name = "rankLoses", nullable = false)
    private int rankLoses = 0;
    @OneToMany(mappedBy = "player1", fetch = FetchType.LAZY)
    private List<Game> gamesAsP1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2", fetch = FetchType.LAZY)
    private List<Game> gamesAsP2 = new ArrayList<>();
    @OneToMany(mappedBy = "transmitter", fetch = FetchType.LAZY)
    private List<Message> messagesAsTransmitter = new ArrayList<>();
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Message> messagesAsReceiver = new ArrayList<>();
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<Login> logins = new ArrayList<>();
    @ManyToMany(mappedBy = "participants", fetch = FetchType.LAZY)
    private List<Tournament> participatedTournaments = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "friends")
    private List<Player> friendsAsSoliciter = new ArrayList<>();
    @ManyToMany(mappedBy = "friendsAsSoliciter", fetch = FetchType.LAZY)
    private List<Player> friendsAsReceiver = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "friend_request")
    private List<Player> sentFriendRequest = new ArrayList<>();
    @ManyToMany(mappedBy = "sentFriendRequest", fetch = FetchType.LAZY)
    private List<Player> receivedFriendRequest = new ArrayList<>();
    @ManyToMany
    @JoinTable(name = "pending_messages")
    private List<Player> pending_messages = new ArrayList<>();
    @Column(name="code")
    private int code;
    @Column(name="times_vs_global_ia")
    private int timesVSglobalIa = 0;
    @Column(name="times_vs_own_ia")
    private int timesVSownlIa = 0;

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
        this.code = (int) (Math.random()*100000);
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

    public void addNormalGameResult(boolean won){
        if(won){++normalWins;}
        else{++normalLoses;}
    }

    public void addRankedGameResult(boolean won){
        if(won){++rankWins;}
        else{++rankLoses;}
    }

    public int getNormalWins() {
        return normalWins;
    }

    public void setNormalWins(int normalWins) {
        this.normalWins = normalWins;
    }

    public int getNormalLoses() {
        return normalLoses;
    }

    public void setNormalLoses(int normalLoses) {
        this.normalLoses = normalLoses;
    }

    public int getRankWins() {
        return rankWins;
    }

    public void setRankWins(int rankWins) {
        this.rankWins = rankWins;
    }

    public int getRankLoses() {
        return rankLoses;
    }

    public void setRankLoses(int rankLoses) {
        this.rankLoses = rankLoses;
    }

    public List<Player> getPending_messages() {
        return pending_messages;
    }

    public void setPending_messages(List<Player> pending_messages) {
        this.pending_messages = pending_messages;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTimesVSglobalIa() {
        return timesVSglobalIa;
    }

    public void setTimesVSglobalIa(int timesVSglobalIa) {
        this.timesVSglobalIa = timesVSglobalIa;
    }

    public int getTimesVSownlIa() {
        return timesVSownlIa;
    }

    public void setTimesVSownlIa(int timesVSownlIa) {
        this.timesVSownlIa = timesVSownlIa;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", rankScore=" + rankScore +
                ", normalWins=" + normalWins +
                ", normalLoses=" + normalLoses +
                ", rankWins=" + rankWins +
                ", rankLoses=" + rankLoses +
                '}';
    }
}
