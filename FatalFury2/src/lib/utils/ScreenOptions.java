package lib.utils;

import javax.swing.*;
import java.awt.*;

/**
 * The type Screen options.
 */
// Clase que se encarga de actualizar el modo en el que se muestra el juego
// pantalla completa o en ventana
public class ScreenOptions {

    /**
     * The constant screen.
     */
    private static JFrame screen;

    /**
     * Init.
     *
     * @param screen_ the screen
     */
    public static void init(JFrame screen_){
        screen = screen_;
    }


    /**
     * Update.
     */
    public static void update(){
        screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if(xmlReader.IsFullscren()){
            screen.dispose();
            screen.setUndecorated(true);
            screen.setExtendedState(screen.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        }else {
            screen.dispose();
            screen.setUndecorated(false);
            Dimension d = new Dimension(1280,720);
            screen.setSize(d);
            screen.setExtendedState(screen.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
        }



        screen.setVisible(true);

        screen.setResizable(false);
        screen.setFocusable(true);
        screen.setLocationRelativeTo(null);

    }
}
