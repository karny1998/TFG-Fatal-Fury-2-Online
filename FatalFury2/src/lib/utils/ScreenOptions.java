package lib.utils;

import lib.objects.networking.gui.gameGUI;
import videojuegos.Principal;

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
     * The constant fullscreen.
     */
    private static boolean fullscreen = false;

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
            fullscreen = true;
            ((gameGUI)(((Principal)screen).getGui())).setFullscreen(true);
        }else {
            screen.dispose();
            screen.setUndecorated(false);
            Dimension d = new Dimension(1280,720);
            screen.setSize(d);
            screen.setExtendedState(screen.getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
            fullscreen = false;
            ((gameGUI)(((Principal)screen).getGui())).setFullscreen(false);
        }



        screen.setVisible(true);

        screen.setResizable(false);
        screen.setFocusable(true);
        screen.setLocationRelativeTo(null);

    }

    /**
     * Is fullscreen boolean.
     *
     * @return the boolean
     */
    public static boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Sets fullscreen.
     *
     * @param fullscreen the fullscreen
     */
    public static void setFullscreen(boolean fullscreen) {
        ScreenOptions.fullscreen = fullscreen;
    }
}
