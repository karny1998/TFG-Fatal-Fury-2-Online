package lib.objects.networking;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.objects.*;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

import static lib.Enums.Item_Type.SHADOW_1;
import static lib.Enums.Item_Type.SHADOW_2;

public class online_round extends round {
    //private static network_manager networking;
    private connection con;
    private boolean isServer = false;
    private int messageIdentifier = 3;

    /**
     * Instantiates a new Round.
     *
     * @param p    the p
     * @param e    the e
     * @param time the time
     * @param sP   the s p
     * @param sE   the s e
     */
    public online_round(character_controller p, character_controller e, int time, score sP, score sE, connection con, boolean isServer, int mI) {
        super(p, e, time, sP, sE);
        this.con = con;
        this.isServer = isServer;
        this.messageIdentifier = mI;
    }

    /**
     * Gets animation.
     *
     * @param screenObjects the screen objects
     */
// Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    @Override
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        hitBox pHurt = player.getPlayer().getHurtbox();
        hitBox eHurt = enemy.getPlayer().getHurtbox();

        if(isServer) {
            fightManagement(pHurt, eHurt);
            character p = player.getPlayer(), e = enemy.getPlayer();
            String msg = p.getState().toString() + ":" + p.getOrientation() + ":" + p.getLife() + ":" + p.getX() + ":" + p.getY() + ":" +
                    e.getState().toString() + ":" + e.getOrientation() + ":" + e.getLife() + ":" + e.getX() + ":" + e.getY() + ":" + timeLeft;
            System.out.println("Se envia: " + msg);
            con.send(messageIdentifier, msg);
        }
        else{
            character p = player.getPlayer(), e = enemy.getPlayer();
            String msg = con.receive(messageIdentifier);
            if(!msg.equals("") && !msg.contains(":")){
                System.out.println(msg);
            }
            if(!msg.equals("") && !msg.equals("NONE")){
                String aux[] = msg.split(":");
                Movement pS = Movement.valueOf(aux[0]), eS = Movement.valueOf(aux[5]);
                int pO = Integer.parseInt(aux[1]), eO = Integer.parseInt(aux[6]);
                int pL = Integer.parseInt(aux[2]), eL = Integer.parseInt(aux[7]);
                int pX = Integer.parseInt(aux[3]), eX = Integer.parseInt(aux[8]);
                int pY = Integer.parseInt(aux[4]), eY = Integer.parseInt(aux[9]);
                int time = Integer.parseInt(aux[10]);
                timeLeft = time;
                p.applyDamage(p.getLife()-pL);
                p.setX(pX);p.setY(pY);
                p.setOrientation(pO);
                e.applyDamage(e.getLife()-eL);
                e.setX(eX);e.setY(eY);
                e.setOrientation(eO);
                if(character.isKnockback(pS)){
                    p.setState(pS,pHurt,eHurt);
                }
                if(character.isKnockback(eS)){
                    e.setState(eS,eHurt,pHurt);
                }
            }
            else{
                // Es posible que convenga simplemente ignorar y ya
                fightManagement(pHurt, eHurt);
            }
        }
        cameraManagement(pHurt, eHurt);

        // Obtención de los frames a dibujar del jugador
        screenObject ply;
        ply = player.getAnimation(pHurt,eHurt);
        screenObjects.put(Item_Type.PLAYER, ply);
        // Obtención de los frames a dibujar del enemigo
        ply = enemy.getAnimation(eHurt,pHurt);
        screenObjects.put(Item_Type.ENEMY, ply);

        shadow1.setX(pHurt.getX()+pHurt.getWidth()/2 - shadow1.getWidth()/2);
        shadow2.setX(eHurt.getX()+eHurt.getWidth()/2 - shadow2.getWidth()/2);
        screenObjects.put(SHADOW_1,shadow1);
        screenObjects.put(SHADOW_2,shadow2);
    }

    /*
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
    }*/
}
