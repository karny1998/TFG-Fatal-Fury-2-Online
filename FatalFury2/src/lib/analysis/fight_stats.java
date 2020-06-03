import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Estadísticas de una pelea
 */
@XmlRootElement(name = "fight")
public class fight_stats {
    /**
     * Identificador de la pelea
     */
    private int id = 0;
    /**
     * Estadísticas de las rondas de la pelea
     */
    private List<round_stats> rounds = new ArrayList<round_stats>();
    /**
     * The Current round.
     */
    private int currentRound = 0;

    /**
     * Instantiates a new Fight stats.
     */
    public fight_stats() {
        rounds.add(currentRound, new round_stats());
    }

    /**
     * Instantiates a new Fight stats.
     *
     * @param id           the id
     * @param rounds       the rounds
     * @param currentRound the current round
     */
    public fight_stats(int id, List<round_stats> rounds, int currentRound) {
        this.id = id;
        this.rounds = rounds;
        this.currentRound = currentRound;
    }

    /**
     * Get result int.
     *
     * @return the int
     */
    public int getResult(){
        int pWins = 0, iaWins = 0;
        for(int i = 0; i < rounds.size(); ++i){
            if(rounds.get(i).getResult() == 1){
                ++iaWins;
            }
            else if(rounds.get(i).getResult() == 2){
                ++pWins;
            }
        }
        if(iaWins > pWins){return 1;}
        else if(iaWins == pWins){return 0;}
        else{return 2;}
    }

    /**
     * Block ratio double.
     *
     * @return the double
     */
    public double blockRatio(){
        double ratio = 0.0;
        for(int i = 0; i < rounds.size(); ++i){
            ratio +=  rounds.get(i).blockRatio();
        }
        return ratio/rounds.size();
    }

    /**
     * Hit ratio double.
     *
     * @return the double
     */
    public double hitRatio(){
        double ratio = 0.0;
        for(int i = 0; i < rounds.size(); ++i){
            ratio +=  rounds.get(i).hitRatio();
        }
        return ratio/rounds.size();
    }

    /**
     * Next round.
     */
    public void nextRound(){
        ++currentRound;
        rounds.add(currentRound, new round_stats());
        rounds.get(currentRound).setId(currentRound);
    }

    /**
     * Current round round stats.
     *
     * @return the round stats
     */
    public round_stats currentRound(){
        return rounds.get(currentRound);
    }

    /**
     * Add received hit.
     */
    public void addReceivedHit(){
        rounds.get(currentRound).addReceivedHit();
    }

    /**
     * Add blocked hit.
     */
    public void addBlockedHit(){
        rounds.get(currentRound).addBlockedHit();
    }

    /**
     * Add realized hits.
     */
    public void addRealizedHits(){
        rounds.get(currentRound).addRealizedHits();
    }

    /**
     * Add successful hits.
     */
    public void addSuccessfulHits(){
        rounds.get(currentRound).addSuccessfulHits();
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
    @XmlElement
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets rounds.
     *
     * @return the rounds
     */
    public List<round_stats> getRounds() {
        return rounds;
    }

    /**
     * Sets rounds.
     *
     * @param rounds the rounds
     */
    @XmlElementWrapper(name = "rounds")
    @XmlElement(name = "round")
    public void setRounds(List<round_stats> rounds) {
        this.rounds = rounds;
    }

    /**
     * Gets current round.
     *
     * @return the current round
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Sets current round.
     *
     * @param currentRound the current round
     */
    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
}