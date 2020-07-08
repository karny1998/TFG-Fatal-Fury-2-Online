package database.models;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The type Tournament.
 */
@Entity(name = "TOURNAMENT")
@Table(name = "tournament")
public class Tournament {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "organizer", nullable = false)
    @ManyToOne
    private Player organizer;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    @JoinColumn(name = "winner", nullable = true)
    @ManyToOne
    private Player winner;
    @ManyToMany
    private List<Player> participants;

    /**
     * Instantiates a new Tournament.
     */
    public Tournament(){}

    /**
     * Instantiates a new Tournament.
     *
     * @param organizer the organizer
     */
    public Tournament(Player organizer){
        this.organizer = organizer;
        this.date = LocalDateTime.now();
    }

    /**
     * Instantiates a new Tournament.
     *
     * @param organizer the organizer
     * @param winner    the winner
     */
    public Tournament(Player organizer, Player winner){
        this.organizer = organizer;
        this.date = LocalDateTime.now();
        this.winner = winner;
    }

    /**
     * Instantiates a new Tournament.
     *
     * @param organizer the organizer
     * @param date      the date
     * @param winner    the winner
     */
    public Tournament(Player organizer, LocalDateTime date, Player winner){
        this.organizer = organizer;
        this.date = date;
        this.winner = winner;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
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
     * Gets organizer.
     *
     * @return the organizer
     */
    public Player getOrganizer() {
        return organizer;
    }

    /**
     * Sets organizer.
     *
     * @param organizer the organizer
     */
    public void setOrganizer(Player organizer) {
        this.organizer = organizer;
    }

    /**
     * Gets winner.
     *
     * @return the winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Sets winner.
     *
     * @param winner the winner
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * Gets participants.
     *
     * @return the participants
     */
    public List<Player> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", organizer=" + organizer.getUsername() +
                ", date=" + date +
                ", winner=" + winner.getUsername() +
                '}';
    }
}
