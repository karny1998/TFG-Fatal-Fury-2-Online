package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.Enums.Round_Results;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;

interface roundListener {
    void roundEnded();
}

public class round {
    // Tiempo restante hasta final de ronda
    int timeLeft;
    // Controlador del usuario
    character_controller player;
    // Anterior movimiento del jugador
    Movement player_old_state = Movement.STANDING;
    // Controlador del enemigo
    character_controller enemy;
    // Anterior movimiento del enemigo
    Movement enemy_old_state = Movement.STANDING;
    // Resultado de la ronda
    Round_Results result;
    // Listeners de la ronda
    private List<roundListener> listeners = new ArrayList<roundListener>();
    // Timers de la ronda
    Timer checkLifes;
    Timer roundTimer;
    // Comprobación de parada de timers
    boolean paused1;
    boolean paused2;

    public round (character_controller p, character_controller e, int time) {
        this.player = p;
        this.enemy = e;
        player_old_state = p.getPlayer().getState();
        enemy_old_state = e.getPlayer().getState();
        this.result = Round_Results.UNFINISHED;
        this.timeLeft = time;
    }

    public void addListener(roundListener toAdd) {
        listeners.add(toAdd);
    }

    // Parar la ronda
    public void pause() {
        if (roundTimer.isRunning()) {
            roundTimer.stop();
            paused1 = true;
        }
        if (checkLifes.isRunning()) {
            checkLifes.stop();
            paused2 = true;
        }
    }

    // Retomar la ronda
    public void resume() {
        if (paused1) {
            roundTimer.start();
            paused1 = false;
        }
        if (paused2) {
            checkLifes.start();
            paused2 = false;
        }
    }

    // Empezar una nueva ronda
    public void startRound(boolean hasEnd) {
        // Timer tiempo límite de ronda
        roundTimer = new Timer(1000, null);
        // Timer comprobar si un personaje se muere
        checkLifes = new Timer(15, null);
        roundTimer.addActionListener(e -> {
            if (timeLeft == 0) {
                int playerLife = player.getPlayer().getLife();
                int enemyLife = enemy.getPlayer().getLife();
                if (playerLife > enemyLife) {
                    result = Round_Results.WIN;
                } else if (enemyLife > playerLife) {
                    result = Round_Results.LOSE;
                } else {
                    result = Round_Results.TIE;
                }
                roundTimer.stop();
                checkLifes.stop();
                for (roundListener l : listeners)
                    l.roundEnded();
            } else {
                timeLeft--;
            }
        });
        checkLifes.addActionListener(e -> {
            int playerLife = player.getPlayer().getLife();
            int enemyLife = enemy.getPlayer().getLife();
            if (playerLife == 0) {
                result = Round_Results.LOSE;
                checkLifes.stop();
                roundTimer.stop();
            }
            else if (enemyLife == 0) {
                result = Round_Results.WIN;
                checkLifes.stop();
                roundTimer.stop();
                for (roundListener l : listeners)
                    l.roundEnded();
            }
        });
        if (hasEnd) {
            roundTimer.start();
            paused1 = false;
        }
        checkLifes.start();
        paused2 = false;
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
    public character_controller getPlayerControler() {
        return player;
    }

    public void setPlayerControler(user_controller player) {
        this.player = player;
    }

    public character_controller getEnemyControler() {
        return enemy;
    }

    public void setEnemyControler(enemy_controller enemy) {
        this.enemy = enemy;
    }

    public character_controller getPlayer() {
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

    public character_controller getEnemy() {
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

    public Round_Results getResult() {
        return this.result;
    }

    public void setResult(Round_Results result) {
        this.result = result;
    }
}


