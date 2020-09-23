import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Historial de estadísticas de partidas
 */
@XmlRootElement(name = "history")
public class history_stats {
    /**
     * The Fights.
     */
    private List<fight_stats> fights = new ArrayList<fight_stats>();

    /**
     * Instantiates a new History stats.
     */
    public history_stats() {}

    /**
     * Gets fights.
     *
     * @return the fights
     */
    public List<fight_stats> getFights() {
        return fights;
    }

    /**
     * Sets fights.
     *
     * @param fights the fights
     */
    @XmlElementWrapper(name = "fights")
    @XmlElement(name = "fight")
    public void setFights(List<fight_stats> fights) {
        this.fights = fights;
    }
}
