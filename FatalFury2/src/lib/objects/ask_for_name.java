package lib.objects;

import lib.Enums.Item_Type;
import lib.input.controlListener;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;


/**
 * The type Ask for name.
 */
public class ask_for_name {
    /**
     * The F.
     */
    private Font f;

    {
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont(100f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The Img path 1.
     */
    private URL imgPath_1 = this.getClass().getResource("/assets/sprites/menu/rank_register/base.png");
    /**
     * The Img path 2.
     */
    private URL imgPath_2 = this.getClass().getResource("/assets/sprites/menu/rank_register/ar.png");
    /**
     * The Img path 3.
     */
    private URL imgPath_3 = this.getClass().getResource("/assets/sprites/menu/rank_register/ab.png");
    /**
     * The Fondo.
     */
    private screenObject fondo = new screenObject(0, 0,  1280, 720, new ImageIcon(imgPath_1).getImage(), Item_Type.MENU);

    /**
     * The X flechas.
     */
    private int[] xFlechas = {465,615,765};
    /**
     * The Y ar.
     */
    private int yAr = 200;
    /**
     * The Y ab.
     */
    private int yAb = 400;


    /**
     * The Reference time.
     */
    private long referenceTime;

    /**
     * The Valores.
     */
    private char[] valores = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y','Z','-','.'};

    /**
     * The Resultado.
     */
    private int[] resultado = {0,0,0};

    /**
     * The Actual.
     */
    public int actual;

    /**
     * The Ar.
     */
    private screenObject ar;
    /**
     * The Ab.
     */
    private screenObject ab;

    /**
     * The Done.
     */
    private boolean done;

    /**
     * Instantiates a new Ask for name.
     */
    public  ask_for_name(){
        actual = 0;
        done = false;
        ar = new screenObject(xFlechas[actual], yAr,  54, 36, new ImageIcon(imgPath_2).getImage(), Item_Type.P1_SELECT);
        ab = new screenObject(xFlechas[actual], yAb,  54, 36, new ImageIcon(imgPath_3).getImage(), Item_Type.P2_SELECT);referenceTime = System.currentTimeMillis();
        String path = System.getProperty("user.dir") + "/.files/last_name.txt";
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            if ((aux = b.readLine()) != null) {
                resultado[0] = toValue(aux.charAt(0));
                resultado[1] = toValue(aux.charAt(1));
                resultado[2] = toValue(aux.charAt(2));
            }
            b.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * To value int.
     *
     * @param c the c
     * @return the int
     */
    private int toValue(char c){
        for(int i = 0; i < valores.length; i++){
            if (valores[i] == c){
                return i;
            }
        }
        return 0;
    }

    /**
     * Gestion menu boolean.
     *
     * @param screenObjects the screen objects
     * @return the boolean
     */
    public Boolean gestionMenu(Map<Item_Type, screenObject> screenObjects){

        screenObjects.put(Item_Type.MENU, fondo);
        ar = new screenObject(xFlechas[actual], yAr,  54, 36, new ImageIcon(imgPath_2).getImage(), Item_Type.P1_SELECT);
        ab = new screenObject(xFlechas[actual], yAb,  54, 36, new ImageIcon(imgPath_3).getImage(), Item_Type.P2_SELECT);
        screenObjects.put(Item_Type.P1_SELECT, ar);
        screenObjects.put(Item_Type.P2_SELECT, ab);
        if(controlListener.getStatus(0, controlListener.ENT_INDEX)){
            done = true;
            audio_manager.menu.play(menu_audio.indexes.option_selected);
        }
        return done;
    }

    /**
     * Write name.
     *
     * @param g the g
     */
    public void writeName(Graphics2D g){
        long current = System.currentTimeMillis();
        if(current - referenceTime > 125){
            if(controlListener.getStatus(0, controlListener.DE_INDEX)){
                actual = actual + 1 > resultado.length-1 ? resultado.length - 1 :  actual + 1;
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
            }
            else if(controlListener.getStatus(0, controlListener.IZ_INDEX)){
                actual = actual - 1 < 0 ? 0 :  actual - 1;
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Move_cursor);
            }
            else if(controlListener.getStatus(0, controlListener.AR_INDEX)){
                resultado[actual] = resultado[actual] + 1 > valores.length-1 ? 0 :  resultado[actual] + 1;
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Move_cursor);
            }
            else if(controlListener.getStatus(0, controlListener.AB_INDEX)){
                resultado[actual] = resultado[actual] - 1 < 0 ? valores.length -1 :  resultado[actual] - 1;
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Move_cursor);
            }

            referenceTime = current;
        }


        int x = 440;
        int y = 370;
        g.setFont(f);
        for(int i = 0; i < resultado.length; i++){
            if(i == actual){
                 g.setColor(new Color(255, 221, 0));
             } else {
                g.setColor(new Color(140, 120, 0));
            }
            g.drawString(String.valueOf(valores[resultado[i]]), x, y);
            x+=150;
        }
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        String out = "";
        for(int i = 0; i < resultado.length; i++){
            out += valores[resultado[i]];
        }
        return out;
    }

}
