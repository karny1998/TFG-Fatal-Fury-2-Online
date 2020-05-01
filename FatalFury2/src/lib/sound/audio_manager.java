package lib.sound;

import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class audio_manager {

    public static menu_audio menu;
    public static fight_audio fight;

    private static double volumen_general;
    private static double volumen_musica;
    private static double volumen_voces;
    private static double volumen_sfx;


    private static String optionsFilePath = "files/options.xml";

    enum estado {
        NONE,
        MENUS,
        PELEA
    }

    private static estado actual = estado.NONE;

    public audio_manager(){
        update();
        menu = new menu_audio();
        menu.update(volumen_musica, volumen_sfx, volumen_voces);
        actual = estado.MENUS;
    }

    public static void startFight(Playable_Character p1, Playable_Character p2, Scenario_type map){
        if (actual == estado.MENUS){
            menu.close();
            fight = new fight_audio(p1, p2, map);
            fight.update(volumen_musica, volumen_sfx, volumen_voces);
            actual = estado.PELEA;
        }
    }

    public static void endFight(){
        if (actual == estado.PELEA){
            menu = new menu_audio();
            menu.update(volumen_musica, volumen_sfx, volumen_voces);
            fight.close();
            actual = estado.MENUS;
        }
    }

    public static void update(){
        readOptions();
        if (actual == estado.PELEA){
            fight.update(volumen_musica, volumen_sfx, volumen_voces);
        } else if (actual == estado.MENUS){
            menu.update(volumen_musica, volumen_sfx, volumen_voces);
        }
    }

    public static void readOptions(){
        try {
            File input = new File(optionsFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();

            NodeList p1 = doc.getElementsByTagName("volumen").item(0).getChildNodes();


            int vols[] = new int[4];
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


}
