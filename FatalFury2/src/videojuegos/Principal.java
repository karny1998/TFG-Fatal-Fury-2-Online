package videojuegos;

import lib.Enums.Playable_Character;
import lib.objects.*;
import lib.sound.Sound;

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
                Sound test =  new Sound(Playable_Character.ANDY);
                test.play("Ost/Rivers.wav");
                Principal ex = new Principal();
                ex.setVisible(true);
            }
        });
    }
}