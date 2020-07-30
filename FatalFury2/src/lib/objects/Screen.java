package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.debug.Debug;
import lib.utils.Pair;
import videojuegos.Principal;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static lib.Enums.Item_Type.*;

/**
 * The type Screen.
 */
// Clase que se encarga de mostrar todo por pantalla
public class Screen extends JPanel{
    Principal principal;
    /**
     * The constant resX.
     */
// Resolución del juego, tiempo de actualización de los cálculos, y tiempo de refresco de la pantalla
    static int resX = 1280, /**
     * The Res y.
     */
    resY = 720, /**
     * The Timer delay.
     */
    timerDelay = 1, /**
     * The Refresh delay.
     */
    refreshDelay = 15;
    /**
     * The constant debug.
     */
// Si se está debugeando o no
    static Boolean debug = true;
    /**
     * The D.
     */
    private Debug d = new Debug(debug, resX, resY, refreshDelay);
    /**
     * The Screen objects.
     */
// Lista de objetos a mostrar por pantalla, identificados por Item_types
    private Map<Item_Type, screenObject> screenObjects = new HashMap<Item_Type, screenObject>();
    /**
     * The Game.
     */
// El controlador del juego en sí
    private game_controller game;
    /**
     * The Timers.
     */
// Lista de timers (en verdad ya no sería necesario)
    private Map<String, Timer> timers = new HashMap<String, Timer>();
    /**
     * The List int.
     */
// Lista de los timpos de Item_types que pertecen a la interfaz
    List<Item_Type> listInt;

    private boolean showingGUI = false;
    /**
     * The Order.
     */
// Orden de pintado por pantalla de los Item_type
    Item_Type[] order = {Item_Type.SCENARY_1, Item_Type.SCENARY_2, Item_Type.SHADOW_1, Item_Type. SHADOW_2, Item_Type.ENEMY,
            Item_Type.PLAYER, Item_Type.ENEMYTHROWABLE, Item_Type.PLAYERTHROWABLE, Item_Type.ANNOUNCEMENT,
            SCORE_FRAME,BONUS,
            LIFE_TEXT, LIFEN1, LIFEN2, LIFEN3, LIFEN4, LIFEN5,
            TIME_TEXT, TIMEN1, TIMEN2, TIMEN3, TIMEN4, TIMEN5,
            SCORE_TEXT, SCOREN1, SCOREN2, SCOREN3, SCOREN4, SCOREN5,
            TOTAL_TEXT, TOTALN1, TOTALN2, TOTALN3, TOTALN4, TOTALN5,
            Item_Type.MENU, Item_Type.SURE, Item_Type.TIMER1, Item_Type.TIMER2, Item_Type.TIMERFRAME,
            Item_Type.HPBAR1, Item_Type.HPBAR2, Item_Type.NAME1, Item_Type.NAME2,
            Item_Type.INDICATOR1, Item_Type.INDICATOR2,
            Item_Type.BUBBLE1, Item_Type.BUBBLE2, Item_Type.BUBBLE3, Item_Type.BUBBLE4,
            Item_Type.P1_SELECT, Item_Type.P2_SELECT, Item_Type.P1_MUG, Item_Type.P2_MUG, Item_Type.P1_NAME, Item_Type.P2_NAME,
            Item_Type.BACKGROUND};

    /**
     * Start game.
     */
// Inicia el juego
    private void startGame(){
        // Timer encargado de gestionar el juego en si
        Timer game_control = new Timer(timerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.getFrame(screenObjects);
            }
        });
        game_control.start();
        timers.put("game_control", game_control);
    }

    /**
     * Stop game.
     */
// Para el timer del juego
    private void stopGame(){
        timers.get("game_control").stop();
        System.exit(0);
    }

    /**
     * Instantiates a new Screen.
     */
// Inicia todo
    public Screen(Principal principal) {
        this.principal = principal;
        setSurfaceSize();
        // Controlador del juego
        game = new game_controller(this);

        // Timer encargado del refresco de la pantalla
        Timer screen_refresh = new Timer(refreshDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!showingGUI) {
                    repaint();
                }
            }
        });
        screen_refresh.start();
        // Inicia el juego
        startGame();
        // Item_types que pertenecen a la interfaz
        Item_Type interfacee[] = {HPBAR1, HPBAR2, NAME1, NAME2, INDICATOR1, INDICATOR2, BUBBLE1,
                BUBBLE2, BUBBLE3, BUBBLE4, TIMER1, TIMER2, TIMERFRAME,};
        listInt = Arrays.asList(interfacee);
        this.setLayout(null);
    }

    /**
     * Sets surface size.
     */
//Configura la resulución inicial
    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    /**
     * Do drawing.
     *
     * @param g the g
     */
// Muestra por pantalla los screenObjects en la lista
    private void doDrawing(Graphics g) {
        // Ajusta el toolkit en base al SO para mantener el rendimiento
        String OS = System.getProperty("os.name").toLowerCase();
        if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix")){
            Toolkit.getDefaultToolkit().sync();
        }

        Graphics2D g2d = (Graphics2D) g;
        Dimension d = this.getSize();
        // Offset a bajar el scenario
        int offset = 0;
        // Si ha terminado la ronda o pelena el offset es 0
        if(game.getFight() != null && !game.getFight().getEnd()) {
            Movement s = game.getFight().getPlayer().getPlayer().getState();
            boolean ended = s == Movement.VICTORY_FIGHT || s == Movement.VICTORY_ROUND || s == Movement.DEFEAT;
            s = game.getFight().getEnemy().getPlayer().getState();
            ended = (ended || s == Movement.VICTORY_FIGHT || s == Movement.VICTORY_ROUND || s == Movement.DEFEAT);
            if(!ended) {
                offset = (int) (game.getFight().getCurrentRound().getScenaryOffsetY() * 3);
                g2d.translate(0, -offset);
            }
        }
        // Se escala la pantalla
        g2d.scale((double)d.width/(double)resX,(double)d.height/(double)resY);
        // Se pintan por pantalla todos los screenObjects en el orden indicado
        for(int i = 0; i < order.length; ++i) {
            screenObject img = screenObjects.get(order[i]);
            if(img != null) {
                // Si es parte de la interfaz se ajusta en base al offset para que no se baje
                if(listInt.contains(order[i])){
                    g2d.drawImage(img.getImg(), img.getX(), img.getY()+offset, img.getWidth(), img.getHeight(), null);
                }
                else {
                    g2d.drawImage(img.getImg(), img.getX(), img.getY(), img.getWidth(), img.getHeight(), null);
                }
            }
        }
        // Si hay una pelea en marcha se aplica el offeset calculado
        if(game.getFight() != null) {
            game.writeDirecly(g2d, offset);
        }
        // En caso contrario se pinta con offset 0
        else {
            game.writeDirecly(g2d, 0);
        }
    }

    /**
     * Paint component.
     *
     * @param g the g
     */
    @Override
    public void paintComponent(Graphics g) {
        if(showingGUI)System.out.println("Ssssssssssss");
        super.paintComponent(g);
        d.drawFPS(g);
        doDrawing(g);
    }

    public Map<Item_Type, screenObject> getScreenObjects() {
        return screenObjects;
    }

    public void setScreenObjects(Map<Item_Type, screenObject> screenObjects) {
        this.screenObjects = screenObjects;
    }

    public boolean isShowingGUI() {
        return showingGUI;
    }

    public void setShowingGUI(boolean showingGUI) {
        this.showingGUI = showingGUI;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }
}
