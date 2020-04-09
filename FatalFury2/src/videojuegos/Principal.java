package videojuegos;

import lib.input.controlListener;
import lib.objects.Screen;

import javax.swing.*;
import java.awt.*;


public class Principal extends JFrame {

    public Principal() {
        controlListener control = new controlListener();

        addKeyListener(control);

        setFocusable(true);

        initUI();
    }

    private void initUI() {
        add(new Screen());
        setResizable(false);
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