package videojuegos;

import lib.Enums.Audio_Type;
import lib.Enums.Music;
import lib.objects.Screen;
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
                //TEST
                Sound announcer =  new Sound(Audio_Type.Music_Audio);
                announcer.playMusic(Music.TEST, false);
                Principal ex = new Principal();
                ex.setVisible(true);
            }
        });
    }
}