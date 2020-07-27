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

/**
 * The type Online fight controller.
 */
public class online_fight_controller extends fight_controller {
    /**
     * The Con.
     */
    private connection con;
    /**
     * The Is server.
     */
    private boolean isServer = false;
    /**
     * The Current round online.
     */
    private online_round currentRoundOnline;
    /**
     * The Message identifier.
     */
    private int messageIdentifier = 3, /**
     * The Tramits identifier.
     */
    tramitsIdentifier = -2;
    /**
     * The Reconnecting.
     */
    private boolean reconnecting = true;
    /**
     * The F.
     */
    private Font f = null;
    /**
     * The Font stream.
     */
    private InputStream fontStream = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");

    /**
     * Instantiates a new Online fight controller.
     *
     * @param p        the p
     * @param e        the e
     * @param s        the s
     * @param con      the con
     * @param isServer the is server
     * @param mI       the m i
     * @param tID      the t id
     */
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

    /**
     * Gets animation.
     *
     * @param screenObjects the screen objects
     */
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

    /**
     * Start new round.
     *
     * @param hasEnd the has end
     */
    @Override
    public void startNewRound(boolean hasEnd) {
        currentRound = new round(player,enemy,roundTime, scorePlayer, scoreEnemy);
        currentRound.addListener(this);
        currentRound.startRound(hasEnd);
    }

    /**
     * Write direcly.
     *
     * @param g      the g
     * @param offset the offset
     */
    @Override
    public void writeDirecly(Graphics2D g, int offset){
        if(reconnecting){
            g.setFont(f);
            g.setColor(Color.YELLOW);
            g.drawString("Lost connection, please wait",200,280);
        }
    }
}
