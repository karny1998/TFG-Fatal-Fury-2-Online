package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;
import lib.debug.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Screen extends JPanel {

    static int resX = 1280, resY = 720, timerDelay = 1, refreshDelay = 15;
    static Boolean debug = true, inGame = true;
    private Map<Item_Type, screenObject> screenObjects = new HashMap<Item_Type, screenObject>();
    private fight_controller fight;
    private scenary scene;
    private Debug d = new Debug(debug, resX, resY, refreshDelay);
    private Map<String, Timer> timers = new HashMap<String, Timer>();


    private void startGame(){
        Timer fight_control = new Timer(timerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fight.getAnimation(screenObjects);
            }
        });
        Timer scenary_control = new Timer(timerDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //CALCULAR COSAS DEL JUGADOR
                screenObject ply = scene.getFrame1();
                screenObjects.put(Item_Type.SCENARY_1, ply);
                ply = scene.getFrame2();
                screenObjects.put(Item_Type.SCENARY_2, ply);
            }
        });
        fight_control.start();
        scenary_control.start();
        timers.put("fight_control", fight_control);
        timers.put("scenary_control", scenary_control);
    }

    private void stopGame(){
        timers.get("fight_control").stop();
        timers.get("scenary_control").stop();
        inGame = false;
    }

    public Screen() {
        user_controller user = new user_controller(Playable_Character.TERRY);
        enemy_controller enemy = new enemy_controller(Playable_Character.TERRY);
        fight = new fight_controller(user,enemy);

        scene = new scenary();
        scene.setAnim1(usa.generateAnimation1());
        scene.setAnim2(usa.generateAnimation2());


        setSurfaceSize();
        Timer screen_refresh = new Timer(refreshDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        screen_refresh.start();
        //Por el momento fijo
        startGame();
    }

    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    private void doDrawingInGame(Graphics g) {
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
        fight.getPlayerControler().getPlayer().getHitbox().drawHitBox(g2d);
        fight.getEnemyControler().getPlayer().getHitbox().drawHitBox(g2d);
        fight.getPlayerControler().getPlayer().getHurtbox().drawHitBox(g2d);
        fight.getEnemyControler().getPlayer().getHurtbox().drawHitBox(g2d);
    }

    private void doDrawingInMenu(Graphics g) {

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        d.drawFPS(g);
        if(inGame) {
            doDrawingInGame(g);
        }
        else{
            doDrawingInMenu(g);
        }
    }
}
