package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.Enums.Round_Results;
import lib.Enums.box_type;
import lib.sound.audio_manager;
import lib.sound.fight_audio;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static lib.Enums.Item_Type.SHADOW_1;
import static lib.Enums.Item_Type.SHADOW_2;

/**
 * The interface Round listener.
 */
interface roundListener {
    /**
     * Round ended.
     */
    void roundEnded();
}

/**
 * The type Round.
 */
public class round {
    /**
     * The Time left.
     */
// Tiempo restante hasta final de ronda
    protected int timeLeft;
    /**
     * The Player.
     */
// Controlador del usuario
    protected character_controller player;
    /**
     * The Player old state.
     */
// Anterior movimiento del jugador
    protected Movement player_old_state = Movement.STANDING;
    /**
     * The Enemy.
     */
// Controlador del enemigo
    protected character_controller enemy;
    /**
     * The Enemy old state.
     */
// Anterior movimiento del enemigo
    protected Movement enemy_old_state = Movement.STANDING;

    /**
     * The Player hit.
     */
    protected Boolean playerHit = false;
    /**
     * The Enemy hit.
     */
    protected Boolean enemyHit = false;
    /**
     * The Result.
     */
// Resultado de la ronda
    protected Round_Results result;
    /**
     * The Listeners.
     */
// Listeners de la ronda
    protected List<roundListener> listeners = new ArrayList<roundListener>();
    /**
     * The Check lifes.
     */
// Timers de la ronda
    protected Timer checkLifes = new Timer(1000, null);;
    /**
     * The Round timer.
     */
    protected Timer roundTimer = new Timer(1000, null);;
    /**
     * The Paused 1.
     */
// Comprobación de parada de timers
    protected boolean paused1;
    /**
     * The Paused 2.
     */
    protected boolean paused2;
    /**
     * The Score player.
     */
// Score p1
    protected score scorePlayer;
    /**
     * The Score enemy.
     */
// Score p2
    protected  score scoreEnemy;
    /**
     * The Is perfect.
     */
// Perfect
    protected boolean isPerfect;
    /**
     * The Is time out.
     */
// Time Out
    protected boolean isTimeOut;
    /**
     * The Is double ko.
     */
// Double KO
    protected boolean isDoubleKO;
    /**
     * The Enemy ended.
     */
    protected boolean enemyEnded = false;
    /**
     * The Player ended.
     */
    protected boolean playerEnded = false;
    /**
     * The Shadow 1.
     */
    protected screenObject shadow1 = new screenObject(136,670,207,39,new ImageIcon(this.getClass().getResource("/assets/sprites/characters/shadow.png")).getImage(), SHADOW_1);
    /**
     * The Shadow 2.
     */
    protected screenObject shadow2 = new screenObject(136,670,207,39,new ImageIcon(this.getClass().getResource("/assets/sprites/characters/shadow.png")).getImage(), SHADOW_2);

    /**
     * The Map limit.
     */
    protected hitBox mapLimit = new hitBox(-145,0,1571,720, box_type.HURTBOX);
    /**
     * The Camera limit.
     */
    protected hitBox cameraLimit = new hitBox(40,0,1200,720,box_type.HURTBOX);
    /**
     * The Scenary offset.
     */
    protected int scenaryOffset = 0;
    /**
     * The Scenary offset y.
     */
    protected int scenaryOffsetY = 0;

    /**
     * Instantiates a new Round.
     *
     * @param p    the p
     * @param e    the e
     * @param time the time
     * @param sP   the s p
     * @param sE   the s e
     */
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

    /**
     * Add listener.
     *
     * @param toAdd the to add
     */
    public void addListener(roundListener toAdd) {
        listeners.add(toAdd);
    }

    /**
     * Pause.
     */
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

    /**
     * Resume.
     */
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

    /**
     * Start round.
     *
     * @param hasEnd the has end
     */
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

    /**
     * Collides management.
     *
     * @param pHurt the p hurt
     * @param eHurt the e hurt
     */
// Gestion de daños
    public void collidesManagement(hitBox pHurt, hitBox eHurt){
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

                    // REGISTRO DE INFORMACIÓN PARA ESTADÍSTICAS
                    enemy.getPlayer().getStats().getActualFight().addBlockedHit();
                    /////////////////////////////////////////////
                }
                enemy_old_state = enemy_act_state;
                enemyHit = true;
            }
            return;
        }

        // Si el jugador ha golpeado al rival y ha cambiado de estado, o no había golpeado aún
        if((playerHits || hitsCollides) && (pStateChanged || !playerHit)){
            playerHit = true;

            // REGISTRO DE INFORMACIÓN PARA ESTADÍSTICAS
            enemy.getPlayer().getStats().getActualFight().addReceivedHit();
            /////////////////////////////////////////////

            // Si se está cubriendo y el movimiento no es ser lanzado, se aplica el daño reducido
            if(enemyCovers && playerState != Movement.THROW){
                enemy.getPlayer().applyDamage((int) (dmgP*0.5));

                // REGISTRO DE INFORMACIÓN PARA ESTADÍSTICAS
                enemy.getPlayer().getStats().getActualFight().addBlockedHit();
                /////////////////////////////////////////////

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

            // REGISTRO DE INFORMACIÓN PARA ESTADÍSTICAS
            enemy.getPlayer().getStats().getActualFight().addSuccessfulHits();
            /////////////////////////////////////////////

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

    /**
     * Fight management.
     *
     * @param pHurt          the p hurt
     * @param eHurt          the e hurt
     * @param manageCollides the manage collides
     */
// Gestión de colisiones, cambios de orientación y daños entre los personajes
    public void fightManagement(hitBox pHurt, hitBox eHurt, boolean manageCollides){
        if(manageCollides) {
            // Gestiona las colisiones y daños
            collidesManagement(pHurt, eHurt);
        }
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

    /**
     * Camera management.
     *
     * @param pHurt the p hurt
     * @param eHurt the e hurt
     */
// Gestiona la cámara (el escenario) en base a las posiciones de los personajes
    public void cameraManagement(hitBox pHurt,hitBox eHurt){
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

    /**
     * Gets animation.
     *
     * @param screenObjects the screen objects
     */
// Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        hitBox pHurt = player.getPlayer().getHurtbox();
        hitBox eHurt = enemy.getPlayer().getHurtbox();

        fightManagement(pHurt,eHurt, true);
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

    /**
     * Gets time left.
     *
     * @return the time left
     */
// Getters y setters
    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * Sets time left.
     *
     * @param timeLeft the time left
     */
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public character_controller getPlayer() {
        return player;
    }

    /**
     * Sets player.
     *
     * @param player the player
     */
    public void setPlayer(character_controller player) {
        this.player = player;
    }

    /**
     * Gets player old state.
     *
     * @return the player old state
     */
    public Movement getPlayer_old_state() {
        return player_old_state;
    }

    /**
     * Sets player old state.
     *
     * @param player_old_state the player old state
     */
    public void setPlayer_old_state(Movement player_old_state) {
        this.player_old_state = player_old_state;
    }

    /**
     * Gets enemy.
     *
     * @return the enemy
     */
    public character_controller getEnemy() {
        return enemy;
    }

    /**
     * Sets enemy.
     *
     * @param enemy the enemy
     */
    public void setEnemy(character_controller enemy) {
        this.enemy = enemy;
    }

    /**
     * Gets enemy old state.
     *
     * @return the enemy old state
     */
    public Movement getEnemy_old_state() {
        return enemy_old_state;
    }

    /**
     * Sets enemy old state.
     *
     * @param enemy_old_state the enemy old state
     */
    public void setEnemy_old_state(Movement enemy_old_state) {
        this.enemy_old_state = enemy_old_state;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public Round_Results getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(Round_Results result) {
        this.result = result;
    }

    /**
     * Gets listeners.
     *
     * @return the listeners
     */
    public List<roundListener> getListeners() {
        return listeners;
    }

    /**
     * Sets listeners.
     *
     * @param listeners the listeners
     */
    public void setListeners(List<roundListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * Gets check lifes.
     *
     * @return the check lifes
     */
    public Timer getCheckLifes() {
        return checkLifes;
    }

    /**
     * Sets check lifes.
     *
     * @param checkLifes the check lifes
     */
    public void setCheckLifes(Timer checkLifes) {
        this.checkLifes = checkLifes;
    }

    /**
     * Gets round timer.
     *
     * @return the round timer
     */
    public Timer getRoundTimer() {
        return roundTimer;
    }

    /**
     * Sets round timer.
     *
     * @param roundTimer the round timer
     */
    public void setRoundTimer(Timer roundTimer) {
        this.roundTimer = roundTimer;
    }

    /**
     * Is paused 1 boolean.
     *
     * @return the boolean
     */
    public boolean isPaused1() {
        return paused1;
    }

    /**
     * Sets paused 1.
     *
     * @param paused1 the paused 1
     */
    public void setPaused1(boolean paused1) {
        this.paused1 = paused1;
    }

    /**
     * Is paused 2 boolean.
     *
     * @return the boolean
     */
    public boolean isPaused2() {
        return paused2;
    }

    /**
     * Sets paused 2.
     *
     * @param paused2 the paused 2
     */
    public void setPaused2(boolean paused2) {
        this.paused2 = paused2;
    }

    /**
     * Gets score player.
     *
     * @return the score player
     */
    public score getScorePlayer() {
        return scorePlayer;
    }

    /**
     * Sets score player.
     *
     * @param scorePlayer the score player
     */
    public void setScorePlayer(score scorePlayer) {
        this.scorePlayer = scorePlayer;
    }

    /**
     * Gets score enemy.
     *
     * @return the score enemy
     */
    public score getScoreEnemy() {
        return scoreEnemy;
    }

    /**
     * Sets score enemy.
     *
     * @param scoreEnemy the score enemy
     */
    public void setScoreEnemy(score scoreEnemy) {
        this.scoreEnemy = scoreEnemy;
    }

    /**
     * Is perfect boolean.
     *
     * @return the boolean
     */
    public boolean isPerfect() { return isPerfect; }

    /**
     * Sets perfect.
     *
     * @param perfect the perfect
     */
    public void setPerfect(boolean perfect) {
        isPerfect = perfect;
    }

    /**
     * Is time out boolean.
     *
     * @return the boolean
     */
    public boolean isTimeOut() {
        return isTimeOut;
    }

    /**
     * Sets time out.
     *
     * @param TimeOut the time out
     */
    public void setTimeOut(boolean TimeOut) {
        isTimeOut = TimeOut;
    }

    /**
     * Is double ko boolean.
     *
     * @return the boolean
     */
    public boolean isDoubleKO() {
        return isDoubleKO;
    }

    /**
     * Sets double ko.
     *
     * @param doubleKO the double ko
     */
    public void setDoubleKO(boolean doubleKO) {
        isDoubleKO = doubleKO;
    }

    /**
     * Gets scenary offset.
     *
     * @return the scenary offset
     */
    public int getScenaryOffset() {
        return scenaryOffset;
    }

    /**
     * Sets scenary offset.
     *
     * @param scenaryOffset the scenary offset
     */
    public void setScenaryOffset(int scenaryOffset) {
        this.scenaryOffset = scenaryOffset;
    }

    /**
     * Gets player hit.
     *
     * @return the player hit
     */
    public Boolean getPlayerHit() {
        return playerHit;
    }

    /**
     * Sets player hit.
     *
     * @param playerHit the player hit
     */
    public void setPlayerHit(Boolean playerHit) {
        this.playerHit = playerHit;
    }

    /**
     * Gets enemy hit.
     *
     * @return the enemy hit
     */
    public Boolean getEnemyHit() {
        return enemyHit;
    }

    /**
     * Sets enemy hit.
     *
     * @param enemyHit the enemy hit
     */
    public void setEnemyHit(Boolean enemyHit) {
        this.enemyHit = enemyHit;
    }

    /**
     * Is enemy ended boolean.
     *
     * @return the boolean
     */
    public boolean isEnemyEnded() {
        return enemyEnded;
    }

    /**
     * Sets enemy ended.
     *
     * @param enemyEnded the enemy ended
     */
    public void setEnemyEnded(boolean enemyEnded) {
        this.enemyEnded = enemyEnded;
    }

    /**
     * Is player ended boolean.
     *
     * @return the boolean
     */
    public boolean isPlayerEnded() {
        return playerEnded;
    }

    /**
     * Sets player ended.
     *
     * @param playerEnded the player ended
     */
    public void setPlayerEnded(boolean playerEnded) {
        this.playerEnded = playerEnded;
    }

    /**
     * Gets shadow 1.
     *
     * @return the shadow 1
     */
    public screenObject getShadow1() {
        return shadow1;
    }

    /**
     * Sets shadow 1.
     *
     * @param shadow1 the shadow 1
     */
    public void setShadow1(screenObject shadow1) {
        this.shadow1 = shadow1;
    }

    /**
     * Gets shadow 2.
     *
     * @return the shadow 2
     */
    public screenObject getShadow2() {
        return shadow2;
    }

    /**
     * Sets shadow 2.
     *
     * @param shadow2 the shadow 2
     */
    public void setShadow2(screenObject shadow2) {
        this.shadow2 = shadow2;
    }

    /**
     * Gets map limit.
     *
     * @return the map limit
     */
    public hitBox getMapLimit() {
        return mapLimit;
    }

    /**
     * Sets map limit.
     *
     * @param mapLimit the map limit
     */
    public void setMapLimit(hitBox mapLimit) {
        this.mapLimit = mapLimit;
    }

    /**
     * Gets camera limit.
     *
     * @return the camera limit
     */
    public hitBox getCameraLimit() {
        return cameraLimit;
    }

    /**
     * Sets camera limit.
     *
     * @param cameraLimit the camera limit
     */
    public void setCameraLimit(hitBox cameraLimit) {
        this.cameraLimit = cameraLimit;
    }

    /**
     * Gets scenary offset y.
     *
     * @return the scenary offset y
     */
    public int getScenaryOffsetY() {
        return scenaryOffsetY;
    }

    /**
     * Sets scenary offset y.
     *
     * @param scenaryOffsetY the scenary offset y
     */
    public void setScenaryOffsetY(int scenaryOffsetY) {
        this.scenaryOffsetY = scenaryOffsetY;
    }
}


