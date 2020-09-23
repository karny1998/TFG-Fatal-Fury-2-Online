package lib.menus;

import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;
import lib.input.controlListener;
import lib.objects.screenObject;
import lib.sound.audio_manager;
import lib.sound.menu_audio;

import javax.swing.*;
import java.util.Map;

/**
 * The type Character menu.
 */
public class character_menu {

    /**
     * The Path.
     */
    private String path = "/assets/sprites/menu/character/";

    /**
     * The enum Estados.
     */
    private enum estados {
        /**
         * P 1 select estados.
         */
        P1_SELECT,
        /**
         * P 2 select estados.
         */
        P2_SELECT,
        /**
         * Done estados.
         */
        DONE };
    /**
     * The Actual.
     */
    estados actual;
    /**
     * The P 1 sound.
     */
    menu_audio.indexes p1_sound, /**
     * The P 2 sound.
     */
    p2_sound;


    /**
     * The Screen objects.
     */
    Map<Item_Type, screenObject> screenObjects;

    /**
     * The P 1.
     */
    private screenObject p1, /**
     * The P 2.
     */
    p2, /**
     * The P 1 mug.
     */
    p1_mug, /**
     * The P 1 name.
     */
    p1_name, /**
     * The P 2 mug.
     */
    p2_mug, /**
     * The P 2 name.
     */
    p2_name, /**
     * The Fondo.
     */
    fondo;

    /**
     * The Pos 1.
     */
    int pos_1, /**
     * The Pos 2.
     */
    pos_2;
    /**
     * The Incrementos.
     */
    int incrementos[] = {0, 173, 173+174};
    /**
     * The Mugs.
     */
    String mugs[] = { "andy/andy_mugshot.png", "mai/mai_mugshot.png", "terry/terry_mugshot.png" };
    /**
     * The Names.
     */
    String names[] = { "andy/andy_name.png", "mai/mai_name.png", "terry/terry_name.png" };
    /**
     * The X 1.
     */
    int x1 = 400, /**
     * The X 2.
     */
    x2 = 400;
    /**
     * The Y 1.
     */
    int y1 = 426, /**
     * The Y 2.
     */
    y2 = 441;
    /**
     * The W.
     */
    int w = 135, /**
     * The H.
     */
    h = 134;

    /**
     * The Tipo.
     */
    int tipo;

    /**
     * Gets p 1 ch.
     *
     * @return the p 1 ch
     */
    public Playable_Character getP1_ch() {
        return p1_ch;
    }

    /**
     * Gets p 2 ch.
     *
     * @return the p 2 ch
     */
    public Playable_Character getP2_ch() {
        return p2_ch;
    }


    /**
     * The P 1 ch.
     */
    Playable_Character p1_ch, /**
     * The P 2 ch.
     */
    p2_ch;


    /**
     * The P 2 aux.
     */
    private String p2_aux = "";

    /**
     * Instantiates a new Character menu.
     *
     * @param tipo_ the tipo
     */
    public character_menu(int tipo_){
        tipo = tipo_;
        pos_1 = 0; pos_2 = 0;
        actual = estados.P1_SELECT;
        fondo = new screenObject(0, 0,  1280, 720, new ImageIcon( this.getClass().getResource(path  + "menu.png")).getImage(), Item_Type.MENU);
        p1 = new screenObject(x1, y1,  w, h, new ImageIcon( this.getClass().getResource(path  + "p1_off.png")).getImage(), Item_Type.P1_SELECT);
        switch (tipo){
            case 0:
                // VS P2
                p2_aux = "p2_";
                break;
            case 1:
                // VS COM
                p2_aux = "com_";
                break;
        }
    }

    /**
     * Gestion menu boolean.
     *
     * @param screenObjects the screen objects
     * @return the boolean
     */
    public Boolean gestionMenu(Map<Item_Type, screenObject> screenObjects){
        long currentTime = System.currentTimeMillis();
        screenObjects.put(Item_Type.MENU, fondo);
        screenObjects.put(Item_Type.P1_SELECT, p1);
        screenObjects.put(Item_Type.P2_SELECT, p2);
        screenObjects.put(Item_Type.P1_MUG, p1_mug);
        screenObjects.put(Item_Type.P2_MUG, p2_mug);
        screenObjects.put(Item_Type.P1_NAME, p1_name);
        screenObjects.put(Item_Type.P2_NAME, p2_name);
        boolean res = gestionMenu2( screenObjects);
        return res;


    }

    /**
     * Gestion menu 2 boolean.
     *
     * @param screenObjects the screen objects
     * @return the boolean
     */
    public Boolean gestionMenu2(Map<Item_Type, screenObject> screenObjects){
        if(actual == estados.P1_SELECT){


            p1_mug =  new screenObject(0, 75,  416, 380, new ImageIcon( this.getClass().getResource(path  + mugs[pos_1])).getImage(), Item_Type.P1_MUG);
            p1_name =  new screenObject(0, 400,  356, 299, new ImageIcon( this.getClass().getResource(path  + names[pos_1])).getImage(), Item_Type.P1_MUG);

            if (controlListener.menuInput(1, controlListener.IZ_INDEX) && pos_1 > 0){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
                pos_1 -- ;
                p1 = new screenObject(x1 + incrementos[pos_1], y1,  w, h, new ImageIcon( this.getClass().getResource(path  + "p1_off.png")).getImage(), Item_Type.P1_SELECT);
            } else if (controlListener.menuInput(1, controlListener.DE_INDEX) && pos_1 < 2){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
                pos_1 ++ ;
                p1 = new screenObject(x1 + incrementos[pos_1], y1,  w, h, new ImageIcon( this.getClass().getResource(path  + "p1_off.png")).getImage(), Item_Type.P1_SELECT);
            } else if (controlListener.menuInput(1, controlListener.ENT_INDEX)) {
                audio_manager.menu.play(menu_audio.indexes.fight_selected);
                actual = estados.P2_SELECT;
                p1 = new screenObject(x1 + incrementos[pos_1], y1,  w, h, new ImageIcon( this.getClass().getResource(path  + "p1_on.png")).getImage(), Item_Type.P1_SELECT);
                p2 = new screenObject(x2 + incrementos[pos_2], y2, w , h, new ImageIcon( this.getClass().getResource(path + p2_aux + "off.png")).getImage(), Item_Type.P2_SELECT);
                switch (pos_1){
                    case 0:
                        p1_ch = Playable_Character.ANDY;
                        p1_sound = menu_audio.indexes.Andy;
                        break;
                    case 1:
                        p1_ch = Playable_Character.MAI;
                        p1_sound = menu_audio.indexes.Mai;
                        break;
                    case 2:
                        p1_ch = Playable_Character.TERRY;
                        p1_sound = menu_audio.indexes.Terry;
                        break;
                }
            }
            return false;
        } else if(actual == estados.P2_SELECT){
            p2_mug =  new screenObject(864, 75,  416, 380, new ImageIcon( this.getClass().getResource(path  + mugs[pos_2])).getImage(), Item_Type.P2_MUG);
            p2_name =  new screenObject(924, 400,  356, 299, new ImageIcon( this.getClass().getResource(path  + names[pos_2])).getImage(), Item_Type.P2_MUG);
            int mando = 1;
            switch (tipo){
                case 0:
                    mando = 2;
                    break;
                case 1:
                    mando = 1;
                    break;
            }

            if (controlListener.menuInput(mando, controlListener.IZ_INDEX) && pos_2 > 0){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
                pos_2 -- ;
                p2 = new screenObject(x2 + incrementos[pos_2], y2, w , h, new ImageIcon( this.getClass().getResource(path + p2_aux + "off.png")).getImage(), Item_Type.P2_SELECT);
            } else if (controlListener.menuInput(mando, controlListener.DE_INDEX) && pos_2 < 2){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);

                pos_2 ++ ;
                p2 = new screenObject(x2 + incrementos[pos_2], y2, w , h, new ImageIcon( this.getClass().getResource(path + p2_aux + "off.png")).getImage(), Item_Type.P2_SELECT);
            } else if (controlListener.menuInput(mando, controlListener.ENT_INDEX)) {
                audio_manager.menu.play(menu_audio.indexes.fight_selected);
                actual = estados.DONE;
                p2 = new screenObject(x2 + incrementos[pos_2], y2, w , h, new ImageIcon( this.getClass().getResource(path + p2_aux + "on.png")).getImage(), Item_Type.P2_SELECT);
                screenObjects.put(Item_Type.P2_SELECT, p2);
                switch (pos_2){
                    case 0:
                        p2_ch = Playable_Character.ANDY;
                        p2_sound = menu_audio.indexes.Andy;
                        break;
                    case 1:
                        p2_ch = Playable_Character.MAI;
                        p2_sound = menu_audio.indexes.Mai;
                        break;
                    case 2:
                        p2_ch = Playable_Character.TERRY;
                        p2_sound = menu_audio.indexes.Terry;
                        break;
                }

            }
            return false;
        } else if(actual == estados.DONE){
            audio_manager.menu.play(p1_sound);
            try {
            Thread.sleep(1000);
            audio_manager.menu.play(menu_audio.indexes.Versus);
            Thread.sleep(1000);
            audio_manager.menu.play(p2_sound);
            Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }


}
