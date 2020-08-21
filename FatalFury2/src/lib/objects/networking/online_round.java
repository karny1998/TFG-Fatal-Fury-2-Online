package lib.objects.networking;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.objects.*;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

import static lib.Enums.Item_Type.SHADOW_1;
import static lib.Enums.Item_Type.SHADOW_2;

/**
 * The type Online round.
 */
public class online_round extends round {
    /**
     * The constant con.
     */
//private static network_manager networking;
    private connection con;
    /**
     * The Is server.
     */
    private boolean isServer = false;
    /**
     * The Message identifier.
     */
    private int messageIdentifier = 3;

    /**
     * Instantiates a new Round.
     *
     * @param p        the p
     * @param e        the e
     * @param time     the time
     * @param sP       the s p
     * @param sE       the s e
     * @param con      the con
     * @param isServer the is server
     * @param mI       the m i
     */
    public online_round(character_controller p, character_controller e, int time, score sP, score sE, connection con, boolean isServer, int mI) {
        super(p, e, time, sP, sE);
        this.con = con;
        this.isServer = isServer;
        System.out.println("Soy server: " + isServer);
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
            fightManagement(pHurt, eHurt, true);
            character p = player.getPlayer(), e = enemy.getPlayer();
            String msg = p.getState().toString() + ":" + p.getOrientation() + ":" + p.getLife() + ":" + p.getX() + ":" + p.getY() + ":" +
                    e.getState().toString() + ":" + e.getOrientation() + ":" + e.getLife() + ":" + e.getX() + ":" + e.getY() + ":" + timeLeft;
            con.sendString(messageIdentifier, msg);
            // Obtención de los frames a dibujar del jugador
            screenObject ply;
            ply = player.getAnimation(pHurt,eHurt);
            screenObjects.put(Item_Type.PLAYER, ply);
            // Obtención de los frames a dibujar del enemigo
            ply = enemy.getAnimation(eHurt,pHurt);
            screenObjects.put(Item_Type.ENEMY, ply);
        }
        else{
            character p = player.getPlayer(), e = enemy.getPlayer();
            String msg = con.receiveString(messageIdentifier);
            if(!msg.equals("") && !msg.equals("NONE")){
                System.out.println(msg);
                String aux[] = msg.split(":");
                Movement pS = Movement.valueOf(aux[0]), eS = Movement.valueOf(aux[5]);
                int pO = Integer.parseInt(aux[1]), eO = Integer.parseInt(aux[6]);
                int pL = Integer.parseInt(aux[2]), eL = Integer.parseInt(aux[7]);
                int pX = Integer.parseInt(aux[3]), eX = Integer.parseInt(aux[8]);
                int pY = Integer.parseInt(aux[4]), eY = Integer.parseInt(aux[9]);
                int time = Integer.parseInt(aux[10]);
                timeLeft = time;
                p.applyDamage(p.getLife()-pL);
                e.applyDamage(e.getLife()-eL);

                if(character.isKnockback(pS) && !p.inKnockback()){
                    p.setState(pS,pHurt,eHurt);
                }
                if(character.isKnockback(eS) && !e.inKnockback()){
                    e.setState(eS,eHurt,pHurt);
                }

                p.setOrientation(pO);
                e.setOrientation(eO);

                // Obtención de los frames a dibujar del jugador
                screenObject ply;
                ply = player.getAnimation(pHurt,eHurt);
                ply.setX(pX);
                ply.setY(pY);
                screenObjects.put(Item_Type.PLAYER, ply);
                // Obtención de los frames a dibujar del enemigo
                ply = enemy.getAnimation(eHurt,pHurt);
                ply.setX(eX);
                ply.setY(eY);
                screenObjects.put(Item_Type.ENEMY, ply);

                p.setX(pX);
                p.setY(pY);
                player.setX(pX);
                player.setY(pY);
                pHurt = player.getPlayer().getHurtbox();

                e.setX(eX);
                e.setY(eY);
                enemy.setX(pX);
                enemy.setY(pY);
                eHurt = enemy.getPlayer().getHurtbox();

                ///////////////////////////////////////////////////////////
                //fightManagement(pHurt, eHurt, false);
                ///////////////////////////////////////////////////////////
            }
        }

        cameraManagement(pHurt, eHurt);

        shadow1.setX(pHurt.getX()+pHurt.getWidth()/2 - shadow1.getWidth()/2);
        shadow2.setX(eHurt.getX()+eHurt.getWidth()/2 - shadow2.getWidth()/2);
        screenObjects.put(SHADOW_1,shadow1);
        screenObjects.put(SHADOW_2,shadow2);
    }
}