package lib.objects.networking.gui;

import lib.Enums.Fight_Results;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

/**
 * The type Online mode gui.
 */
public class online_mode_gui {
    /**
     * The Online controller.
     */
    private online_mode online_controller;
    /**
     * The Components on screen.
     */
    private Map<guiItems, Component> componentsOnScreen = new HashMap<>();
    /**
     * The Items on screen.
     */
    private List<guiItems> itemsOnScreen = new ArrayList<>();
    /**
     * The Friend messages.
     */
    private List<message> friendMessages;
    /**
     * The Online state.
     */
    private GameState onlineState = GameState.LOGIN_REGISTER;
    /**
     * The Gui.
     */
    private gameGUI gui;
    /**
     * The Principal.
     */
    private Principal principal;
    /**
     * The M.
     */
    private double m = 1.0;
    /**
     * The Friends.
     */
    private List<String> friends;
    /**
     * The Pending messages.
     */
    private List<String> pendingMessages;
    /**
     * The Friend selected.
     */
    private int friendSelected = -1;
    /**
     * The X table click.
     */
    private int xTableClick = 0, /**
     * The Y table click.
     */
    yTableClick = 0;
    /**
     * The Profile to show.
     */
    private profile profileToShow;
    /**
     * The Grey 1.
     */
    private Color grey1 = new Color(33, 32, 57), /**
     * The Grey 2.
     */
    grey2 = new Color(66, 64, 114), /**
     * The Grey 3.
     */
    grey3 = new Color(45, 48, 85),
    /**
     * The Grey 4.
     */
    grey4 = new Color(99, 96, 171), /**
     * The Brown.
     */
    brown = new Color(140, 105, 57), /**
     * The Blue.
     */
    blue = new Color(0, 0, 148);
    /**
     * The Profile.
     */
    private profile_gui profile;
    /**
     * The Char sel.
     */
    private character_selection_gui char_sel;
    /**
     * The User logged.
     */
    private String userLogged = null;
    /**
     * The Notifier.
     */
    private notificationsReceiver notifier;// = new notificationsReceiver();
    /**
     * The Pinger.
     */
    private serverPinger pinger;
    /**
     * The Chatgui.
     */
    private chat_gui chatgui;
    /**
     * The Notifications.
     */
    private notifications_gui notifications;
    /**
     * The F.
     */
    private Font f, /**
     * The F 2.
     */
    f2, /**
     * The F 3.
     */
    f3;

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

    /**
     * Instantiates a new Online mode gui.
     *
     * @param online_controller the online controller
     * @param screen            the screen
     * @param onlineState       the online state
     */
    public online_mode_gui(online_mode online_controller, Screen screen, GameState onlineState) {
        this.online_controller = online_controller;
        this.onlineState = onlineState;
        this.gui = (gameGUI) screen.getPrincipal().getGui();
        this.principal = screen.getPrincipal();
        this.pinger = new serverPinger(online_controller.getConToServer());
        this.pinger.start();
        this.notifier = new notificationsReceiver();
        this.notifier.start();
    }

    /**
     * Draw gui.
     */
    public void drawGUI() {
        if (!notifier.isAlive()) {
            this.notifier = new notificationsReceiver();
            this.notifier.start();
        }
        if (gui.getMultiplier() != m) {
            m = gui.getMultiplier();
            try {
                f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (25.0 * m));
                f2 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (15.0 * m));
                f3 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float) (35.0 * m));
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        principal.guiOn();
        switch (onlineState) {
            case SERVER_PROBLEM:
                server_problem(false);
                break;
            case LOGIN_REGISTER:
                login_register();
                break;
            case LOGIN:
                login(false, "", "");
                break;
            case REGISTER:
                register(false, "", "", "", "");
                break;
            case PRINCIPAL_GUI:
                principalGUI();
                break;
            case PROFILE_GUI:
                profileGUI();
                break;
            case ONLINE_RANKING:
                ranking();
                break;
            case ONLINE_SEARCHING_FIGHT:
                break;
            case GAME_END:
                if (!componentsOnScreen.containsKey(guiItems.GAME_RESULT)) {
                    gui.setBack(6);
                    new end_game_gui(this, online_controller.isHost(), online_controller.getRival(),
                            online_controller.getChar1(), online_controller.getChar2(),
                            online_controller.getResult(), online_controller.getRankPoints(), online_controller.isRanked());
                }
                break;
            case CHARACTER_SELECTION:
                /*if(!componentsOnScreen.containsKey(guiItems.SELECT_TERRY)) {
                    character_selection(false, "127.0.0.1", "ss",false);
                }*/
                break;
            default:
                break;
        }
    }

    /**
     * Res int.
     *
     * @param x the x
     * @return the int
     */
    public int res(int x) {
        return (int) (((double) (x * m)) + 0.5);
    }

    /**
     * Load icon image icon.
     *
     * @param path the path
     * @param x    the x
     * @param y    the y
     * @return the image icon
     */
    public ImageIcon loadIcon(String path, int x, int y) {
        ImageIcon aux;
        BufferedImage img = null;
        try {
            img = ImageIO.read(this.getClass().getResource(path));
            Image scaled = img.getScaledInstance(res(x), res(y), Image.SCALE_SMOOTH);
            aux = new ImageIcon(scaled);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return aux;
    }

    /**
     * Add components.
     *
     * @param items      the items
     * @param components the components
     */
    public void addComponents(guiItems items[], Component components[]) {
        for (int i = 0; i < items.length; ++i) {
            gui.add(components[i]);
            componentsOnScreen.put(items[i], components[i]);
            itemsOnScreen.add(items[i]);
        }
    }

    /**
     * Enable components.
     *
     * @param items  the items
     * @param enable the enable
     */
    public void enableComponents(guiItems items[], boolean enable) {
        for (int i = 0; i < items.length; ++i) {
            if (componentsOnScreen.containsKey(items[i])) {
                componentsOnScreen.get(items[i]).setEnabled(enable);
                if (items[i] == guiItems.FRIEND_LIST || items[i] == guiItems.HISTORIAL) {
                    JScrollPane scroll = (JScrollPane) componentsOnScreen.get(items[i]);
                    scroll.getVerticalScrollBar().setEnabled(enable);
                    scroll.getViewport().getView().setEnabled(enable);
                }
            }
        }
    }

    /**
     * Delete components.
     *
     * @param items the items
     */
    public void deleteComponents(guiItems items[]) {
        for (int i = 0; i < items.length; ++i) {
            if (componentsOnScreen.containsKey(items[i])) {
                gui.remove(componentsOnScreen.get(items[i]));
                componentsOnScreen.remove(items[i]);
                itemsOnScreen.remove(items[i]);
            }
        }
    }

    /**
     * Clear gui.
     */
    public void clearGui() {
        gui.removeAll();
        componentsOnScreen.clear();
        itemsOnScreen.clear();
    }

    /**
     * Reload gui.
     */
    public void reloadGUI() {
        try {
            for (int i = 0; i < itemsOnScreen.size(); ++i) {
                try {
                    gui.remove(componentsOnScreen.get(itemsOnScreen.get(i)));
                } catch (Exception e) {
                }
            }
            for (int i = 0; i < itemsOnScreen.size(); ++i) {
                try {
                    gui.add(componentsOnScreen.get(itemsOnScreen.get(i)));
                } catch (Exception e) {
                }
            }
            gui.revalidate();
            gui.repaint();
        } catch (Exception e) {
        }
    }

    /**
     * Auxiliar backgroud j label.
     *
     * @return the j label
     */
    public JLabel auxiliarBackgroud() {
        JLabel back = new JLabel(loadIcon("/assets/sprites/menu/auxiliar.png", 1013, 683));
        back.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                new guiListener(online_mode_gui.this, guiItems.AUXILIAR_BACKGROUND).actionPerformed(null);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });
        back.setOpaque(false);
        back.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        back.setBounds(0, 0, res(1013), res(683));
        return back;
    }

    /**
     * Back button j button.
     *
     * @return the j button
     */
    public JButton backButton() {
        JButton back = new JButton("Back");
        back.addActionListener(new guiListener(this, guiItems.BACK));
        back.setIcon(new ImageIcon(getClass().getResource("/assets/sprites/menu/back.png")));
        back.setFont(f);
        back.setForeground(Color.YELLOW);
        back.setOpaque(false);
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        back.setBounds(0, res(20), res(200), res(60));
        return back;
    }

    /**
     * Generate simple button j button.
     *
     * @param text     the text
     * @param id       the id
     * @param font     the font
     * @param color    the color
     * @param back     the back
     * @param x        the x
     * @param y        the y
     * @param w        the w
     * @param h        the h
     * @param noMargin the no margin
     * @return the j button
     */
    public JButton generateSimpleButton(String text, guiItems id, Font font, Color color, Color back, int x, int y, int w,
                                        int h, boolean noMargin) {
        JButton aux = new JButton(text);
        if (id != null) {
            aux.addActionListener(new guiListener(this, id));
        }
        aux.setFont(font);
        aux.setForeground(color);
        aux.setBackground(back);
        aux.setBounds(res(x), res(y), res(w), res(h));
        if (noMargin) {
            aux.setMargin(new Insets(0, 0, 0, 0));
        }
        return aux;
    }

    /**
     * Generate simple text field j text field.
     *
     * @param text        the text
     * @param font        the font
     * @param color       the color
     * @param back        the back
     * @param x           the x
     * @param y           the y
     * @param w           the w
     * @param h           the h
     * @param editable    the editable
     * @param emptyBorder the empty border
     * @return the j text field
     */
    public JTextField generateSimpleTextField(String text, Font font, Color color, Color back, int x, int y, int w, int h,
                                              boolean editable, boolean emptyBorder) {
        JTextField aux = new JTextField(text);
        if (emptyBorder) {
            aux.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        }
        aux.setEditable(editable);
        aux.setFont(font);
        aux.setForeground(color);
        aux.setBackground(back);
        if (editable) {
            aux.setBorder(BorderFactory.createCompoundBorder(
                    aux.getBorder(),
                    BorderFactory.createEmptyBorder(0, 10, 0, 0)));
        }
        aux.setBounds(res(x), res(y), res(w), res(h));
        return aux;
    }

    /**
     * Generate simple text area j text area.
     *
     * @param text        the text
     * @param font        the font
     * @param color       the color
     * @param back        the back
     * @param x           the x
     * @param y           the y
     * @param w           the w
     * @param h           the h
     * @param editable    the editable
     * @param emptyBorder the empty border
     * @return the j text area
     */
    public JTextArea generateSimpleTextArea(String text, Font font, Color color, Color back, int x, int y, int w, int h,
                                            boolean editable, boolean emptyBorder) {
        JTextArea aux = new JTextArea(text);
        if (emptyBorder) {
            aux.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        }
        aux.setEditable(editable);
        aux.setFont(font);
        aux.setForeground(color);
        aux.setBackground(back);
        ;
        aux.setBounds(res(x), res(y), res(w), res(h));
        return aux;
    }

    /**
     * Center line string.
     *
     * @param msg the msg
     * @param max the max
     * @return the string
     */
    public String centerLine(String msg, int max) {
        String res = msg;
        int x = (max - msg.length()) / 2;
        for (int i = 0; i < x; ++i) {
            res = " " + res;
        }
        return res;
    }

    /**
     * Fill text string.
     *
     * @param msg    the msg
     * @param max    the max
     * @param nLines the n lines
     * @return the string
     */
    public String fillText(String msg, int max, int nLines) {
        String aux[] = msg.split(" ");
        List<String> lines = new ArrayList<>();
        String res = "", line = "";
        for (int i = 0; i < aux.length; ++i) {
            if (line.length() + aux[i].length() <= max) {
                line += aux[i] + " ";
            } else {
                line = line.substring(0, line.length() - 1);
                lines.add(centerLine(line, max));
                line = "";
                --i;
            }
        }
        if (!line.equals("")) {
            lines.add(centerLine(line, max));
        }
        for (int i = 0; i < lines.size(); ++i) {
            res += (lines.get(i) + "\n");
        }
        int x = (nLines - lines.size()) / 2;
        for (int i = 0; i < x; ++i) {
            res = "\n" + res;
        }
        return res;
    }

    /**
     * Pop up.
     *
     * @param msg the msg
     */
    public void popUp(String msg) {
        popUp(msg, guiItems.POP_UP_BUTTON);
    }

    /**
     * Pop up.
     *
     * @param msg  the msg
     * @param okey the okey
     */
    public void popUp(String msg, guiItems okey) {

        closeAllPopUps();

        msg = fillText(msg, 23, 4);

        JTextArea popup = generateSimpleTextArea(msg, f, Color.YELLOW, grey1, 405, 250, 490, 150, false, true);

        JButton popupB = generateSimpleButton("Okey", okey, f, Color.YELLOW, grey2, 580, 420, 120, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540, 280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370), res(220), res(540), res(280));

        guiItems items[] = {guiItems.POP_UP, okey, guiItems.POP_UP_TABLE};
        Component components[] = {popup, popupB, table};

        guiItems items2[] = new guiItems[itemsOnScreen.size()];

        for (int i = 0; i < itemsOnScreen.size(); ++i) {
            items2[i] = itemsOnScreen.get(i);
        }

        enableComponents(items2, false);

        addComponents(items, components);

        for (int i = 0; i < items.length; ++i) {
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0, guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0, guiItems.POP_UP);
        itemsOnScreen.add(0, okey);

        reloadGUI();
    }

    /**
     * Searching game.
     *
     * @param friendChallenge the friend challenge
     * @param rank            the rank
     */
    public void searchingGame(boolean friendChallenge, boolean rank) {
        if (!componentsOnScreen.containsKey(guiItems.CANCEL_SEARCH_GAME) && !componentsOnScreen.containsKey(guiItems.CANCEL_SEARCH_RANK_GAME)) {
            clearGui();
            setOnlineState(GameState.ONLINE_SEARCHING_FIGHT);

            String msg = "Searching game";
            if (rank) {
                msg = "Searching ranked game";
            } else if (friendChallenge) {
                msg = "Waiting friend answer.";
            }
            msg = fillText(msg, 16, 2);

            guiItems type = guiItems.CANCEL_SEARCH_GAME;
            if (rank) {
                type = guiItems.CANCEL_SEARCH_RANK_GAME;
            } else if (friendChallenge) {
                type = guiItems.CANCEL_FRIEND_CHALLENGE;
            }

            JTextArea popup = generateSimpleTextArea(msg, f3, Color.YELLOW, grey1, 405, 250, 490, 150, false, true);

            JButton popupB = generateSimpleButton("Cancel", type, f, Color.YELLOW, grey2, 545, 420, 190, 60, false);

            ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540, 280);
            JLabel table = new JLabel(icon);
            table.setBounds(res(370), res(220), res(540), res(280));

            guiItems items[] = {guiItems.POP_UP, type, guiItems.POP_UP_TABLE};
            Component components[] = {popup, popupB, table};

            addComponents(items, components);

            reloadGUI();
        }
    }

    /**
     * Pop up with confirmation.
     *
     * @param msg the msg
     * @param yes the yes
     * @param no  the no
     */
    public void popUpWithConfirmation(String msg, guiItems yes, guiItems no) {
        if (componentsOnScreen.containsKey(guiItems.QUIT_YES)) {
            return;
        }
        closeAllPopUps();

        msg = fillText(msg, 23, 4);

        JTextArea popup = generateSimpleTextArea(msg, f, Color.YELLOW, grey1, 400, 250, 490, 150, false, true);

        JButton popupB1 = generateSimpleButton("Yes", yes, f, Color.YELLOW, grey2, 420, 420, 200, 60, false);
        JButton popupB2 = generateSimpleButton("No", no, f, Color.YELLOW, grey2, 660, 420, 200, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540, 280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370), res(220), res(540), res(280));

        guiItems items[] = {guiItems.POP_UP, yes, no, guiItems.POP_UP_TABLE};
        Component components[] = {popup, popupB1, popupB2, table};

        guiItems items2[] = new guiItems[itemsOnScreen.size()];

        for (int i = 0; i < itemsOnScreen.size(); ++i) {
            items2[i] = itemsOnScreen.get(i);
        }

        enableComponents(items2, false);

        addComponents(items, components);

        for (int i = 0; i < items.length; ++i) {
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0, guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0, guiItems.POP_UP);
        itemsOnScreen.add(0, yes);
        itemsOnScreen.add(0, no);

        reloadGUI();
    }

    /**
     * Server problem.
     */
    public void server_problem(boolean serverClosed) {
        if (!componentsOnScreen.containsKey(guiItems.RETRY_CONNECTION) && !componentsOnScreen.containsKey(guiItems.CLOSE_GAME)) {
            clearGui();
            if (serverClosed) {
                popUp("Server has been closed.", guiItems.CLOSE_GAME);
            } else {
                popUpWithConfirmation("Connection to server couldnt be established. Retry?", guiItems.RETRY_CONNECTION, guiItems.QUIT_YES);
            }
        }
    }

    /**
     * Login register.
     */
    public void login_register() {
        if (!componentsOnScreen.containsKey(guiItems.LOGIN_BUTTON)) {
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

    /**
     * Login.
     *
     * @param show the show
     * @param user the user
     * @param pass the pass
     */
    public void login(boolean show, String user, String pass) {
        if (!componentsOnScreen.containsKey(guiItems.LOGIN_BUTTON)) {
            clearGui();

            JButton login = generateSimpleButton("Login", guiItems.LOGIN_BUTTON, f, Color.YELLOW, grey1, 515, 610, 250, 60, false);

            JButton recover = generateSimpleButton("Recover account", guiItems.RECOVER_BUTTON, f2, Color.YELLOW, grey2, 780, 620, 225, 40, false);

            JTextField username = generateSimpleTextField(user, f, Color.YELLOW, grey2, 365, 520, 250, 60, true, false);

            JTextField username2 = generateSimpleTextField("Username", f, Color.YELLOW, blue, 365, 460, 250, 60, false, true);

            JPasswordField password = new JPasswordField(pass);
            if (!show) {
                password.setEchoChar('*');
            } else {
                password.setEchoChar((char) 0);
            }
            password.setEditable(true);
            password.setFont(f);
            password.setForeground(Color.YELLOW);
            password.setBackground(grey2);
            password.setBounds((int) (665 * m), (int) (520 * m), (int) (250 * m), (int) (60 * m));

            JTextField password2 = generateSimpleTextField("Password", f, Color.YELLOW, blue, 665, 460, 250, 60, false, true);

            guiItems items[] = {guiItems.LOGIN_BUTTON, guiItems.USERNAME, guiItems.PASSWORD, guiItems.USERNAME_TEXT,
                    guiItems.PASSWORD_TEXT, guiItems.BACK, guiItems.RECOVER_BUTTON};
            Component components[] = {login, username, password, username2, password2, backButton(), recover};
            addComponents(items, components);

            if (show) {
                JButton showPass = generateSimpleButton("Hide", guiItems.HIDE, f2, Color.YELLOW, grey2, 930, 530, 80, 40, false);
                showPass.setMargin(new Insets((int) (5 * m), (int) (5 * m), (int) (5 * m), (int) (5 * m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            } else {
                JButton showPass = generateSimpleButton("Show", guiItems.SHOW, f2, Color.YELLOW, grey2, 930, 530, 80, 40, false);
                showPass.setMargin(new Insets((int) (5 * m), (int) (5 * m), (int) (5 * m), (int) (5 * m)));
                addComponents(new guiItems[]{guiItems.SHOW}, new Component[]{showPass});
            }

            gui.setBack(1);
            gui.repaint();
            gui.revalidate();
        }
    }

    /**
     * Register.
     *
     * @param show  the show
     * @param user  the user
     * @param pass1 the pass 1
     * @param pass2 the pass 2
     * @param eml   the eml
     */
    public void register(Boolean show, String user, String pass1, String pass2, String eml) {
        if (!componentsOnScreen.containsKey(guiItems.REGISTER_BUTTON)) {
            clearGui();
            if (show) {
                JButton showPass = generateSimpleButton("Hide", guiItems.HIDE, f2, Color.YELLOW, grey2, 493, 181, 80, 40, false);
                showPass.setMargin(new Insets((int) (5 * m), (int) (5 * m), (int) (5 * m), (int) (5 * m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            } else {
                JButton showPass = generateSimpleButton("Show", guiItems.SHOW, f2, Color.YELLOW, grey2, 493, 181, 80, 40, false);
                showPass.setMargin(new Insets((int) (5 * m), (int) (5 * m), (int) (5 * m), (int) (5 * m)));
                addComponents(new guiItems[]{guiItems.SHOW}, new Component[]{showPass});
            }

            JButton login = generateSimpleButton("Register", guiItems.REGISTER_BUTTON, f, Color.YELLOW, grey1, 515, 600, 250, 60, false);

            JTextField username = generateSimpleTextField(user, f, Color.YELLOW, grey2, 290, 100, 680, 60, true, false);

            JTextField username2 = generateSimpleTextField("Username", f, Color.YELLOW, grey1, 290, 30, 680, 60, false, true);

            JPasswordField password = new JPasswordField(pass1);
            if (!show) {
                password.setEchoChar('*');
            } else {
                password.setEchoChar((char) 0);
            }
            password.setEditable(true);
            password.setFont(f);
            password.setForeground(Color.YELLOW);
            password.setBackground(grey2);
            password.setBounds((int) (290 * m), (int) (240 * m), (int) (680 * m), (int) (60 * m));

            JTextField password2 = generateSimpleTextField("Password", f, Color.YELLOW, grey1, 290, 170, 680, 60, false, true);

            JPasswordField password_2 = new JPasswordField(pass2);
            if (!show) {
                password_2.setEchoChar('*');
            } else {
                password_2.setEchoChar((char) 0);
            }
            password_2.setEditable(true);
            password_2.setFont(f);
            password_2.setForeground(Color.YELLOW);
            password_2.setBackground(grey2);
            password_2.setBounds((int) (290 * m), (int) (380 * m), (int) (680 * m), (int) (60 * m));

            JTextField password2_2 = generateSimpleTextField("Repeat password", f, Color.YELLOW, grey1, 290, 310, 680, 60, false, true);

            JTextField email = generateSimpleTextField(eml, f, Color.YELLOW, grey2, 290, 510, 680, 60, true, false);

            JTextField email2 = generateSimpleTextField("Email", f, Color.YELLOW, grey1, 290, 450, 680, 60, false, true);

            ImageIcon icon = loadIcon("/assets/sprites/menu/tablon_register.png", 788, 720);
            JLabel table = new JLabel(icon);
            table.setBounds(res(246), 0, res(788), res(720));

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

    /**
     * Ranking.
     */
    public void ranking() {
        if (!componentsOnScreen.containsKey(guiItems.RANKING)) {
            guiItems items[] = {guiItems.AUXILIAR_BACKGROUND};
            Component components[] = {auxiliarBackgroud()};
            new rank_gui(this);
            new friend_list_gui(this, friends, pendingMessages);
        }
    }

    /**
     * Change pass.
     *
     * @param show   the show
     * @param oldpas the oldpas
     * @param pass1  the pass 1
     * @param pass2  the pass 2
     */
    public void changePass(Boolean show, String oldpas, String pass1, String pass2) {
        closeAllPopUps();

        JButton confirm = generateSimpleButton("Confirm", guiItems.CONFIRM_CHANGE_PASS, f, Color.YELLOW, grey1, 415, 528, 200, 60, false);

        JButton cancel = generateSimpleButton("Cancel", guiItems.CANCEL_CHANGE_PASS, f, Color.YELLOW, grey1, 657, 528, 200, 60, false);

        JTextField oldpass2 = generateSimpleTextField("Old password", f, Color.YELLOW, grey1, 290, 95, 680, 60, false, true);

        JPasswordField oldpass = new JPasswordField(oldpas);
        if (!show) {
            oldpass.setEchoChar('*');
        } else {
            oldpass.setEchoChar((char) 0);
        }
        oldpass.setEditable(true);
        oldpass.setFont(f);
        oldpass.setForeground(Color.YELLOW);
        oldpass.setBackground(grey2);
        oldpass.setBounds((int) (290 * m), (int) (165 * m), (int) (680 * m), (int) (60 * m));

        JPasswordField password = new JPasswordField(pass1);
        if (!show) {
            password.setEchoChar('*');
        } else {
            password.setEchoChar((char) 0);
        }
        password.setEditable(true);
        password.setFont(f);
        password.setForeground(Color.YELLOW);
        password.setBackground(grey2);
        password.setBounds((int) (290 * m), (int) (305 * m), (int) (680 * m), (int) (60 * m));

        JTextField password2 = generateSimpleTextField("New password", f, Color.YELLOW, grey1, 290, 235, 300, 60, false, true);

        JPasswordField password_2 = new JPasswordField(pass2);
        if (!show) {
            password_2.setEchoChar('*');
        } else {
            password_2.setEchoChar((char) 0);
        }
        password_2.setEditable(true);
        password_2.setFont(f);
        password_2.setForeground(Color.YELLOW);
        password_2.setBackground(grey2);
        password_2.setBounds((int) (290 * m), (int) (445 * m), (int) (680 * m), (int) (60 * m));

        JTextField password2_2 = generateSimpleTextField("Repeat new password", f, Color.YELLOW, grey1, 290, 375, 680, 60, false, true);

        ImageIcon icon = loadIcon("/assets/sprites/menu/tablon_register.png", 788, 590);
        JLabel table = new JLabel(icon);
        table.setBounds(res(246), 65, res(788), res(590));

        guiItems items[] = {guiItems.CONFIRM_CHANGE_PASS, guiItems.CANCEL_CHANGE_PASS, guiItems.OLD_PASS, guiItems.OLD_PASS_TEXT, guiItems.NEW_PASS, guiItems.NEW_PASS_TEXT,
                guiItems.NEW_PASS_REPEAT, guiItems.NEW_PASS_REPEAT_TEXT, guiItems.REGISTER_TABLE};
        Component components[] = {confirm, cancel, oldpass, oldpass2, password, password2, password_2, password2_2, table};

        addComponents(items, components);

        for (int i = 0; i < items.length; ++i) {
            itemsOnScreen.remove(items[i]);
        }
        for (int i = items.length - 1; i >= 0; --i) {
            itemsOnScreen.add(0, items[i]);
        }

        gui.setBack(2);

        if (show) {
            JButton showPass = generateSimpleButton("Hide", guiItems.HIDE, f2, Color.YELLOW, grey2, 590, 246, 80, 40, false);
            showPass.setMargin(new Insets((int) (5 * m), (int) (5 * m), (int) (5 * m), (int) (5 * m)));
            itemsOnScreen.add(0, guiItems.HIDE);
            componentsOnScreen.put(guiItems.HIDE, showPass);
        } else {
            JButton showPass = generateSimpleButton("Show", guiItems.SHOW, f2, Color.YELLOW, grey2, 590, 246, 80, 40, false);
            showPass.setMargin(new Insets((int) (5 * m), (int) (5 * m), (int) (5 * m), (int) (5 * m)));
            itemsOnScreen.add(0, guiItems.SHOW);
            componentsOnScreen.put(guiItems.SHOW, showPass);
        }

        reloadGUI();
    }

    /**
     * Principal gui.
     */
    public void principalGUI() {
        if (!componentsOnScreen.containsKey(guiItems.NORMAL_BUTTON)) {
            clearGui();

            new friend_list_gui(this, friends, pendingMessages);

            this.notifications = new notifications_gui(this);
            notifications.showNotifications();

            JButton normal = generateSimpleButton("Normal mode", guiItems.NORMAL_BUTTON, f, Color.YELLOW, grey1, 308, 375, 400, 65, false);
            JButton ranked = generateSimpleButton("Ranked mode", guiItems.RANKED_BUTTON, f, Color.YELLOW, grey1, 308, 450, 400, 65, false);
            JButton ranking = generateSimpleButton("Ranking", guiItems.RANKING_BUTTON, f, Color.YELLOW, grey1, 508, 520, 200, 65, false);
            JButton vsia = generateSimpleButton("Vs IA", guiItems.VS_IA_BUTTON, f, Color.YELLOW, grey1, 308, 520, 200, 65, false);
            JButton profile = generateSimpleButton("Profile", guiItems.PROFILE_BUTTON, f, Color.YELLOW, grey1, 308, 595, 200, 65, false);
            JButton exit = generateSimpleButton("Quit", guiItems.QUIT_BUTTON, f, Color.YELLOW, grey1, 508, 595, 200, 65, false);
            JButton back = backButton();

            guiItems items[] = {guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.RANKING_BUTTON, guiItems.VS_IA_BUTTON, guiItems.PROFILE_BUTTON,
                    guiItems.QUIT_BUTTON, guiItems.BACK, guiItems.AUXILIAR_BACKGROUND};
            Component components[] = {normal, ranked, ranking, vsia, profile, exit, back, auxiliarBackgroud()};

            addComponents(items, components);

            gui.setBack(2);
            gui.repaint();
            gui.revalidate();
        }
    }

    /**
     * Friend interaction pop up.
     */
    public void friendInteractionPopUp() {
        JButton invite, msg, profile, delete;
        int x = 830, y = yTableClick;

        if (y > 317 + 35) {
            y -= 160;
        }

        invite = generateSimpleButton("Challenge", guiItems.INVITE_FRIEND, f2, Color.YELLOW, grey3, x, y, 200, 40, false);
        msg = generateSimpleButton("Chat", guiItems.MESSAGE_FRIEND, f2, Color.YELLOW, grey3, x, y + 40, 200, 40, false);
        profile = generateSimpleButton("Profile", guiItems.PROFILE_FRIEND, f2, Color.YELLOW, grey3, x, y + 80, 200, 40, false);
        delete = generateSimpleButton("Delete", guiItems.DELETE_FRIEND, f2, Color.YELLOW, grey3, x, y + 120, 200, 40, false);

        guiItems items[] = {guiItems.INVITE_FRIEND, guiItems.MESSAGE_FRIEND, guiItems.PROFILE_FRIEND, guiItems.DELETE_FRIEND};
        Component components[] = {invite, msg, profile, delete};

        for (int i = 0; i < items.length; ++i) {
            if (componentsOnScreen.containsKey(items[i])) {
                gui.remove(componentsOnScreen.get(items[i]));
            }
        }
        for (int i = 0; i < items.length; ++i) {
            for (int j = 0; j < itemsOnScreen.size(); ++j) {
                if (itemsOnScreen.get(j) == items[i]) {
                    itemsOnScreen.remove(j);
                    --j;
                }
            }
        }

        addComponents(items, components);
        for (int i = 0; i < items.length; ++i) {
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0, guiItems.INVITE_FRIEND);
        itemsOnScreen.add(0, guiItems.MESSAGE_FRIEND);
        itemsOnScreen.add(0, guiItems.PROFILE_FRIEND);
        itemsOnScreen.add(0, guiItems.DELETE_FRIEND);

        enableComponents(new guiItems[]{guiItems.HISTORIAL}, false);

        reloadGUI();
    }

    /**
     * Add friend.
     */
    public void addFriend() {
        closeAllPopUps();

        JTextField popup = generateSimpleTextField("Introduce the username:", f, Color.YELLOW, grey1, 405, 250, 490, 60, false, true);

        JTextField name = generateSimpleTextField("", f, Color.YELLOW, grey1, 485, 325, 300, 60, true, false);

        JButton popupB1 = generateSimpleButton("Add", guiItems.CONFIRM_ADD_BUTTON, f, Color.YELLOW, grey2, 420, 420, 200, 60, false);
        JButton popupB2 = generateSimpleButton("Cancel", guiItems.CANCEL_ADD_BUTTON, f, Color.YELLOW, grey2, 660, 420, 200, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540, 280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370), res(220), res(540), res(280));

        guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_NAME, guiItems.CONFIRM_ADD_BUTTON, guiItems.CANCEL_ADD_BUTTON, guiItems.POP_UP_TABLE};
        Component components[] = {popup, name, popupB1, popupB2, table};

        addComponents(items, components);

        for (int i = 0; i < items.length; ++i) {
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0, guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0, guiItems.POP_UP);
        itemsOnScreen.add(0, guiItems.INTRODUCE_NAME);
        itemsOnScreen.add(0, guiItems.CONFIRM_ADD_BUTTON);
        itemsOnScreen.add(0, guiItems.CANCEL_ADD_BUTTON);

        items = new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.RANKING_BUTTON, guiItems.QUIT_BUTTON,
                guiItems.PROFILE_BUTTON, guiItems.ADD_FRIEND, guiItems.FRIEND_LIST, guiItems.BACK, guiItems.VS_IA_BUTTON};
        enableComponents(items, false);

        reloadGUI();
    }

    public void selectIa() {
        closeAllPopUps();

        JButton global = generateSimpleButton("Global IA", guiItems.GLOBAL_IA_BUTTON, f, Color.YELLOW, grey2, 405, 250, 462, 60, false);
        JButton personal = generateSimpleButton("Personal IA", guiItems.PERSONAL_IA_BUTTON, f, Color.YELLOW, grey2, 405, 330, 462, 60, false);

        JButton cancel = generateSimpleButton("Cancel", guiItems.CANCEL_SELECT_IA, f, Color.YELLOW, grey2, 535, 420, 200, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540, 280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370), res(220), res(540), res(280));

        guiItems items[] = {guiItems.GLOBAL_IA_BUTTON, guiItems.PERSONAL_IA_BUTTON, guiItems.CANCEL_SELECT_IA, guiItems.POP_UP_TABLE};
        Component components[] = {global, personal, cancel, table};

        addComponents(items, components);

        for (int i = 0; i < items.length; ++i) {
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0, guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0, guiItems.CANCEL_SELECT_IA);
        itemsOnScreen.add(0, guiItems.PERSONAL_IA_BUTTON);
        itemsOnScreen.add(0, guiItems.GLOBAL_IA_BUTTON);

        items = new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.RANKING_BUTTON, guiItems.QUIT_BUTTON,
                guiItems.PROFILE_BUTTON, guiItems.ADD_FRIEND, guiItems.FRIEND_LIST, guiItems.BACK, guiItems.VS_IA_BUTTON};
        enableComponents(items, false);

        reloadGUI();
    }

    /**
     * Verify account.
     */
    public void verifyAccount() {
        closeAllPopUps();

        JTextField popup = generateSimpleTextField("   Verification code", f, Color.YELLOW, grey1, 405, 250, 490, 60, false, true);

        JTextField name = generateSimpleTextField("", f, Color.YELLOW, grey1, 485, 325, 300, 60, true, false);

        JButton popupB1 = generateSimpleButton("Okey", guiItems.VERIFY_BUTTON, f, Color.YELLOW, grey2, 533, 420, 200, 60, false);

        ImageIcon icon = loadIcon("/assets/sprites/menu/pop_up.png", 540, 280);
        JLabel table = new JLabel(icon);
        table.setBounds(res(370), res(220), res(540), res(280));

        guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_CODE, guiItems.VERIFY_BUTTON, guiItems.POP_UP_TABLE};
        Component components[] = {popup, name, popupB1, table};

        guiItems items2[] = new guiItems[itemsOnScreen.size()];

        for (int i = 0; i < itemsOnScreen.size(); ++i) {
            items2[i] = itemsOnScreen.get(i);
        }

        enableComponents(items2, false);

        addComponents(items, components);

        for (int i = 0; i < items.length; ++i) {
            itemsOnScreen.remove(items[i]);
        }

        itemsOnScreen.add(0, guiItems.POP_UP_TABLE);
        itemsOnScreen.add(0, guiItems.POP_UP);
        itemsOnScreen.add(0, guiItems.INTRODUCE_CODE);
        itemsOnScreen.add(0, guiItems.VERIFY_BUTTON);

        reloadGUI();
    }

    /**
     * Close verification.
     */
    public void closeVerification() {
        guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_CODE, guiItems.VERIFY_BUTTON, guiItems.POP_UP_TABLE};
        deleteComponents(items);
        guiItems items2[] = new guiItems[itemsOnScreen.size()];

        for (int i = 0; i < itemsOnScreen.size(); ++i) {
            items2[i] = itemsOnScreen.get(i);
        }

        enableComponents(items2, true);

        reloadGUI();
    }

    /**
     * Chat.
     *
     * @param friend the friend
     */
    public void chat(String friend) {
        sendableObjectsList msgs = (sendableObjectsList) online_controller.getConToServer().sendStringWaitingAnswerObject(msgID.toServer.request, "MESSAGE HISTORIAL:" + friend, 0);
        friendMessages = new ArrayList<>();
        for (sendableObject m : msgs.getMsgs()) {
            friendMessages.add((message) m);
        }
        chatgui = new chat_gui(this, friendMessages, friend);
        pendingMessages.remove(friend);
        new friend_list_gui(this, friends, pendingMessages);
        reloadGUI();
        online_controller.getConToServer().sendString(msgID.toServer.request, "NOTIFY READ MESSAGES:" + friend);
    }

    /**
     * Profile gui.
     */
    public void profileGUI() {
        if (!componentsOnScreen.containsKey(guiItems.PROFILE_NAME)) {
            clearGui();
            notifications.showNotifications();
            profile = new profile_gui(this, profileToShow);
            gui.setBack(3);
            gui.repaint();
            gui.revalidate();
        }
    }

    /**
     * Character selection.
     *
     * @param isHost   the is host
     * @param ip       the ip
     * @param rival    the rival
     * @param isRanked the is ranked
     */
    public void character_selection_online(boolean isHost, String ip, String rival, boolean isRanked) {
        if (!componentsOnScreen.containsKey(guiItems.SELECT_TERRY)) {
            clearGui();
            if (isHost) {
                gui.setBack(5);
            } else {
                gui.setBack(4);
            }
            char_sel = new character_selection_gui(this, isHost, ip, rival, isRanked,0);
            if (char_sel.isAllOk()) {
                System.out.println("Se ha creado la gui de selección ");
            } else {
                setOnlineState(GameState.PRINCIPAL_GUI);
                clearGui();
                principalGUI();
                popUp("Has been a problem establishing the connection.");
            }
        }
    }

    public void character_selection_ia(boolean isGlobal) {
        if (!componentsOnScreen.containsKey(guiItems.SELECT_TERRY)) {
            setOnlineState(GameState.CHARACTER_SELECTION);
            clearGui();
            gui.setBack(5);
            if(isGlobal) {
                char_sel = new character_selection_gui(this, true, null, "GLOBAL IA", false, 2);
            }
            else{
                char_sel = new character_selection_gui(this, true, null, "PERSONAL IA", false, 1);
            }
            if (char_sel.isAllOk()) {
                reloadGUI();
                System.out.println("Se ha creado la gui de selección ");
            } else {
                setOnlineState(GameState.PRINCIPAL_GUI);
                clearGui();
                principalGUI();
                popUp("Has been a problem establishing the connection.");
            }
        }
    }

    public void closeAllPopUps() {
        guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_NAME,
                guiItems.CONFIRM_ADD_BUTTON, guiItems.CANCEL_ADD_BUTTON,
                guiItems.POP_UP_TABLE, guiItems.YES_BUTTON, guiItems.NOT_BUTTON,
                guiItems.POP_UP_BUTTON, guiItems.QUIT_YES, guiItems.QUIT_NO,
                guiItems.LOG_OUT_NO, guiItems.LOG_OUT_YES, guiItems.ACCEPT_FRIEND,
                guiItems.REJECT_FRIEND, guiItems.CONFIRM_DELETE, guiItems.CANCEL_DELETE,
                guiItems.CANCEL_SEARCH_GAME, guiItems.CANCEL_SEARCH_RANK_GAME,
                guiItems.VERIFY_BUTTON, guiItems.VERIFY_POP_BUTTON, guiItems.BAD_CODE,
                guiItems.CONFIRM_CHANGE_PASS, guiItems.OLD_PASS, guiItems.NEW_PASS,
                guiItems.OLD_PASS_TEXT, guiItems.NEW_PASS_TEXT, guiItems.NEW_PASS_REPEAT,
                guiItems.NEW_PASS_REPEAT_TEXT, guiItems.CANCEL_FRIEND_CHALLENGE,
                guiItems.ACCEPT_FRIEND_CHALLENGE, guiItems.REJECT_FRIEND_CHALLENGE,
                guiItems.REGISTER_TABLE, guiItems.OKEY_INVITATION_CANCELLED,
                guiItems.RETRY_CONNECTION, guiItems.GLOBAL_IA_BUTTON,
                guiItems.PERSONAL_IA_BUTTON, guiItems.CANCEL_SELECT_IA};
        deleteComponents(items);
    }

    /**
     * Close add friend.
     */
    public void closeAddFriend() {
        if (componentsOnScreen.containsKey(guiItems.CANCEL_ADD_BUTTON)) {
            guiItems items[] = {guiItems.POP_UP, guiItems.INTRODUCE_NAME, guiItems.CONFIRM_ADD_BUTTON, guiItems.CANCEL_ADD_BUTTON, guiItems.POP_UP_TABLE};
            deleteComponents(items);

            items = new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.RANKING_BUTTON, guiItems.QUIT_BUTTON,
                    guiItems.PROFILE_BUTTON, guiItems.ADD_FRIEND, guiItems.FRIEND_LIST, guiItems.BACK, guiItems.VS_IA_BUTTON};
            enableComponents(items, true);
            reloadGUI();
        }
    }

    public void closeSelectIa() {
        guiItems items[] = {guiItems.GLOBAL_IA_BUTTON, guiItems.PERSONAL_IA_BUTTON, guiItems.CANCEL_SELECT_IA, guiItems.POP_UP_TABLE};
        deleteComponents(items);
        items = new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.RANKING_BUTTON, guiItems.QUIT_BUTTON,
                guiItems.PROFILE_BUTTON, guiItems.ADD_FRIEND, guiItems.FRIEND_LIST, guiItems.BACK, guiItems.VS_IA_BUTTON};
        enableComponents(items, true);
        reloadGUI();
    }

    /**
     * Close friend interaction pop up.
     */
    public void closeFriendInteractionPopUp(){
        if(componentsOnScreen.containsKey(guiItems.INVITE_FRIEND)) {
            deleteComponents(new guiItems[]{guiItems.INVITE_FRIEND, guiItems.MESSAGE_FRIEND, guiItems.PROFILE_FRIEND, guiItems.DELETE_FRIEND});
            enableComponents(new guiItems[]{guiItems.HISTORIAL}, true);
            reloadGUI();
        }
    }

    /**
     * Close chat.
     */
    public void closeChat(){
        if(componentsOnScreen.containsKey(guiItems.CHAT)) {
            deleteComponents(new guiItems[]{guiItems.CHAT, guiItems.SEND_MESSAGE, guiItems.MESSAGE_WRITER, guiItems.CLOSE_CHAT});
            enableComponents(new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.RANKING_BUTTON,
                    guiItems.QUIT_BUTTON, guiItems.PROFILE_BUTTON, guiItems.BACK}, true);
            pendingMessages.remove(friends.get(friendSelected));

            new friend_list_gui(this,friends,pendingMessages);
            reloadGUI();
            online_controller.getConToServer().sendString(msgID.toServer.request,"NOTIFY READ MESSAGES:"+friends.get(friendSelected));
        }
    }

    /**
     * Close pop up with confirmation.
     *
     * @param yes the yes
     * @param no  the no
     */
    public void closePopUpWithConfirmation(guiItems yes, guiItems no){
        deleteComponents(new guiItems[]{guiItems.POP_UP_TABLE, guiItems.POP_UP, yes, no});
        guiItems items[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items[i] = itemsOnScreen.get(i);
        }

        enableComponents(items, true);
        reloadGUI();
    }

    /**
     * Close searching game.
     */
    public void closeSearchingGame(){
        clearGui();
        setOnlineState(GameState.PRINCIPAL_GUI);
    }

    /**
     * Close pop up.
     */
    public void closePopUp(){
        closePopUp(guiItems.POP_UP_BUTTON);
    }

    /**
     * Close pop up.
     *
     * @param okey the okey
     */
    public void closePopUp(guiItems okey){
        deleteComponents(new guiItems[]{guiItems.POP_UP_TABLE, guiItems.POP_UP, okey});
        guiItems items[] = new guiItems[itemsOnScreen.size()];

        for(int i = 0; i < itemsOnScreen.size();++i){
            items[i] = itemsOnScreen.get(i);
        }

        enableComponents(items, true);
        reloadGUI();
    }

    /**
     * Close friend list.
     */
    public void closeFriendList(){
        deleteComponents(new guiItems[]{guiItems.ADD_FRIEND, guiItems.FRIEND_LIST});
        reloadGUI();
    }

    /**
     * Close change pass.
     */
    public void closeChangePass(){
        deleteComponents(new guiItems[]{guiItems.CONFIRM_CHANGE_PASS, guiItems.CANCEL_CHANGE_PASS, guiItems.OLD_PASS, guiItems.OLD_PASS_TEXT, guiItems.NEW_PASS, guiItems.NEW_PASS_TEXT,
                guiItems.NEW_PASS_REPEAT, guiItems.NEW_PASS_REPEAT_TEXT, guiItems.REGISTER_TABLE, guiItems.HIDE, guiItems.SHOW});
        reloadGUI();
    }

    /**
     * Load friends.
     */
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

    /**
     * Friend request.
     *
     * @param friend the friend
     */
    public void friendRequest(String friend){
        popUpWithConfirmation("You have received   friend request from:  "+friend,guiItems.ACCEPT_FRIEND, guiItems.REJECT_FRIEND);
        updateButtonWithInformation(guiItems.ACCEPT_FRIEND, friend);
        updateButtonWithInformation(guiItems.REJECT_FRIEND, friend);
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
                try {
                    String notification = con.receiveNotifications();
                    String res[] = notification.split(":");
                    if (res[0].equals("FRIEND REQUEST")) {
                        if(friends.contains(res[1])){
                            friends.remove(res[1]);
                            pendingMessages.remove(res[1]);
                            new friend_list_gui(online_mode_gui.this, friends, pendingMessages);
                        }
                        notifications.addFriendRequest(res[1]);
                    }
                    else if (res[0].equals("NEW FRIEND") && (onlineState == GameState.PRINCIPAL_GUI || onlineState == GameState.PROFILE_GUI)) {
                        if(!friends.contains(res[1])) {
                            friends.add(res[1]);
                            Collections.sort(friends);
                            new friend_list_gui(online_mode_gui.this, friends, pendingMessages);
                        }
                    }
                    else if (res[0].equals("MESSAGE RECEIVED") && (onlineState == GameState.PRINCIPAL_GUI || onlineState == GameState.PROFILE_GUI)) {
                        if (componentsOnScreen.containsKey(guiItems.CHAT) && friends.get(friendSelected).equals(res[1])) {
                            message aux = friendMessages.get(friendMessages.size() - 1);
                            chatgui.addMessage(new message(aux.getId() + 1, res[1], userLogged, res[2]));
                        } else {
                            boolean yet = false;
                            for (int i = 0; !yet && i < pendingMessages.size(); ++i) {
                                yet = pendingMessages.get(i).equals(res[1]);
                            }
                            if (!yet) {
                                pendingMessages.add(res[1]);
                                new friend_list_gui(online_mode_gui.this, friends, pendingMessages);
                            }
                        }
                    }
                    else if (res[0].equals("DELETED FRIEND") && (onlineState == GameState.PRINCIPAL_GUI || onlineState == GameState.PROFILE_GUI)) {
                        if(friends.contains(res[1])) {
                            friends.remove(res[1]);
                            pendingMessages.remove(res[1]);
                            new friend_list_gui(online_mode_gui.this, friends, pendingMessages);
                        }
                    }
                    else if (res[0].equals("SEARCH GAME") || res[0].equals("SEARCH RANKED GAME")) {
                        if (onlineState == GameState.PRINCIPAL_GUI || onlineState == GameState.PROFILE_GUI) {
                            String ip = res[2], name = res[3];
                            boolean isHost = Boolean.parseBoolean(res[1]);
                            setOnlineState(GameState.CHARACTER_SELECTION);
                            clearGui();
                            boolean isRanked = res[0].equals("SEARCH RANKED GAME");
                            character_selection_online(isHost, ip, name, isRanked);
                            System.out.println("Se cambia el estado a CHARACTER_SELECTION");
                        }
                    }
                    else if (res[0].equals("SERVER CLOSED")) {
                        setOnlineState(GameState.SERVER_PROBLEM);
                        clearGui();
                    }
                    else if (res[0].equals("FRIEND CHALLENGE")) {
                        if ((onlineState == GameState.PRINCIPAL_GUI || onlineState == GameState.PROFILE_GUI)
                                && !componentsOnScreen.containsKey(guiItems.ACCEPT_FRIEND_CHALLENGE)
                                && !componentsOnScreen.containsKey(guiItems.OKEY_INVITATION_CANCELLED)
                                && !componentsOnScreen.containsKey(guiItems.QUIT_YES)
                                && !componentsOnScreen.containsKey(guiItems.QUIT_YES)
                                && !componentsOnScreen.containsKey(guiItems.LOG_OUT_YES)
                                && !componentsOnScreen.containsKey(guiItems.CONFIRM_ADD_BUTTON)) {
                            popUpWithConfirmation(res[1] + " has challenged you. Accept the invitation?", guiItems.ACCEPT_FRIEND_CHALLENGE, guiItems.REJECT_FRIEND_CHALLENGE);
                            updateButtonWithInformation(guiItems.ACCEPT_FRIEND_CHALLENGE, res[1]);
                            updateButtonWithInformation(guiItems.REJECT_FRIEND_CHALLENGE, res[1]);
                        } else {
                            online_controller.getConToServer().sendString(msgID.toServer.request, "ANSWER CHALLENGE:" + res[1] + ":false");
                        }
                    }
                    else if (res[0].equals("FRIEND CHALLENGE CANCELED")) {
                        if (componentsOnScreen.containsKey(guiItems.ACCEPT_FRIEND_CHALLENGE)) {
                            closePopUpWithConfirmation(guiItems.ACCEPT_FRIEND_CHALLENGE, guiItems.REJECT_FRIEND_CHALLENGE);
                            popUp("The invitation has been cancelled.", guiItems.OKEY_INVITATION_CANCELLED);
                        }
                    }
                    else if (res[0].equals("PLAYER BUSY")) {
                        closeSearchingGame();
                        setOnlineState(GameState.ONLINE_SEARCHING_FIGHT);
                        popUp("The player is busy right now.", guiItems.FRIEND_BUSY);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Update button with information.
     *
     * @param item the item
     * @param info the info
     */
    public void updateButtonWithInformation(guiItems item, String info){
        JButton aux = ((JButton)componentsOnScreen.get(item));
        for(ActionListener al : aux.getActionListeners() ) {
            aux.removeActionListener( al );
        }
        aux.addActionListener(new guiListener(online_mode_gui.this, item,info));
    }

    /**
     * The type Server pinger.
     */
    protected class serverPinger extends Thread{
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
         * The Time reference.
         */
        private long timeReference = System.currentTimeMillis();

        /**
         * Instantiates a new Server pinger.
         *
         * @param con the con
         */
        public serverPinger(connection con) {
            this.con = con;
            this.thread = new Thread(this);
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
                try {
                    con.sendString(msgID.toServer.ping, "PING");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String p = con.receiveString(msgID.toServer.ping);
                if(p.equals("PING")){
                    timeReference = System.currentTimeMillis();
                }
                else{
                    if(System.currentTimeMillis() - timeReference > 10000){
                        onlineState = GameState.SERVER_PROBLEM;
                        setUserLogged("");
                    }
                }
            }
        }

        /**
         * Gets con.
         *
         * @return the con
         */
        public connection getCon() {
            return con;
        }

        /**
         * Sets con.
         *
         * @param con the con
         */
        public void setCon(connection con) {
            this.con = con;
        }

        /**
         * Is stop boolean.
         *
         * @return the boolean
         */
        public boolean isStop() {
            return stop;
        }

        /**
         * Sets stop.
         *
         * @param stop the stop
         */
        public void setStop(boolean stop) {
            this.stop = stop;
        }

        /**
         * Gets thread.
         *
         * @return the thread
         */
        public Thread getThread() {
            return thread;
        }

        /**
         * Gets time reference.
         *
         * @return the time reference
         */
        public long getTimeReference() {
            return timeReference;
        }

        /**
         * Sets time reference.
         *
         * @param timeReference the time reference
         */
        public void setTimeReference(long timeReference) {
            this.timeReference = timeReference;
        }
    }

    /**
     * Gets components on screen.
     *
     * @return the components on screen
     */
    public Map<guiItems, Component> getComponentsOnScreen() {
        return componentsOnScreen;
    }

    /**
     * Sets components on screen.
     *
     * @param componentsOnScreen the components on screen
     */
    public void setComponentsOnScreen(Map<guiItems, Component> componentsOnScreen) {
        this.componentsOnScreen = componentsOnScreen;
    }

    /**
     * Gets items on screen.
     *
     * @return the items on screen
     */
    public List<guiItems> getItemsOnScreen() {
        return itemsOnScreen;
    }

    /**
     * Sets items on screen.
     *
     * @param itemsOnScreen the items on screen
     */
    public void setItemsOnScreen(List<guiItems> itemsOnScreen) {
        this.itemsOnScreen = itemsOnScreen;
    }

    /**
     * Gets online state.
     *
     * @return the online state
     */
    public GameState getOnlineState() {
        return onlineState;
    }

    /**
     * Sets online state.
     *
     * @param onlineState the online state
     */
    public void setOnlineState(GameState onlineState) {
        this.onlineState = onlineState;
        online_controller.setOnlineState(onlineState);
    }

    /**
     * Gets gui.
     *
     * @return the gui
     */
    public gameGUI getGui() {
        return gui;
    }

    /**
     * Sets gui.
     *
     * @param gui the gui
     */
    public void setGui(gameGUI gui) {
        this.gui = gui;
    }

    /**
     * Gets principal.
     *
     * @return the principal
     */
    public Principal getPrincipal() {
        return principal;
    }

    /**
     * Sets principal.
     *
     * @param principal the principal
     */
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * Gets m.
     *
     * @return the m
     */
    public double getM() {
        return m;
    }

    /**
     * Sets m.
     *
     * @param m the m
     */
    public void setM(double m) {
        this.m = m;
    }

    /**
     * Gets friends.
     *
     * @return the friends
     */
    public List<String> getFriends() {
        return friends;
    }

    /**
     * Sets friends.
     *
     * @param friends the friends
     */
    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    /**
     * Gets friend selected.
     *
     * @return the friend selected
     */
    public int getFriendSelected() {
        return friendSelected;
    }

    /**
     * Sets friend selected.
     *
     * @param friendSelected the friend selected
     */
    public void setFriendSelected(int friendSelected) {
        this.friendSelected = friendSelected;
    }

    /**
     * Gets grey 1.
     *
     * @return the grey 1
     */
    public Color getGrey1() {
        return grey1;
    }

    /**
     * Sets grey 1.
     *
     * @param grey1 the grey 1
     */
    public void setGrey1(Color grey1) {
        this.grey1 = grey1;
    }

    /**
     * Gets grey 2.
     *
     * @return the grey 2
     */
    public Color getGrey2() {
        return grey2;
    }

    /**
     * Sets grey 2.
     *
     * @param grey2 the grey 2
     */
    public void setGrey2(Color grey2) {
        this.grey2 = grey2;
    }

    /**
     * Gets grey 3.
     *
     * @return the grey 3
     */
    public Color getGrey3() {
        return grey3;
    }

    /**
     * Sets grey 3.
     *
     * @param grey3 the grey 3
     */
    public void setGrey3(Color grey3) {
        this.grey3 = grey3;
    }

    /**
     * Gets brown.
     *
     * @return the brown
     */
    public Color getBrown() {
        return brown;
    }

    /**
     * Sets brown.
     *
     * @param brown the brown
     */
    public void setBrown(Color brown) {
        this.brown = brown;
    }

    /**
     * Gets blue.
     *
     * @return the blue
     */
    public Color getBlue() {
        return blue;
    }

    /**
     * Sets blue.
     *
     * @param blue the blue
     */
    public void setBlue(Color blue) {
        this.blue = blue;
    }

    /**
     * Gets f.
     *
     * @return the f
     */
    public Font getF() {
        return f;
    }

    /**
     * Sets f.
     *
     * @param f the f
     */
    public void setF(Font f) {
        this.f = f;
    }

    /**
     * Gets f 2.
     *
     * @return the f 2
     */
    public Font getF2() {
        return f2;
    }

    /**
     * Sets f 2.
     *
     * @param f2 the f 2
     */
    public void setF2(Font f2) {
        this.f2 = f2;
    }

    /**
     * Gets f 3.
     *
     * @return the f 3
     */
    public Font getF3() {
        return f3;
    }

    /**
     * Sets f 3.
     *
     * @param f3 the f 3
     */
    public void setF3(Font f3) {
        this.f3 = f3;
    }

    /**
     * Gets table click.
     *
     * @return the table click
     */
    public int getxTableClick() {
        return xTableClick;
    }

    /**
     * Sets table click.
     *
     * @param xYableClick the x yable click
     */
    public void setxTableClick(int xYableClick) {
        this.xTableClick = xYableClick;
    }

    /**
     * Gets table click.
     *
     * @return the table click
     */
    public int getyTableClick() {
        return yTableClick;
    }

    /**
     * Sets table click.
     *
     * @param yTableClick the y table click
     */
    public void setyTableClick(int yTableClick) {
        this.yTableClick = yTableClick;
    }

    /**
     * Gets friend messages.
     *
     * @return the friend messages
     */
    public List<message> getFriendMessages() {
        return friendMessages;
    }

    /**
     * Sets friend messages.
     *
     * @param friendMessages the friend messages
     */
    public void setFriendMessages(List<message> friendMessages) {
        this.friendMessages = friendMessages;
    }

    /**
     * Gets grey 4.
     *
     * @return the grey 4
     */
    public Color getGrey4() {
        return grey4;
    }

    /**
     * Sets grey 4.
     *
     * @param grey4 the grey 4
     */
    public void setGrey4(Color grey4) {
        this.grey4 = grey4;
    }

    /**
     * Gets profile to show.
     *
     * @return the profile to show
     */
    public profile getProfileToShow() {
        return profileToShow;
    }

    /**
     * Sets profile to show.
     *
     * @param profileToShow the profile to show
     */
    public void setProfileToShow(profile profileToShow) {
        this.profileToShow = profileToShow;
    }

    /**
     * Sets profile.
     *
     * @param profile the profile
     */
    public void setProfile(profile_gui profile) {
        this.profile = profile;
    }

    /**
     * Gets profile.
     *
     * @return the profile
     */
    public profile_gui getProfile() {
        return profile;
    }

    /**
     * Gets online controller.
     *
     * @return the online controller
     */
    public online_mode getOnline_controller() {
        return online_controller;
    }

    /**
     * Sets online controller.
     *
     * @param online_controller the online controller
     */
    public void setOnline_controller(online_mode online_controller) {
        this.online_controller = online_controller;
    }

    /**
     * Gets char sel.
     *
     * @return the char sel
     */
    public character_selection_gui getChar_sel() {
        return char_sel;
    }

    /**
     * Sets char sel.
     *
     * @param char_sel the char sel
     */
    public void setChar_sel(character_selection_gui char_sel) {
        this.char_sel = char_sel;
    }

    /**
     * Gets user logged.
     *
     * @return the user logged
     */
    public String getUserLogged() {
        return userLogged;
    }

    /**
     * Sets user logged.
     *
     * @param userLogged the user logged
     */
    public void setUserLogged(String userLogged) {
        this.online_controller.setUserLogged(userLogged);
        this.userLogged = userLogged;
    }

    /**
     * Gets notifier.
     *
     * @return the notifier
     */
    public notificationsReceiver getNotifier() {
        return notifier;
    }

    /**
     * Sets notifier.
     *
     * @param notifier the notifier
     */
    public void setNotifier(notificationsReceiver notifier) {
        this.notifier = notifier;
    }

    /**
     * Gets chatgui.
     *
     * @return the chatgui
     */
    public chat_gui getChatgui() {
        return chatgui;
    }

    /**
     * Sets chatgui.
     *
     * @param chatgui the chatgui
     */
    public void setChatgui(chat_gui chatgui) {
        this.chatgui = chatgui;
    }

    /**
     * Gets notifications.
     *
     * @return the notifications
     */
    public notifications_gui getNotifications() {
        return notifications;
    }

    /**
     * Sets notifications.
     *
     * @param notifications the notifications
     */
    public void setNotifications(notifications_gui notifications) {
        this.notifications = notifications;
    }

    /**
     * Gets pending messages.
     *
     * @return the pending messages
     */
    public List<String> getPendingMessages() {
        return pendingMessages;
    }

    /**
     * Sets pending messages.
     *
     * @param pendingMessages the pending messages
     */
    public void setPendingMessages(List<String> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    /**
     * Gets pinger.
     *
     * @return the pinger
     */
    public serverPinger getPinger() {
        return pinger;
    }

    /**
     * Sets pinger.
     *
     * @param pinger the pinger
     */
    public void setPinger(serverPinger pinger) {
        this.pinger = pinger;
    }
}
