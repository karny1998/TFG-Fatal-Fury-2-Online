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
import javax.swing.table.*;
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
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),
            brown = new Color(140,105,57), blue = new Color(0,0,148);
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
            default:
                break;
        }
        //screen.revalidate();
    }

    private int res(int x){
        return (int)(x*m);
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
        back.setOpaque(false);
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        back.setBounds(0,res(20),res(200),res(60));
        return back;
    }

    public JButton generateSimpleButton(String text, guiItems id, Font font, Color color, Color back, int x, int y, int w, int h){
        JButton aux = new JButton(text);
        aux.addActionListener(new guiListener(id));
        aux.setFont(font);
        aux.setForeground(color);
        aux.setBackground(back);
        aux.setBounds(res(x),res(y), res(w),res(h));
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
        aux.setBackground(back);;
        aux.setBounds(res(x),res(y), res(w),res(h));
        return aux;
    }

    public void popUp(String msg){
        JTextField popup = generateSimpleTextField(msg, f, Color.YELLOW, grey1, 370, 220, 540, 280, false, false);
        popup.setBorder(javax.swing.BorderFactory.createLineBorder(brown,5));
        popup.setHorizontalAlignment(JTextField.CENTER);

        JButton popupB = generateSimpleButton("Okey", guiItems.POP_UP_BUTTON, f, Color.YELLOW, grey2, 580, 420, 120, 60);

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
            clearGui();
            JButton login = generateSimpleButton("Login", guiItems.LOGIN_BUTTON, f, Color.YELLOW, grey1, 370, 560, 250, 60);

            JButton register = generateSimpleButton("Sign in", guiItems.REGISTER_BUTTON, f, Color.YELLOW, grey1, 660, 560, 250, 60);

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

            JButton login = generateSimpleButton("Login", guiItems.LOGIN_BUTTON, f, Color.YELLOW, grey1, 515, 610, 250, 60);

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
                JButton showPass = generateSimpleButton("Hide", guiItems.HIDE, f2, Color.YELLOW, grey2, 930, 530, 80, 40);
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            }
            else{
                JButton showPass = generateSimpleButton("Show", guiItems.SHOW, f2, Color.YELLOW, grey2, 930, 530, 80, 40);
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
                JButton showPass = generateSimpleButton("Hide", guiItems.HIDE, f2, Color.YELLOW, grey2, 493, 181, 80, 40);
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.HIDE}, new Component[]{showPass});
            }
            else{
                JButton showPass = generateSimpleButton("Show", guiItems.SHOW, f2, Color.YELLOW, grey2, 493, 181, 80, 40);
                showPass.setMargin(new Insets((int)(5*m),(int)(5*m),(int)(5*m),(int)(5*m)));
                addComponents(new guiItems[]{guiItems.SHOW}, new Component[]{showPass});
            }

            JButton login = generateSimpleButton("Register", guiItems.REGISTER_BUTTON, f, Color.YELLOW, grey1, 515, 600, 250, 60);

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

            JLabel table = new JLabel(new ImageIcon(getClass().getResource("/assets/sprites/menu/tablon_register.png")));
            table.setBounds(res(346),0,res(788),res(720));

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

            List<String>  friends = new ArrayList<>();
            friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");

            JTable table;
            JScrollPane scrollPane;
            TableCellRenderer tableRenderer;
            table = new JTable(new JTableButtonModel(friends));
            table.setBounds(res(1030),0,res(250),res(635));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRowHeight(res(60));
            table.setOpaque(true);
            table.setFillsViewportHeight(true);
            table.setBackground(grey1);
            JTableHeader header = table.getTableHeader();
            header.setOpaque(true);
            header.setBackground(grey1);
            header.setDefaultRenderer(new SimpleHeaderRenderer());
            header.setPreferredSize(new Dimension(res(250), res(70)));
            header.setReorderingAllowed(false);

            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(res(250));

            tableRenderer = table.getDefaultRenderer(JButton.class);
            table.setDefaultRenderer(JButton.class, new JTableButtonRenderer(tableRenderer));

            scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBounds(res(1030),0,res(250),res(635));
            scrollPane.setBackground(grey1);

            JButton addFriend = generateSimpleButton("Add friend", guiItems.REGISTER_BUTTON, f, Color.YELLOW, grey1, 1030, 630, 250, 60);

            guiItems items[] = {guiItems.ADD_FRIEND, guiItems.FRIEND_LIST};
            Component components[] = {addFriend, scrollPane};

            addComponents(items, components);

            gui.setBack(2);
            gui.repaint();
            gui.revalidate();
        }
    }

            //https://www.codejava.net/java-se/swing/jtable-column-header-custom-renderer-examples
    public class SimpleHeaderRenderer extends JLabel implements TableCellRenderer {
        public SimpleHeaderRenderer() {
            setFont(f3);
            setForeground(Color.YELLOW);
            setBackground(Color.BLUE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }

    //https://www.tutorialspoint.com/how-can-we-add-insert-a-jbutton-to-jtable-cell-in-java
    class JTableButtonRenderer implements TableCellRenderer {
        private TableCellRenderer defaultRenderer;
        public JTableButtonRenderer(TableCellRenderer renderer) {
            defaultRenderer = renderer;
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof Component) {
                return (Component) value;
            }
            return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    class JTableButtonModel extends AbstractTableModel {
        private Object[][] rows;
        private String[] columns = {" Friends"};

        public JTableButtonModel(List<String> friends){
            rows = new Object[friends.size()][1];
            for(int i = 0; i < friends.size();++i){
                JButton aux = new JButton(friends.get(i));
                aux.setFont(f);
                aux.setForeground(Color.YELLOW);
                aux.setBackground(grey2);
                aux.addActionListener(new guiListener(guiItems.FRIEND_SEL_BUTTON));
                rows[i][0] = aux;
            }
        }

        public String getColumnName(int column) {
            return columns[column];
        }
        public int getRowCount() {
            return rows.length;
        }
        public int getColumnCount() {
            return columns.length;
        }
        public Object getValueAt(int row, int column) {
            return rows[row][column];
        }
        public boolean isCellEditable(int row, int column) {
            return true;
        }
        public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
    }

    private enum guiItems{LOGIN_BUTTON, REGISTER_BUTTON, USERNAME, USERNAME_TEXT,
        PASSWORD, PASSWORD_TEXT, BACK, PASSWORD_2, PASSWORD_2_TEXT, EMAIL, EMAIL_TEXT,
        SHOW, HIDE, REGISTER_TABLE, POP_UP, POP_UP_BUTTON, FRIEND_LIST,FRIEND_SEL_BUTTON,ADD_FRIEND}

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
                    pass = ((JTextField)componentsOnScreen.get(guiItems.PASSWORD)).getText();
                    user = ((JTextField)componentsOnScreen.get(guiItems.USERNAME)).getText();
                    switch (type){
                        case SHOW:
                            clearGui();
                            login(true,user,pass);
                            break;
                        case HIDE:
                            clearGui();
                            login(false,user,pass);
                            break;
                        case BACK:
                            clearGui();
                            onlineState = GameState.LOGIN_REGISTER;
                            break;
                        case LOGIN_BUTTON:
                            //popUp("TU PUTA MADRE, GRACIAS");
                            clearGui();
                            onlineState = GameState.PRINCIPAL_GUI;
                            break;
                        case POP_UP_BUTTON:
                            clearGui();
                            login(false,user,pass);
                        default:
                            System.out.println("SE HA PRETADO UN BOTON");
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
                            System.out.println("SE HA PRETADO UN BOTON");
                            break;
                    }
                    break;
                default:
                    System.out.println("SE HA PRETADO UN BOTON");
                    break;
            }
        }
    }
}
