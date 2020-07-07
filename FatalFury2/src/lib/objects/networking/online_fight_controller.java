package lib.objects.networking;

import lib.Enums.Fight_Results;
import lib.Enums.Item_Type;
import lib.maps.scenary;
import lib.objects.character_controller;
import lib.objects.fight_controller;
import lib.objects.round;
import lib.objects.screenObject;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

public class online_fight_controller extends fight_controller {
    private connection con;
    private boolean isServer = false;
    private online_round currentRoundOnline;
    private int messageIdentifier = 3, tramitsIdentifier = -2;
    private boolean reconnecting = true;
    private Font f = null;
    private InputStream fontStream = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");

    public online_fight_controller(character_controller p, character_controller e, scenary s, connection con, boolean isServer, int mI, int tID){
        super(p, e, s);
        try {
            this.f = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(60f);
        }catch (Exception ex){ex.printStackTrace();};
        currentRoundOnline = new online_round(player,enemy,roundTime, scorePlayer, scoreEnemy, con, isServer, messageIdentifier);
        currentRound = currentRoundOnline;
        this.con = con;
        this.isServer = isServer;
        this.messageIdentifier = mI;
        this.tramitsIdentifier = tID;
        showIntro();
    }

    @Override
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        if(con.isConnected()) {
            if(reconnecting){
                reconnecting = false;
                resumeFight();
            }
            fight_management(screenObjects);
            // RONDA
            currentRoundOnline.getAnimation(screenObjects);
        }
        else{
            if(!reconnecting){
                reconnecting = true;
                pauseFight();
            }
        }
    }

    @Override
    public void startNewRound(boolean hasEnd) {
        currentRound = new round(player,enemy,roundTime, scorePlayer, scoreEnemy);
        currentRound.addListener(this);
        currentRound.startRound(hasEnd);
    }

    @Override
    public void writeDirecly(Graphics2D g, int offset){
        if(reconnecting){
            g.setFont(f);
            g.setColor(Color.YELLOW);
            g.drawString("Lost connection, please wait",200,280);
        }
    }
}
