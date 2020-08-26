package database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The type Login.
 */
@Entity(name = "LOGIN")
@Table(name = "login")
@IdClass(Login.class)
public class Login implements Serializable {
    @Id
    @JoinColumn(name = "player")
    @ManyToOne
    private Player player;
    @Id
    @JoinColumn(name = "date")
    private LocalDateTime date;

    /**
     * Instantiates a new Login.
     */
    public Login(){}

    /**
     * Instantiates a new Login.
     *
     * @param player the player
     */
    public Login(Player player){
        this.player = player;
        this.date = LocalDateTime.now();
    }

    /**
     * Instantiates a new Login.
     *
     * @param player the player
     * @param date   the date
     */
    public Login(Player player, LocalDateTime date){
        this.player = player;
        this.date = date;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets player.
     *
     * @param player the player
     */
    public void setPlayer(Player player) {
        this.player = player;
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

    @Override
    public String toString() {
        return "Login{" +
                "player=" + player.getUsername() +
                ", date=" + date +
                '}';
    }
}
