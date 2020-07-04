package lib.objects.networking;

import lib.Enums.Fight_Results;
import lib.Enums.Item_Type;
import lib.maps.scenary;
import lib.objects.character_controller;
import lib.objects.fight_controller;
import lib.objects.round;
import lib.objects.screenObject;

import javax.swing.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

public class online_fight_controller extends fight_controller {
    private connection con;
    private boolean isServer = false;
    private online_round currentRoundOnline;
    private int messageIdentifier = 3;

    public online_fight_controller(character_controller p, character_controller e, scenary s, connection con, boolean isServer, int mI){
        super(p, e, s);
        currentRoundOnline = new online_round(player,enemy,roundTime, scorePlayer, scoreEnemy, con, isServer, messageIdentifier);
        currentRound = currentRoundOnline;
        this.con = con;
        this.isServer = isServer;
        this.messageIdentifier = mI;
        showIntro();
    }

    @Override
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        fight_management(screenObjects);
        // RONDA
        currentRoundOnline.getAnimation(screenObjects);
    }

    @Override
    public void startNewRound(boolean hasEnd) {
        currentRound = new round(player,enemy,roundTime, scorePlayer, scoreEnemy);
        currentRound.addListener(this);
        currentRound.startRound(hasEnd);
    }
}
