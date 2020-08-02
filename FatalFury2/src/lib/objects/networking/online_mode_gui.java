package lib.objects.networking;

import lib.Enums.GameState;
import lib.Enums.guiItems;
import lib.objects.Screen;
import videojuegos.Principal;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class online_mode_gui {
    private Map<guiItems,Component> componentsOnScreen = new HashMap<>();
    private List<guiItems> itemsOnScreen = new ArrayList<>();
    private GameState onlineState = GameState.LOGIN_REGISTER;
    private gameGUI gui;
    private Principal principal;
    private double m = 1.0;
    private List<String> friends;
    private int friendSelected = -1;
    private int xYableClick = 0, yTableClick = 0;
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),grey3 = new Color(45,48,85),
            grey4 = new Color(99,96,171), brown = new Color(140,105,57), blue = new Color(0,0,148);
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
        return (int)(((double)(x*m))+0.5);
    }

    private void addComponents(guiItems items[], Component components[]){
        for(int i = 0; i < items.length; ++i){
            gui.add(components[i]);
            componentsOnScreen.put(items[i], components[i]);
            itemsOnScreen.add(items[i]);
        }
    }

    public void clearGui(){
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

    public JButton generateSimpleButton(String text, guiItems id, Font font, Color color, Color back, int x, int y, int w, int h){
        JButton aux = new JButton(text);
        aux.addActionListener(new guiListener(this,id));
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

    public void popUpWithConfirmation(String msg){
        JTextArea popup = generateSimpleTextArea(msg, f, Color.YELLOW, grey1, 370, 220, 540, 280, false, false);
        popup.setBorder(javax.swing.BorderFactory.createLineBorder(brown,5));

        JButton yes = generateSimpleButton("Yes", guiItems.YES_BUTTON, f, Color.YELLOW, grey2, 486, 420, 120, 60);
        JButton no = generateSimpleButton("No", guiItems.NOT_BUTTON, f, Color.YELLOW, grey2, 666, 420, 120, 60);

        gui.add(yes);
        componentsOnScreen.put(guiItems.YES_BUTTON, yes);
        itemsOnScreen.add(0,guiItems.YES_BUTTON);
        gui.add(no);
        componentsOnScreen.put(guiItems.NOT_BUTTON, no);
        itemsOnScreen.add(1,guiItems.NOT_BUTTON);
        gui.add(popup);
        componentsOnScreen.put(guiItems.POP_UP, popup);
        itemsOnScreen.add(2,guiItems.POP_UP);
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

            friends = new ArrayList<>();
            friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");friends.add("JOSH1");friends.add("JOSH2");friends.add("JOSH3");friends.add("JOSH4");friends.add("JOSH5");

            friendList(friends);

            JButton normal = generateSimpleButton("Normal mode", guiItems.NORMAL_BUTTON, f, Color.YELLOW, grey1, 308, 375, 400, 65);
            JButton ranked = generateSimpleButton("Ranked mode", guiItems.RANKED_BUTTON, f, Color.YELLOW, grey1, 308, 450, 400, 65);
            JButton tournaments = generateSimpleButton("Tournament mode", guiItems.TOURNAMENT_BUTTON, f, Color.YELLOW, grey1, 308, 520, 400, 65);
            JButton profile = generateSimpleButton("Profile", guiItems.PROFILE_BUTTON, f, Color.YELLOW, grey1, 308, 595, 200, 65);
            JButton exit = generateSimpleButton("Quit", guiItems.QUIT_BUTTON, f, Color.YELLOW, grey1, 508, 595, 200, 65);

            guiItems items[] = {guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.TOURNAMENT_BUTTON, guiItems.PROFILE_BUTTON,
                    guiItems.QUIT_BUTTON};
            Component components[] = {normal, ranked, tournaments, profile, exit};

            addComponents(items, components);

            gui.setBack(2);
            gui.repaint();
            gui.revalidate();
        }
    }

    private void friendList(List<String>  friends){

        JTable table;
        JScrollPane scrollPane;
        TableCellRenderer tableRenderer;
        table = new JTable(new FriendsButtonsTableModel(friends));
        table.setBounds(res(1030),0,res(250),res(635));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(res(60));
        table.setOpaque(true);
        table.setFillsViewportHeight(true);
        table.setBackground(grey1);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel listSelectionModel = table.getSelectionModel();
        FriendTableSelectionListener friendSelector = new FriendTableSelectionListener();
        listSelectionModel.addListSelectionListener(friendSelector);
        table.setSelectionModel(listSelectionModel);
        table.addMouseListener(friendSelector);

        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(grey1);
        header.setDefaultRenderer(new HeaderFriendListRenderer());
        header.setPreferredSize(new Dimension(res(250), res(70)));
        header.setReorderingAllowed(false);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(res(250));

        tableRenderer = table.getDefaultRenderer(JButton.class);
        table.setDefaultRenderer(JButton.class, new FriendsButtonsTableRenderer(tableRenderer));

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(res(1030),0,res(250),res(635));
        scrollPane.setBackground(grey1);
        scrollPane.getVerticalScrollBar().setBackground(grey3);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.YELLOW;
            }
            /*@Override
            protected JButton createDecreaseButton(int orientation) {
                return new BasicArrowButton(orientation, grey2, UIManager.getColor("ScrollBar.thumbShadow"),
                        UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
            }
            @Override
            return new BasicArrowButton(orientation, grey2, UIManager.getColor("ScrollBar.thumbShadow"),
                        UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
            }*/
        });

        JButton addFriend = generateSimpleButton("Add friend", guiItems.ADD_FRIEND, f, Color.YELLOW, grey1, 1030, 630, 250, 60);

        guiItems items[] = {guiItems.ADD_FRIEND, guiItems.FRIEND_LIST};
        Component components[] = {addFriend, scrollPane};

        addComponents(items, components);
    }

    class FriendTableSelectionListener extends MouseAdapter implements ListSelectionListener {
        private boolean clicking = false;

        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (lsm.isSelectionEmpty()) {
                System.out.println("No tienes amigos");
            } else {
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                boolean ended = false;
                for (int i = minIndex; !ended && i <= maxIndex; i++) {
                    if (!clicking && lsm.isSelectedIndex(i)) {
                        clicking = true;
                        ended = true;
                    }
                    else{
                        friendSelected = i;
                        clicking = false;
                        ended = true;
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            xYableClick = (int) (0.5 + me.getX());
            yTableClick = (int) (0.5 + (me.getYOnScreen()-online_mode_gui.this.componentsOnScreen.get(guiItems.FRIEND_LIST).getLocationOnScreen().getY())/m);
            new guiListener(online_mode_gui.this,guiItems.FRIEND_SEL_BUTTON).actionPerformed(null);
        }
    }

    //https://www.codejava.net/java-se/swing/jtable-column-header-custom-renderer-examples
    public class HeaderFriendListRenderer extends JLabel implements TableCellRenderer {
        public HeaderFriendListRenderer() {
            setFont(f3);
            setForeground(Color.YELLOW);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }

    //https://www.tutorialspoint.com/how-can-we-add-insert-a-jbutton-to-jtable-cell-in-java
    class FriendsButtonsTableRenderer implements TableCellRenderer {
        private TableCellRenderer defaultRenderer;

        public FriendsButtonsTableRenderer(TableCellRenderer renderer) {
            defaultRenderer = renderer;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof Component) {
                Component aux = (Component) value;
                if(isSelected){
                    aux.setBackground(grey3);
                }
                else{
                    aux.setBackground(grey2);
                }
                return aux;
            }
            return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    class FriendsButtonsTableModel extends AbstractTableModel {
        private Object[][] rows;
        private String[] columns = {" Friends"};

        public FriendsButtonsTableModel(List<String> friends){
            rows = new Object[friends.size()][1];
            for(int i = 0; i < friends.size();++i){
                JButton aux = new JButton(friends.get(i));
                aux.setFont(f);
                aux.setForeground(Color.YELLOW);
                aux.setBackground(grey2);
                aux.addActionListener(new guiListener(online_mode_gui.this,guiItems.FRIEND_SEL_BUTTON));
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
            return false;
        }
        public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
    }

    public void friendInteractionPopUp(){
        JButton invite, msg, profile, delete;
        int x = 830, y = yTableClick;

        if(y > 317+35){
            y -= 160;
        }

        invite = generateSimpleButton("Challenge",guiItems.INVITE_FRIEND,f2,Color.YELLOW, grey3, x, y, 200,40);
        msg = generateSimpleButton("Chat",guiItems.MESSAGE_FRIEND,f2,Color.YELLOW, grey3, x, y+40, 200,40);
        profile = generateSimpleButton("Profile",guiItems.PROFILE_FRIEND,f2,Color.YELLOW, grey3, x, y+80, 200,40);
        delete = generateSimpleButton("Delete",guiItems.DELETE_FRIEND,f2,Color.YELLOW, grey3, x, y+120, 200,40);

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

        gui.repaint();
        gui.revalidate();
    }

    public void addFriend(){
        JTextField popup = generateSimpleTextField("Introduce the username:", f, Color.YELLOW, grey2, 370, 220, 540, 280, false, false);
        popup.setBorder(javax.swing.BorderFactory.createLineBorder(brown,5));
        popup.setHorizontalAlignment(JTextField.CENTER);

        JTextField name = generateSimpleTextField("", f, Color.YELLOW, grey1, 370, 220, 300, 60, false, false);

        JButton popupB = generateSimpleButton("Okey", guiItems.POP_UP_BUTTON, f, Color.YELLOW, grey2, 580, 420, 120, 60);

        gui.add(popupB);
        componentsOnScreen.put(guiItems.POP_UP_BUTTON, popupB);
        itemsOnScreen.add(0,guiItems.POP_UP_BUTTON);
        gui.add(popup);
        componentsOnScreen.put(guiItems.POP_UP, popup);
        itemsOnScreen.add(1,guiItems.POP_UP);
        reloadGUI();
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

    public int getxYableClick() {
        return xYableClick;
    }

    public void setxYableClick(int xYableClick) {
        this.xYableClick = xYableClick;
    }

    public int getyTableClick() {
        return yTableClick;
    }

    public void setyTableClick(int yTableClick) {
        this.yTableClick = yTableClick;
    }
}
