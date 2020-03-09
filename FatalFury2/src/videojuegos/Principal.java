package videojuegos;

import lib.objects.Screen;

import javax.swing.*;
import java.awt.*;


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
                //TEST

                Principal ex = new Principal();
                ex.setVisible(true);
            }
        });
    }
}