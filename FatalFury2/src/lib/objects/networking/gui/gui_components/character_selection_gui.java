package lib.objects.networking.gui.gui_components;

import lib.Enums.GameState;
import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;
import lib.objects.networking.connection;
import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.online_mode_gui;
import lib.objects.networking.msgID;
import lib.utils.sendableObjects.simpleObjects.profile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * The type Character selection gui.
 */
public class character_selection_gui {
    /**
     * The Gui.
     */
    private online_mode_gui gui;
    /**
     * The Grey 1.
     */
    private Color grey1 = new Color(33,32,57),
    /**
     * The Grey 2.
     */
    grey2 = new Color(66,64,114),
    /**
     * The Grey 3.
     */
    grey3 = new Color(45,48,85),
    /**
     * The Grey 4.
     */
    grey4 = new Color(99,96,171),
    /**
     * The Brown.
     */
    brown = new Color(140,105,57),
    /**
     * The Blue.
     */
    blue = new Color(0,0,148);
    /**
     * The F.
     */
    private Font f,
    /**
     * The F 2.
     */
    f2,
    /**
     * The F 3.
     */
    f3,
    /**
     * The F 4.
     */
    f4;
    /**
     * The Terry icon.
     */
    private ImageIcon terryIcon,
    /**
     * The Andy icon.
     */
    andyIcon,
    /**
     * The Mai icon.
     */
    maiIcon,
    /**
     * The Terry mug.
     */
    terryMug,
    /**
     * The Andy mug.
     */
    andyMug,
    /**
     * The Mai mug.
     */
    maiMug,
    /**
     * The Terry combos.
     */
    terryCombos,
    /**
     * The Andy combos.
     */
    andyCombos,
    /**
     * The Mai combos.
     */
    maiCombos,
    /**
     * The Unknown mug.
     */
    unknownMug,
    /**
     * The Australia.
     */
    australia,
    /**
     * The China.
     */
    china,
    /**
     * The Usa.
     */
    usa,
    /**
     * The Usa icon.
     */
    usaIcon,
    /**
     * The China icon.
     */
    chinaIcon,
    /**
     * The Australia icon.
     */
    australiaIcon;
    /**
     * The Is host.
     */
    private boolean isHost = false;
    /**
     * The Chosen character.
     */
    private Playable_Character chosen_character = Playable_Character.TERRY;
    /**
     * The Chosen map.
     */
    private Scenario_type chosen_map = Scenario_type.USA;
    /**
     * The Timer.
     */
    private Timer timer;
    /**
     * The Time.
     */
    private int time = 5;
    /**
     * The Time on screen.
     */
    private JTextField timeOnScreen;
    /**
     * The Port.
     */
    private int port = 0;
    /**
     * The Ip.
     */
    private String ip,
    /**
     * The Rival name.
     */
    rivalName;
    /**
     * The Con to client.
     */
    private connection conToClient;
    /**
     * The Is ranked.
     */
    private boolean isRanked = false;
    /**
     * The All ok.
     */
    private boolean allOk = false;
    /**
     * The Vs ia.
     */
//0 = online 1 = own ia 2 = global ia
    private int vsIa = 0;

    /**
     * Instantiates a new Character selection gui.
     *
     * @param gui       the gui
     * @param isHost    the is host
     * @param ip        the ip
     * @param port      the port
     * @param rivalName the rival name
     * @param isRanked  the is ranked
     * @param vsIa      the vs ia
     */
    public character_selection_gui (online_mode_gui gui, boolean isHost, String ip, int port, String rivalName, boolean isRanked, int vsIa){
        this.isHost = isHost;
        this.vsIa = vsIa;
        if(vsIa > 0){
            this.isHost = true;
        }
        this.isRanked = isRanked;
        this.ip = ip;
        this.port = port;
        this.rivalName = rivalName;
        this.gui = gui;

        if(vsIa == 0) {
            boolean ok = gui.getOnline_controller().generateConToClient(ip, port);
            if (!ok) {
                return;
            }
            this.allOk = true;
            this.conToClient = gui.getOnline_controller().getConToClient();
        }
        else {
            this.allOk = true;
        }

        try {
            f4 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float)res(60));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.f = gui.getF();
        this.f2 = gui.getF2();
        this.f3 = gui.getF3();

        terryIcon = gui.loadIcon("/assets/sprites/menu/character/terry/terry_icon.png",100,100);
        andyIcon = gui.loadIcon("/assets/sprites/menu/character/andy/andy_icon.png",100,100);
        maiIcon = gui.loadIcon("/assets/sprites/menu/character/mai/mai_icon.png",100,100);
        terryMug = gui.loadIcon("/assets/sprites/menu/character/terry/terry_mugshot.png",312,285);
        andyMug = gui.loadIcon("/assets/sprites/menu/character/andy/andy_mugshot.png",312,285);
        maiMug = gui.loadIcon("/assets/sprites/menu/character/mai/mai_mugshot.png",312,285);
        terryCombos = gui.loadIcon("/assets/sprites/menu/character/terry/terry_name.png",267,224);
        andyCombos = gui.loadIcon("/assets/sprites/menu/character/andy/andy_name.png",267,224);
        maiCombos = gui.loadIcon("/assets/sprites/menu/character/mai/mai_name.png",267,224);
        unknownMug = gui.loadIcon("/assets/sprites/menu/character/unknown_mughsot.png",312,285);

        australia = gui.loadIcon("/assets/sprites/menu/map/australia.png",436,244);
        china = gui.loadIcon("/assets/sprites/menu/map/china.png",436,244);
        usa = gui.loadIcon("/assets/sprites/menu/map/usa.png",436,244);
        australiaIcon = gui.loadIcon("/assets/sprites/menu/australia.png",100,64);
        chinaIcon = gui.loadIcon("/assets/sprites/menu/china.png",100,64);
        usaIcon = gui.loadIcon("/assets/sprites/menu/usa.png",100,64);

        characterSelection();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(vsIa == 0) {
                    String trat = conToClient.receiveString(msgID.toClient.quit);
                    if (trat.equals("LEFT THE GAME")) {
                        close();
                        timer.stop();
                        return;
                    }
                }
                --time;
                String aux = Integer.toString(time);
                if (time < 10) {
                    aux = "0" + aux;
                }
                timeOnScreen.setText(aux);
                gui.getGui().repaint();
                if (time == 0) {
                    timer.stop();
                    if (vsIa == 0) {
                        generateOnlineGame();
                    } else {
                        generateIaGame();
                    }
                }
            }
        });
        timer.start();
    }

    /**
     * Generate game.
     */
    private void generateOnlineGame(){
        if(isHost) {
            System.out.println("El host pasa a notificar el personaje y mapa");
            boolean ok = conToClient.reliableSendString(msgID.toClient.tramits, chosen_character.toString()+":"+chosen_map.toString(), 200);
            System.out.println("El host ha notificado el personaje y mapa");
            if(!ok){
                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                gui.clearGui();
                gui.popUp("Connection lost with the rival.");
                return;
            }
            Playable_Character rivalC = null;
            ok = false;
            int i = 0;
            System.out.println("El host pasa a recibir el personaje");
            while(!ok && i < 30){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("SE ENTRA A RECIBIR");
                String msg = conToClient.receiveString(msgID.toClient.tramits);
                System.out.println("TRAS ENTRAR A RECIBIR " + msg);
                if(msg.contains(":")){
                    ok = true;
                    String res[] = msg.split(":");
                    rivalC = Playable_Character.valueOf(res[0]);
                }
                else{
                    ++i;
                }
            }
            if(!ok){
                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                gui.clearGui();
                gui.popUp("Connection lost with the rival.");
                return;
            }
            System.out.println("El host ha recibido el personaje");
            System.out.println("Es host y ha elegido " + chosen_character.toString() );
            System.out.println(" y el otro "+ rivalC.toString());
            gui.getOnline_controller().generateFight(isHost, chosen_character, rivalC, chosen_map, isRanked,rivalName);
        }
        else{
            Playable_Character rivalC = null;
            boolean ok = false;
            int i = 0;
            System.out.println("El no host pasa a recibir el personaje y mapa");
            while(!ok && i < 30){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("SE ENTRA A RECIBIR");
                String msg = conToClient.receiveString(msgID.toClient.tramits);
                System.out.println("TRAS ENTRAR A RECIBIR " + msg);
                if(msg.contains(":")){
                    ok = true;
                    String res[] = msg.split(":");
                    rivalC = Playable_Character.valueOf(res[0]);
                    chosen_map = Scenario_type.valueOf(res[1]);
                }
                else{
                    ++i;
                }
            }
            if(!ok){
                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                gui.clearGui();
                gui.popUp("Connection lost with the rival.");
                return;
            }
            System.out.println("El no host ha recibido el personaje y mapa");
            System.out.println("El no host pasa a enviar el personaje");
            ok = conToClient.reliableSendString(msgID.toClient.tramits, chosen_character.toString()+":empty", 200);
            System.out.println("El no host ha recibido el personaje y mapa");
            if(!ok){
                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                gui.clearGui();
                gui.popUp("Connection lost with the rival.");
                return;
            }
            System.out.println("El no host pasa ha enviado el personaje");
            System.out.println("No es host y ha elegido " + chosen_character.toString());
            System.out.println(" y el otro "+ rivalC.toString());
            gui.getOnline_controller().generateFight(isHost, rivalC, chosen_character, chosen_map, isRanked,rivalName);
        }
    }

    /**
     * Generate ia game.
     */
    void generateIaGame(){
        gui.getOnline_controller().generateVsIAFight(chosen_character,chosen_map,vsIa == 2);
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
     * Character selection.
     */
    public void characterSelection(){
        JButton terry, andy, mai, usaB = null, chinaB = null, australiaB = null;

        if(!isHost) {
            terry = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 416, 451, 100, 100, true);
            mai = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 590, 451, 100, 100, true);
            andy = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 763, 451, 100, 100, true);
        }
        else{
            terry = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 416, 551, 100, 100, true);
            mai = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 590, 551, 100, 100, true);
            andy = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 763, 551, 100, 100, true);

            usaB = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 416, 425, 100, 67, true);
            usaB.setIcon(usaIcon);
            usaB.addActionListener(new mapSelectionListener(guiItems.SELECT_USA));
            chinaB = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 590, 425, 100, 67, true);
            chinaB.setIcon(chinaIcon);
            chinaB.addActionListener(new mapSelectionListener(guiItems.SELECT_CHINA));
            australiaB = gui.generateSimpleButton("", null, f, Color.YELLOW, grey1, 763, 425, 100, 67, true);
            australiaB.setIcon(australiaIcon);
            australiaB.addActionListener(new mapSelectionListener(guiItems.SELECT_AUSTRALIA));
        }

        terry.setIcon(terryIcon);
        terry.addActionListener(new characterSelectionListener(guiItems.SELECT_TERRY));
        andy.setIcon(andyIcon);
        andy.addActionListener(new characterSelectionListener(guiItems.SELECT_ANDY));
        mai.setIcon(maiIcon);
        mai.addActionListener(new characterSelectionListener(guiItems.SELECT_MAI));

        JLabel terryM = new JLabel(terryMug), terryC = new JLabel(terryCombos),
                andyM = new JLabel(andyMug), andyC = new JLabel(andyCombos),
                maiM = new JLabel(maiMug), maiC = new JLabel(maiCombos),
                unknowM = new JLabel(unknownMug), usaMap = null, chinaMap = null, ausMap = null;

        JTextField rival = null ;

        if(isHost){
            terryM.setBounds(res(50), res(100),res(312), res(285));
            andyM.setBounds(res(50), res(100),res(312), res(285));
            maiM.setBounds(res(50), res(100),res(312), res(285));
            terryC.setBounds(res(80), res(390),res(267), res(224));
            andyC.setBounds(res(80), res(390),res(267), res(224));
            maiC.setBounds(res(80), res(390),res(267), res(224));
            unknowM.setBounds(res(918), res(200),res(312), res(285));
            rival = gui.generateSimpleTextField(rivalName, f, Color.YELLOW, new Color(0,0,0,0), 918, 500, 250, 60, false, true);

            usaMap = new JLabel(usa);
            chinaMap = new JLabel(china);
            chinaMap.setVisible(false);
            ausMap = new JLabel(australia);
            ausMap.setVisible(false);
            usaMap.setBounds(res(413), res(130), res(436),res(244));
            chinaMap.setBounds(res(413), res(130), res(436),res(244));
            ausMap.setBounds(res(413), res(130), res(436),res(244));
        }
        else{
            terryM.setBounds(res(918), res(100),res(312), res(285));
            andyM.setBounds(res(918), res(100),res(312), res(285));
            maiM.setBounds(res(918), res(100),res(312), res(285));
            terryC.setBounds(res(933), res(405),res(267), res(224));
            andyC.setBounds(res(933), res(405),res(267), res(224));
            maiC.setBounds(res(933), res(405),res(267), res(224));
            unknowM.setBounds(res(50), res(200),res(312), res(285));
            rival = gui.generateSimpleTextField(rivalName, f, Color.YELLOW, new Color(0,0,0,0), 50, 500, 250, 60, false, true);
        }

        rival.setHorizontalAlignment(JTextField.CENTER);

        andyM.setVisible(false); andyC.setVisible(false);
        maiM.setVisible(false); maiC.setVisible(false);

        JTextField tm = gui.generateSimpleTextField(Integer.toString(time),f4,Color.YELLOW,new Color(0,0,54),600,40, 95,45,false,true);
        tm.setHighlighter(null);
        timeOnScreen = tm;

        JButton back = gui.backButton();

        guiItems items[];
        Component comps[];
        if(isHost) {
            items = new guiItems[]{guiItems.SELECT_TERRY, guiItems.SELECT_ANDY, guiItems.SELECT_MAI,
                    guiItems.UNKNOWN_MUG, guiItems.TERRY_MUG, guiItems.TERRY_COMBOS,
                    guiItems.ANDY_MUG, guiItems.ANDY_COMBOS, guiItems.MAI_MUG, guiItems.MAI_COMBOS,
                    guiItems.TIMER, guiItems.SELECT_USA, guiItems.SELECT_CHINA, guiItems.SELECT_AUSTRALIA,
                    guiItems.USA_MAP, guiItems.AUSTRALIA_MAP, guiItems.CHINA_MAP, guiItems.BACK, guiItems.RIVAL_USERNAME} ;
            comps = new Component[]{terry, andy, mai, unknowM, terryM, terryC, andyM, andyC, maiM, maiC, tm,
                    usaB, chinaB, australiaB, usaMap,ausMap,chinaMap, back, rival};
        }
        else{
            items = new guiItems[]{guiItems.SELECT_TERRY, guiItems.SELECT_ANDY, guiItems.SELECT_MAI,
                    guiItems.UNKNOWN_MUG, guiItems.TERRY_MUG, guiItems.TERRY_COMBOS,
                    guiItems.ANDY_MUG, guiItems.ANDY_COMBOS, guiItems.MAI_MUG, guiItems.MAI_COMBOS,
                    guiItems.TIMER, guiItems.BACK, guiItems.RIVAL_USERNAME} ;
            comps = new Component[]{terry, andy, mai, unknowM, terryM, terryC, andyM, andyC, maiM, maiC, tm, back, rival};
        }

        gui.addComponents(items, comps);
        gui.reloadGUI();
    }

    /**
     * Close.
     */
    public void close(){
        timer.stop();
        gui.setOnlineState(GameState.PRINCIPAL_GUI);
        gui.clearGui();
    }

    /**
     * The type Character selection listener.
     */
    public class characterSelectionListener implements ActionListener {
        /**
         * The Type.
         */
        private guiItems type;

        /**
         * Instantiates a new Character selection listener.
         *
         * @param type the type
         */
        public characterSelectionListener(guiItems type) {
            this.type = type;
        }

        /**
         * Action performed.
         *
         * @param actionEvent the action event
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(time <= 1){return;}
            Map<guiItems,Component> aux = gui.getComponentsOnScreen();
            boolean terry = false, andy = false, mai = false;
            switch (type){
                case SELECT_TERRY:
                    terry = true;
                    chosen_character = Playable_Character.TERRY;
                    break;
                case SELECT_ANDY:
                    andy = true;
                    chosen_character = Playable_Character.ANDY;
                    break;
                case SELECT_MAI:
                    mai = true;
                    chosen_character = Playable_Character.MAI;
                    break;
                default:
                    terry = true;
                    chosen_character = Playable_Character.TERRY;
                    break;
            }
            System.out.println("se ha seleccionado " + chosen_character.toString());
            aux.get(guiItems.TERRY_MUG).setVisible(terry);
            aux.get(guiItems.TERRY_COMBOS).setVisible(terry);
            aux.get(guiItems.ANDY_MUG).setVisible(andy);
            aux.get(guiItems.ANDY_COMBOS).setVisible(andy);
            aux.get(guiItems.MAI_MUG).setVisible(mai);
            aux.get(guiItems.MAI_COMBOS).setVisible(mai);
        }
    }

    /**
     * The type Map selection listener.
     */
    public class mapSelectionListener implements ActionListener {
        /**
         * The Type.
         */
        private guiItems type;

        /**
         * Instantiates a new Map selection listener.
         *
         * @param type the type
         */
        public mapSelectionListener(guiItems type) {
            this.type = type;
        }

        /**
         * Action performed.
         *
         * @param actionEvent the action event
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(time <= 1){return;}
            Map<guiItems,Component> aux = gui.getComponentsOnScreen();
            boolean usa = false, china = false, australia = false;
            switch (type){
                case SELECT_USA:
                    usa = true;
                    chosen_map = Scenario_type.USA;
                    break;
                case SELECT_CHINA:
                    china = true;
                    chosen_map = Scenario_type.CHINA;
                    break;
                case SELECT_AUSTRALIA:
                    australia = true;
                    chosen_map = Scenario_type.AUSTRALIA;
                    break;
                default:
                    usa = true;
                    chosen_map = Scenario_type.USA;
                    break;
            }
            System.out.println("se ha seleccionado " + chosen_map.toString());
            aux.get(guiItems.AUSTRALIA_MAP).setVisible(australia);
            aux.get(guiItems.CHINA_MAP).setVisible(china);
            aux.get(guiItems.USA_MAP).setVisible(usa);
        }
    }

    /**
     * Is host boolean.
     *
     * @return the boolean
     */
    public boolean isHost() {
        return isHost;
    }

    /**
     * Sets host.
     *
     * @param host the host
     */
    public void setHost(boolean host) {
        isHost = host;
    }

    /**
     * Gets chosen character.
     *
     * @return the chosen character
     */
    public Playable_Character getChosen_character() {
        return chosen_character;
    }

    /**
     * Sets chosen character.
     *
     * @param chosen_character the chosen character
     */
    public void setChosen_character(Playable_Character chosen_character) {
        this.chosen_character = chosen_character;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets rival name.
     *
     * @return the rival name
     */
    public String getRivalName() {
        return rivalName;
    }

    /**
     * Sets rival name.
     *
     * @param rivalName the rival name
     */
    public void setRivalName(String rivalName) {
        this.rivalName = rivalName;
    }

    /**
     * Is all ok boolean.
     *
     * @return the boolean
     */
    public boolean isAllOk() {
        return allOk;
    }

    /**
     * Sets all ok.
     *
     * @param allOk the all ok
     */
    public void setAllOk(boolean allOk) {
        this.allOk = allOk;
    }

    /**
     * Gets con to client.
     *
     * @return the con to client
     */
    public connection getConToClient() {
        return conToClient;
    }

    /**
     * Sets con to client.
     *
     * @param conToClient the con to client
     */
    public void setConToClient(connection conToClient) {
        this.conToClient = conToClient;
    }
}
