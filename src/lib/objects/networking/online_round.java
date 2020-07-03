package lib.objects.networking;

import lib.objects.character_controller;
import lib.objects.round;
import lib.objects.score;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class online_round extends round {
    private static network_manager networking;
    private connection con;
    private boolean isServer = false;

    /**
     * Instantiates a new Round.
     *
     * @param p    the p
     * @param e    the e
     * @param time the time
     * @param sP   the s p
     * @param sE   the s e
     */
    public online_round(character_controller p, character_controller e, int time, score sP, score sE, connection con, boolean isServer) {
        super(p, e, time, sP, sE);
        this.con = con;
        this.isServer = isServer;
        //networking = new network_manager(this, sc, ad, isServer);
        //networking.start();
    }

    private class network_manager extends Thread{
        private DatagramSocket socket;
        private InetAddress address;
        private boolean isServer = false;
        private boolean stop = false;
        private online_round currentRound;

        private final Thread thread;

        private network_manager(online_round currentRound, DatagramSocket sc, InetAddress ad, boolean isServer) {
            this.thread = new Thread(this);
            this.socket = sc;
            this.address = ad;
            this.isServer = isServer;
            this.currentRound = currentRound;
        }

        @Override
        public void start(){
            this.start();
        }

        public synchronized void doStop() {
            this.stop = true;
        }

        private synchronized boolean keepRunning() {
            return this.stop == false;
        }

        @Override
        public void run(){
            while(keepRunning()) {
                // gestion de la conexión
            }
        }
    }
}
