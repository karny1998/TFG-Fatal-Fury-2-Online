package lib.objects;

import lib.debug.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import lib.Enums.*;

public class Screen extends JPanel {

    static int resX = 1280, resY = 720, timerDelay = 150, refreshDelay = 150;
    static Boolean debug = true;

    private Map<Item_Type, screenObject> screenObjects = new HashMap<Item_Type, screenObject>();
    private user_controller user;
    private Debug d = new Debug(debug, resX, resY, timerDelay);



    public Screen() {
        user = new user_controller(Playable_Character.TERRY);
        setSurfaceSize();
        Timer user_control = new Timer(timerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //CALCULAR COSAS DEL JUGADOR
                screenObject ply = user.getAnimation();
                screenObjects.put(Item_Type.PLAYER, ply);
            }
        });

        Timer screen_refresh = new Timer(refreshDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        screen_refresh.start();
        user_control.start();
    }

    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    private void doDrawing(Graphics g) {
        Item_Type[] order = {Item_Type.SCENARY, Item_Type.PLAYER, Item_Type.ENEMY,Item_Type.PLAYERTHROWABLE, Item_Type.ENEMYTHROWABLE};
        Graphics2D g2d = (Graphics2D) g;
        d.drawFPS(g2d);
        for(int i = 0; i < order.length; ++i) {
            screenObject img = screenObjects.get(order[i]);
            if(img != null) {
                g2d.drawImage(img.getImg(), img.getX(), img.getY(), img.getWidth(), img.getHeight(), null);
            }
        }
        screenObjects.clear();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}