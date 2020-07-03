package lib.input;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The type Control listener.
 */
// Clase encargada de gestionar los eventos de teclado
public class controlListener implements KeyListener {

    /**
     * The Key status.
     */
// ALmacena el estado de cada tecla
    public static Boolean[] keyStatus;
    /**
     * The Input queue 1.
     */
// Cola de los 10 ultimos inputs
    static Queue<Integer> inputQueue_1, /**
     * The Input queue 2.
     */
    inputQueue_2, /**
     * The General.
     */
    general;
    /**
     * The Queue size.
     */
    static int QUEUE_SIZE = 10;
    /**
     * The Buttons.
     */
    static int BUTTONS = 10;
    /**
     * The constant optionsFilePath.
     */
    private static String optionsFilePath = System.getProperty("user.dir") + "/.files/options.xml";
    /**
     * The Current key.
     */
    static int currentKey, /**
     * The Last key.
     */
    lastKey;

    /**
     * The Mando 1.
     */
// Almacena el estado de cada tecla perteneciente a los mandos
    public static Boolean[] mando1, /**
     * The Mando 2.
     */
    mando2, /**
     * The Menus 1.
     */
    menus1, /**
     * The Menus 2.
     */
    menus2;
    /**
     * The constant enter1.
     */
    public static Boolean enter1, /**
     * The Enter 2.
     */
    enter2;

    /**
     * The constant AR_INDEX.
     */
    public static final int AR_INDEX = 0;
    /**
     * The constant AB_INDEX.
     */
    public static final int AB_INDEX = 1;
    /**
     * The constant IZ_INDEX.
     */
    public static final int IZ_INDEX = 2;
    /**
     * The constant DE_INDEX.
     */
    public static final int DE_INDEX = 3;
    /**
     * The constant A_INDEX.
     */
    public static final int A_INDEX = 4;
    /**
     * The constant B_INDEX.
     */
    public static final int B_INDEX = 5;
    /**
     * The constant C_INDEX.
     */
    public static final int C_INDEX = 6;
    /**
     * The constant D_INDEX.
     */
    public static final int D_INDEX = 7;
    /**
     * The constant ENT_INDEX.
     */
    public static final int ENT_INDEX = 8;
    /**
     * The constant ESC_INDEX.
     */
    public static final int ESC_INDEX = 9;


    /**
     * The Movimientos.
     */
// Vector de correspondencias entre pulsaciones de teclado y acciones a realizar por el personaje
    public static String[] movimientos = {
                        "AR",
                        "AB",
                        "IZ",
                        "DE",
                        "A",
                        "B",
                        "C",
                        "D"
                    };

    /**
     * The Key bindings 1.
     */
    static int[] keyBindings1, /**
     * The Key bindings 2.
     */
    keyBindings2;


    /**
     * Instantiates a new Control listener.
     */
// Constructor de la clase, inicializa todos los vectores
    public controlListener(){
        keyStatus = new Boolean[526];

        for(int i = 0; i < keyStatus.length; i++){
            keyStatus[i] = false;
        }
        mando1 = new Boolean[BUTTONS];
        mando2 = new Boolean[BUTTONS];
        menus1 = new Boolean[BUTTONS];
        menus2 = new Boolean[BUTTONS];
        enter1 = false;
        enter2 = false;

        for(int i = 0; i < mando1.length; i++){
            mando1[i] = false;  mando2[i] = false; menus1[i] = false; menus2[i] = false;
        }

        keyBindings1 = new int[BUTTONS];
        keyBindings2 = new int[BUTTONS];

        update();

        inputQueue_1 = new LinkedList<>();
        inputQueue_2 = new LinkedList<>();
        lastKey = -1;
        currentKey = -1;

    }


    /**
     * Key typed.
     *
     * @param e the e
     */
// Implementación de las funciones asbtractas de la clase KeyListener
    @Override
    public void keyTyped(KeyEvent e) {
        currentKey = e.getKeyChar();
    }

    /**
     * Key pressed.
     *
     * @param e the e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() < keyStatus.length){
            this.keyStatus[e.getKeyCode()] = true;
            lastKey = e.getKeyCode();
            for(int i = 0; i < keyBindings1.length; i++){
                if(e.getKeyCode() == keyBindings1[i]){
                    mando1[i] = true;
                    menus1[i] = true;
                    this.inputQueue_1.add(e.getKeyCode());
                    if(this.inputQueue_1.size() > this.QUEUE_SIZE){
                        this.inputQueue_1.remove();
                    }

                }
                if(e.getKeyCode() == keyBindings2[i]){
                    mando2[i] = true;
                    menus2[i] = true;
                    this.inputQueue_2.add(e.getKeyCode());
                    if(this.inputQueue_2.size() > this.QUEUE_SIZE){
                        this.inputQueue_2.remove();
                    }
                }
            }
        }
    }

    /**
     * Key released.
     *
     * @param e the e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() < keyStatus.length) {
            this.keyStatus[e.getKeyCode()] = false;
            for (int i = 0; i < keyBindings1.length; i++) {
                if (e.getKeyCode() == keyBindings1[i]) {
                    mando1[i] = false;
                }
                if (e.getKeyCode() == keyBindings2[i]) {
                    mando2[i] = false;
                }
            }
            lastKey = -1;
            currentKey = -1;
        }
    }


    /**
     * Get current key int.
     *
     * @return the int
     */
// Devuelve el indice de la tecla pulsada actualmente
    public static int getCurrentKey(){
        return (int) currentKey;
    }

    /**
     * Get last key int.
     *
     * @param player the player
     * @return the int
     */
// Devuelve el indice de la ultima tecla pulsada por el jugador 1, jugador 2, o en general
    public static int getLastKey(int player){

        switch (player){
            case 0:
                return lastKey;
            case 1:
                if(inputQueue_1.size() == 0){
                    return  -1;
                }
                return inputQueue_1.peek();
            case 2:
                if(inputQueue_2.size() == 0){
                    return  -1;
                }
                return inputQueue_2.peek();
            default:
                throw new RuntimeException("Invalid player number");
        }
    }

    /**
     * Is pressed boolean.
     *
     * @param keycode the keycode
     * @return the boolean
     */
// Devuelve cierto si la tecla pasada como parametro esta pulsada
    public static boolean isPressed(int keycode){
        if(keycode == -1){
            return false;
        }
        return keyStatus[keycode];
    }

    /**
     * Get queue queue.
     *
     * @param player the player
     * @return the queue
     */
// Devuelve la cola que almacena los 10 ultimos inputs del jugador 1 o del jugador 2
    public static Queue<Integer> getQueue(int player){
        switch (player){
            case 1:
                return inputQueue_1;
            case 2:
                return inputQueue_2;
            default:
                throw new RuntimeException("Invalid player number");
        }
    }

    /**
     * Any key pressed boolean.
     *
     * @return the boolean
     */
// Devuelve cierto si se ha pulsado al menos una tecla del teclado
    public static boolean anyKeyPressed(){
        Boolean status = false;
        for(int i = 0; i < keyStatus.length; i++){
            status = status || keyStatus[i];
        }
        return status;
    }

    /**
     * Get status boolean.
     *
     * @param player the player
     * @param index  the index
     * @return the boolean
     */
// Devuelve el estado de la tecla index en el mando del jugador 1, j ugador 2 o de ambos
    public static boolean getStatus(int player, int index){
        switch (player){
            case 0:
                return mando1[index] || mando2[index];
            case 1:
                return mando1[index];
            case 2:
                return mando2[index];
            default:
                throw new RuntimeException("Invalid player number");
        }
    }

    /**
     * Menu input boolean.
     *
     * @param player the player
     * @param index  the index
     * @return the boolean
     */
// Devuelve el estado de la tecla index durante la navegación de menús
    // en el mando del jugador 1, j ugador 2 o de ambos.
    public static boolean menuInput(int player, int index){
        switch (player){
            case 0:
                if(menus1[index] || menus2[index]){
                    menus1[index] = false;
                    menus2[index] = false;
                    return mando1[index] || mando2[index];
                } else {
                    return false;
                }
            case 1:
                if(menus1[index]){
                    menus1[index] = false;
                    return mando1[index];
                } else {
                    return false;
                }
            case 2:
                if(menus2[index]){
                    menus2[index] = false;
                    return mando2[index];
                } else {
                    return false;
                }
            default:
                throw new RuntimeException("Invalid player number");
        }
    }

    /**
     * Get move string.
     *
     * @param player the player
     * @return the string
     */
// Metodo encargado de transformar el estado de un mando en los movimientos del personaje
    public static String getMove(int player){
        Boolean[] actual;
        String mov = "";
        switch (player){
            case 1:
                actual = mando1;
                break;
            case 2:
                actual =  mando2;
                break;
            default:
                throw new RuntimeException("Invalid player number");
        }
        for(int i = 0; i < actual.length - 2; i++){
            if( actual[i] ){
                if(!mov.equals("")){mov += "-";}
                mov += movimientos[i];
            }
        }
        return mov;
    }

    /**
     * Update.
     */
// Método encargado de leer el fichero de opciones y actualizar los valores de las teclas asignadas a los mandos
    public static void update(){
        try {
            File input = new File(optionsFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();

            Node opciones = doc.getChildNodes().item(0);
            NodeList p1 = doc.getElementsByTagName("controles_jugador_1").item(0).getChildNodes();
            NodeList p2 = doc.getElementsByTagName("controles_jugador_2").item(0).getChildNodes();

            int indice = 0;
            for (int i = 0; i < p1.getLength(); i++) {
                Node node = p1.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    keyBindings1[indice] = Integer.parseInt(node.getTextContent());
                    indice++;
                }
            }
            indice = 0;
            for (int i = 0; i < p2.getLength(); i++) {
                Node node = p2.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    keyBindings2[indice] = Integer.parseInt(node.getTextContent());
                    indice++;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Get back boolean.
     *
     * @return the boolean
     */
// Devuelve cierto si la tecla Backspace está siendo pulsada
    public static boolean getBack(){
        return keyStatus[8];
    }

    /**
     * Get esc boolean.
     *
     * @return the boolean
     */
// Devuelve cierto si la tecla Escape está siendo pulsada
    public static boolean getEsc(){
        return keyStatus[27];
    }


}
