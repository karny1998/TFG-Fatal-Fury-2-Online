package lib.objects.networking.gui;

import videojuegos.Principal;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The type Game gui.
 */
public class gameGUI extends JPanel {
    /**
     * The Background 1.
     */
    private Image background1 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/base_2.png")).getImage(),
    /**
     * The Background 2.
     */
    background2 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/base.png")).getImage(),
    /**
     * The Background 3.
     */
    background3 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/base_simple.png")).getImage(),
    /**
     * The Background 4.
     */
    background4 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/character_selection.png")).getImage(),
    /**
     * The Background 5.
     */
    background5 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/character_selection2.png")).getImage(),
    /**
     * The Background 6.
     */
    background6 = new ImageIcon(this.getClass().getResource("/assets/sprites/menu/fight_end.png")).getImage();
    /**
     * The Principal.
     */
    private Principal principal;
    /**
     * The constant resX.
     */
    private static int resX = 1280, /**
     * The Res y.
     */
    resY = 720, /**
     * The Back.
     */
    back = 1;
    /**
     * The Fullscreen.
     */
    private boolean fullscreen = false;
    /**
     * The Multiplier.
     */
    private double multiplier = 1.0;
    /**
     * The F.
     */
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

    /**
     * Instantiates a new Game gui.
     *
     * @param principal the principal
     */
    public gameGUI(Principal principal) {
        this.principal = principal;
        setSurfaceSize();
        this.setLayout(null);
    }

    /**
     * Sets surface size.
     */
    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    /**
     * Paint component.
     *
     * @param g the g
     */
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

    /**
     * Gets back.
     *
     * @return the back
     */
    public static int getBack() {
        return back;
    }

    /**
     * Sets back.
     *
     * @param back the back
     */
    public static void setBack(int back) {
        gameGUI.back = back;
    }

    /**
     * Is fullscreen boolean.
     *
     * @return the boolean
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Sets fullscreen.
     *
     * @param fullscreen the fullscreen
     */
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

    /**
     * Gets multiplier.
     *
     * @return the multiplier
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Sets multiplier.
     *
     * @param multiplier the multiplier
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
