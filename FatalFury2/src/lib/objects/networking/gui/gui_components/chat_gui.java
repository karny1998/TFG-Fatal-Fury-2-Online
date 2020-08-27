package lib.objects.networking.gui.gui_components;

import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.guiListener;
import lib.objects.networking.gui.online_mode_gui;
import lib.utils.sendableObjects.simpleObjects.message;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * The type Chat gui.
 */
public class chat_gui {
    /**
     * The Gui.
     */
    private online_mode_gui gui;
    /**
     * The Msgs.
     */
    private List<message> msgs;
    /**
     * The Friend.
     */
    private String friend;
    /**
     * The Grey 1.
     */
    private Color grey1 = new Color(33,32,57), /**
     * The Grey 2.
     */
    grey2 = new Color(66,64,114), /**
     * The Grey 3.
     */
    grey3 = new Color(45,48,85),
    /**
     * The Grey 4.
     */
    grey4 = new Color(99,96,171), /**
     * The Brown.
     */
    brown = new Color(140,105,57), /**
     * The Blue.
     */
    blue = new Color(0,0,148);
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
    /**
     * The Model.
     */
    private ChatTextsTableModel model;

    /**
     * Instantiates a new Chat gui.
     *
     * @param gui    the gui
     * @param msgs   the msgs
     * @param friend the friend
     */
    public chat_gui(online_mode_gui gui, List<message> msgs, String friend){
        this.gui = gui;
        this.msgs = msgs;
        this.friend = friend;
        this.f = gui.getF();
        this.f2 = gui.getF2();
        this.f3 = gui.getF3();

        for(int i = 0; i < msgs.size();++i){
            message aux = msgs.get(i);
            if(aux.getContent().length() > 30){
                msgs.add(i+1,new message(aux.getId(),aux.getTransmitter(),aux.getReceiver(),aux.getContent().substring(30,aux.getContent().length())));
                aux.setContent(aux.getContent().substring(0,30));
            }
        }

        gui.closeChat();

        chat();
    }

    /**
     * Res int.
     *
     * @param x the x
     * @return the int
     */
    private int res(int x){
        return gui.res(x);
    }

    /**
     * Chat.
     */
    private void chat(){

        JTable table;
        JScrollPane scrollPane;
        TableCellRenderer tableRenderer;
        model = new ChatTextsTableModel();
        table = new JTable(model);
        table.setBounds(res(530),res(230),res(600),res(400));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(res(40));
        table.setOpaque(true);
        table.setFillsViewportHeight(true);
        table.setBackground(grey1);
        table.setRowSelectionAllowed(false);
        table.setShowGrid(false);

        table.addComponentListener(new ComponentAdapter() {
                                       public void componentResized(ComponentEvent e) {
                                           table.scrollRectToVisible(table.getCellRect(table.getRowCount()-1, 0, true));
                                       }});

        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(grey2);
        header.setDefaultRenderer(new HeaderChatRenderer());
        header.setPreferredSize(new Dimension(res(500), res(60)));
        header.setReorderingAllowed(false);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(res(500));

        tableRenderer = table.getDefaultRenderer(JTextField.class);
        table.setDefaultRenderer(JTextField.class, new ChatTableRenderer(tableRenderer));

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(res(530),res(230),res(500),res(400));
        scrollPane.setBackground(grey1);
        scrollPane.getVerticalScrollBar().setBackground(grey3);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.YELLOW;
            }
        });
        //scrollPane.setBorder(new LineBorder(Color.black, 0));

        JButton send = gui.generateSimpleButton("Send", guiItems.SEND_MESSAGE, f2, Color.YELLOW, grey1, 930, 630, 100, 60, false);

        JTextField writer = gui.generateSimpleTextField("", f2, Color.YELLOW, grey1, 530, 630, 400, 60, true, false);
        writer.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new guiListener(gui, guiItems.SEND_MESSAGE).actionPerformed(null);
            }
        });

        JButton close = gui.generateSimpleButton("X", guiItems.CLOSE_CHAT, f, Color.YELLOW, grey1, 960, 242, 40, 40, true);

        guiItems items[] = {guiItems.CHAT, guiItems.SEND_MESSAGE, guiItems.MESSAGE_WRITER, guiItems.CLOSE_CHAT};
        Component components[] = {scrollPane, send,writer, close};

        gui.addComponents(items, components);

        gui.getItemsOnScreen().remove(guiItems.MESSAGE_WRITER);
        gui.getItemsOnScreen().add(0,guiItems.MESSAGE_WRITER);
        gui.getItemsOnScreen().remove(guiItems.SEND_MESSAGE);
        gui.getItemsOnScreen().add(0,guiItems.SEND_MESSAGE);
        gui.getItemsOnScreen().remove(guiItems.CHAT);
        gui.getItemsOnScreen().add(0,guiItems.CHAT);
        gui.getItemsOnScreen().remove(guiItems.CLOSE_CHAT);
        gui.getItemsOnScreen().add(0,guiItems.CLOSE_CHAT);

        gui.enableComponents(new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON,guiItems.VS_IA_BUTTON,
                guiItems.RANKING_BUTTON, guiItems.QUIT_BUTTON, guiItems.PROFILE_BUTTON,
                guiItems.BACK}, false);
        gui.reloadGUI();
    }

    /**
     * The type Header chat renderer.
     */
//https://www.codejava.net/java-se/swing/jtable-column-header-custom-renderer-examples
    public class HeaderChatRenderer extends JLabel implements TableCellRenderer {
        /**
         * Instantiates a new Header chat renderer.
         */
        public HeaderChatRenderer() {
            setFont(f3);
            setForeground(Color.YELLOW);
            setBorder(new LineBorder(grey3, 5));
            setHorizontalAlignment(JTextField.LEFT);
        }

        /**
         * Gets table cell renderer component.
         *
         * @param table      the table
         * @param value      the value
         * @param isSelected the is selected
         * @param hasFocus   the has focus
         * @param row        the row
         * @param column     the column
         * @return the table cell renderer component
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }

    /**
     * The type Chat table renderer.
     */
    class ChatTableRenderer implements TableCellRenderer {
        /**
         * The Default renderer.
         */
        private TableCellRenderer defaultRenderer;

        /**
         * Instantiates a new Chat table renderer.
         *
         * @param renderer the renderer
         */
        public ChatTableRenderer(TableCellRenderer renderer) {
            defaultRenderer = renderer;
        }

        /**
         * Gets table cell renderer component.
         *
         * @param table      the table
         * @param value      the value
         * @param isSelected the is selected
         * @param hasFocus   the has focus
         * @param row        the row
         * @param column     the column
         * @return the table cell renderer component
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof Component) {
                return (Component) value;
            }
            return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    /**
     * The type Chat texts table model.
     */
    class ChatTextsTableModel extends AbstractTableModel {
        /**
         * The Rows.
         */
        private Object[][] rows;
        /**
         * The Columns.
         */
        private String[] columns = {friend};

        /**
         * Instantiates a new Chat texts table model.
         */
        public ChatTextsTableModel(){
            rows = new Object[msgs.size()][1];
            for(int i = 0; i < msgs.size();++i){
                JTextField aux;
                if(msgs.get(i).getTransmitter().equals(friend)) {
                    aux = gui.generateSimpleTextField(" " + msgs.get(i).getContent(),f2,Color.YELLOW,grey2,0,0,482,40,false,true);
                }
                else{
                    aux = gui.generateSimpleTextField(msgs.get(i).getContent() + "  ",f2,Color.YELLOW,grey4,0,0,482,40,false,true);
                    aux.setHorizontalAlignment(JTextField.RIGHT);
                }
                rows[i][0] = aux;
            }
        }

        /**
         * Sets value at.
         *
         * @param value  the value
         * @param row    the row
         * @param column the column
         */
        @Override
        public void setValueAt(Object value, int row, int column)
        {
            JTextField aux = (JTextField)value;
            Object[][] rowsAux = new Object[row+1][1];
            System.arraycopy(rows, 0, rowsAux, 0, rows.length);
            rows = rowsAux;
            rows[row][column] = aux;
        }

        /**
         * Gets column name.
         *
         * @param column the column
         * @return the column name
         */
        public String getColumnName(int column) {
            return columns[column];
        }

        /**
         * Gets row count.
         *
         * @return the row count
         */
        public int getRowCount() {
            return rows.length;
        }

        /**
         * Gets column count.
         *
         * @return the column count
         */
        public int getColumnCount() {
            return columns.length;
        }

        /**
         * Gets value at.
         *
         * @param row    the row
         * @param column the column
         * @return the value at
         */
        public Object getValueAt(int row, int column) {
            return rows[row][column];
        }

        /**
         * Is cell editable boolean.
         *
         * @param row    the row
         * @param column the column
         * @return the boolean
         */
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        /**
         * Gets column class.
         *
         * @param column the column
         * @return the column class
         */
        public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
    }

    /**
     * Add message.
     *
     * @param m the m
     */
    public void addMessage(message m){
        if(m.getContent().length() <= 30) {
            JTextField aux;
            if (m.getTransmitter().equals(friend)) {
                aux = gui.generateSimpleTextField(" " + m.getContent(), f2, Color.YELLOW, grey2, 0, 0, 482, 40, false, true);
            } else {
                aux = gui.generateSimpleTextField(m.getContent() + "  ", f2, Color.YELLOW, grey4, 0, 0, 482, 40, false, true);
                aux.setHorizontalAlignment(JTextField.RIGHT);
            }
            model.setValueAt(aux, model.getRowCount(), 0);
            model.fireTableDataChanged();
            gui.reloadGUI();
            gui.enableComponents(new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON, guiItems.VS_IA_BUTTON,
                    guiItems.RANKING_BUTTON, guiItems.QUIT_BUTTON, guiItems.PROFILE_BUTTON,
                    guiItems.BACK}, false);
        }
        else{
            addMessage(new message(m.getId(),m.getTransmitter(),m.getReceiver(),m.getContent().substring(0,30)));
            addMessage(new message(m.getId(),m.getTransmitter(),m.getReceiver(),m.getContent().substring(30,m.getContent().length())));
        }
    }
}
