package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.Enums.Round_Results;
import lib.sound.audio_manager;
import lib.sound.fight_audio;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static lib.Enums.Item_Type.SHADOW_1;
import static lib.Enums.Item_Type.SHADOW_2;

interface roundListener {
    void roundEnded();
}

public class round {
    // Tiempo restante hasta final de ronda
    private int timeLeft;
    // Controlador del usuario
    private character_controller player;
    // Anterior movimiento del jugador
    private Movement player_old_state = Movement.STANDING;
    // Controlador del enemigo
    private character_controller enemy;
    // Anterior movimiento del enemigo
    private Movement enemy_old_state = Movement.STANDING;

    private Boolean playerHit = false;
    private Boolean enemyHit = false;
    // Resultado de la ronda
    private Round_Results result;
    // Listeners de la ronda
    private List<roundListener> listeners = new ArrayList<roundListener>();
    // Timers de la ronda
    private Timer checkLifes = new Timer(1000, null);;
    private Timer roundTimer = new Timer(1000, null);;
    // Comprobación de parada de timers
    private boolean paused1;
    private boolean paused2;
    // Score p1
    private score scorePlayer;
    // Score p2
    private  score scoreEnemy;
    // Perfect
    private boolean isPerfect;
    // Time Out
    private boolean isTimeOut;
    // Double KO
    boolean isDoubleKO;
    private boolean enemyEnded = false;
    private boolean playerEnded = false;
    private screenObject shadow1 = new screenObject(136,670,207,39,new ImageIcon(this.getClass().getResource("/assets/sprites/characters/shadow.png")).getImage(), SHADOW_1);
    private screenObject shadow2 = new screenObject(136,670,207,39,new ImageIcon(this.getClass().getResource("/assets/sprites/characters/shadow.png")).getImage(), SHADOW_2);

    private hitBox mapLimit = new hitBox(-145,0,1571,720,box_type.HURTBOX);
    private hitBox cameraLimit = new hitBox(40,0,1200,720,box_type.HURTBOX);
    private int scenaryOffset = 0;
    private int scenaryOffsetY = 0;

    public round (character_controller p, character_controller e, int time, score sP, score sE) {
        this.player = p;
        this.enemy = e;
        p.getPlayer().setMapLimit(cameraLimit);
        e.getPlayer().setMapLimit(cameraLimit);
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
            if (playerLife == 0 && enemyLife == 0) {
                isDoubleKO = true;
                result = Round_Results.TIE;
                checkLifes.stop();
                roundTimer.stop();
                for (roundListener l : listeners)
                    l.roundEnded();
            }
            else if (playerLife == 0) {
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

    // Gestion de daños
    void collidesManagement(hitBox pHurt, hitBox eHurt){
        // Estado actual de ambos
        Movement player_act_state = player.getPlayer().getState();
        Movement enemy_act_state = enemy.getPlayer().getState();

        // Si está recibiendo knockback no se calcula daños¡, ya que ya lo ha recibido
        if(player.getPlayer().inKnockback() || enemy.getPlayer().inKnockback()){
            return;
        }

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

        // Si han cambiado de estado se actualizan
        if(pStateChanged){
            player_old_state = player_act_state;
            playerHit = false;
        }
        if(eStateChanged){
            enemy_old_state = enemy_act_state;
            enemyHit = false;
        }

        // Daños de cada uno
        int dmgP = player.getPlayer().getDamage();
        int dmgE = enemy.getPlayer().getDamage();

        // Si no se han golpeado entre sí ni en hitbox ni hurtbox
        if(!hitsCollides && !playerHits && !enemyHits){
            // Se comprueba si se estaban cubriendo y les golpearon en la coverbox
            // y se aplica un knockback u otro en base al daño del ataque
            if(playerCovers){
                int dmg = enemy.getPlayer().getDamage();
                // KNOCKBACK CUBIERTO
                if(dmg > 10) {
                    player.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_HARD, pHurt, eHurt);
                }
                else{
                    player.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_SOFT, pHurt, eHurt);
                }
                player_old_state = player_act_state;
                playerHit = true;
            }
            if(enemyCovers){
                int dmg = player.getPlayer().getDamage();
                // KNOCKBACK CUBIERTO
                if(dmg > 10) {
                    enemy.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_HARD, eHurt, pHurt);
                }
                else{
                    enemy.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_SOFT, eHurt, pHurt);
                }
                enemy_old_state = enemy_act_state;
                enemyHit = true;
            }
            return;
        }

        // Si el jugador ha golpeado al rival y ha cambiado de estado, o no había golpeado aún
        if((playerHits || hitsCollides) && (pStateChanged || !playerHit)){
            playerHit = true;
            // Si se está cubriendo y el movimiento no es ser lanzado, se aplica el daño reducido
            if(enemyCovers && playerState != Movement.THROW){
                enemy.getPlayer().applyDamage((int) (dmgP*0.5));
                scorePlayer.addHit((int) (dmgP*10*0.5));
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Hit_1);
            }
            // Si no se estaba protegiendo o el ataque era el lanzamiento, se hace daño real
            else{
                enemy.getPlayer().applyDamage(dmgP);
                scorePlayer.addHit(dmgP*10);
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Hit_2);
            }
            // Si está lanzando al rival, cambia el estado del rival a ser lanzado
            if(playerState == Movement.THROW){
                enemy.getPlayer().setState(Movement.THROWN_OUT, eHurt, pHurt);
            }
            // Si estaba bloqueando se le aplica al enemigo el knockback correspondiente en base al daño
            else if(enemyState == Movement.STANDING_BLOCK || enemyState == Movement.WALKING){
                if(dmgP > 10) {
                    enemy.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_HARD, eHurt, pHurt);
                }
                else{
                    enemy.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_SOFT, eHurt, pHurt);
                }
            }
            // Si el enemigo no estaba agachado, realiza el knockback correspondiente
            else if(!enemy.getPlayer().isCrouched()){
                if(enemy.getPlayer().isJumping()){
                    enemy.getPlayer().setState(Movement.JUMP_KNOCKBACK, eHurt, pHurt);
                }
                else if(dmgP > 10) {
                    enemy.getPlayer().setState(Movement.MEDIUM_KNOCKBACK, eHurt, pHurt);
                }
                else{
                    enemy.getPlayer().setState(Movement.SOFT_KNOCKBACK, eHurt, pHurt);
                }
            }
            // Si el enemigo estaba agachado, realiza el knockback correspondiente
            else{
                if(player.getPlayer().getState() == Movement.CROUCHED_BLOCK){
                    enemy.getPlayer().setState(Movement.CROUCHED_BLOCK_KNOCKBACK, pHurt, eHurt);
                }
                else {
                    enemy.getPlayer().setState(Movement.CROUCHED_KNOCKBACK, pHurt, eHurt);
                }
            }
        }
        // Si el rival ha golpeado al rival y ha cambiado de estado, o no había golpeado aún
        if((enemyHits || hitsCollides) && (eStateChanged || !enemyHit)){
            enemyHit = true;
            // Si se está cubriendo y el movimiento no es ser lanzado, se aplica el daño reducido
            if(playerCovers && enemyState != Movement.THROW){
                player.getPlayer().applyDamage((int) (dmgE*0.5));
                scoreEnemy.addHit((int) (dmgE*10*0.5));
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Hit_1);
            }
            // Si no se estaba protegiendo o el ataque era el lanzamiento, se hace daño real
            else{
                player.getPlayer().applyDamage(dmgE);
                scoreEnemy.addHit(dmgE*10);
                audio_manager.fight.playSfx(fight_audio.sfx_indexes.Hit_2);
            }
            // Si está lanzando al jugador, cambia el estado del rival a ser lanzado
            if(enemyState == Movement.THROW){
                player.getPlayer().setState(Movement.THROWN_OUT, pHurt, eHurt);
            }
            // Si estaba bloqueando se le aplica al jugador el knockback correspondiente en base al daño
            else if(playerState == Movement.STANDING_BLOCK || playerState == Movement.WALKING){
                if(dmgE > 10) {
                    player.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_HARD, pHurt, eHurt);
                }
                else{
                    player.getPlayer().setState(Movement.STANDING_BLOCK_KNOCKBACK_SOFT, pHurt, eHurt);
                }
            }
            // Si el jugador no estaba agachado, realiza el knockback correspondiente
            else if(!player.getPlayer().isCrouched()){
                if(player.getPlayer().isJumping()){
                    player.getPlayer().setState(Movement.JUMP_KNOCKBACK, pHurt, eHurt);
                }
                else if(dmgE > 10) {
                    player.getPlayer().setState(Movement.MEDIUM_KNOCKBACK, pHurt, eHurt);
                }
                else{
                    player.getPlayer().setState(Movement.SOFT_KNOCKBACK, pHurt, eHurt);
                }
            }
            // Si el jugador estaba agachado, realiza el knockback correspondiente
            else{
                if(player.getPlayer().getState() == Movement.CROUCHED_BLOCK){
                    player.getPlayer().setState(Movement.CROUCHED_BLOCK_KNOCKBACK, pHurt, eHurt);
                }
                else {
                    player.getPlayer().setState(Movement.CROUCHED_KNOCKBACK, pHurt, eHurt);
                }
            }
        }
    }

    // Gestión de colisiones, cambios de orientación y daños entre los personajes
    void fightManagement(hitBox pHurt, hitBox eHurt){
        // Gestiona las colisiones y daños
        collidesManagement(pHurt, eHurt);
        // EL 400 ES EL ANCHO DE LA IMAGEN
        // Si se sobrepasan y alguno está mirando en las direccion incorrecta
        if((player.getPlayer().getOrientation() == -1 && enemy.getPlayer().getOrientation() == 1
           && pHurt.getX() > eHurt.getX() || player.getPlayer().getOrientation() == 1 && enemy.getPlayer().getOrientation() == -1
                && pHurt.getX() < eHurt.getX())){
            // Si el enemigo ha terminado el movimiento y el jugador no, se guarda esa info
            if(enemy.getPlayer().endedMovement() && !player.getPlayer().endedMovement()){
                enemyEnded = true;
            }
            // Si el jugador ha terminado el movimiento y el enemigo no, se guarda esa info
            else if(!enemy.getPlayer().endedMovement() && player.getPlayer().endedMovement()){
                playerEnded = true;
            }
        }
        // Si ambos personajes tienen la misma orientacion, y no están siendo lanzados, y ambos han terminado el
        // movimiento que inició el cambio de orientación
        if(player.getPlayer().getOrientation() == enemy.getPlayer().getOrientation()
            && player.getPlayer().getState() != Movement.THROWN_OUT
            && enemy.getPlayer().getState() != Movement.THROWN_OUT
            && (player.getPlayer().endedMovement() || playerEnded) && (enemy.getPlayer().endedMovement() || enemyEnded)){
            int o =  player.getPlayer().getOrientation();
            if(pHurt.getX() > eHurt.getX() && o == 1){
                enemy.getPlayer().setOrientation(-1);
                enemy.getPlayer().setX(enemy.getPlayer().getX() + 400);
            }
            else if(pHurt.getX() > eHurt.getX() && o == -1){
                player.getPlayer().setOrientation(1);
                player.getPlayer().setX(player.getPlayer().getX() - 400);
            }
            else if(pHurt.getX() < eHurt.getX() && o == 1){
                player.getPlayer().setOrientation(-1);
                player.getPlayer().setX(player.getPlayer().getX() + 400);
            }
            else if(pHurt.getX() < eHurt.getX() && o == -1){
                enemy.getPlayer().setOrientation(1);
                enemy.getPlayer().setX(enemy.getPlayer().getX() - 400);
            }
            enemyEnded = false;
            playerEnded = false;
        }
        // Si ambos personajes están mirando en la orientacion incorrecta, y no están siendo lanzados, y ambos han
        // terminado el movimiento que inició el cambio de orientación
        else if(player.getPlayer().getOrientation() == -1 && enemy.getPlayer().getOrientation() == 1
                && pHurt.getX() > eHurt.getX()
                && (player.getPlayer().endedMovement() || playerEnded || player.getPlayer().getState() == Movement.THROWN_OUT)
                && (enemy.getPlayer().endedMovement() || enemyEnded || enemy.getPlayer().getState() == Movement.THROWN_OUT)){
            if(player.getPlayer().getState() != Movement.THROWN_OUT) {
                player.getPlayer().setOrientation(1);
                player.getPlayer().setX(player.getPlayer().getX() - 400);
            }
            if(enemy.getPlayer().getState() != Movement.THROWN_OUT) {
                enemy.getPlayer().setOrientation(-1);
                enemy.getPlayer().setX(enemy.getPlayer().getX() + 400);
            }
            if(player.getPlayer().getState() != Movement.THROWN_OUT && enemy.getPlayer().getState() != Movement.THROWN_OUT) {
                enemyEnded = false;
                playerEnded = false;
            }
        }
        // Si ambos personajes están mirando en la orientacion incorrecta, y no están siendo lanzados, y ambos han
        // terminado el movimiento que inició el cambio de orientación
        else if(player.getPlayer().getOrientation() == 1 && enemy.getPlayer().getOrientation() == -1
                && pHurt.getX() < eHurt.getX()
                && (player.getPlayer().endedMovement() || playerEnded || player.getPlayer().getState() == Movement.THROWN_OUT)
                && (enemy.getPlayer().endedMovement() || enemyEnded || enemy.getPlayer().getState() == Movement.THROWN_OUT)){
            if(player.getPlayer().getState() != Movement.THROWN_OUT) {
                player.getPlayer().setOrientation(-1);
                player.getPlayer().setX(player.getPlayer().getX() + 400);
            }
            if(enemy.getPlayer().getState() != Movement.THROWN_OUT) {
                enemy.getPlayer().setOrientation(1);
                enemy.getPlayer().setX(enemy.getPlayer().getX() - 400);
            }
            if(player.getPlayer().getState() != Movement.THROWN_OUT && enemy.getPlayer().getState() != Movement.THROWN_OUT) {
                enemyEnded = false;
                playerEnded = false;
            }
        }
    }

    // Gestiona la cámara (el escenario) en base a las posiciones de los personajes
    void cameraManagement(hitBox pHurt,hitBox eHurt){
        int xP, xE;
        // La x de cada personaje en base a la oientación
        if(pHurt.getX() < eHurt.getX()){
            xP = pHurt.getX();
            xE = eHurt.getX()+eHurt.getWidth();
        }
        else{
            xE = eHurt.getX();
            xP = pHurt.getX()+pHurt.getWidth();
        }
        // Si alguno de los dos se sale por el lado derecho, y aun queda escenario por recorrer
        if(Math.min(xP,xE) > cameraLimit.getX() && Math.max(xP,xE) >= cameraLimit.getX()+cameraLimit.getWidth()
            && cameraLimit.getX()+cameraLimit.getWidth() + 40 - scenaryOffset < mapLimit.getX()+mapLimit.getWidth()){
            if(xP > xE && player.getPlayer().inDisplacement()
                || pHurt.collides(eHurt) && enemy.getPlayer().inDisplacement()){
                --scenaryOffset;
                enemy.getPlayer().setX(enemy.getPlayer().getX()-1);
            }
            else if(xP < xE && enemy.getPlayer().inDisplacement()
                    || pHurt.collides(eHurt) && player.getPlayer().inDisplacement()) {
                player.getPlayer().setX(player.getPlayer().getX()-1);
                --scenaryOffset;
            }
        }
        // Si alguno de los dos se sale por el lado izquierdo, y aun queda escenario por recorrer
        else if(Math.min(xP,xE) <= cameraLimit.getX() && Math.max(xP,xE) < cameraLimit.getX()+cameraLimit.getWidth()
                && cameraLimit.getX() - 40 - scenaryOffset > mapLimit.getX()){
            if(xP > xE && enemy.getPlayer().inDisplacement()
                    || pHurt.collides(eHurt) && enemy.getPlayer().inDisplacement()){
                player.getPlayer().setX(player.getPlayer().getX()+1);
                ++scenaryOffset;
            }
            else if( xP < xE && player.getPlayer().inDisplacement()
                    || pHurt.collides(eHurt) && player.getPlayer().inDisplacement()) {
                enemy.getPlayer().setX(enemy.getPlayer().getX()+1);
                ++scenaryOffset;
            }
        }

        // Si ambos están en el suelo, offsetY = 0
        if (player.getPlayer().getY() == 290 && enemy.getPlayer().getY() == 290){
            scenaryOffsetY = 0;
        }
        // Sino se saca en base a la mayor altura de los dos personajes y una proporción
        else{
            int hP = player.getPlayer().getY();
            int hE = enemy.getPlayer().getY();
            int yRef = Math.min(hP,hE);
            scenaryOffsetY = -11*(290-yRef)/525;
        }
    }

    // Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        hitBox pHurt = player.getPlayer().getHurtbox();
        hitBox eHurt = enemy.getPlayer().getHurtbox();

        fightManagement(pHurt,eHurt);
        cameraManagement(pHurt,eHurt);

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

    public boolean isPerfect() { return isPerfect; }

    public void setPerfect(boolean perfect) {
        isPerfect = perfect;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean TimeOut) {
        isTimeOut = TimeOut;
    }

    public boolean isDoubleKO() {
        return isDoubleKO;
    }

    public void setDoubleKO(boolean doubleKO) {
        isDoubleKO = doubleKO;
    }

    public int getScenaryOffset() {
        return scenaryOffset;
    }

    public void setScenaryOffset(int scenaryOffset) {
        this.scenaryOffset = scenaryOffset;
    }

    public Boolean getPlayerHit() {
        return playerHit;
    }

    public void setPlayerHit(Boolean playerHit) {
        this.playerHit = playerHit;
    }

    public Boolean getEnemyHit() {
        return enemyHit;
    }

    public void setEnemyHit(Boolean enemyHit) {
        this.enemyHit = enemyHit;
    }

    public boolean isEnemyEnded() {
        return enemyEnded;
    }

    public void setEnemyEnded(boolean enemyEnded) {
        this.enemyEnded = enemyEnded;
    }

    public boolean isPlayerEnded() {
        return playerEnded;
    }

    public void setPlayerEnded(boolean playerEnded) {
        this.playerEnded = playerEnded;
    }

    public screenObject getShadow1() {
        return shadow1;
    }

    public void setShadow1(screenObject shadow1) {
        this.shadow1 = shadow1;
    }

    public screenObject getShadow2() {
        return shadow2;
    }

    public void setShadow2(screenObject shadow2) {
        this.shadow2 = shadow2;
    }

    public hitBox getMapLimit() {
        return mapLimit;
    }

    public void setMapLimit(hitBox mapLimit) {
        this.mapLimit = mapLimit;
    }

    public hitBox getCameraLimit() {
        return cameraLimit;
    }

    public void setCameraLimit(hitBox cameraLimit) {
        this.cameraLimit = cameraLimit;
    }

    public int getScenaryOffsetY() {
        return scenaryOffsetY;
    }

    public void setScenaryOffsetY(int scenaryOffsetY) {
        this.scenaryOffsetY = scenaryOffsetY;
    }
}


