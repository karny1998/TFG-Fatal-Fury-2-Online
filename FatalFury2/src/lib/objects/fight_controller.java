package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;

import java.util.Map;

// Clase que representa el controlador encargado de la gestión de una pelea
public class fight_controller {
    // Controlador del usuario
    user_controller player;
    // Anterior movimiento del jugador
    Movement player_old_state = Movement.STANDING;
    // Controlador del enemigo
    enemy_controller enemy;
    // Anterior movimiento del enemigo
    Movement enemy_old_state = Movement.STANDING;

    public fight_controller(user_controller p, enemy_controller e){
        this.player = p;
        this.enemy = e;
        player_old_state = p.getPlayer().getState();
        enemy_old_state = e.getPlayer().getState();
    }

    // Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        // Calculo de daños y colisiones
        Movement player_act_state = player.getPlayer().getState();
        Movement enemy_act_state = enemy.getPlayer().getState();
        hitBox pHit = player.getPlayer().getHitbox();
        hitBox eHit = enemy.getPlayer().getHitbox();
        hitBox pHurt = player.getPlayer().getHurtbox();
        hitBox eHurt = enemy.getPlayer().getHurtbox();
        if(!eHit.collides(pHit)){
            if(pHit.collides(eHurt) && player_old_state != player_act_state){
                enemy.getPlayer().applyDamage(player.getPlayer().getDamage());
            }
            if(player_old_state != player_act_state){
                player_old_state = player_act_state;
            }
            if(eHit.collides(pHurt) && enemy_old_state != enemy_act_state){
                player.getPlayer().applyDamage(enemy.getPlayer().getDamage());
            }
            if(enemy_old_state != enemy_act_state){
                enemy_old_state = enemy_act_state;
            }
        }
        // Obtención de los frames a dibujar del jugador
        screenObject ply;
        if(player.getPlayer().getLife() > 0) {
            ply = player.getAnimation(pHurt.collides(eHurt));
            screenObjects.put(Item_Type.PLAYER, ply);
        }
        else{
            screenObjects.remove(Item_Type.PLAYER);
        }
        // Obtención de los frames a dibujar del enemigo
        if(enemy.getPlayer().getLife() > 0) {
            ply = enemy.getAnimation(pHurt.collides(eHurt));
            screenObjects.put(Item_Type.ENEMY, ply);
        }
        else{
            screenObjects.remove(Item_Type.ENEMY);
        }
    }

    // Getters y setters
    public user_controller getPlayerControler() {
        return player;
    }

    public void setPlayerControler(user_controller player) {
        this.player = player;
    }

    public enemy_controller getEnemyControler() {
        return enemy;
    }

    public void setEnemyControler(enemy_controller enemy) {
        this.enemy = enemy;
    }

    public user_controller getPlayer() {
        return player;
    }

    public void setPlayer(user_controller player) {
        this.player = player;
    }

    public Movement getPlayer_old_state() {
        return player_old_state;
    }

    public void setPlayer_old_state(Movement player_old_state) {
        this.player_old_state = player_old_state;
    }

    public enemy_controller getEnemy() {
        return enemy;
    }

    public void setEnemy(enemy_controller enemy) {
        this.enemy = enemy;
    }

    public Movement getEnemy_old_state() {
        return enemy_old_state;
    }

    public void setEnemy_old_state(Movement enemy_old_state) {
        this.enemy_old_state = enemy_old_state;
    }
}
