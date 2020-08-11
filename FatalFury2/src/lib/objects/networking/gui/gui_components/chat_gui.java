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

public class chat_gui {
    private online_mode_gui gui;
    private List<message> msgs;
    private String friend;
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),grey3 = new Color(45,48,85),
            grey4 = new Color(99,96,171), brown = new Color(140,105,57), blue = new Color(0,0,148);
    private Font f,f2,f3;
    private ChatTextsTableModel model;

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

    private int res(int x){
        return gui.res(x);
    }

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

        gui.enableComponents(new guiItems[]{guiItems.NORMAL_BUTTON, guiItems.RANKED_BUTTON,
                guiItems.RANKING_BUTTON, guiItems.QUIT_BUTTON, guiItems.PROFILE_BUTTON,
                guiItems.BACK}, false);
        gui.reloadGUI();
    }

    //https://www.codejava.net/java-se/swing/jtable-column-header-custom-renderer-examples
    public class HeaderChatRenderer extends JLabel implements TableCellRenderer {
        public HeaderChatRenderer() {
            setFont(f3);
            setForeground(Color.YELLOW);
            setBorder(new LineBorder(grey3, 5));
            setHorizontalAlignment(JTextField.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }

    class ChatTableRenderer implements TableCellRenderer {
        private TableCellRenderer defaultRenderer;

        public ChatTableRenderer(TableCellRenderer renderer) {
            defaultRenderer = renderer;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof Component) {
                return (Component) value;
            }
            return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    class ChatTextsTableModel extends AbstractTableModel {
        private Object[][] rows;
        private String[] columns = {friend};

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

        @Override
        public void setValueAt(Object value, int row, int column)
        {
            JTextField aux = (JTextField)value;
            Object[][] rowsAux = new Object[row+1][1];
            System.arraycopy(rows, 0, rowsAux, 0, rows.length);
            rows = rowsAux;
            rows[row][column] = aux;
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
        }
        else{
            addMessage(new message(m.getId(),m.getTransmitter(),m.getReceiver(),m.getContent().substring(0,30)));
            addMessage(new message(m.getId(),m.getTransmitter(),m.getReceiver(),m.getContent().substring(30,m.getContent().length())));
        }
    }
}
