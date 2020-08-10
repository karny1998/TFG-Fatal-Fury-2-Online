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

public class character_selection_gui {
    private online_mode_gui gui;
    private Color grey1 = new Color(33,32,57), grey2 = new Color(66,64,114),grey3 = new Color(45,48,85),
            grey4 = new Color(99,96,171), brown = new Color(140,105,57), blue = new Color(0,0,148);
    private Font f,f2,f3,f4;
    private ImageIcon terryIcon, andyIcon,  maiIcon,
            terryMug, andyMug,  maiMug, terryCombos,
            andyCombos,  maiCombos, unknownMug,
            australia, china, usa, usaIcon, chinaIcon, australiaIcon;
    private boolean isHost = false;
    private Playable_Character chosen_character = Playable_Character.TERRY;
    private Scenario_type chosen_map = Scenario_type.USA;
    private Timer timer;
    private int time = 20;
    private JTextField timeOnScreen;
    private String ip, rivalName;
    private connection conToClient;

    public character_selection_gui (online_mode_gui gui, boolean isHost, String ip, String rivalName){
        this.ip = ip;
        this.rivalName = rivalName;
        this.gui = gui;

        gui.getOnline_controller().generateConToClient(ip);
        this.conToClient = gui.getOnline_controller().getConToClient();

        try {
            f4 = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont((float)res(60));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isHost = isHost;
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
                --time;
                String aux = Integer.toString(time);
                if(time < 10){
                    aux = "0"+aux;
                }
                timeOnScreen.setText(aux);
                gui.getGui().repaint();
                if(time == 0){
                    timer.stop();
                    generateGame();
                }
            }
        });
        timer.start();
    }

    private void generateGame(){
        if(isHost) {
            boolean ok = conToClient.reliableSendString(msgID.toClient.tramits, chosen_character.toString()+":"+chosen_map.toString(), 200);
            if(!ok){
                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                gui.clearGui();
                gui.popUp("Connection lost with the rival.");
            }
            Playable_Character rivalC = null;
            ok = false;
            int i = 0;
            while(!ok && i < 200){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String msg = conToClient.receiveString(msgID.toClient.tramits);
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
            }
            gui.getOnline_controller().generateFight(isHost, chosen_character, rivalC, chosen_map);
        }
        else{
            Playable_Character rivalC = null;
            boolean ok = false;
            int i = 0;
            while(!ok && i < 200){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String msg = conToClient.receiveString(msgID.toClient.tramits);
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
            }
            ok = conToClient.reliableSendString(msgID.toClient.tramits, chosen_character.toString()+":", 200);
            if(!ok){
                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                gui.clearGui();
                gui.popUp("Connection lost with the rival.");
            }
            gui.getOnline_controller().generateFight(isHost, rivalC, chosen_character, chosen_map);
        }
    }

    private int res(int x){
        return gui.res(x);
    }

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

        JTextField rival = gui.generateSimpleTextField(rivalName, f, Color.YELLOW, new Color(0,0,0,0), 416, 451, 100, 100, false, true);

        if(isHost){
            terryM.setBounds(50, 100,312, 285);
            andyM.setBounds(50, 100,312, 285);
            maiM.setBounds(50, 100,312, 285);
            terryC.setBounds(80, 390,267, 224);
            andyC.setBounds(80, 390,267, 224);
            maiC.setBounds(80, 390,267, 224);
            unknowM.setBounds(918, 200,312, 285);

            usaMap = new JLabel(usa);
            chinaMap = new JLabel(china);
            chinaMap.setVisible(false);
            ausMap = new JLabel(australia);
            ausMap.setVisible(false);
            usaMap.setBounds(413, 130, 436,244);
            chinaMap.setBounds(413, 130, 436,244);
            ausMap.setBounds(413, 130, 436,244);
        }
        else{
            terryM.setBounds(918, 100,312, 285);
            andyM.setBounds(918, 100,312, 285);
            maiM.setBounds(918, 100,312, 285);
            terryC.setBounds(933, 405,267, 224);
            andyC.setBounds(933, 405,267, 224);
            maiC.setBounds(933, 405,267, 224);
            unknowM.setBounds(50, 200,312, 285);
        }

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
                    guiItems.USA_MAP, guiItems.AUSTRALIA_MAP, guiItems.CHINA_MAP, guiItems.BACK} ;
            comps = new Component[]{terry, andy, mai, unknowM, terryM, terryC, andyM, andyC, maiM, maiC, tm,
                    usaB, chinaB, australiaB, usaMap,ausMap,chinaMap, back};
        }
        else{
            items = new guiItems[]{guiItems.SELECT_TERRY, guiItems.SELECT_ANDY, guiItems.SELECT_MAI,
                    guiItems.UNKNOWN_MUG, guiItems.TERRY_MUG, guiItems.TERRY_COMBOS,
                    guiItems.ANDY_MUG, guiItems.ANDY_COMBOS, guiItems.MAI_MUG, guiItems.MAI_COMBOS,
                    guiItems.TIMER, guiItems.BACK} ;
            comps = new Component[]{terry, andy, mai, unknowM, terryM, terryC, andyM, andyC, maiM, maiC, tm, back};
        }

        gui.addComponents(items, comps);
        gui.reloadGUI();
    }

    public class characterSelectionListener implements ActionListener {
        private guiItems type;

        public characterSelectionListener(guiItems type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
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
            aux.get(guiItems.TERRY_MUG).setVisible(terry);
            aux.get(guiItems.TERRY_COMBOS).setVisible(terry);
            aux.get(guiItems.ANDY_MUG).setVisible(andy);
            aux.get(guiItems.ANDY_COMBOS).setVisible(andy);
            aux.get(guiItems.MAI_MUG).setVisible(mai);
            aux.get(guiItems.MAI_COMBOS).setVisible(mai);
        }
    }

    public class mapSelectionListener implements ActionListener {
        private guiItems type;

        public mapSelectionListener(guiItems type) {
            this.type = type;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
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
            aux.get(guiItems.AUSTRALIA_MAP).setVisible(australia);
            aux.get(guiItems.CHINA_MAP).setVisible(china);
            aux.get(guiItems.USA_MAP).setVisible(usa);
        }
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public Playable_Character getChosen_character() {
        return chosen_character;
    }

    public void setChosen_character(Playable_Character chosen_character) {
        this.chosen_character = chosen_character;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRivalName() {
        return rivalName;
    }

    public void setRivalName(String rivalName) {
        this.rivalName = rivalName;
    }
}
