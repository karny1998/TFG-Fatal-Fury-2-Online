package lib.objects;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Screen extends JPanel {

    private List<screenObject> screenObjects = new ArrayList<screenObject>();
    private user_controller user;

    static int resX = 1280, resY = 720;

    public Screen() {
        user = new user_controller(character.terry);
        setSurfaceSize();
        Timer user_control = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                screenObjects.add(user.getAnimation());
                repaint();
            }
        });
        user_control.start();
    }

    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for(int i = 0; i < screenObjects.size(); ++i) {
            screenObject img = screenObjects.get(i);
            g2d.drawImage(img.getImg(), img.getX(), img.getY(), img.getImg().getWidth(null)*5,
                    img.getImg().getHeight(null)*5, null);
        }
        screenObjects.clear();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}