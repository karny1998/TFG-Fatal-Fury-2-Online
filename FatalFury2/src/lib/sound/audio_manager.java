package lib.sound;

import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * The type Audio manager.
 */
public class audio_manager {

    /**
     * The constant menu.
     */
    public static menu_audio menu;
    /**
     * The constant fight.
     */
    public static fight_audio fight;

    /**
     * The constant volumen_general.
     */
    private static double volumen_general;
    /**
     * The constant volumen_musica.
     */
    private static double volumen_musica;
    /**
     * The constant volumen_voces.
     */
    private static double volumen_voces;
    /**
     * The constant volumen_sfx.
     */
    private static double volumen_sfx;

    /**
     * The constant guiAbsoluteVolume.
     */
    private static double guiAbsoluteVolume;
    /**
     * The constant onOnline.
     */
    private static boolean onOnline = false;

    /**
     * The constant optionsFilePath.
     */
    private static String optionsFilePath = System.getProperty("user.dir") + "/.files/options.xml";

    /**
     * The enum Estado.
     */
    enum estado {
        /**
         * None estado.
         */
        NONE,
        /**
         * Menus estado.
         */
        MENUS,
        /**
         * Pelea estado.
         */
        PELEA
    }

    /**
     * The constant actual.
     */
    private static estado actual = estado.NONE;

    /**
     * Instantiates a new Audio manager.
     */
    public audio_manager(){
        update();
        guiAbsoluteVolume = volumen_musica;
        menu = new menu_audio();
        if(onOnline){
            menu.update(guiAbsoluteVolume, guiAbsoluteVolume, guiAbsoluteVolume);
        }
        else{
            menu.update(volumen_musica, volumen_sfx, volumen_voces);
        }
        actual = estado.MENUS;
    }

    /**
     * Start fight.
     *
     * @param p1  the p 1
     * @param p2  the p 2
     * @param map the map
     */
    public static void startFight(Playable_Character p1, Playable_Character p2, Scenario_type map){
        if (actual == estado.MENUS){
            menu.close();
            fight = new fight_audio(p1, p2, map);
            if(onOnline){
                fight.update(guiAbsoluteVolume, guiAbsoluteVolume, guiAbsoluteVolume);
            }
            else {
                fight.update(volumen_musica, volumen_sfx, volumen_voces);
            }
            actual = estado.PELEA;
        }
    }

    /**
     * End fight.
     */
    public static void endFight(){
        if (actual == estado.PELEA){
            fight.close();
            menu = new menu_audio();
            if(onOnline){
                menu.update_init(guiAbsoluteVolume, guiAbsoluteVolume, guiAbsoluteVolume);
            }
            else {
                menu.update_init(volumen_musica, volumen_sfx, volumen_voces);
            }
            actual = estado.MENUS;
        }
    }

    /**
     * Update.
     */
    public static void update(){
        readOptions();

        if(onOnline){
            if (actual == estado.PELEA){
                fight.update(guiAbsoluteVolume, guiAbsoluteVolume, guiAbsoluteVolume);
            } else if (actual == estado.MENUS){
                menu.update(guiAbsoluteVolume, guiAbsoluteVolume, guiAbsoluteVolume);
            }
        }
        else {
            if (actual == estado.PELEA){
                fight.update(volumen_musica, volumen_sfx, volumen_voces);
            } else if (actual == estado.MENUS){
                menu.update(volumen_musica, volumen_sfx, volumen_voces);
            }
        }
    }

    /**
     * Update.
     *
     * @param lvl the lvl
     */
    public static void update(double lvl){
        guiAbsoluteVolume = lvl;
        if(fight != null){fight.update(lvl, lvl, lvl);}
        if(menu != null){menu.update(lvl, lvl, lvl);}
    }

    /**
     * Read options.
     */
    public static void readOptions(){
        try {


            File input = new File(optionsFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();


            NodeList p1 = doc.getElementsByTagName("general").item(0).getChildNodes();


            int vols[] = new int[5];
            int indice = 0;
            for (int i = 0; i < p1.getLength(); i++) {
                Node node = p1.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    vols[indice] = Integer.parseInt(node.getTextContent());
                    indice++;
                }
            }
            volumen_general =((double)vols[0] / 100.0);
            volumen_musica =  ((double)vols[1] / 100.0);
            volumen_voces =  ((double)vols[2] / 100.0);
            volumen_sfx =  ((double)vols[3] / 100.0);

            volumen_musica *= volumen_general;
            volumen_voces *= volumen_general;
            volumen_sfx *= volumen_general;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets gui absolute volume.
     *
     * @return the gui absolute volume
     */
    public static double getGuiAbsoluteVolume() {
        return guiAbsoluteVolume;
    }

    /**
     * Sets gui absolute volume.
     *
     * @param guiAbsoluteVolume the gui absolute volume
     */
    public static void setGuiAbsoluteVolume(double guiAbsoluteVolume) {
        audio_manager.guiAbsoluteVolume = guiAbsoluteVolume;
    }

    /**
     * Is on online boolean.
     *
     * @return the boolean
     */
    public static boolean isOnOnline() {
        return onOnline;
    }

    /**
     * Sets on online.
     *
     * @param onOnline the on online
     */
    public static void setOnOnline(boolean onOnline) {
        audio_manager.onOnline = onOnline;
    }
}
