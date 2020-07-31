package lib.objects.networking;

import lib.Enums.GameState;
import lib.Enums.Item_Type;
import lib.menus.menu_generator;
import lib.objects.Screen;
import lib.objects.screenObject;
import lib.utils.Pair;
import videojuegos.Principal;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class online_mode_gui {
    private Map<guiItems,Component> componentsOnScreen = new HashMap<>();
    private List<guiItems> itemsOnScreen = new ArrayList<>();
    private GameState onlineState = GameState.LOGIN_REGISTER;
    private gameGUI gui;
    private Principal principal;
    private double m = 1.0;
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
    private Font f2;
    {
        try {
            f2 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont(15f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public online_mode_gui(Screen screen, GameState onlineState){
        this.onlineState = onlineState;
        this.gui = (gameGUI) screen.getPrincipal().getGui();
        this.principal = screen.getPrincipal();
    }

    public void drawGUI(){
        if(gui.getMultiplier() != m){
            m = gui.getMultiplier();
            try {
                f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (25.0*m));
                f2 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (15.0*m));
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        principal.guiOn();
        switch (onlineState){
            case LOGIN_REGISTER:
                login_register();
                break;
            case LOGIN:
                login(false, "","");
                break;
            case REGISTER:
                register(false, "","","","");
            default:
                break;
        }
        //screen.revalidate();
    }

    private void addComponents(guiItems items[], Component components[]){
        for(int i = 0; i < items.length; ++i){
            gui.add(components[i]);
            componentsOnScreen.put(items[i], components[i]);
            itemsOnScreen.add(items[i]);
        }
    }

    private void clearGui(){
        for(int i = 0; i < itemsOnScreen.size();++i){
            gui.remove(componentsOnScreen.get(itemsOnScreen.get(i)));
        }
        componentsOnScreen.clear();
        itemsOnScreen.clear();
    }

    private void reloadGUI(){
        for(int i = 0; i < itemsOnScreen.size();++i){
            gui.remove(componentsOnScreen.get(itemsOnScreen.get(i)));
        }
        for(int i = 0; i < itemsOnScreen.size();++i){
            gui.add(componentsOnScreen.get(itemsOnScreen.get(i)));
        }
        gui.revalidate();
        gui.repaint();
    }

    public JButton backButton(){
        JButton back = new JButton("Back");
        back.addActionListener(new guiListener(guiItems.BACK));
        back.setIcon(new ImageIcon(getClass().getResource("/assets/sprites/menu/back.png")));
        back.setFont(f);
        back.setForeground(Color.YELLOW);
        //back.setBackground(new Color(0,0,148));
        back.setOpaque(false);
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        back.setBounds((int)(0),(int)(20*m), (int)(200*m),(int)(60*m));
        return back;
    }

    public void popUp(String msg){
        JTextField popup = new JTextField(msg);
        popup.setHorizontalAlignment(JTextField.CENTER);
        popup.addActionListener(new guiListener(guiItems.POP_UP));
        popup.setFont(f);
        popup.setForeground(Color.YELLOW);
        popup.setBackground(new Color(33,32,57));
        popup.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(140,105,57),5));
        popup.setBounds((int)(370*m),(int)(220*m), (int)(540*m),(int)(280*m));

        JButton popupB = new JButton("Okey");
        popupB.addActionListener(new guiListener(guiItems.POP_UP_BUTTON));
        popupB.setFont(f);
        popupB.setForeground(Color.YELLOW);
        popupB.setBackground(new Color(66,64,114));
        popupB.setBounds((int)(580*m),(int)(420*m), (int)(120*m),(int)(60*m));

        gui.add(popupB);
        componentsOnScreen.put(guiItems.POP_UP_BUTTON, popupB);
        itemsOnScreen.add(0,guiItems.POP_UP_BUTTON);
        gui.add(popup);
        componentsOnScreen.put(guiItems.POP_UP, popup);
        itemsOnScreen.add(1,guiItems.POP_UP);
        reloadGUI();
    }

    public void login_register(){
        if(!componentsOnScreen.containsKey(guiItems.LOGIN_BUTTON)) {
            JButton login = new JButton("Login");
            login.addActionListener(new guiListener(guiItems.LOGIN_BUTTON));
            login.setFont(f);
            login.setForeground(Color.YELLOW);
            login.setBackground(new Color(33,32,57));
            login.setBounds((int)(370*m),(int)(560*m), (int)(250*m),(int)(60*m));

            JButton register = new JButton("Sign in");
            register.addActionListener(new guiListener(guiItems.REGISTER_BUTTON));
            register.setFont(f);
            register.setForeground(Color.YELLOW);
            register.setBackground(new Color(33,32,57));
            register.setBounds((int)(660*m),(int)(560*m), (int)(250*m),(int)(60*m));

            guiItems items[] = {guiItems.LOGIN_BUTTON, guiItems.REGISTER_BUTTON, guiItems.BACK};
            Component components[] = {login, register, backButton()};

            addComponents(items, components);

            gui.setBack(1);
            gui.repaint();
            gui.revalidate();
        }
    }

    public void login(boolean show, String user, String pass){
        if(!componentsOnScreen.containsKey(guiItems.LOGIN_BUTTON)) {
            JButton login = new JButton("Login");
            login.addActionListener(new guiListener(guiItems.LOGIN_BUTTON));
            login.setFont(f);
            login.setForeground(Color.YELLOW);
            login.setBackground(new Color(33,32,57));
            login.setBounds((int)(515*m),(int)(610*m), (int)(250*m),(int)(60*m));

            JTextField username = new JTextField(user);
            username.setEditable(true);
            username.setFont(f);
            username.setForeground(Color.YELLOW);
            username.setBackground(new Color(66,64,114));
            username.setBounds((int)(365*m),(int)(520*m), (int)(250*m),(int)(60*m));

            JTextField username2 = new JTextField(" Username");
            username2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            username2.setEditable(false);
            username2.setFont(f);
            username2.setForeground(Color.YELLOW);
            username2.setBackground(new Color(0,0,148));
            username2.setBounds((int)(365*m),(int)(460*m), (int)(250*m),(int)(60*m));

            JPasswordField password = new JPasswordField(pass);
            if(!show) {
                password.setEchoChar('*');
            }
            else{
                password.setEchoChar((char) 0);
            }
            password.setEditable(true);
            password.setFont(f);
            password.setForeground(Color.YELLOW);
            password.setBackground(new Color(66,64,114));
            password.setBounds((int)(665*m),(int)(520*m), (int)(250*m),(int)(60*m));

            JTextField password2 = new JTextField(" Password");
            password2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            password2.setEditable(false);
            password2.setFont(f);
            password2.setForeground(Color.YELLOW);
            password2.setBackground(new Color(0,0,148));
            password2.setBounds((int)(665*m),(int)(460*m), (int)(250*m),(int)(60*m));

            guiItems items[] = {guiItems.LOGIN_BUTTON, guiItems.USERNAME, guiItems.PASSWORD, guiItems.USERNAME_TEXT, guiItems.PASSWORD_TEXT, guiItems.BACK};
            Component components[] = {login, username, password, username2, password2, backButton()};
            addComponents(items, components);

            if(show){
                JButton showPass = new JButton("Hide");
                showPass.addActionListener(new guiListener(guiItems.HIDE));
                showPass.setBounds((int)(930*m),(int)(530*m),(int)(80*m),(int)(40*m));
                showPass.setFont(f2);
                showPass.setForeground(Color.YELLOW);
                showPass.setBackground(new Color(66,64,114));
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            }
            else{
                JButton showPass = new JButton("Show");
                showPass.addActionListener(new guiListener(guiItems.SHOW));
                showPass.setBounds((int)(930*m),(int)(530*m),(int)(80*m),(int)(40*m));
                showPass.setFont(f2);
                showPass.setForeground(Color.YELLOW);
                showPass.setBackground(new Color(66,64,114));
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.SHOW}, new Component[]{showPass});
            }

            gui.setBack(1);
            gui.repaint();
            gui.revalidate();
        }
    }

    public void register(Boolean show, String user, String pass1, String pass2, String eml){
        if(!componentsOnScreen.containsKey(guiItems.REGISTER_BUTTON)) {
            if(show){
                JButton showPass = new JButton("Hide");
                showPass.addActionListener(new guiListener(guiItems.HIDE));
                showPass.setBounds((int)(493*m),(int)(181*m),(int)(80*m),(int)(40*m));
                showPass.setFont(f2);
                showPass.setForeground(Color.YELLOW);
                showPass.setBackground(new Color(66,64,114));
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            }
            else{
                JButton showPass = new JButton("Show");
                showPass.addActionListener(new guiListener(guiItems.SHOW));
                showPass.setBounds((int)(493*m),(int)(181*m),(int)(80*m),(int)(40*m));
                showPass.setFont(f2);
                showPass.setForeground(Color.YELLOW);
                showPass.setBackground(new Color(66,64,114));
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.SHOW}, new Component[]{showPass});
            }

            JButton login = new JButton("Register");
            login.setFont(f);
            login.setForeground(Color.YELLOW);
            login.setBackground(new Color(33,32,57));
            login.setBounds((int)(515*m),(int)(600*m), (int)(250*m),(int)(60*m));

            JTextField username = new JTextField(user);
            username.setEditable(true);
            username.setFont(f);
            username.setForeground(Color.YELLOW);
            username.setBackground(new Color(66,64,114));
            username.setBounds((int)(290*m),(int)(100*m), (int)(680*m),(int)(60*m));

            JTextField username2 = new JTextField("Username");
            username2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            username2.setEditable(false);
            username2.setFont(f);
            username2.setForeground(Color.YELLOW);
            username2.setBackground(new Color(33,32,57));;
            username2.setBounds((int)(290*m),(int)(30*m), (int)(680*m),(int)(60*m));

            JPasswordField password = new JPasswordField(pass1);
            if(!show) {
                password.setEchoChar('*');
            }
            else{
                password.setEchoChar((char) 0);
            }
            password.setEditable(true);
            password.setFont(f);
            password.setForeground(Color.YELLOW);
            password.setBackground(new Color(66,64,114));
            password.setBounds((int)(290*m),(int)(240*m), (int)(680*m),(int)(60*m));

            JTextField password2 = new JTextField("Password");
            password2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            password2.setEditable(false);
            password2.setFont(f);
            password2.setForeground(Color.YELLOW);
            password2.setBackground(new Color(33,32,57));
            password2.setBounds((int)(290*m),(int)(170*m), (int)(250*m),(int)(60*m));

            JPasswordField password_2 = new JPasswordField(pass2);
            if(!show) {
                password_2.setEchoChar('*');
            }
            else{
                password_2.setEchoChar((char) 0);
            }
            password_2.setEditable(true);
            password_2.setFont(f);
            password_2.setForeground(Color.YELLOW);
            password_2.setBackground(new Color(66,64,114));
            password_2.setBounds((int)(290*m),(int)(380*m), (int)(680*m),(int)(60*m));

            JTextField password2_2 = new JTextField("Repeat password");
            password2_2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            password2_2.setEditable(false);
            password2_2.setFont(f);
            password2_2.setForeground(Color.YELLOW);
            password2_2.setBackground(new Color(33,32,57));
            password2_2.setBounds((int)(290*m),(int)(310*m), (int)(680*m),(int)(60*m));

            JTextField email = new JTextField(eml);
            email.setEditable(true);
            email.setFont(f);
            email.setForeground(Color.YELLOW);
            email.setBackground(new Color(66,64,114));
            email.setBounds((int)(290*m),(int)(510*m), (int)(680*m),(int)(60*m));

            JTextField email2 = new JTextField("Email");
            email2.setBorder(javax.swing.BorderFactory.createEmptyBorder());
            email2.setEditable(false);
            email2.setFont(f);
            email2.setForeground(Color.YELLOW);
            email2.setBackground(new Color(33,32,57));
            email2.setBounds((int)(290*m),(int)(450*m), (int)(680*m),(int)(60*m));

            JLabel table = new JLabel(new ImageIcon(getClass().getResource("/assets/sprites/menu/tablon_register.png")));
            table.setBounds((int)(246*m),0,(int)(788*m),(int)(720*m));

            guiItems items[] = {guiItems.REGISTER_BUTTON, guiItems.USERNAME, guiItems.PASSWORD, guiItems.USERNAME_TEXT,
                    guiItems.PASSWORD_TEXT, guiItems.PASSWORD_2, guiItems.PASSWORD_2_TEXT, guiItems.EMAIL, guiItems.EMAIL_TEXT,
                    guiItems.BACK, guiItems.REGISTER_TABLE};
            Component components[] = {login, username, password, username2, password2, password_2, password2_2,
                    email, email2, backButton(), table};

            addComponents(items, components);

            gui.setBack(2);
            gui.repaint();
            gui.revalidate();
        }
    }

    private enum guiItems{LOGIN_BUTTON, REGISTER_BUTTON, USERNAME, USERNAME_TEXT,
        PASSWORD, PASSWORD_TEXT, BACK, PASSWORD_2, PASSWORD_2_TEXT, EMAIL, EMAIL_TEXT,
        SHOW, HIDE, REGISTER_TABLE, POP_UP, POP_UP_BUTTON}

    private class guiListener implements ActionListener {
        private guiItems type;

        public guiListener(guiItems type){this.type = type;}

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String user, pass, pass2, email;
            switch (onlineState){
                case LOGIN_REGISTER:
                    switch (type){
                        case LOGIN_BUTTON:
                            clearGui();
                            onlineState = GameState.LOGIN;
                            break;
                        case REGISTER_BUTTON:
                            clearGui();
                            onlineState = GameState.REGISTER;
                            break;
                        default:
                            break;
                    }
                    break;
                case LOGIN:
                    switch (type){
                        case SHOW:
                            pass = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD)).getText();
                            user = ((JTextField)componentsOnScreen.get(guiItems.USERNAME)).getText();
                            clearGui();
                            login(true,user,pass);
                            break;
                        case HIDE:
                            pass = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD)).getText();
                            user = ((JTextField)componentsOnScreen.get(guiItems.USERNAME)).getText();
                            clearGui();
                            login(false,user,pass);
                            break;
                        case BACK:
                            clearGui();
                            onlineState = GameState.LOGIN_REGISTER;
                            break;
                        case LOGIN_BUTTON:
                            popUp("TU PUTA MADRE, GRACIAS");
                            break;
                        case POP_UP_BUTTON:
                            pass = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD)).getText();
                            user = ((JTextField)componentsOnScreen.get(guiItems.USERNAME)).getText();
                            clearGui();
                            login(false,user,pass);
                        default:
                            break;
                    }
                    break;
                case REGISTER:
                    switch (type){
                        case SHOW:
                            pass = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD)).getText();
                            pass2 = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD_2)).getText();
                            user = ((JTextField)componentsOnScreen.get(guiItems.USERNAME)).getText();
                            email = ((JTextField)componentsOnScreen.get(guiItems.EMAIL)).getText();
                            clearGui();
                            register(true,user,pass,pass2,email);
                            break;
                        case HIDE:
                            pass = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD)).getText();
                            pass2 = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD_2)).getText();
                            user = ((JTextField)componentsOnScreen.get(guiItems.USERNAME)).getText();
                            email = ((JTextField)componentsOnScreen.get(guiItems.EMAIL)).getText();
                            clearGui();
                            register(false,user,pass,pass2,email);
                            break;
                        case BACK:
                            clearGui();
                            onlineState = GameState.LOGIN_REGISTER;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
