package database.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The type Message.
 */
@Entity(name = "MESSAGE")
@Table(name = "message")
public class Message implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private Integer id;
    @JoinColumn(name = "transmitter", nullable = false)
    @ManyToOne
    private Player transmitter;
    @JoinColumn(name = "receiver", nullable = false)
    @ManyToOne
    private Player receiver;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * Instantiates a new Message.
     */
    public Message() {}

    /**
     * Instantiates a new Message.
     *
     * @param transmitter the transmitter
     * @param receiver    the receiver
     * @param content     the content
     */
    public Message(Player transmitter, Player receiver, String content) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.date = LocalDateTime.now();
        this.content = content;
    }

    /**
     * Instantiates a new Message.
     *
     * @param transmitter the transmitter
     * @param receiver    the receiver
     * @param date        the date
     * @param content     the content
     */
    public Message(Player transmitter, Player receiver, LocalDateTime date, String content) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.date = date;
        this.content = content;
    }

    /**
     * Gets transmitter.
     *
     * @return the transmitter
     */
    public Player getTransmitter() {
        return transmitter;
    }

    /**
     * Sets transmitter.
     *
     * @param transmitter the transmitter
     */
    public void setTransmitter(Player transmitter) {
        this.transmitter = transmitter;
    }

    /**
     * Gets receiver.
     *
     * @return the receiver
     */
    public Player getReceiver() {
        return receiver;
    }

    /**
     * Sets receiver.
     *
     * @param receiver the receiver
     */
    public void setReceiver(Player receiver) {
        this.receiver = receiver;
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
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
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

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", transmitter=" + transmitter.getUsername() +
                ", receiver=" + receiver.getUsername() +
                ", date=" + date +
                ", content='" + content + '\'' +
                '}';
    }
}
