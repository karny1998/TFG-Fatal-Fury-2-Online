package lib.objects.networking.gui.gui_components;

import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.guiListener;
import lib.objects.networking.gui.online_mode_gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class friend_list_gui {
    private online_mode_gui gui;
    private List<String> friends;
    private List<String> pendingMessages;
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),grey3 = new Color(45,48,85),
            grey4 = new Color(99,96,171), brown = new Color(140,105,57), blue = new Color(0,0,148);
    private Font f,f2,f3;

    public friend_list_gui(online_mode_gui gui, List<String> friends, List<String> pendingMessages){
        this.gui = gui;
        this.friends = friends;
        this.pendingMessages = pendingMessages;
        this.f = gui.getF();
        this.f2 = gui.getF2();
        this.f3 = gui.getF3();
        this.gui.closeFriendList();
        friendList();
    }

    private int res(int x){
        return gui.res(x);
    }

    private void friendList(){
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
        scrollPane.setBorder(new LineBorder(Color.BLACK, 0));

        JButton addFriend = gui.generateSimpleButton("Add friend", guiItems.ADD_FRIEND, f, Color.YELLOW, grey1, 1030, 630, 250, 60, false);

        guiItems items[] = {guiItems.ADD_FRIEND, guiItems.FRIEND_LIST};
        Component components[] = {addFriend, scrollPane};

        gui.addComponents(items, components);

        gui.getItemsOnScreen().remove(guiItems.ADD_FRIEND);
        gui.getItemsOnScreen().add(0,guiItems.ADD_FRIEND);
    }

    class FriendTableSelectionListener extends MouseAdapter implements ListSelectionListener {
        private boolean clicking = false;
        private int times = 0;
        private long reference;
        private int actRow, previousRow;

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
                        gui.setFriendSelected(i);
                        clicking = false;
                        ended = true;
                        actRow = i;
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if(gui.getFriendSelected() < 0){return;}

            if(me.getYOnScreen() >  (gui.getComponentsOnScreen().get(guiItems.FRIEND_LIST).getLocationOnScreen().getY() + res(70 + 60*friends.size()))){
                gui.closeFriendInteractionPopUp();
                return;
            }

            int xTableClick = (int) (0.5 + me.getX());
            int yTableClick = (int) (0.5 + (me.getYOnScreen() - gui.getComponentsOnScreen().get(guiItems.FRIEND_LIST).getLocationOnScreen().getY()) / gui.getM());

            gui.setxTableClick(xTableClick);
            gui.setyTableClick(yTableClick);

            if(times == 0){
                reference = System.currentTimeMillis();
                times = 1;
                previousRow = actRow;
                new guiListener(gui, guiItems.FRIEND_SEL_BUTTON).actionPerformed(null);
            }
            else if(times == 1){
                long act = System.currentTimeMillis();
                if(act - reference < 1000.0 && actRow == previousRow){
                    gui.closeFriendInteractionPopUp();
                    new guiListener(gui,guiItems.MESSAGE_FRIEND).actionPerformed(null);
                }
                else{
                    new guiListener(gui, guiItems.FRIEND_SEL_BUTTON).actionPerformed(null);
                }

                if(actRow == previousRow) {
                    times = 0;
                    actRow = -1;
                    previousRow = -2;
                }
                else{
                    previousRow = actRow;
                }
            }
            else{
                new guiListener(gui, guiItems.FRIEND_SEL_BUTTON).actionPerformed(null);
            }
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
                if(pendingMessages.contains(friends.get(i))){
                    aux.setForeground(new Color(230,115,0));
                }
                aux.setBackground(grey2);
                aux.addActionListener(new guiListener(gui,guiItems.FRIEND_SEL_BUTTON));
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
}
