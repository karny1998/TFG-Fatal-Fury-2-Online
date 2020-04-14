package lib.objects;

import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class ask_for_name {
    private String name = "";
    private animation anim;
    private Font f = new Font("Orbitron", Font.ROMAN_BASELINE, 60);
    private long timeReference = System.currentTimeMillis();

    public  ask_for_name(){
        timeReference = System.currentTimeMillis();
        loadAnim();
        new IsKeyPressed();
        String path = "files/last_name.txt";
        try {
            FileReader f = new FileReader(path);
            BufferedReader b = new BufferedReader(f);
            String aux = "";
            if ((aux = b.readLine()) != null) {
                name = aux;
            }
            b.close();
        }
        catch (Exception e){}
    }

    private void loadAnim(){
        String path = "assets/sprites/menu/rank_register/";
        anim = new animation();
        anim.setHasEnd(false);
        screenObject s = new screenObject(191, 186,  897, 347, new ImageIcon(path  + "rank_register_1.png").getImage(), Item_Type.MENU);
        anim.addFrame(s,250.0,0,0);
        s = new screenObject(191, 186,  897, 347, new ImageIcon(path  + "rank_register_2.png").getImage(), Item_Type.MENU);
        anim.addFrame(s,250.0,0,0);
        anim.setSound(null);
        anim.setSoundType(Character_Voices.Win);
    }

    public  screenObject getAnimation(){
        return anim.getFrame(191, 186, 1);
    }

    public  void writeName(Graphics2D g){
        long current = System.currentTimeMillis();
        String readed = "";
        controlKey cK = controlKey.NONE;
        if(current - timeReference > 100.0){
            cK = IsKeyPressed.keyPressed();
            timeReference = current;
        }
        if(cK == controlKey.ESCAPE){
            name = name.substring(0, name.length()-1);
        }
        else if(cK != controlKey.NONE && name.length() < 10){
            name += cK.toString();
        }
        g.setFont(f);
        g.setColor(Color.YELLOW);
        g.drawString(name, 271, 376);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
