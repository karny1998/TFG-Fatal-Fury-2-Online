package lib.objects.networking.gui.gui_components;

import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.guiListener;
import lib.objects.networking.gui.online_mode_gui;
import lib.utils.sendableObjects.simpleObjects.game;
import lib.utils.sendableObjects.simpleObjects.profile;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * The type Profile gui.
 */
public class profile_gui {
    /**
     * The Gui.
     */
    private online_mode_gui gui;
    /**
     * The Prof.
     */
    private profile prof;
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
     * The Terry.
     */
    private ImageIcon terry, /**
     * The Andy.
     */
    andy, /**
     * The Mai.
     */
    mai;
    /**
     * The Scroll 1.
     */
    private JScrollPane scroll1, /**
     * The Scroll 2.
     */
    scroll2;
    /**
     * The In scroll 1.
     */
    private boolean inScroll1 = true;

    /**
     * Instantiates a new Profile gui.
     *
     * @param gui  the gui
     * @param prof the prof
     */
    public profile_gui(online_mode_gui gui, profile prof){
        this.gui = gui;
        this.prof = prof;
        this.f = gui.getF();
        this.f2 = gui.getF2();
        this.f3 = gui.getF3();

        terry = gui.loadIcon("/assets/sprites/characters/terry/icon.png",70,70);
        andy = gui.loadIcon("/assets/sprites/characters/andy/icon.png",70,70);
        mai = gui.loadIcon("/assets/sprites/characters/mai/icon.png",70,70);

        profile();
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
     * Profile.
     */
    public void profile(){
        new friend_list_gui(gui,gui.getFriends(), gui.getPendingMessages());

        JTextField name = null;
        if(prof.getUser().equals(gui.getUserLogged())){
            name = gui.generateSimpleTextField(prof.getUser(), f3, Color.YELLOW, grey2, 256, 25, 500, 60, false, true);
        }
        else{
            name = gui.generateSimpleTextField(prof.getUser(), f3, Color.YELLOW, grey2, 256, 25, 500, 100, false, true);
        }
        JTextField points = gui.generateSimpleTextField("Ranked points: " + prof.getPoints(), f, Color.YELLOW, grey4, 256, 150, 500, 60, false, true);
        JTextField normals = gui.generateSimpleTextField("Normal: " + prof.getNormalWins() + "W " + prof.getNormalLoses() + "L", f, Color.YELLOW, grey4, 50, 235, 450, 60, false, true);
        JTextField rankeds = gui.generateSimpleTextField("Rankeds: " + prof.getRankedWins() + "W " + prof.getRankedLoses() + "L", f, Color.YELLOW, grey4, 530, 235, 450, 60, false, true);
        name.setHorizontalAlignment(JTextField.CENTER);
        points.setHorizontalAlignment(JTextField.CENTER);
        normals.setHorizontalAlignment(JTextField.CENTER);
        rankeds.setHorizontalAlignment(JTextField.CENTER);
        JButton back = gui.backButton();

        JTable table;
        JScrollPane scrollPane;
        TableCellRenderer tableRenderer;
        table = new JTable(new HistorialTableModel(false));
        table.setBounds(res(50), res(320), res(950), res(350));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(res(70));
        table.setOpaque(true);
        table.setFillsViewportHeight(true);
        table.setBackground(grey1);
        table.setRowSelectionAllowed(false);
        table.setShowGrid(false);

        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {}
            @Override
            public void mousePressed(MouseEvent mouseEvent) {}
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                new guiListener( gui, guiItems.AUXILIAR_BACKGROUND).actionPerformed(null);
            }
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {}
            @Override
            public void mouseExited(MouseEvent mouseEvent) {}
        });

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(res(950), res(0)));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(res(70));
        columnModel.getColumn(1).setPreferredWidth(res(330));
        columnModel.getColumn(2).setPreferredWidth(res(133));
        columnModel.getColumn(3).setPreferredWidth(res(330));
        columnModel.getColumn(4).setPreferredWidth(res(70));

        tableRenderer = table.getDefaultRenderer(Component.class);
        table.setDefaultRenderer(Component.class, new HistorialTableRenderer(tableRenderer));

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(res(50), res(320), res(950), res(350));
        scrollPane.setBackground(grey1);
        scrollPane.getVerticalScrollBar().setBackground(grey3);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.YELLOW;
            }
        });
        scrollPane.setBorder(new LineBorder(Color.black, 0));
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                gui.closeAllPopUps();
            }
        });

        scroll1 = scrollPane;

        table = new JTable(new HistorialTableModel(true));
        table.setBounds(res(50), res(320), res(500), res(350));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(res(70));
        table.setOpaque(true);
        table.setFillsViewportHeight(true);
        table.setBackground(grey1);
        table.setRowSelectionAllowed(false);
        table.setShowGrid(false);

        header = table.getTableHeader();
        header.setPreferredSize(new Dimension(res(950), res(0)));

        columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(res(70));
        columnModel.getColumn(1).setPreferredWidth(res(330));
        columnModel.getColumn(2).setPreferredWidth(res(80));

        tableRenderer = table.getDefaultRenderer(Component.class);
        table.setDefaultRenderer(Component.class, new HistorialTableRenderer(tableRenderer));

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(res(50), res(320), res(500), res(350));

        scrollPane.setBackground(grey1);
        scrollPane.getVerticalScrollBar().setBackground(grey3);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.YELLOW;
            }
        });
        scrollPane.setBorder(new LineBorder(Color.black, 0));
        scroll2 = scrollPane;
        scroll2.setVisible(false);

        guiItems items[] = {guiItems.BACK, guiItems.HISTORIAL , guiItems.AUXILIAR_BACKGROUND, guiItems.PROFILE_NAME, guiItems.PROFILE_POINTS, guiItems.PROFILE_NORMALS,
                guiItems.PROFILE_RANKEDS, guiItems.HISTORIAL2};
        Component components[] = {back, scroll1, gui.auxiliarBackgroud(),name, points, normals, rankeds, scroll2};

        gui.addComponents(items, components);

        if(prof.getUser().equals(gui.getUserLogged())){
            JButton change = gui.generateSimpleButton("Change password", guiItems.CHANGE_PASS_BUTTON, f, Color.YELLOW, grey1, 321, 97, 370, 40, false);
            gui.getComponentsOnScreen().put(guiItems.CHANGE_PASS_BUTTON, change);
            gui.getItemsOnScreen().add(0,guiItems.CHANGE_PASS_BUTTON);
            gui.reloadGUI();
        }
    }

    /**
     * The type Historial table renderer.
     */
    class HistorialTableRenderer implements TableCellRenderer {
        /**
         * The Default renderer.
         */
        private TableCellRenderer defaultRenderer;

        /**
         * Instantiates a new Historial table renderer.
         *
         * @param renderer the renderer
         */
        public HistorialTableRenderer(TableCellRenderer renderer) {
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
     * The type Historial table model.
     */
    class HistorialTableModel extends AbstractTableModel {
        /**
         * The Rows.
         */
        private Object[][] rows;
        /**
         * The Columns.
         */
        private String[] columns = {"CHAR1","Host", "TYPE","Client", "CHAR2"};

        /**
         * Instantiates a new Historial table model.
         *
         * @param half the half
         */
        public HistorialTableModel(boolean half){
            rows = new Object[prof.getGames().size()][5];
            if(half){
                columns = new String[]{"CHAR1","Host", "TYPE"};
                rows = new Object[prof.getGames().size()][3];
            }
            ArrayList<game> games = prof.getGames();
            for(int i = 0; i < games.size() && !(i == 5 && half);++i){
                JTextField aux1, aux2, aux3;
                Color c1, c2, c3;
                if(games.get(i).getResult() == 0){
                    c1 = Color.GREEN;
                    c2 = c1;
                }
                else if(games.get(i).getResult() == 1){
                    c1 = Color.GREEN;
                    c2 = Color.RED;
                }
                else {
                    c2 = Color.GREEN;
                    c1 = Color.RED;
                }

                if(i % 2 == 1){
                    c3 = grey4;
                }
                else{
                    c3 = grey2;
                }

                aux1 = gui.generateSimpleTextField(games.get(i).getPlayer1(),f,c1,c3,0,0,330,70,false,true);
                aux1.setBorder(BorderFactory.createCompoundBorder(
                        aux1.getBorder(),
                        BorderFactory.createEmptyBorder(0, 10, 0, 0)));
                aux2 = gui.generateSimpleTextField(games.get(i).getPlayer2(),f,c2,c3,0,0,330,70,false,true);
                aux2.setBorder(BorderFactory.createCompoundBorder(
                        aux2.getBorder(),
                        BorderFactory.createEmptyBorder(0, 0, 0, 10)));
                //aux2.setHorizontalAlignment(JTextField.RIGHT);
                if(games.get(i).isRanked()){
                    String r = "Ranked";
                    if(half){r = "Rank";}
                    aux3 = gui.generateSimpleTextField(r,f,Color.YELLOW,c3,0,0,130,70,false,true);
                }
                else {
                    aux3 = gui.generateSimpleTextField("Normal", f, Color.YELLOW, c3, 0, 0, 130, 70, false, true);
                }
                aux1.setHorizontalAlignment(JTextField.CENTER);
                aux2.setHorizontalAlignment(JTextField.CENTER);
                aux3.setHorizontalAlignment(JTextField.CENTER);

                JLabel aux4 = new JLabel(terry), aux5 = new JLabel(terry);

                switch (games.get(i).getCharacter1()){
                    case "MAI":
                        aux4 = new JLabel(mai);
                        break;
                    case "ANDY":
                        aux4 = new JLabel(andy);
                        break;
                    default:
                        break;
                }

                switch (games.get(i).getCharacter2()){
                    case "MAI":
                        aux5 = new JLabel(mai);
                        break;
                    case "ANDY":
                        aux5 = new JLabel(andy);
                        break;
                    default:
                        break;
                }

                rows[i][0] = aux4;
                rows[i][1] = aux1;
                rows[i][2] = aux3;
                if(!half) {
                    rows[i][3] = aux2;
                    rows[i][4] = aux5;
                }
            }
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
     * Swap historial.
     *
     * @param trap the trap
     */
    public void swapHistorial(boolean trap){
        if(trap){
            scroll2.setVisible(true);
            scroll1.setVisible(false);
        }
        else{
            scroll1.setVisible(true);
            scroll2.setVisible(false);
        }
    }
}
