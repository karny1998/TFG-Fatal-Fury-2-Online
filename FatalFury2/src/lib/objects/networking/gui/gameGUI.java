package lib.objects.networking.gui;

import videojuegos.Principal;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class gameGUI extends JPanel {
    private Image background1 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/base_2.png")).getImage(),
        background2 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/base.png")).getImage(),
            background3 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/base_simple.png")).getImage(),
            background4 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/character_selection.png")).getImage(),
            background5 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/character_selection2.png")).getImage(),
            background6 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/fight_end.png")).getImage();
    private Principal principal;
    private static int resX = 1280, resY = 720, back = 1;
    private boolean fullscreen = false;
    private double multiplier = 1.0;
    private Font f;
    {
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont(25f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public gameGUI(Principal principal) {
        this.principal = principal;
        setSurfaceSize();
        this.setLayout(null);
    }

    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(back == 1) {
            g.drawImage(background1,0,0,(int)(resX*multiplier), (int)(resY*multiplier), null);
        }
        else if(back == 2){
            g.drawImage(background2,0,0,(int)(resX*multiplier), (int)(resY*multiplier), null);
        }
        else if(back == 3){
            g.drawImage(background3,0,0,(int)(resX*multiplier), (int)(resY*multiplier), null);
        }
        else if(back == 4){
            g.drawImage(background4,0,0,(int)(resX*multiplier), (int)(resY*multiplier), null);
        }
        else if(back == 5){
            g.drawImage(background5,0,0,(int)(resX*multiplier), (int)(resY*multiplier), null);
        }
        else{
            g.drawImage(background6,0,0,(int)(resX*multiplier), (int)(resY*multiplier), null);
        }
    }

    public static int getBack() {
        return back;
    }

    public static void setBack(int back) {
        gameGUI.back = back;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if(true){
            Dimension d = principal.getGame().getSize();
            multiplier = (double)d.getHeight()/(double)resY;
        }
        else{
            multiplier = 1.0;
        }
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
