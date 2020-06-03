package lib.objects;

import lib.Enums.Item_Type;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Display timer.
 */
public class displayTimer {
    /**
     * The Path.
     */
// Path
    String path = "/assets/sprites/fight_interface/timer/";
    /**
     * The Numbers.
     */
// Imágenes necesarias
    private List<Image> numbers = new ArrayList<>();
    /**
     * The Frame.
     */
    private Image frame;
    /**
     * The Infinite.
     */
    private Image infinite;

    /**
     * Instantiates a new Display timer.
     */
// Constructor por defecto
    public displayTimer() {
        for (int i = 0; i < 10; ++i) {
            numbers.add(new ImageIcon( this.getClass().getResource(path+i+".png")).getImage());
        }
        frame = new ImageIcon( this.getClass().getResource(path+"frame.png")).getImage();
        infinite = new ImageIcon( this.getClass().getResource(path+"infinite.png")).getImage();
    }

    /**
     * Gets timer.
     *
     * @param number the number
     * @return the timer
     */
// Generar los screenObjects para un número del timer
    public List<screenObject> getTimer(int number) {
        int firstDigit = number / 10;
        int secondDigit = number % 10;
        List <screenObject> list = new ArrayList<screenObject>();
        list.add(new screenObject(550+10,15+10,68,96,numbers.get(firstDigit), Item_Type.TIMER1));
        list.add(new screenObject(550+68+30,15+10,68,96,numbers.get(secondDigit), Item_Type.TIMER2));
        list.add(new screenObject(550,15,180,116,frame, Item_Type.TIMERFRAME));
        return list;
    }

    /**
     * Gets timer.
     *
     * @return the timer
     */
// Generar los screenObjects para un número del timer
    public List<screenObject> getTimer() {
        List <screenObject> list = new ArrayList<screenObject>();
        list.add(new screenObject(550+40,15+20,100,80,infinite, Item_Type.TIMER1));
        list.add(new screenObject(550,15,180,116,frame, Item_Type.TIMERFRAME));
        return list;
    }
}
