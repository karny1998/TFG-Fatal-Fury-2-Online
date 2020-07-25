package lib.analysis;

import lib.Enums.Playable_Character;
import lib.objects.ia_loader;

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
     * The accumulated reward.
     */
    private int accumulatedReward = 0;

    private Playable_Character rival = Playable_Character.TERRY;

    private ia_loader.dif lvlRival = ia_loader.dif.HARD;

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
     * Win ratio double.
     *
     * @return the double
     */
    public double winRatio(){
        double iaWins = 0.0;
        for(int i = 0; i < rounds.size(); ++i){
            if(rounds.get(i).getResult() == 1){
                iaWins += 1.0;
            }
            else if(rounds.get(i).getResult() == 0){
                iaWins += 0.5;
            }
        }
        return iaWins/rounds.size();
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
     * Remaining life mean double.
     *
     * @return the double
     */
    public double remainingLifeMean(){
        double ratio = 0.0;
        for(int i = 0; i < rounds.size(); ++i){
            ratio +=  rounds.get(i).getRemaining_life();
        }
        return ratio/rounds.size();
    }

    /**
     * Successful hit per received ratio double.
     *
     * @return the double
     */
    public double successfulHitPerReceivedRatio(){
        double ratio1 = 0.0, ratio2 = 0.0;
        for(int i = 0; i < rounds.size(); ++i){
            ratio1 +=  rounds.get(i).getSuccessful_hits();
            ratio2 +=  rounds.get(i).getReceived_hits();
        }
        if(ratio1+ratio2>0) {
            return ratio1 / (ratio1 + ratio2);
        }
        return 0;
    }

    /**
     * Remaining player life mean double.
     *
     * @return the double
     */
    public double remainingPlayerLifeMean(){
        double ratio = 0.0;
        for(int i = 0; i < rounds.size(); ++i){
            ratio +=  rounds.get(i).getPlayer_remaining_life();
        }
        return ratio/rounds.size();
    }

    /**
     * Remaining time mean double.
     *
     * @return the double
     */
    public double remainingTimeMean(){
        double ratio = 0.0;
        for(int i = 0; i < rounds.size(); ++i){
            ratio +=  rounds.get(i).getRemaining_time();
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

    /**
     * Gets accumulated reward.
     *
     * @return the accumulated reward
     */
    public int getAccumulatedReward() {
        return accumulatedReward;
    }

    /**
     * Sets accumulated reward.
     *
     * @param accumulatedReward the accumulated reward
     */
    public void setAccumulatedReward(int accumulatedReward) {
        this.accumulatedReward = accumulatedReward;
    }

    /**
     * Add reward to the accumulated reward.
     *
     * @param reward the reward
     */
    public void addReward(int reward) {
        this.accumulatedReward += accumulatedReward;
    }

    public Playable_Character getRival() {
        return rival;
    }

    public void setRival(Playable_Character rival) {
        this.rival = rival;
    }

    public ia_loader.dif getLvlRival() {
        return lvlRival;
    }

    public void setLvlRival(ia_loader.dif lvlRival) {
        this.lvlRival = lvlRival;
    }
}
