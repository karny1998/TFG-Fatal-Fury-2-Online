package lib.objects;

import lib.Enums.Item_Type;
import lib.input.controlListener;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ask_for_name {
    private String name = "";
    private animation anim;
    private Font f = new Font("Orbitron", Font.ROMAN_BASELINE, 60);
    private long timeReference = System.currentTimeMillis();

    public  ask_for_name(){
        timeReference = System.currentTimeMillis();
        loadAnim();
        String path = System.getProperty("user.dir") + "/.files/last_name.txt";
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            if ((aux = b.readLine()) != null) {
                name = aux;
            }
            b.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadAnim(){
        String path = "/assets/sprites/menu/rank_register/";
        anim = new animation();
        anim.setHasEnd(false);
        screenObject s = new screenObject(191, 186,  897, 347, new ImageIcon( this.getClass().getResource(path  + "rank_register_1.png")).getImage(), Item_Type.MENU);
        anim.addFrame(s,250.0,0,0);
        s = new screenObject(191, 186,  897, 347, new ImageIcon( this.getClass().getResource(path  + "rank_register_2.png")).getImage(), Item_Type.MENU);
        anim.addFrame(s,250.0,0,0);
        anim.setHasSound(false);
        anim.setSoundType(fight_audio.voice_indexes.Win);
    }

    public  screenObject getAnimation(){
        return anim.getFrame(191, 186, 1);
    }

    public  void writeName(Graphics2D g){
        long current = System.currentTimeMillis();
        boolean ok = false;
       if(current - timeReference > 150.0){
            timeReference = current;
            ok = true;
        }
        int aux = controlListener.getCurrentKey();
        if(ok && controlListener.isPressed(8) && name.length() > 0){
            audio_manager.menu.play(menu_audio.indexes.move_cursor);
            name = name.substring(0, name.length()-1);
        }
        else if(ok && name.length() < 10 && aux != -1) {
                name += (char)aux;
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
