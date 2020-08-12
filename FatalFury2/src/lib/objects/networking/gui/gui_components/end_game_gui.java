package lib.objects.networking.gui.gui_components;

import lib.Enums.Fight_Results;
import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;
import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.online_mode_gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class end_game_gui {
    private online_mode_gui gui;
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),grey3 = new Color(45,48,85),
            grey4 = new Color(99,96,171), brown = new Color(140,105,57), blue = new Color(0,0,148);
    private Font f,f2,f3,f4;
    private ImageIcon terryMug, andyMug,  maiMug, draw, win, lost;
    private boolean isHost = false, isRanked;
    private Playable_Character character1 = Playable_Character.TERRY, character2 = Playable_Character.TERRY;
    private String rivalName;
    private Fight_Results results;
    private int points;

    public end_game_gui(online_mode_gui gui, boolean isHost, String rivalName, Playable_Character c1,
                        Playable_Character c2, Fight_Results results, int points, boolean isRanked){
        this.character1 = c1;
        this.character2 = c2;
        this.rivalName = rivalName;
        this.gui = gui;
        this.results = results;
        this.points = points;
        this.isRanked = isRanked;
        try {
            f4 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float)res(60));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isHost = isHost;
        this.f = gui.getF();
        this.f2 = gui.getF2();
        this.f3 = gui.getF3();

        terryMug = gui.loadIcon("/assets/sprites/menu/character/terry/terry_mugshot.png",312,285);
        andyMug = gui.loadIcon("/assets/sprites/menu/character/andy/andy_mugshot.png",312,285);
        maiMug = gui.loadIcon("/assets/sprites/menu/character/mai/mai_mugshot.png",312,285);
        draw = gui.loadIcon("/assets/sprites/menu/draw_game.png",798,77);
        win = gui.loadIcon("/assets/sprites/menu/you_win.png",708,77);
        lost = gui.loadIcon("/assets/sprites/menu/you_lost.png",708,77);

        end_game();
    }

    private int res(int x){
        return gui.res(x);
    }

    public void end_game(){
        JLabel char1, char2, gameResult;
        JTextField rival, user;
        switch (character1){
            case TERRY:
                char1 = new JLabel(terryMug);
                break;
            case MAI:
                char1 = new JLabel(maiMug);
                break;
            case ANDY:
                char1 = new JLabel(andyMug);
                break;
            default:
                char1 = new JLabel(terryMug);
                break;
        }
        switch (character2){
            case TERRY:
                char2 = new JLabel(terryMug);
                break;
            case MAI:
                char2 = new JLabel(maiMug);
                break;
            case ANDY:
                char2 = new JLabel(andyMug);
                break;
            default:
                char2 = new JLabel(terryMug);
                break;
        }

        switch (results){
            case PLAYER1_WIN:
                if(isHost){
                    gameResult = new JLabel(win);
                }
                else{
                    gameResult = new JLabel(lost);
                }
                gameResult.setBounds(286,300,res(708),res(75));
                break;
            case PLAYER2_WIN:
                if(isHost){
                    gameResult = new JLabel(lost);
                }
                else{
                    gameResult = new JLabel(win);
                }
                gameResult.setBounds(286,300,res(708),res(75));
                break;
            case TIE:
                gameResult = new JLabel(draw);
                gameResult.setBounds(241,300,res(798),res(75));
                break;
            default:
                gameResult = new JLabel(draw);
                gameResult.setBounds(241,300,res(798),res(75));
                break;
        }

        char1.setBounds(res(50), res(170),res(312), res(285));
        char2.setBounds(res(918), res(170),res(312), res(285));
        if(isHost){
            rival = gui.generateSimpleTextField(rivalName, f3, Color.YELLOW, new Color(0,0,0,0), 918, 470, 312, 80, false, true);
            user = gui.generateSimpleTextField(gui.getUserLogged(), f3, Color.YELLOW, new Color(0,0,0,0), 50, 470, 312, 80, false, true);
        }
        else{
            user = gui.generateSimpleTextField(gui.getUserLogged(), f3, Color.YELLOW, new Color(0,0,0,0), 918, 470, 312, 80, false, true);
            rival = gui.generateSimpleTextField(rivalName, f3, Color.YELLOW, new Color(0,0,0,0), 50, 470, 312, 80, false, true);
        }

        JButton confirm = gui.generateSimpleButton("Confirm", guiItems.CONFIRM_END_GAME, f3, Color.YELLOW,grey2,515,550,250,60,false);

        user.setHorizontalAlignment(JTextField.CENTER);
        rival.setHorizontalAlignment(JTextField.CENTER);

        guiItems items[] = {guiItems.GAME_RESULT, guiItems.PLAYER_1_CHAR, guiItems.PLAYER_2_CHAR, guiItems.PLAYER_USERNAME, guiItems.RIVAL_USERNAME, guiItems.CONFIRM_END_GAME} ;
        Component comps[] = {gameResult, char1, char2, user, rival, confirm};

        if(isRanked){
            JTextField pts = gui.generateSimpleTextField(points+"pts", f3, Color.YELLOW, new Color(0,0,0,0), 515, 470, 250, 80, false, true);
            items = new guiItems[]{guiItems.GAME_RESULT, guiItems.PLAYER_1_CHAR, guiItems.PLAYER_2_CHAR, guiItems.PLAYER_USERNAME, guiItems.RIVAL_USERNAME, guiItems.RANK_POINTS, guiItems.CONFIRM_END_GAME} ;
            comps = new Component[]{gameResult, char1, char2, user, rival, pts, confirm};
        }

        gui.addComponents(items,comps);
        gui.reloadGUI();
    }
}
