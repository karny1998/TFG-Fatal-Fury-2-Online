package lib.objects;

import lib.Enums.Item_Type;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static lib.Enums.Item_Type.*;

public class displayScores {
    // Path
    String path = "/assets/sprites/fight_interface/scores/";
    // Imágenes necesarias
    private Item_Type[] typesScore;
    private Item_Type[] typesLife;
    private Item_Type[] typesTime;
    private Item_Type[] typesTotal;
    private List<Image> numbers = new ArrayList<>();
    private Image bonus, life, score, time, total, frame;

    // Constructor por defecto
    public displayScores() {
        for (int i = 0; i < 10; ++i) {
            numbers.add(new ImageIcon( this.getClass().getResource(path+"numbers/"+i+".png")).getImage());
        }
        bonus = new ImageIcon(this.getClass().getResource(path+"bonus.png")).getImage();
        life = new ImageIcon(this.getClass().getResource(path+"life.png")).getImage();
        score = new ImageIcon(this.getClass().getResource(path+"score.png")).getImage();
        time = new ImageIcon(this.getClass().getResource(path+"time.png")).getImage();
        total = new ImageIcon(this.getClass().getResource(path+"total.png")).getImage();
        frame = new ImageIcon(this.getClass().getResource(path+"frame.png")).getImage();
        typesScore = new Item_Type[]{SCOREN1, SCOREN2, SCOREN3, SCOREN4, SCOREN5};
        typesLife = new Item_Type[]{LIFEN1, LIFEN2, LIFEN3, LIFEN4, LIFEN5};
        typesTime = new Item_Type[]{TIMEN1, TIMEN2, TIMEN3, TIMEN4, TIMEN5};
        typesTotal = new Item_Type[]{TOTALN1, TOTALN2, TOTALN3, TOTALN4, TOTALN5};
    }

    // Devuelve una lista de screenObjects para un número de 5 cifras dado un tipo y coordenadas
    List<screenObject> getNumbers(int x, int y, int number, String type) {
        int w = 37;
        int h = 53;
        int spacing = 4;
        StringBuilder numberString = new StringBuilder(Integer.toString(number));
        int zeros = 5 - numberString.length();
        for (int i = 0; i < zeros; ++i) {
            numberString.insert(0, "0");
        }
        Item_Type[] typesNumber;
        List <screenObject> numberList = new ArrayList<>();
        int n1 = Integer.parseInt(String.valueOf(numberString.charAt(0)));
        int n2 = Integer.parseInt(String.valueOf(numberString.charAt(1)));
        int n3 = Integer.parseInt(String.valueOf(numberString.charAt(2)));
        int n4 = Integer.parseInt(String.valueOf(numberString.charAt(3)));
        int n5 = Integer.parseInt(String.valueOf(numberString.charAt(4)));
        switch (type) {
            case "total":
                typesNumber = typesTotal;
                break;
            case "score":
                typesNumber = typesScore;
                break;
            case "life":
                typesNumber = typesLife;
                break;
            case "time":
                typesNumber = typesTime;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        screenObject s1 = new screenObject(x,y,w,h,numbers.get(n1),typesNumber[0]);
        screenObject s2 = new screenObject(x+w+spacing,y,w,h,numbers.get(n2),typesNumber[1]);
        screenObject s3 = new screenObject(x+(w*2)+(spacing*2),y,w,h,numbers.get(n3),typesNumber[2]);
        screenObject s4 = new screenObject(x+(w*3)+(spacing*3),y,w,h,numbers.get(n4),typesNumber[3]);
        screenObject s5 = new screenObject(x+(w*4)+(spacing*4),y,w,h,numbers.get(n5),typesNumber[4]);
        numberList.add(s1);
        numberList.add(s2);
        numberList.add(s3);
        numberList.add(s4);
        numberList.add(s5);
        return numberList;
    }

    // Devuelve los screenObjects para la fila de SCORE
    List<screenObject> getScore(int number) {
        List <screenObject> l = new ArrayList<>();
        int x_text = 294;
        int y_text = 165;
        int x_numbers = 784;
        int y_numbers = 159;
        l.add(new screenObject(x_text,y_text,275,40,score,SCORE_TEXT));
        l.addAll(getNumbers(x_numbers,y_numbers,number,"score"));
        return l;
    }

    // Devuelve los screenObjects para la fila de LIFE
    List<screenObject> getLife(int number) {
        List <screenObject> l = new ArrayList<>();
        int x_text = 294;
        int y_text = 352;
        int x_numbers = 784;
        int y_numbers = 346;
        l.add(new screenObject(x_text,y_text,275,40,life,LIFE_TEXT));
        l.addAll(getNumbers(x_numbers,y_numbers,number,"life"));
        return l;
    }

    // Devuelve los screenObjects para la fila de TIME
    List<screenObject> getTime(int number) {
        List <screenObject> l = new ArrayList<>();
        int x_text = 294;
        int y_text = 450;
        int x_numbers = 784;
        int y_numbers = 444;
        l.add(new screenObject(x_text,y_text,275,40,time,TIME_TEXT));
        l.addAll(getNumbers(x_numbers,y_numbers,number,"time"));
        return l;
    }

    // Devuelve los screenObjects para la fila de TOTAL
    List<screenObject> getTotal(int number) {
        List <screenObject> l = new ArrayList<>();
        int x_text = 294;
        int y_text = 548;
        int x_numbers = 784;
        int y_numbers = 542;
        l.add(new screenObject(x_text,y_text,275,40,total,TOTAL_TEXT));
        l.addAll(getNumbers(x_numbers,y_numbers,number,"total"));
        return l;
    }

    // Devuelve el screenObject para la fila BONUS
    screenObject getBonusTitle() {
        return new screenObject(503,259,275,40,bonus,BONUS);
    }

    // Devuelve el screenObject para el frame
    screenObject getFrame() {
        return new screenObject(273,142,733,469,frame,SCORE_FRAME);
    }
}
