package lib.objects;

import lib.Enums.Item_Type;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class displayTimer {
    // Path
    String path = "assets/sprites/fight_interface/timer/";
    // Imágenes necesarias
    private List<Image> numbers = new ArrayList<>();
    private Image frame;
    private Image infinite;

    // Constructor por defecto
    public displayTimer() {
        for (int i = 0; i < 10; ++i) {
            numbers.add(new ImageIcon(path+i+".png").getImage());
        }
        frame = new ImageIcon(path+"frame.png").getImage();
        infinite = new ImageIcon(path+"infinite.png").getImage();
    }

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

    // Generar los screenObjects para un número del timer
    public List<screenObject> getTimer() {
        List <screenObject> list = new ArrayList<screenObject>();
        list.add(new screenObject(550+40,15+20,100,80,infinite, Item_Type.TIMER1));
        list.add(new screenObject(550,15,180,116,frame, Item_Type.TIMERFRAME));
        return list;
    }
}
