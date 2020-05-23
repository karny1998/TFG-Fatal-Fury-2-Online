package lib.objects;

import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.Enums.ia_type;
import lib.objects.ia_processors.processor_life_round_based;
import lib.objects.ia_processors.processor_tendencies_player_based;
import lib.utils.Pair;
import lib.utils.xmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

public class ia_loader {
    public ia_loader(){}

    private static String path = "/files/ia/";
    public enum dif {
        EASY, NORMAL, HARD, VERY_HARD
    }

    private enum indexes {
        NORMAL_JUMP,
        JUMP_ROLL_RIGHT,
        JUMP_ROLL_LEFT,
        STANDING,
        WALKING,
        WALKING_BACK,
        SOFT_PUNCH,
        SOFT_KICK,
        HARD_PUNCH,
        HARD_KICK,
        CROUCH,
        CROUCHED_WALKING,
        CROUCHED_BLOCK,
        CROUCHING_SOFT_PUNCH,
        CROUCHING_SOFT_KICK,
        CROUCHING_HARD_PUNCH,
        CROUCHING_HARD_KICK,
        THROW,
        DASH,
        CHARGED_PUNCH_A,
        REVERSE_KICK_B,
        SPIN_PUNCH_A
    }
    private static ia_type tipos[][];
    private static ia_processor procesamiento[];
    private static Double pesos[][] = new Double[4][5];
    private static russian_roulette ruleta = new russian_roulette();

    // Carga la información contenida en el fichero de la ia de cada personaje
    public static Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>> loadIA(Playable_Character c, dif d){
        String file = path + c.toString() + "/" + d.toString() +".xml";
        ruleta = new russian_roulette();
        try {
            InputStream is = ia_loader.class.getResourceAsStream(file);
            Document doc = xmlReader.open(is);
            doc.getDocumentElement().normalize();


            for(int i = 0; i < indexes.values().length; i++){
                Node actual = doc.getElementsByTagName( indexes.values()[i].toString() ).item(0);
                if (actual.getNodeType() == Node.ELEMENT_NODE) {
                    double prob = Double.parseDouble(actual.getTextContent());
                    ruleta.addComponent(prob, Movement.valueOf(indexes.values()[i].toString()));
                }
            }
            ruleta.fillRoulette();

            int numC = Integer.parseInt(doc.getElementsByTagName( "caracteres" ).item(0).getTextContent().replaceAll("\\s+",""));

            String ronda1 = doc.getElementsByTagName( "ronda_1" ).item(0).getTextContent().replaceAll("\\s+","");
            String ronda2 = doc.getElementsByTagName( "ronda_2" ).item(0).getTextContent().replaceAll("\\s+","");
            String ronda3 = doc.getElementsByTagName( "ronda_3" ).item(0).getTextContent().replaceAll("\\s+","");
            String ronda4 = doc.getElementsByTagName( "ronda_4" ).item(0).getTextContent().replaceAll("\\s+","");

            String[] rondas = new String[]{ronda1, ronda2, ronda3, ronda4};
            ia_type r1[] = new ia_type[numC];
            ia_type r2[] = new ia_type[numC];
            ia_type r3[] = new ia_type[numC];
            ia_type r4[] = new ia_type[numC];
            tipos = new ia_type[][]{r1, r2, r3, r4};

            for(int j = 0; j < 4; j++){
                for(int i = 0; i < numC; i++){
                    char aux = rondas[j].charAt(i);
                    ia_type actual = null;
                    switch (aux){
                        case 'A':
                            actual = ia_type.AGRESSIVE;
                            break;
                        case 'B':
                            actual = ia_type.BALANCED;
                            break;
                        case 'D':
                            actual = ia_type.DEFENSIVE;
                            break;
                    }
                    tipos[j][i] = actual;
                }
            }

            boolean eleccion[] = new boolean[2];
            ia_processor procesadores[] = {new processor_life_round_based(), new processor_tendencies_player_based()};

            eleccion[0] = Boolean.parseBoolean(doc.getElementsByTagName( "processor_life_round_based" ).item(0).getTextContent().replaceAll("\\s+",""));
            eleccion[1] = Boolean.parseBoolean(doc.getElementsByTagName( "processor_tendencies_player_based" ).item(0).getTextContent().replaceAll("\\s+",""));
            int tam = 0;
            if(eleccion[0]){
                tam++;
            } if(eleccion[1]){
                tam++;
            }
            procesamiento = new ia_processor[tam];
            int index = 0;
            for(int i = 1; i >= 0; i--){
                if(eleccion[i]){
                    procesamiento[index] = procesadores[i];
                    index++;
                }
            }

            NodeList listaPesos = doc.getElementsByTagName("pesos").item(0).getChildNodes();
            int j = 0;
            int indice = 0;
            for (int i = 0; i < listaPesos.getLength(); i++) {
                Node node = listaPesos.item(i);
                if(indice >= 5){j++; indice = 0;}
                if(j >= 4) {break;}
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    pesos[j][indice] = Double.parseDouble(node.getTextContent().replaceAll("\\s+",""));
                    indice++;
                }
            }

            return new Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>>(new Pair<>(procesamiento, ruleta), new Pair<ia_type[][], Double[][]>(tipos, pesos));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }





}
