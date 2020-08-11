package lib.objects.networking.gui.gui_components;

import com.sun.xml.bind.v2.schemagen.XmlSchemaGenerator;
import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.guiListener;
import lib.objects.networking.gui.online_mode_gui;
import lib.objects.networking.msgID;
import lib.utils.sendableObjects.sendableObject;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.message;
import lib.utils.sendableObjects.simpleObjects.profile;

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
import java.util.ArrayList;
import java.util.List;

public class rank_gui {
    private online_mode_gui gui;
    private List<profile> players;
    private String friend;
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),grey3 = new Color(45,48,85),
            grey4 = new Color(99,96,171), brown = new Color(140,105,57), blue = new Color(0,0,148);
    private Font f,f2,f3;
    private ChatTextsTableModel model;

    public rank_gui(online_mode_gui gui){
        this.gui = gui;
        this.f = gui.getF();
        this.f2 = gui.getF2();
        this.f3 = gui.getF3();

        sendableObjectsList aux = (sendableObjectsList) gui.getOnline_controller().getConToServer().sendStringWaitingAnswerObject(msgID.toServer.request, "RANKING", 0);
        this.players = new ArrayList<>();
        for(sendableObject p : aux.getMsgs()){
            players.add((profile)p);
        }

        gui.clearGui();
        rank();
    }

    private int res(int x){
        return gui.res(x);
    }

    private void rank(){

        JTable table;
        JScrollPane scrollPane;
        TableCellRenderer tableRenderer;
        model = new ChatTextsTableModel();
        table = new JTable(model);
        table.setBounds(res(50),res(80),res(935),res(590));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(res(60));
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
        header.setDefaultRenderer(new HeaderRankingRenderer());
        header.setPreferredSize(new Dimension(res(935), res(80)));
        header.setReorderingAllowed(false);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(res(140));
        columnModel.getColumn(1).setPreferredWidth(res(250));
        columnModel.getColumn(2).setPreferredWidth(res(160));
        columnModel.getColumn(3).setPreferredWidth(res(160));
        columnModel.getColumn(4).setPreferredWidth(res(220));

        tableRenderer = table.getDefaultRenderer(JTextField.class);
        table.setDefaultRenderer(JTextField.class, new RankingTableRenderer(tableRenderer));

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(res(50),res(80),res(935),res(590));
        scrollPane.setBackground(grey1);
        scrollPane.getVerticalScrollBar().setBackground(grey3);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.YELLOW;
            }
        });
        //scrollPane.setBorder(new LineBorder(Color.black, 0));

        guiItems items[] = {guiItems.RANKING,guiItems.BACK};
        Component components[] = {scrollPane,gui.backButton()};

        gui.addComponents(items, components);

        gui.reloadGUI();
    }

    //https://www.codejava.net/java-se/swing/jtable-column-header-custom-renderer-examples
    public class HeaderRankingRenderer extends JLabel implements TableCellRenderer {
        public HeaderRankingRenderer() {
            setFont(f3);
            setForeground(Color.ORANGE);
            //setBorder(new LineBorder(grey3, 5));
            setHorizontalAlignment(JTextField.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }

    }

    class RankingTableRenderer implements TableCellRenderer {
        private TableCellRenderer defaultRenderer;

        public RankingTableRenderer(TableCellRenderer renderer) {
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
        private String[] columns = {"RANK","USER", "WINS", "LOSES", "POINTS"};

        public ChatTextsTableModel(){
            rows = new Object[players.size()][5];
            for(int i = 0; i < players.size();++i){
                profile pAux = players.get(i);
                JTextField rank, user, wins, loses,points;
                Color c;
                if(i%2 == 1) {
                    c = grey2;
                }
                else {
                    c = grey4;
                }

                rank = gui.generateSimpleTextField(Integer.toString(i)+".",f,Color.RED,c,0,0,140,40,false,true);
                rank.setHorizontalAlignment(JTextField.CENTER);
                user = gui.generateSimpleTextField(pAux.getUser(),f,Color.YELLOW,c,0,0,250,40,false,true);
                user.setHorizontalAlignment(JTextField.CENTER);
                wins = gui.generateSimpleTextField(Integer.toString(pAux.getRankedWins())+"W",f,Color.YELLOW,c,0,0,160,40,false,true);
                wins.setHorizontalAlignment(JTextField.CENTER);
                loses = gui.generateSimpleTextField(Integer.toString(pAux.getRankedLoses())+"L",f,Color.YELLOW,c,0,0,160,40,false,true);
                loses.setHorizontalAlignment(JTextField.CENTER);
                points = gui.generateSimpleTextField(Integer.toString(pAux.getPoints())+"pts",f,Color.YELLOW,c,0,0,220,40,false,true);
                points.setHorizontalAlignment(JTextField.CENTER);

                rows[i][0] = rank;
                rows[i][1] = user;
                rows[i][2] = wins;
                rows[i][3] = loses;
                rows[i][4] = points;
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
