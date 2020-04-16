package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.Enums.Round_Results;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    Timer checkLifes = new Timer(1000, null);;
    Timer roundTimer = new Timer(1000, null);;
    // Comprobación de parada de timers
    boolean paused1;
    boolean paused2;
    // Score p1
    score scorePlayer;
    // Score p2
    score scoreEnemy;
    // Perfect
    boolean isPerfect;
    // Time Out
    boolean isTimeOut;

    public round (character_controller p, character_controller e, int time, score sP, score sE) {
        this.player = p;
        this.enemy = e;
        player_old_state = p.getPlayer().getState();
        enemy_old_state = e.getPlayer().getState();
        this.result = Round_Results.UNFINISHED;
        this.timeLeft = time;
        this.scorePlayer = sP;
        this.scoreEnemy = sE;
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
                isTimeOut = true;
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
                if (enemyLife == 100) { isPerfect = true; }
                for (roundListener l : listeners)
                    l.roundEnded();
            }
            else if (enemyLife == 0) {
                result = Round_Results.WIN;
                checkLifes.stop();
                roundTimer.stop();
                if (playerLife == 100) { isPerfect = true; }
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

    void collidesManagement(hitBox pHurt, hitBox eHurt){
        Movement player_act_state = player.getPlayer().getState();
        Movement enemy_act_state = enemy.getPlayer().getState();
        hitBox pHit = player.getPlayer().getHitbox();
        hitBox eHit = enemy.getPlayer().getHitbox();
        pHurt = player.getPlayer().getHurtbox();
        eHurt = enemy.getPlayer().getHurtbox();
        hitBox pCover = player.getPlayer().getCoverbox();
        hitBox eCover = enemy.getPlayer().getCoverbox();

        Movement playerState = player.getPlayer().getState(),
                enemyState = enemy.getPlayer().getState();

        Boolean hitsCollides = pHit.collides(eHit);
        Boolean playerHits = eHurt.collides(pHit),
                enemyHits = pHurt.collides(eHit),
                playerCovers = pCover.collides(eHit),
                enemyCovers = eCover.collides(pHit);
        Boolean pStateChanged = player_old_state != player_act_state,
                eStateChanged = enemy_old_state != enemy_act_state;

        if(pStateChanged){
            player_old_state = player_act_state;
        }
        if(eStateChanged){
            enemy_old_state = enemy_act_state;
        }

        if(!hitsCollides && !playerHits && !enemyHits){
            // TENER CUIDADO CON LO DEL STATECHANGED
            if(playerCovers && pStateChanged){
                // KNOCKBACK CUBIERTO
                player_old_state = player_act_state;
            }
            if(enemyCovers && eStateChanged){
                // KNOCKBACK CUBIERTO
                enemy_old_state = enemy_act_state;
            }
            return;
        }

        // EN CASO DE DOBLE AGARRE HAY QUE ELEGIR UN GANADOR
        // Control de daño provocado por el jugador 1
        if((playerHits || hitsCollides) && pStateChanged){
            int dmg = player.getPlayer().getDamage();
            if(enemyCovers && playerState != Movement.THROW){
                enemy.getPlayer().applyDamage((int) (dmg*0.5));
                scorePlayer.addHit((int) (dmg*10*0.5));
            }
            else{
                enemy.getPlayer().applyDamage(dmg);
                scorePlayer.addHit(dmg*10);
            }
            if(playerState == Movement.THROW){
                enemy.getPlayer().setState(Movement.THROWN_OUT, eHurt, pHurt);
            }
            else if(!enemy.getPlayer().isCrouched()){
                // JUMPING KNOCKBACK
                enemy.getPlayer().setY(320);
                if(dmg > 10) {
                    enemy.getPlayer().setState(Movement.MEDIUM_KNOCKBACK, eHurt, pHurt);
                }
                else{
                    enemy.getPlayer().setState(Movement.SOFT_KNOCKBACK, eHurt, pHurt);
                }
            }
            else{
                // JUMPING KNOCKBACK
                enemy.getPlayer().setY(320);
                enemy.getPlayer().setState(Movement.CROUCHED_KNOCKBACK, eHurt, pHurt);
            }
        }
        // Control de daño provocado por el jugador 2
        if((enemyHits || hitsCollides) && eStateChanged){
            int dmg = enemy.getPlayer().getDamage();
            if(playerCovers && enemyState != Movement.THROW){
                player.getPlayer().applyDamage((int) (dmg*0.5));
                scoreEnemy.addHit((int) (dmg*10*0.5));
            }
            else{
                player.getPlayer().applyDamage(dmg);
                scoreEnemy.addHit(dmg*10);
            }
            if(enemyState == Movement.THROW){
                player.getPlayer().setState(Movement.THROWN_OUT, eHurt, pHurt);
            }
            else if(!player.getPlayer().isCrouched()){
                // JUMPING KNOCKBACK
                player.getPlayer().setY(320);
                if(dmg > 10) {
                    player.getPlayer().setState(Movement.MEDIUM_KNOCKBACK, eHurt, pHurt);
                }
                else{
                    player.getPlayer().setState(Movement.SOFT_KNOCKBACK, eHurt, pHurt);
                }
            }
            else{
                // JUMPING KNOCKBACK
                player.getPlayer().setY(320);
                player.getPlayer().setState(Movement.CROUCHED_KNOCKBACK, eHurt, pHurt);
            }
        }
    }

    void fightManagement2(hitBox pHurt, hitBox eHurt){
        collidesManagement(pHurt, eHurt);

        // EL 400 ES EL ANCHO DE LA IMAGEN
        // Si se sobrepasan
        if(player.getPlayer().getOrientation() == -1 && enemy.getPlayer().getOrientation() == 1
                && pHurt.getX() > eHurt.getX() && player.getPlayer().endedMovement()){
            player.getPlayer().setOrientation(1);
            player.getPlayer().setX(player.getPlayer().getX()-400);
            enemy.getPlayer().setOrientation(-1);
            enemy.getPlayer().setX(enemy.getPlayer().getX()+400);
        }
        else if(player.getPlayer().getOrientation() == 1 && enemy.getPlayer().getOrientation() == -1
                && pHurt.getX() < eHurt.getX() && player.getPlayer().endedMovement()){
            player.getPlayer().setOrientation(-1);
            player.getPlayer().setX(player.getPlayer().getX()+400);
            enemy.getPlayer().setOrientation(1);
            enemy.getPlayer().setX(enemy.getPlayer().getX()-400);
        }
    }

    /*void fightManagement(hitBox pHurt, hitBox eHurt){
        // Calculo de daños y colisiones
        Movement player_act_state = player.getPlayer().getState();
        Movement enemy_act_state = enemy.getPlayer().getState();
        hitBox pHit = player.getPlayer().getHitbox();
        hitBox eHit = enemy.getPlayer().getHitbox();
        pHurt = player.getPlayer().getHurtbox();
        eHurt = enemy.getPlayer().getHurtbox();
        if(!eHit.collides(pHit)){
            if(pHit.collides(eHurt) && player_old_state != player_act_state){
                enemy.getPlayer().applyDamage(player.getPlayer().getDamage());
                scorePlayer.addHit(player.getPlayer().getDamage()*10);
                if(player.getPlayer().getState() == Movement.THROW){
                    enemy.getPlayer().setState(Movement.THROWN_OUT, eHurt, pHurt);
                }
                else if(!enemy.getPlayer().isCrouched()){
                    if(player.getPlayer().getDamage() > 10) {
                        enemy.getPlayer().setState(Movement.MEDIUM_KNOCKBACK, eHurt, pHurt);
                    }
                    else{
                        enemy.getPlayer().setState(Movement.SOFT_KNOCKBACK, eHurt, pHurt);
                    }
                }
                else{
                    enemy.getPlayer().setState(Movement.CROUCHED_KNOCKBACK, eHurt, pHurt);
                }
            }
            if(player_old_state != player_act_state){
                player_old_state = player_act_state;
            }
            if(eHit.collides(pHurt) && enemy_old_state != enemy_act_state){
                player.getPlayer().applyDamage(enemy.getPlayer().getDamage());
                scoreEnemy.addHit(enemy.getPlayer().getDamage()*10);
                if(enemy.getPlayer().getState() == Movement.THROW){
                    player.getPlayer().setState(Movement.THROWN_OUT, pHurt, eHurt);
                }
                else if(!player.getPlayer().isCrouched()){
                    if(enemy.getPlayer().getDamage() > 10) {
                        player.getPlayer().setState(Movement.MEDIUM_KNOCKBACK, pHurt, eHurt);
                    }
                    else{
                        player.getPlayer().setState(Movement.SOFT_KNOCKBACK, pHurt, eHurt);
                    }
                }
                else{
                    player.getPlayer().setState(Movement.CROUCHED_KNOCKBACK, pHurt, eHurt);
                }
            }
            if(enemy_old_state != enemy_act_state){
                enemy_old_state = enemy_act_state;
            }
        }

        // EL 400 ES EL ANCHO DE LA IMAGEN
        // Si se sobrepasan
        if(player.getPlayer().getOrientation() == -1 && enemy.getPlayer().getOrientation() == 1
                && pHurt.getX() > eHurt.getX() && player.getPlayer().endedMovement()){
            player.getPlayer().setOrientation(1);
            player.getPlayer().setX(player.getPlayer().getX()-400);
            enemy.getPlayer().setOrientation(-1);
            enemy.getPlayer().setX(enemy.getPlayer().getX()+400);
        }
        else if(player.getPlayer().getOrientation() == 1 && enemy.getPlayer().getOrientation() == -1
                && pHurt.getX() < eHurt.getX() && player.getPlayer().endedMovement()){
            player.getPlayer().setOrientation(-1);
            player.getPlayer().setX(player.getPlayer().getX()+400);
            enemy.getPlayer().setOrientation(1);
            enemy.getPlayer().setX(enemy.getPlayer().getX()-400);
        }
    }*/

    // Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        hitBox pHurt = player.getPlayer().getHurtbox();
        hitBox eHurt = enemy.getPlayer().getHurtbox();

        fightManagement2(pHurt,eHurt);

        // Obtención de los frames a dibujar del jugador
        screenObject ply;
        ply = player.getAnimation(pHurt,eHurt);
        screenObjects.put(Item_Type.PLAYER, ply);
        // Obtención de los frames a dibujar del enemigo
        ply = enemy.getAnimation(eHurt,pHurt);
        screenObjects.put(Item_Type.ENEMY, ply);
    }

    // Getters y setters
    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public character_controller getPlayer() {
        return player;
    }

    public void setPlayer(character_controller player) {
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

    public void setEnemy(character_controller enemy) {
        this.enemy = enemy;
    }

    public Movement getEnemy_old_state() {
        return enemy_old_state;
    }

    public void setEnemy_old_state(Movement enemy_old_state) {
        this.enemy_old_state = enemy_old_state;
    }

    public Round_Results getResult() {
        return result;
    }

    public void setResult(Round_Results result) {
        this.result = result;
    }

    public List<roundListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<roundListener> listeners) {
        this.listeners = listeners;
    }

    public Timer getCheckLifes() {
        return checkLifes;
    }

    public void setCheckLifes(Timer checkLifes) {
        this.checkLifes = checkLifes;
    }

    public Timer getRoundTimer() {
        return roundTimer;
    }

    public void setRoundTimer(Timer roundTimer) {
        this.roundTimer = roundTimer;
    }

    public boolean isPaused1() {
        return paused1;
    }

    public void setPaused1(boolean paused1) {
        this.paused1 = paused1;
    }

    public boolean isPaused2() {
        return paused2;
    }

    public void setPaused2(boolean paused2) {
        this.paused2 = paused2;
    }

    public score getScorePlayer() {
        return scorePlayer;
    }

    public void setScorePlayer(score scorePlayer) {
        this.scorePlayer = scorePlayer;
    }

    public score getScoreEnemy() {
        return scoreEnemy;
    }

    public void setScoreEnemy(score scoreEnemy) {
        this.scoreEnemy = scoreEnemy;
    }

    public boolean isPerfect() {
        return isPerfect;
    }

    public void setPerfect(boolean perfect) {
        isPerfect = perfect;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean TimeOut) {
        isTimeOut = TimeOut;
    }
}


