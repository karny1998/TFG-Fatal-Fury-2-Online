package lib.objects.networking;

import lib.Enums.GameState;
import lib.Enums.Item_Type;
import lib.menus.menu_generator;
import lib.objects.Screen;
import lib.objects.screenObject;
import lib.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class online_mode_gui {
    private Map<Item_Type, screenObject> screenObjects;
    private Map<guiItems,Component> componentsOnScreen = new HashMap<>();
    private GameState onlineState = GameState.ONLINE_MODE;
    private screenObject wall1 = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource("/assets/sprites/menu/base_2.png")).getImage(), Item_Type.BACKGROUND);;;
    private screenObject wall2 = new screenObject(0, 0,  1280, 720, new ImageIcon(menu_generator.class.getResource("/assets/sprites/menu/base.png")).getImage(), Item_Type.BACKGROUND);;;
    private Screen screen;
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
    public online_mode_gui(Screen screen, GameState onlineState){
        this.screenObjects = screen.getScreenObjects();
        this.onlineState = onlineState;
        this.screen = screen;
    }

    public void drawGUI(){
        screen.setShowingGUI(true);
        screenObjects.clear();
        switch (onlineState){
            case LOGIN_REGISTER:
                login_register();
                break;
            case LOGIN:
                login();
                break;
            default:
                break;
        }
        //screen.revalidate();
    }

    public void login_register(){
        if(!componentsOnScreen.containsKey(guiItems.LOGIN_BUTTON)) {
            JButton login = new JButton("Login");
            login.setFont(f);
            login.setForeground(Color.YELLOW);
            login.setBackground(new Color(33,32,57));
            login.setBounds(370,560, 250,60);
            JButton register = new JButton("Sign in");
            register.setFont(f);
            register.setForeground(Color.YELLOW);
            register.setBackground(new Color(132,128,228));
            register.setBounds(660,560, 250,60);
            componentsOnScreen.put(guiItems.LOGIN_BUTTON, login);
            componentsOnScreen.put(guiItems.REGISTER_BUTTON, register);
            screenObjects.put(Item_Type.BACKGROUND,wall1);
        }
    }

    public void login(){
        if(!componentsOnScreen.containsKey(guiItems.LOGIN_BUTTON)) {
            screen.getPrincipal().guiOn();
            JButton login = new JButton("Login");
            login.setFont(f);
            login.setForeground(Color.YELLOW);
            login.setBackground(new Color(33,32,57));
            login.setBounds(370,560, 250,60);
            JTextField username = new JTextField("Username");
            username.setEditable(true);
            username.setFont(f);
            username.setForeground(Color.YELLOW);
            username.setBackground(new Color(33,32,57));
            username.setBounds(660,560, 250,60);
            screen.add(login);
            screen.add(username);
            componentsOnScreen.put(guiItems.LOGIN_BUTTON, login);
            componentsOnScreen.put(guiItems.REGISTER_BUTTON, username);
            screenObjects.put(Item_Type.BACKGROUND,wall1);
        }
    }

    private enum guiItems{LOGIN_BUTTON, REGISTER_BUTTON}
}
