package lib.utils;

import javax.swing.*;
import java.awt.*;

public class ScreenOptions {

    private static JFrame screen;

    public static void init(JFrame screen_){
        screen = screen_;
    }


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
