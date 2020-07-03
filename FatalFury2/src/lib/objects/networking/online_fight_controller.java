package lib.objects.networking;

import lib.Enums.Fight_Results;
import lib.maps.scenary;
import lib.objects.character_controller;
import lib.objects.fight_controller;
import lib.objects.round;

import javax.swing.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class online_fight_controller extends fight_controller {
    private connection con;
    private boolean isServer = false;

    public online_fight_controller(character_controller p, character_controller e, scenary s, connection con, boolean isServer){
        super(p, e, s);
        currentRound = new online_round(player,enemy,roundTime, scorePlayer, scoreEnemy, con, isServer);
        this.con = con;
        this.isServer = isServer;
        showIntro();
    }
}
