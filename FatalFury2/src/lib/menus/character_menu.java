package lib.menus;

import lib.Enums.Item_Type;
import lib.Enums.Playable_Character;
import lib.input.controlListener;
import lib.objects.screenObject;

import javax.swing.*;
import java.util.Map;

public class character_menu {

    private String path = "assets/sprites/menu/character/";
    private enum estados { P1_SELECT, P2_SELECT, DONE };
    estados actual;



    Map<Item_Type, screenObject> screenObjects;

    private screenObject p1, p2, p1_mug, p1_name, p2_mug, p2_name, fondo;

    int pos_1, pos_2;
    int incrementos[] = {0, 173, 173+174};
    int x1 = 400, x2 = 400;
    int y1 = 426, y2 = 441;
    int w = 135, h = 134;

    int tipo;

    public Playable_Character getP1_ch() {
        return p1_ch;
    }

    public Playable_Character getP2_ch() {
        return p2_ch;
    }


    Playable_Character p1_ch, p2_ch;

    long referenceTime;

    public void updateTime(){referenceTime = System.currentTimeMillis();}


    public character_menu(int tipo){
        tipo = tipo;
        pos_1 = 0; pos_2 = 0;
        actual = estados.P1_SELECT;
        fondo = new screenObject(0, 0,  1280, 720, new ImageIcon(path  + "menu.png").getImage(), Item_Type.MENU);
        p1 = new screenObject(x1, y1,  w, h, new ImageIcon(path  + "p1_off.png").getImage(), Item_Type.P1_SELECT);
        switch (tipo){
            case 0:
                // VS P2
                p2 = new screenObject(x2, -y2, w , h, new ImageIcon(path  + "p2_off.png").getImage(), Item_Type.P2_SELECT);

                break;
            case 1:
                // VS COM
                p2 = new screenObject(x2, -y2, w, h, new ImageIcon(path  + "com_off.png").getImage(), Item_Type.P2_SELECT);
                break;
        }
    }
//TODO tiempos input

    public Boolean gestionMenu(Map<Item_Type, screenObject> screenObjects){
        long currentTime = System.currentTimeMillis();
        screenObjects.put(Item_Type.MENU, fondo);
        screenObjects.put(Item_Type.P1_SELECT, p1);
        screenObjects.put(Item_Type.P2_SELECT, p2);
        if( currentTime - referenceTime > 100){
            gestionMenu2( screenObjects);
            referenceTime = currentTime;
        }
        return false;

    }

    public Boolean gestionMenu2(Map<Item_Type, screenObject> screenObjects){
        screenObjects.put(Item_Type.MENU, fondo);
        screenObjects.put(Item_Type.P1_SELECT, p1);
        screenObjects.put(Item_Type.P2_SELECT, p2);
        if(actual == estados.P1_SELECT){
            if (controlListener.getStatus(1, controlListener.IZ_INDEX) && pos_1 > 0){
                pos_1 -- ;
                p1 = new screenObject(x1 + incrementos[pos_1], y1,  w, h, new ImageIcon(path  + "p1_off.png").getImage(), Item_Type.P1_SELECT);
            } else if (controlListener.getStatus(1, controlListener.DE_INDEX) && pos_1 < 2){
                pos_1 ++ ;
                p1 = new screenObject(x1 + incrementos[pos_1], y1,  w, h, new ImageIcon(path  + "p1_off.png").getImage(), Item_Type.P1_SELECT);
            } else if (controlListener.getStatus(1, controlListener.ENT_INDEX)) {
                actual = estados.P2_SELECT;
                p1 = new screenObject(x1 + incrementos[pos_1], y1,  w, h, new ImageIcon(path  + "p1_on.png").getImage(), Item_Type.P1_SELECT);
                switch (pos_1){
                    case 0:
                        p1_ch = Playable_Character.ANDY;
                        break;
                    case 1:
                        p1_ch = Playable_Character.MAI;
                        break;
                    case 2:
                        p1_ch = Playable_Character.TERRY;
                        break;
                }
            }
            return false;
        } else if(actual == estados.P2_SELECT){
            int mando = 1;
            switch (tipo){
                case 0:
                    mando = 2;
                    break;
                case 1:
                    mando = 1;
                    break;
            }

            if (controlListener.getStatus(mando, controlListener.IZ_INDEX) && pos_1 > 0){
                pos_2 -- ;
                p2 = new screenObject(x2 + incrementos[pos_2], y2,  w, h, new ImageIcon(path  + "p2_off.png").getImage(), Item_Type.P2_SELECT);
            } else if (controlListener.getStatus(mando, controlListener.DE_INDEX) && pos_1 < 2){
                pos_2 ++ ;
                p2 = new screenObject(x2 + incrementos[pos_2], y2,  w, h, new ImageIcon(path  + "p2_off.png").getImage(), Item_Type.P2_SELECT);
            } else if (controlListener.getStatus(mando, controlListener.ENT_INDEX)) {
                actual = estados.DONE;
                p2 = new screenObject(x2 + incrementos[pos_2], y2,  w, h, new ImageIcon(path  + "p2_on.png").getImage(), Item_Type.P2_SELECT);
                switch (pos_2){
                    case 0:
                        p2_ch = Playable_Character.ANDY;
                        break;
                    case 1:
                        p2_ch = Playable_Character.MAI;
                        break;
                    case 2:
                        p2_ch = Playable_Character.TERRY;
                        break;
                }

            }
            return false;
        } else if(actual == estados.DONE){
            return true;
        }
        return false;
    }


}
