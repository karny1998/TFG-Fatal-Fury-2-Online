package lib.analysis;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Ia stats.
 */
public class ia_stats {
    /**
     * The Actual fight.
     */
    private int actualFight = 0;
    /**
     * The Fights.
     */
    private List<fight_stats> fights = new ArrayList<fight_stats>();
    /**
     * The history.
     */
    private history_stats history = null;
    /**
     * The file name.
     */
    private String filename = "ia_stadistics.xml";

    /**
     * Instantiates a new Ia stats.
     */
    public ia_stats() {
        fights.add(actualFight, new fight_stats());
        fights.get(actualFight).setId(actualFight);
    }

    /**
     * Load history.
     */
    public void loadHistory(){
        try {
            File file = new File(filename);
            JAXBContext context = JAXBContext.newInstance(history_stats.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            history = (history_stats) unmarshaller.unmarshal(file);

        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Save updated history.
     */
    public void saveUpdatedHistory(){
        if(history == null){
            loadHistory();
        }
        int maxId = 0;
        for(int i = 0; i < history.getFights().size(); ++i){
            if(history.getFights().get(i).getId() > maxId){
                maxId = history.getFights().get(i).getId();
            }
        }
        ++maxId;
        for(int i = 0; i < fights.size(); ++i){
            fights.get(i).setId(maxId);
            history.getFights().add(history.getFights().size(), fights.get(i));
            ++maxId;
        }
        try {
            File file = new File(filename);
            JAXBContext context = JAXBContext.newInstance(history_stats.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(history, file);
        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets actual fight.
     *
     * @return the actual fight
     */
    public fight_stats getActualFight() {
        return fights.get(actualFight);
    }

    /**
     * Sets actual fight.
     *
     * @param actualFight the actual fight
     */
    public void setActualFight(int actualFight) {
        this.actualFight = actualFight;
    }

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
    public void setFights(List<fight_stats> fights) {
        this.fights = fights;
    }

    /**
     * Next fight.
     */
    public void nextFight(){
        ++actualFight;
        fights.add(actualFight, new fight_stats());
        fights.get(actualFight).setId(actualFight);
    }

    /**
     * Gets history.
     *
     * @return the history
     */
    public history_stats getHistory() {
        return history;
    }

    /**
     * Sets history.
     *
     * @param history the history
     */
    public void setHistory(history_stats history) {
        this.history = history;
    }

    /**
     * Gets filename.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets filename.
     *
     * @param filename the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}