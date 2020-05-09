package lib.objects;

import lib.Enums.Item_Type;
import lib.debug.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static lib.Enums.Item_Type.*;

// Clase que se encarga de mostrar todo por pantalla
public class Screen extends JPanel {
    // Resolución del juego, tiempo de actualización de los cálculos, y tiempo de refresco de la pantalla
    static int resX = 1280, resY = 720, timerDelay = 1, refreshDelay = 15;
    // Si se está debugeando o no
    static Boolean debug = true;
    private Debug d = new Debug(debug, resX, resY, refreshDelay);
    // Lista de objetos a mostrar por pantalla, identificados por Item_types
    private Map<Item_Type, screenObject> screenObjects = new HashMap<Item_Type, screenObject>();
    // El controlador del juego en sí
    private game_controller game;
    // Lista de timers (en verdad ya no sería necesario)
    private Map<String, Timer> timers = new HashMap<String, Timer>();

    // Inicia el juego
    private void startGame(){
        // Timer encargado de recalcular
        Timer game_control = new Timer(timerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.getFrame(screenObjects);
            }
        });
        game_control.start();
        timers.put("game_control", game_control);
    }

    // Para el timer del juego
    private void stopGame(){
        timers.get("game_control").stop();
        System.exit(0);
    }

    // Inicia todo
    public Screen() {
        setSurfaceSize();
        // Controlador del juego
        game = new game_controller();

        // Timer encargado del refresco de la pantalla
        Timer screen_refresh = new Timer(refreshDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        screen_refresh.start();
        // Inicia el juego
        startGame();
    }

    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    // Muestra por pantalla los screenObjects en la lista
    private void doDrawing(Graphics g) {

        String OS = System.getProperty("os.name").toLowerCase();
        if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix")){
            Toolkit.getDefaultToolkit().sync();
        }


        Item_Type[] order = {Item_Type.SCENARY_1, Item_Type.SCENARY_2, Item_Type.ENEMY,
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
                            Item_Type.P1_SELECT, Item_Type.P2_SELECT, Item_Type.P1_MUG, Item_Type.P2_MUG, Item_Type.P1_NAME, Item_Type.P2_NAME};
        Graphics2D g2d = (Graphics2D) g;
        Dimension d = this.getSize();
        g2d.scale((double)d.width/(double)resX,(double)d.height/(double)resY);

        for(int i = 0; i < order.length; ++i) {
            screenObject img = screenObjects.get(order[i]);
            if(img != null) {
                g2d.drawImage(img.getImg(), img.getX(), img.getY(), img.getWidth(), img.getHeight(), null);
            }
        }
        game.writeDirecly(g2d);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        d.drawFPS(g);
        doDrawing(g);
    }
}
