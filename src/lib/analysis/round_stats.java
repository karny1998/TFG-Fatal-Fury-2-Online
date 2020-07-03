package lib.analysis;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Estadísticas de una ronda terminada
 */
@XmlRootElement(name = "round")
public class round_stats {
    /**
     * Golpes recibidos
     */
    private int received_hits = 0;
    /**
     * Golpes bloqueados
     */
    private int blocked_hits = 0;
    /**
     * Golpes realizados
     */
    private int realized_hits = 0;
    /**
     * Golpes que han impactado
     */
    private int successful_hits = 0;
    /**
     * Vida restante del jugador
     */
    private int player_remaining_life = 0;
    /**
     * Vida restante
     */
    private int remaining_life = 0;
    /**
     * Tiempo restante
     */
    private int remaining_time = 0;
    /**
     * Resultado: 0 empate, 1 victoria y 2 derrota
     */
    private int result = 0;
    /**
     * Número de ronda
     */
    private int id;

    /**
     * Instantiates a new Round stats.
     */
    public round_stats() {}

    /**
     * Instantiates a new Round stats.
     *
     * @param received_hits         the received hits
     * @param blocked_hits          the blocked hits
     * @param realized_hits         the realized hits
     * @param successful_hits       the successful hits
     * @param player_remaining_life the player remaining life
     * @param remaining_life        the remaining life
     * @param remaining_time        the remaining time
     * @param result                the result
     */
    public round_stats(int received_hits, int blocked_hits, int realized_hits, int successful_hits, int player_remaining_life, int remaining_life, int remaining_time, int result) {
        this.received_hits = received_hits;
        this.blocked_hits = blocked_hits;
        this.realized_hits = realized_hits;
        this.successful_hits = successful_hits;
        this.player_remaining_life = player_remaining_life;
        this.remaining_life = remaining_life;
        this.remaining_time = remaining_time;
        this.result = result;
    }

    /**
     * Add received hit.
     */
    public void addReceivedHit(){
        ++received_hits;
    }

    /**
     * Add blocked hit.
     */
    public void addBlockedHit(){
        ++blocked_hits;
    }

    /**
     * Add realized hits.
     */
    public void addRealizedHits(){
        ++realized_hits;
    }

    /**
     * Add successful hits.
     */
    public void addSuccessfulHits(){
        ++successful_hits;
    }

    /**
     * Block ratio double.
     *
     * @return the double
     */
    public double blockRatio(){
        if(received_hits == 0){return 0;}
        return (double)blocked_hits/received_hits;
    }

    /**
     * Hit ratio double.
     *
     * @return the double
     */
    public double hitRatio(){
        if(realized_hits == 0){return 0;}
        return (double)successful_hits/realized_hits;
    }

    /**
     * Gets received hits.
     *
     * @return the received hits
     */
    public int getReceived_hits() {
        return received_hits;
    }

    /**
     * Sets received hits.
     *
     * @param received_hits the received hits
     */
    @XmlElement(name = "received_hits")
    public void setReceived_hits(int received_hits) {
        this.received_hits = received_hits;
    }

    /**
     * Gets blocked hits.
     *
     * @return the blocked hits
     */
    public int getBlocked_hits() {
        return blocked_hits;
    }

    /**
     * Sets blocked hits.
     *
     * @param blocked_hits the blocked hits
     */
    @XmlElement(name = "blocked_hits")
    public void setBlocked_hits(int blocked_hits) {
        this.blocked_hits = blocked_hits;
    }

    /**
     * Gets realized hits.
     *
     * @return the realized hits
     */
    public int getRealized_hits() {
        return realized_hits;
    }

    /**
     * Sets realized hits.
     *
     * @param realized_hits the realized hits
     */
    @XmlElement(name = "realized_hits")
    public void setRealized_hits(int realized_hits) {
        this.realized_hits = realized_hits;
    }

    /**
     * Gets successful hits.
     *
     * @return the successful hits
     */
    public int getSuccessful_hits() {
        return successful_hits;
    }

    /**
     * Sets successful hits.
     *
     * @param successful_hits the successful hits
     */
    @XmlElement(name = "successful_hits")
    public void setSuccessful_hits(int successful_hits) {
        this.successful_hits = successful_hits;
    }

    /**
     * Gets player remaining life.
     *
     * @return the player remaining life
     */
    public int getPlayer_remaining_life() {
        return player_remaining_life;
    }

    /**
     * Sets player remaining life.
     *
     * @param player_remaining_life the player remaining life
     */
    @XmlElement(name = "player_remaining_life")
    public void setPlayer_remaining_life(int player_remaining_life) {
        this.player_remaining_life = player_remaining_life;
    }

    /**
     * Gets remaining life.
     *
     * @return the remaining life
     */
    public int getRemaining_life() {
        return remaining_life;
    }

    /**
     * Sets remaining life.
     *
     * @param remaining_life the remaining life
     */
    @XmlElement(name = "remaining_life")
    public void setRemaining_life(int remaining_life) {
        this.remaining_life = remaining_life;
    }

    /**
     * Gets remaining time.
     *
     * @return the remaining time
     */
    public int getRemaining_time() {
        return remaining_time;
    }

    /**
     * Sets remaining time.
     *
     * @param remaining_time the remaining time
     */
    @XmlElement(name = "remaining_time")
    public void setRemaining_time(int remaining_time) {
        this.remaining_time = remaining_time;
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
    @XmlElement(name = "result")
    public void setResult(int result) {
        this.result = result;
    }

    /**
     * Set lose.
     */
    public void setLose(){
        result = 2;
    }

    /**
     * Set win.
     */
    public void setWin(){
        result = 1;
    }

    /**
     * Set tie.
     */
    public void setTie(){
        result = 0;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    @XmlElement(name = "id")
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }
}
