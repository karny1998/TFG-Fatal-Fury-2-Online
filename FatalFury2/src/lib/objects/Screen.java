package lib.objects;

import lib.Enums.Item_Type;
import lib.debug.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Screen extends JPanel {

    static int resX = 1280, resY = 720, timerDelay = 1, refreshDelay = 15;
    static Boolean debug = true;
    private Map<Item_Type, screenObject> screenObjects = new HashMap<Item_Type, screenObject>();
    private game_controller game;
    private Debug d = new Debug(debug, resX, resY, refreshDelay);
    private Map<String, Timer> timers = new HashMap<String, Timer>();


    private void startGame(){
        Timer game_control = new Timer(timerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.getFrame(screenObjects);
            }
        });
        game_control.start();
        timers.put("game_control", game_control);
    }

    private void stopGame(){
        timers.get("game_control").stop();
        System.exit(0);
    }

    public Screen() {
        game = new game_controller();

        setSurfaceSize();
        Timer screen_refresh = new Timer(refreshDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        screen_refresh.start();

        startGame();
    }

    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    private void doDrawing(Graphics g) {
        Item_Type[] order = {Item_Type.SCENARY_1, Item_Type.SCENARY_2, Item_Type.ENEMY,
                            Item_Type.PLAYER, Item_Type.ENEMYTHROWABLE, Item_Type.PLAYERTHROWABLE,
                            Item_Type.MENU};
        Graphics2D g2d = (Graphics2D) g;
        for(int i = 0; i < order.length; ++i) {
            screenObject img = screenObjects.get(order[i]);
            if(img != null) {
                g2d.drawImage(img.getImg(), img.getX(), img.getY(), img.getWidth(), img.getHeight(), null);
            }
        }
        /*game.getFight().getPlayerControler().getPlayer().getHitbox().drawHitBox(g2d);
        game.getFight().getEnemyControler().getPlayer().getHitbox().drawHitBox(g2d);
        game.getFight().getPlayerControler().getPlayer().getHurtbox().drawHitBox(g2d);
        game.getFight().getEnemyControler().getPlayer().getHurtbox().drawHitBox(g2d);*/
    }

    private void doDrawingInMenu(Graphics g) {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        d.drawFPS(g);
        doDrawing(g);
    }
}
