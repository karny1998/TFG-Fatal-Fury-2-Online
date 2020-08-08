package lib.objects.networking.gui;

import lib.Enums.GameState;
import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;
import lib.objects.Screen;
import lib.objects.networking.connection;
import lib.objects.networking.gui.gui_components.*;
import lib.objects.networking.msgID;
import lib.objects.networking.online_mode;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.message;
import lib.utils.sendableObjects.simpleObjects.profile;
import lib.utils.sendableObjects.simpleObjects.string;
import videojuegos.Principal;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class online_mode_gui {
    private online_mode online_controller;
    private Map<guiItems,Component> componentsOnScreen = new HashMap<>();
    private List<guiItems> itemsOnScreen = new ArrayList<>();
    private List<message> friendMessages;
    private GameState onlineState = GameState.LOGIN_REGISTER;
    private gameGUI gui;
    private Principal principal;
    private double m = 1.0;
    private List<String> friends;
    private List<String> pendingMessages;
    private int friendSelected = -1;
    private int xTableClick = 0, yTableClick = 0;
    private profile profileToShow;
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),grey3 = new Color(45,48,85),
            grey4 = new Color(99,96,171), brown = new Color(140,105,57), blue = new Color(0,0,148);
    private profile_gui profile;
    private character_selection_gui char_sel;
    private String userLogged = null;
    private notificationsReceiver notifier;// = new notificationsReceiver();
    private chat_gui chatgui;
    private notifications_gui notifications;
    private Font f,f2,f3;
    {
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont(25f);
            f2 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont(15f);
            f3 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont(35f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public online_mode_gui(online_mode online_controller, Screen screen, GameState onlineState){
        this.online_controller = online_controller;
        this.onlineState = onlineState;
        this.gui = (gameGUI) screen.getPrincipal().getGui();
        this.principal = screen.getPrincipal();
        this.notifier = new notificationsReceiver();
        this.notifier.start();
    }

    public void drawGUI(){
        if(gui.getMultiplier() != m){
            m = gui.getMultiplier();
            try {
                f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (25.0*m));
                f2 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (15.0*m));
                f3 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (35.0*m));
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
                break;
            case PRINCIPAL_GUI:
                principalGUI();
                break;
            case PROFILE_GUI:
                profileGUI();
                break;
            case CHARACTER_SELECTION:
                character_selection();
                break;
            default:
                break;
        }
    }

    public int res(int x){
        return (int)(((double)(x*m))+0.5);
    }

    public ImageIcon loadIcon(String path, int x, int y){
        ImageIcon aux;
        BufferedImage img = null;
        try {
            img = ImageIO.read(this.getClass().getResource(path));
            Image scaled = img.getScaledInstance(res(x), res(y), Image.SCALE_SMOOTH);
            aux = new ImageIcon(scaled);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return aux;
    }

    public void addComponents(guiItems items[], Component components[]){
        for(int i = 0; i < items.length; ++i){
            gui.add(components[i]);
            componentsOnScreen.put(items[i], components[i]);
            itemsOnScreen.add(items[i]);
        }
    }

    public void enableComponents(guiItems items[], boolean enable){
        for(int i = 0; i < items.length; ++i){
            if(componentsOnScreen.containsKey(items[i])){
                componentsOnScreen.get(items[i]).setEnabled(enable);
                if(items[i] == guiItems.FRIEND_LIST || items[i] == guiItems.HISTORIAL){
                    JScrollPane scroll = (JScrollPane) componentsOnScreen.get(items[i]);
                    scroll.getVerticalScrollBar().setEnabled(enable);
                    scroll.getViewport().getView().setEnabled(enable);
                }
            }
        }
    }

    public void deleteComponents(guiItems items[]){
        for(int i = 0; i < items.length; ++i){
            if(componentsOnScreen.containsKey(items[i])){
                gui.remove(componentsOnScreen.get(items[i]));
                componentsOnScreen.remove(items[i]);
                itemsOnScreen.remove(items[i]);
            }
        }
    }

    public void clearGui(){
        gui.removeAll();
        componentsOnScreen.clear();
        itemsOnScreen.clear();
    }

    public void reloadGUI(){
        for(int i = 0; i < itemsOnScreen.size();++i){
            gui.remove(componentsOnScreen.get(itemsOnScreen.get(i)));
        }
        for(int i = 0; i < itemsOnScreen.size();++i){
            gui.add(componentsOnScreen.get(itemsOnScreen.get(i)));
        }
        gui.revalidate();
        gui.repaint();
    }

    public JLabel auxiliarBackgroud(){
        JLabel back = new JLabel(loadIcon("/assets/sprites/menu/auxiliar.png", 1013,683));
        back.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {}
            @Override
            public void mousePressed(MouseEvent mouseEvent) {}
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                new guiListener( online_mode_gui.this, guiItems.AUXILIAR_BACKGROUND).actionPerformed(null);
            }
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {}
            @Override
            public void mouseExited(MouseEvent mouseEvent) {}
        });
        back.setOpaque(false);
        back.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        back.setBounds(0,0,res(1013),res(683));
        return back;
    }

    public JButton backButton(){
        JButton back = new JButton("Back");
        back.addActionListener(new guiListener(this,guiItems.BACK));
        back.setIcon(new ImageIcon(getClass().getResource("/assets/sprites/menu/back.png")));
        back.setFont(f);
        back.setForeground(Color.YELLOW);
        back.setOpaque(false);
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        back.setBounds(0,res(20),res(200),res(60));
        return back;
    }

    public JButton generateSimpleButton(String text, guiItems id, Font font, Color color, Color back, int x, int y, int w,
                                        int h, boolean noMargin){
        JButton aux = new JButton(text);
        if(id != null) {
            aux.addActionListener(new guiListener(this, id));
        }
        aux.setFont(font);
        aux.setForeground(color);
        aux.setBackground(back);
        aux.setBounds(res(x),res(y), res(w),res(h));
        if(noMargin){
            aux.setMargin(new Insets(0,0,0,0));
        }
        return aux;
    }

    public JTextField generateSimpleTextField(String text, Font font, Color color, Color back, int x, int y, int w, int h,
                                              boolean editable, boolean emptyBorder){
        JTextField aux = new JTextField(text);
        if(emptyBorder) {
            aux.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        }
        aux.setEditable(editable);
        aux.setFont(font);
        aux.setForeground(color);
        aux.setBackground(back);
        if(editable){
            aux.setBorder(BorderFactory.createCompoundBorder(
                    aux.getBorder(),
                    BorderFactory.createEmptyBorder(0, 10, 0, 0)));
        }
        aux.setBounds(res(x),res(y), res(w),res(h));
        return aux;
    }

    public JTextArea generateSimpleTextArea(String text, Font font, Color color, Color back, int x, int y, int w, int h,
                                              boolean editable, boolean emptyBorder){
        JTextArea aux = new JTextArea(text);
        if(emptyBorder) {
            aux.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        }
        aux.setEditable(editable);
        aux.setFont(font);
        aux.setForeground(color);
        aux.setBackground(back);;
        aux.setBounds(res(x),res(y), res(w),res(h));
        return aux;
    }

    public String centerLine(String msg, int max){
        String res = msg;
        int x = (max - msg.length())/2;
        for(int i = 0; i < x; ++i){
            res = " " + res;
        }
        return res;
    }

    public String fillText(String msg, int max, int nLines){
        String aux[] = msg.split(" ");
        List<String> lines = new ArrayList<>();
        String res = "", line = "";
        for(int i = 0; i < aux.length;++i){
            if(line.length() + aux[i].length() <= max){
                line += aux[i] + " ";
            }
            else{
                line = line.substring(0, line.length() - 1);
                lines.add(centerLine(line, max));
                line = "";
                --i;
            }
        }
        if(!line.equals("")){
            lines.add(centerLine(line, max));
        }
        for(int i = 0; i < lines.size();++i){
            res += (lines.get(i) + "\n");
        }
        int x = (nLines - lines.size())/2;
        for(int i = 0; i < x;++i){
            res = "\n" + res;
        }
        return res;
    }

    public void popUp(String msg){
        popUp(msg, guiItems.POP_UP_BUTTON);
    }

    public void popUp(String msg, guiItems okey){

        msg = fillText(msg, 24,4);

        JTextArea popup = generateSimpleTextArea(msg, f, Color.YELLOW, grey1, 405, 250, 490, 150, false, true);

        JButton popupB = generateSimpleButton("Okey", okey, f, Color.YELLOW, grey2, 580, 420, 120, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540,280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370),res(220),res(540),res(280));

        guiItems items[] = {guiItems.POP_UP, okey, guiItems.POP_UP_TABLE};
        Component components[] = {popup, popupB, table};

        guiItems items2[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items2[i] = itemsOnScreen.get(i);
        }

        enableComponents(items2, false);

        addComponents(items, components);

        for(int i = 0; i < items.length; ++i){
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0,guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0,guiItems.POP_UP);
        itemsOnScreen.add(0,okey);

        reloadGUI();
    }

    public void searchingGame(boolean rank){
        String msg = "Searching game";
        if(rank){
            msg = "Searching ranked game";
        }

        guiItems type = guiItems.CANCEL_SEARCH_GAME;
        /*if(rank){
            type = guiItems.CANCEL_SEARCH_RANK_GAME;
        }*/

        JTextField popup = generateSimpleTextField(msg, f3, Color.YELLOW, grey1, 400, 250, 490, 150, false, true);
        popup.setHorizontalAlignment(JTextField.CENTER);

        JButton popupB = generateSimpleButton("Cancel", type, f, Color.YELLOW, grey2, 545, 420, 190, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540,280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370),res(220),res(540),res(280));

        guiItems items[] = {guiItems.POP_UP, type, guiItems.POP_UP_TABLE};
        Component components[] = {popup, popupB, table};

        guiItems items2[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items2[i] = itemsOnScreen.get(i);
        }

        enableComponents(items2, false);

        addComponents(items, components);

        for(int i = 0; i < items.length; ++i){
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0,guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0,guiItems.POP_UP);
        itemsOnScreen.add(0,type);

        reloadGUI();
    }

    public void popUpWithConfirmation(String msg, guiItems yes, guiItems no){
        msg = fillText(msg, 24,4);

        JTextArea popup = generateSimpleTextArea(msg, f, Color.YELLOW, grey1, 400, 250, 490, 150, false, true);

        JButton popupB1 = generateSimpleButton("Yes", yes, f, Color.YELLOW, grey2, 420, 420, 200, 60, false);
        JButton popupB2 = generateSimpleButton("No", no, f, Color.YELLOW, grey2, 660, 420, 200, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540,280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370),res(220),res(540),res(280));

        guiItems items[] = {guiItems.POP_UP, yes, no, guiItems.POP_UP_TABLE};
        Component components[] = {popup, popupB1, popupB2, table};

        guiItems items2[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items2[i] = itemsOnScreen.get(i);
        }

        enableComponents(items2, false);

        addComponents(items, components);

        for(int i = 0; i < items.length; ++i){
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0,guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0,guiItems.POP_UP);
        itemsOnScreen.add(0,yes);
        itemsOnScreen.add(0,no);

        reloadGUI();
    }

    public void login_register(){
        if(!componentsOnScreen.containsKey(guiItems.LOGIN_BUTTON)) {
            clearGui();
            JButton login = generateSimpleButton("Login", guiItems.LOGIN_BUTTON, f, Color.YELLOW, grey1, 370, 560, 250, 60, false);

            JButton register = generateSimpleButton("Sign up", guiItems.REGISTER_BUTTON, f, Color.YELLOW, grey1, 660, 560, 250, 60, false);

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
            clearGui();

            JButton login = generateSimpleButton("Login", guiItems.LOGIN_BUTTON, f, Color.YELLOW, grey1, 515, 610, 250, 60, false);

            JTextField username = generateSimpleTextField(user, f, Color.YELLOW, grey2, 365, 520, 250, 60, true, false);

            JTextField username2 = generateSimpleTextField("Username", f, Color.YELLOW, blue, 365, 460, 250, 60, false, true);

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
            password.setBackground(grey2);
            password.setBounds((int)(665*m),(int)(520*m), (int)(250*m),(int)(60*m));

            JTextField password2 = generateSimpleTextField("Password", f, Color.YELLOW, blue, 665, 460, 250, 60, false, true);

            guiItems items[] = {guiItems.LOGIN_BUTTON, guiItems.USERNAME, guiItems.PASSWORD, guiItems.USERNAME_TEXT, guiItems.PASSWORD_TEXT, guiItems.BACK};
            Component components[] = {login, username, password, username2, password2, backButton()};
            addComponents(items, components);

            if(show){
                JButton showPass = generateSimpleButton("Hide", guiItems.HIDE, f2, Color.YELLOW, grey2, 930, 530, 80, 40, false);
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            }
            else{
                JButton showPass = generateSimpleButton("Show", guiItems.SHOW, f2, Color.YELLOW, grey2, 930, 530, 80, 40, false);
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
            clearGui();
            if(show){
                JButton showPass = generateSimpleButton("Hide", guiItems.HIDE, f2, Color.YELLOW, grey2, 493, 181, 80, 40, false);
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            }
            else{
                JButton showPass = generateSimpleButton("Show", guiItems.SHOW, f2, Color.YELLOW, grey2, 493, 181, 80, 40, false);
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.SHOW}, new Component[]{showPass});
            }

            JButton login = generateSimpleButton("Register", guiItems.REGISTER_BUTTON, f, Color.YELLOW, grey1, 515, 600, 250, 60, false);

            JTextField username = generateSimpleTextField(user, f, Color.YELLOW, grey2, 290, 100, 680, 60, true, false);

            JTextField username2 = generateSimpleTextField("Username", f, Color.YELLOW, grey1, 290, 30, 680, 60, false, true);

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
            password.setBackground(grey2);
            password.setBounds((int)(290*m),(int)(240*m), (int)(680*m),(int)(60*m));

            JTextField password2 = generateSimpleTextField("Password", f, Color.YELLOW, grey1, 290, 170, 680, 60, false, true);

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
            password_2.setBackground(grey2);
            password_2.setBounds((int)(290*m),(int)(380*m), (int)(680*m),(int)(60*m));

            JTextField password2_2 = generateSimpleTextField("Repeat password", f, Color.YELLOW, grey1, 290, 310, 680, 60, false, true);

            JTextField email = generateSimpleTextField(eml, f, Color.YELLOW, grey2, 290, 510, 680, 60, true, false);

            JTextField email2 = generateSimpleTextField("Email", f, Color.YELLOW, grey1, 290, 450, 680, 60, false, true);

            ImageIcon icon = loadIcon("/assets/sprites/menu/tablon_register.png",788,720);
            JLabel table = new JLabel(icon);
            table.setBounds(res(246),0,res(788),res(720));

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

    public void principalGUI(){
        if(!componentsOnScreen.containsKey(guiItems.FRIEND_LIST)) {
            clearGui();

            new friend_list_gui(this,friends, pendingMessages);

            this.notifications = new notifications_gui(this);
            notifications.showNotifications();

            JButton normal = generateSimpleButton("Normal mode", guiItems.NORMAL_BUTTON, f, Color.YELLOW, grey1, 308, 375, 400, 65, false);
            JButton ranked = generateSimpleButton("Ranked mode", guiItems.RANKED_BUTTON, f, Color.YELLOW, grey1, 308, 450, 400, 65, false);
            JButton tournaments = generateSimpleButton("Tournament mode", guiItems.TOURNAMENT_BUTTON, f, Color.YELLOW, grey1, 308, 520, 400, 65, false);
            JButton profile = generateSimpleButton("Profile", guiItems.PROFILE_BUTTON, f, Color.YELLOW, grey1, 308, 595, 200, 65, false);
            JButton exit = generateSimpleButton("Quit", guiItems.QUIT_BUTTON, f, Color.YELLOW, grey1, 508, 595, 200, 65, false);
            JButton back = backButton();

            guiItems items[] = {guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.TOURNAMENT_BUTTON, guiItems.PROFILE_BUTTON,
                    guiItems.QUIT_BUTTON, guiItems.BACK, guiItems.AUXILIAR_BACKGROUND};
            Component components[] = {normal, ranked, tournaments, profile, exit, back, auxiliarBackgroud()};

            addComponents(items, components);

            gui.setBack(2);
            gui.repaint();
            gui.revalidate();
        }
    }

    public void friendInteractionPopUp(){
        JButton invite, msg, profile, delete;
        int x = 830, y = yTableClick;

        if(y > 317+35){
            y -= 160;
        }

        invite = generateSimpleButton("Challenge",guiItems.INVITE_FRIEND,f2,Color.YELLOW, grey3, x, y, 200,40, false);
        msg = generateSimpleButton("Chat",guiItems.MESSAGE_FRIEND,f2,Color.YELLOW, grey3, x, y+40, 200,40, false);
        profile = generateSimpleButton("Profile",guiItems.PROFILE_FRIEND,f2,Color.YELLOW, grey3, x, y+80, 200,40, false);
        delete = generateSimpleButton("Delete",guiItems.DELETE_FRIEND,f2,Color.YELLOW, grey3, x, y+120, 200,40, false);

        guiItems items[] = {guiItems.INVITE_FRIEND, guiItems.MESSAGE_FRIEND, guiItems.PROFILE_FRIEND, guiItems.DELETE_FRIEND};
        Component components[] = {invite, msg, profile, delete};

        for(int i = 0; i < items.length; ++i){
            if(componentsOnScreen.containsKey(items[i])){
                gui.remove(componentsOnScreen.get(items[i]));
            }
        }
        for(int i = 0; i < items.length; ++i){
            for(int j = 0; j < itemsOnScreen.size();++j){
                if(itemsOnScreen.get(j) == items[i]){
                    itemsOnScreen.remove(j);
                    --j;
                }
            }
        }

        addComponents(items, components);
        for(int i = 0; i < items.length; ++i){
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0,guiItems.INVITE_FRIEND);
        itemsOnScreen.add(0,guiItems.MESSAGE_FRIEND);
        itemsOnScreen.add(0,guiItems.PROFILE_FRIEND);
        itemsOnScreen.add(0,guiItems.DELETE_FRIEND);

        enableComponents(new guiItems[]{guiItems.HISTORIAL}, false);

        reloadGUI();
    }

    public void addFriend(){
        JTextField popup = generateSimpleTextField("Introduce the username:", f, Color.YELLOW, grey1, 405, 250, 490, 60, false, true);

        JTextField name = generateSimpleTextField("", f, Color.YELLOW, grey1, 485, 325, 300, 60, true, false);

        JButton popupB1 = generateSimpleButton("Add", guiItems.CONFIRM_ADD_BUTTON, f, Color.YELLOW, grey2, 420, 420, 200, 60, false);
        JButton popupB2 = generateSimpleButton("Cancel", guiItems.CANCEL_ADD_BUTTON, f, Color.YELLOW, grey2, 660, 420, 200, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png",540,280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370),res(220),res(540),res(280));
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_NAME, guiItems.CONFIRM_ADD_BUTTON, guiItems.CANCEL_ADD_BUTTON, guiItems.POP_UP_TABLE};
                deleteComponents(items);
                addFriend();
            }
        });

        guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_NAME, guiItems.CONFIRM_ADD_BUTTON, guiItems.CANCEL_ADD_BUTTON, guiItems.POP_UP_TABLE};
        Component components[] = {popup, name, popupB1, popupB2, table};

        addComponents(items, components);

        for(int i = 0; i < items.length; ++i){
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0,guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0,guiItems.POP_UP);
        itemsOnScreen.add(0,guiItems.INTRODUCE_NAME);
        itemsOnScreen.add(0,guiItems.CONFIRM_ADD_BUTTON);
        itemsOnScreen.add(0,guiItems.CANCEL_ADD_BUTTON);

        items = new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.TOURNAMENT_BUTTON, guiItems.QUIT_BUTTON,
                guiItems.PROFILE_BUTTON, guiItems.ADD_FRIEND,guiItems.FRIEND_LIST, guiItems.BACK};
        enableComponents(items, false);

        reloadGUI();
    }

    public void chat(String friend){
        sendableObjectsList msgs = (sendableObjectsList)online_controller.getConToServer().sendStringWaitingAnswerObject(msgID.toServer.request,"MESSAGE HISTORIAL:"+friend,0);
        friendMessages = new ArrayList<>();
        for(sendableObject m : msgs.getMsgs()){
            friendMessages.add((message) m);
        }
        chatgui = new chat_gui(this,friendMessages, friend);
        pendingMessages.remove(friend);
        closeFriendList();
        new friend_list_gui(this,friends,pendingMessages);
        reloadGUI();
    }

    public void profileGUI(){
        if(!componentsOnScreen.containsKey(guiItems.PROFILE_NAME)){
            clearGui();
            notifications.showNotifications();
            profile = new profile_gui(this, profileToShow);
            gui.setBack(3);
            gui.repaint();
            gui.revalidate();
        }
    }

    public void character_selection(){
        if(!componentsOnScreen.containsKey(guiItems.SELECT_TERRY)){
            char_sel = new character_selection_gui(this,true);
            if(char_sel.isHost()){
                gui.setBack(5);
            }
            else {
                gui.setBack(4);
            }
        }
    }

    public void closeAddFriend(){
        if(componentsOnScreen.containsKey(guiItems.CANCEL_ADD_BUTTON)){
            guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_NAME, guiItems.CONFIRM_ADD_BUTTON, guiItems.CANCEL_ADD_BUTTON, guiItems.POP_UP_TABLE};
            deleteComponents(items);

            items = new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.TOURNAMENT_BUTTON, guiItems.QUIT_BUTTON,
                    guiItems.PROFILE_BUTTON, guiItems.ADD_FRIEND,guiItems.FRIEND_LIST, guiItems.BACK};
            enableComponents(items, true);
            reloadGUI();
        }
    }

    public void closeFriendInteractionPopUp(){
        if(componentsOnScreen.containsKey(guiItems.INVITE_FRIEND)) {
            deleteComponents(new guiItems[]{guiItems.INVITE_FRIEND, guiItems.MESSAGE_FRIEND, guiItems.PROFILE_FRIEND, guiItems.DELETE_FRIEND});
            enableComponents(new guiItems[]{guiItems.HISTORIAL}, true);
            reloadGUI();
        }
    }

    public void closeChat(){
        if(componentsOnScreen.containsKey(guiItems.CHAT)) {
            deleteComponents(new guiItems[]{guiItems.CHAT, guiItems.SEND_MESSAGE, guiItems.MESSAGE_WRITER, guiItems.CLOSE_CHAT});
            enableComponents(new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.TOURNAMENT_BUTTON,
                    guiItems.QUIT_BUTTON, guiItems.PROFILE_BUTTON, guiItems.BACK}, true);
            reloadGUI();
        }
    }

    public void closePopUpWithConfirmation(guiItems yes, guiItems no){
        deleteComponents(new guiItems[]{guiItems.POP_UP_TABLE, guiItems.POP_UP, yes, no});
        guiItems items[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items[i] = itemsOnScreen.get(i);
        }

        enableComponents(items, true);
        reloadGUI();
    }

    public void closeSearchingGame(){
        deleteComponents(new guiItems[]{guiItems.POP_UP_TABLE, guiItems.POP_UP, guiItems.CANCEL_SEARCH_GAME});
        guiItems items[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items[i] = itemsOnScreen.get(i);
        }

        enableComponents(items, true);
        reloadGUI();
    }

    public void closePopUp(){
        closePopUp(guiItems.POP_UP_BUTTON);
    }

    public void closePopUp(guiItems okey){
        deleteComponents(new guiItems[]{guiItems.POP_UP_TABLE, guiItems.POP_UP, okey});
        guiItems items[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items[i] = itemsOnScreen.get(i);
        }

        enableComponents(items, true);
        reloadGUI();
    }

    public void closeFriendList(){
        deleteComponents(new guiItems[]{guiItems.ADD_FRIEND, guiItems.FRIEND_LIST});
    }

    public void loadFriends(){
        sendableObjectsList res = (sendableObjectsList) online_controller.getConToServer().sendStringWaitingAnswerObject(msgID.toServer.request, "FRIEND LIST",0);
        friends = new ArrayList<>();
        for(sendableObject s : res.getMsgs()){
            friends.add(((string)s).getContent());
        }
        Collections.sort(friends);
        res = (sendableObjectsList) online_controller.getConToServer().sendStringWaitingAnswerObject(msgID.toServer.request, "FRIENDS PENDING MESSAGES",0);
        pendingMessages = new ArrayList<>();
        for(sendableObject s : res.getMsgs()){
            pendingMessages.add(((string)s).getContent());
        }
        Collections.sort(pendingMessages);
    }

    public void friendRequest(String friend){
        popUpWithConfirmation("You have received   friend request from:  "+friend,guiItems.ACCEPT_FRIEND, guiItems.REJECT_FRIEND);
        JButton aux = ((JButton)componentsOnScreen.get(guiItems.ACCEPT_FRIEND));
        for(ActionListener al : aux.getActionListeners() ) {
            aux.removeActionListener( al );
        }
        aux.addActionListener(new guiListener(online_mode_gui.this, guiItems.ACCEPT_FRIEND,friend));
        aux = ((JButton)componentsOnScreen.get(guiItems.REJECT_FRIEND));
        for(ActionListener al : aux.getActionListeners() ) {
            aux.removeActionListener( al );
        }
        aux.addActionListener(new guiListener(online_mode_gui.this, guiItems.REJECT_FRIEND,friend));
    }

    /**
     * The type Receiver.
     */
    protected class notificationsReceiver extends Thread{
        /**
         * The Con.
         */
        private connection con;
        /**
         * The Stop.
         */
        private boolean stop = false;
        /**
         * The Thread.
         */
        private final Thread thread;

        /**
         * Instantiates a new Receiver.
         */
        public notificationsReceiver() {
            this.thread = new Thread(this);
            this.con = online_controller.getConToServer();
        }

        /**
         * Start.
         */
        @Override
        public void start(){
            this.thread.start();
        }

        /**
         * Do stop.
         */
        public synchronized void doStop() {
            this.stop = true;
        }

        /**
         * Keep running boolean.
         *
         * @return the boolean
         */
        private synchronized boolean keepRunning() {
            return this.stop == false;
        }

        /**
         * Run.
         */
        @Override
        public void run(){
            while(keepRunning()) {
                String notification = con.receiveNotifications();
                String res[] = notification.split(":");
                if(res[0].equals("FRIEND REQUEST") && (onlineState == GameState.PRINCIPAL_GUI ||  onlineState == GameState.PROFILE_GUI) ){
                    notifications.addFriendRequest(res[1]);
                }
                else if(res[0].equals("NEW FRIEND") && (onlineState == GameState.PRINCIPAL_GUI ||  onlineState == GameState.PROFILE_GUI) ){
                    friends.add(res[1]);
                    Collections.sort(friends);
                    closeFriendList();
                    new friend_list_gui(online_mode_gui.this, friends,pendingMessages);
                }
                else if(res[0].equals("MESSAGE RECEIVED") && (onlineState == GameState.PRINCIPAL_GUI ||  onlineState == GameState.PROFILE_GUI) ){
                    if(componentsOnScreen.containsKey(guiItems.CHAT) && friends.get(friendSelected).equals(res[1])){
                        message aux = friendMessages.get(friendMessages.size()-1);
                        chatgui.addMessage(new message(aux.getId()+1, res[1], userLogged,res[2]));
                    }
                    else{
                        boolean yet = false;
                        for(int i = 0; !yet && i < pendingMessages.size(); ++i){
                            yet = pendingMessages.equals(res[1]);
                        }
                        if(!yet){
                            pendingMessages.add(res[1]);
                            closeFriendList();
                            new friend_list_gui(online_mode_gui.this, friends,pendingMessages);
                        }
                    }
                }
                else if(res[0].equals("DELETED FRIEND") && (onlineState == GameState.PRINCIPAL_GUI ||  onlineState == GameState.PROFILE_GUI) ){
                    friends.remove(res[1]);
                    pendingMessages.remove(res[1]);
                    closeFriendList();
                    new friend_list_gui(online_mode_gui.this, friends, pendingMessages);
                }
                else if(res[0].equals("SEARCH GAME")){
                    String ip = res[2];
                    boolean isHost = Boolean.parseBoolean(res[1]);
                    online_controller.generateFight(ip, isHost, Playable_Character.TERRY, Playable_Character.TERRY, Scenario_type.USA);
                }
                else if(res[0].equals("SERVER CLOSED")){
                    popUp("Server has been closed.", guiItems.CLOSE_GAME);
                }
            }
        }
    }

    public Map<guiItems, Component> getComponentsOnScreen() {
        return componentsOnScreen;
    }

    public void setComponentsOnScreen(Map<guiItems, Component> componentsOnScreen) {
        this.componentsOnScreen = componentsOnScreen;
    }

    public List<guiItems> getItemsOnScreen() {
        return itemsOnScreen;
    }

    public void setItemsOnScreen(List<guiItems> itemsOnScreen) {
        this.itemsOnScreen = itemsOnScreen;
    }

    public GameState getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(GameState onlineState) {
        this.onlineState = onlineState;
        online_controller.setOnlineState(onlineState);
    }

    public gameGUI getGui() {
        return gui;
    }

    public void setGui(gameGUI gui) {
        this.gui = gui;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public double getM() {
        return m;
    }

    public void setM(double m) {
        this.m = m;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public int getFriendSelected() {
        return friendSelected;
    }

    public void setFriendSelected(int friendSelected) {
        this.friendSelected = friendSelected;
    }

    public Color getGrey1() {
        return grey1;
    }

    public void setGrey1(Color grey1) {
        this.grey1 = grey1;
    }

    public Color getGrey2() {
        return grey2;
    }

    public void setGrey2(Color grey2) {
        this.grey2 = grey2;
    }

    public Color getGrey3() {
        return grey3;
    }

    public void setGrey3(Color grey3) {
        this.grey3 = grey3;
    }

    public Color getBrown() {
        return brown;
    }

    public void setBrown(Color brown) {
        this.brown = brown;
    }

    public Color getBlue() {
        return blue;
    }

    public void setBlue(Color blue) {
        this.blue = blue;
    }

    public Font getF() {
        return f;
    }

    public void setF(Font f) {
        this.f = f;
    }

    public Font getF2() {
        return f2;
    }

    public void setF2(Font f2) {
        this.f2 = f2;
    }

    public Font getF3() {
        return f3;
    }

    public void setF3(Font f3) {
        this.f3 = f3;
    }

    public int getxTableClick() {
        return xTableClick;
    }

    public void setxTableClick(int xYableClick) {
        this.xTableClick = xYableClick;
    }

    public int getyTableClick() {
        return yTableClick;
    }

    public void setyTableClick(int yTableClick) {
        this.yTableClick = yTableClick;
    }

    public List<message> getFriendMessages() {
        return friendMessages;
    }

    public void setFriendMessages(List<message> friendMessages) {
        this.friendMessages = friendMessages;
    }

    public Color getGrey4() {
        return grey4;
    }

    public void setGrey4(Color grey4) {
        this.grey4 = grey4;
    }

    public profile getProfileToShow() {
        return profileToShow;
    }

    public void setProfileToShow(profile profileToShow) {
        this.profileToShow = profileToShow;
    }

    public void setProfile(profile_gui profile) {
        this.profile = profile;
    }

    public profile_gui getProfile() {
        return profile;
    }

    public online_mode getOnline_controller() {
        return online_controller;
    }

    public void setOnline_controller(online_mode online_controller) {
        this.online_controller = online_controller;
    }

    public character_selection_gui getChar_sel() {
        return char_sel;
    }

    public void setChar_sel(character_selection_gui char_sel) {
        this.char_sel = char_sel;
    }

    public String getUserLogged() {
        return userLogged;
    }

    public void setUserLogged(String userLogged) {
        this.online_controller.setUserLogged(userLogged);
        this.userLogged = userLogged;
    }

    public notificationsReceiver getNotifier() {
        return notifier;
    }

    public void setNotifier(notificationsReceiver notifier) {
        this.notifier = notifier;
    }

    public chat_gui getChatgui() {
        return chatgui;
    }

    public void setChatgui(chat_gui chatgui) {
        this.chatgui = chatgui;
    }

    public notifications_gui getNotifications() {
        return notifications;
    }

    public void setNotifications(notifications_gui notifications) {
        this.notifications = notifications;
    }

    public List<String> getPendingMessages() {
        return pendingMessages;
    }

    public void setPendingMessages(List<String> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }
}
