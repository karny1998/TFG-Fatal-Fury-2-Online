package videojuegos;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class dir{
    public double x, y, z = 0;
    dir(double _x, double _y){x = _x; y = _y;}
}

class Screen extends JPanel {

    private Image mshi;

    static int resX = 1920, resY = 1080, x = resX/2, y = resY/2, incX = 1, incY = 1;
    int wide = 120, height = 160;
    static int state = 0;
    List<dir> lista = new ArrayList<dir>();

    public Screen() {

        loadImage();
        setSurfaceSize();
        Timer t = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dir d = new dir(new Random().nextDouble()*2-1.15, new Random().nextDouble()*2-1.15);
                while(Math.sqrt(d.x*d.x+d.y*d.y) > 0.9 || Math.sqrt(d.x*d.x+d.y*d.y) < 0.85) {
                    d = new dir(new Random().nextDouble()*2-1.15, new Random().nextDouble()*2-1.15);
                }
                lista.add(d);
            }
        });
        Timer t2 = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < lista.size(); ++i) {
                    lista.get(i).z++;
                    if(lista.get(i).z > 2000) {lista.remove(i);--i;}
                }
                repaint();
            }
        });
        t.start();
        t2.start();
    }

    private void loadImage() {
        mshi = new ImageIcon("foto.jpeg").getImage();
    }

    private void setSurfaceSize() {

        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for(int i = lista.size()-1; i > -1; --i) {
            //g2d.drawImage(mshi, (int)(d.z*d.x)+x, (int)(d.z*d.y)+y, null);
            dir d = lista.get(i);
            g2d.drawImage(mshi, (int)(d.z*d.x)+x, (int)(d.z*d.y)+y, 1+(int)(d.z*wide/1000), 1+(int)(d.z*height/1000), null);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
}

public class Principal extends JFrame {

    public Principal() {
        initUI();
    }

    private void initUI() {
        add(new Screen());
        pack();
        setTitle("Fatal Fury 2");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Principal ex = new Principal();
                ex.setVisible(true);
            }
        });
    }
}