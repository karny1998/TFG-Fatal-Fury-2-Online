package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Round_Results;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// Clase que representa el controlador encargado de la gestión de una pelea
public class fight_controller implements roundListener {
    // Segundos que dura una ronda
    final int roundTime = 90;
    // Path
    String path = "assets/sprites/fight_interface/";
    // Ronda actual de la pelea
    round currentRound;
    // Lista de resultados de todas las rondas
    List<Round_Results> results;
    // Controlador del usuario
    character_controller player;
    // Score p1
    score scorePlayer = new score();
    // Controlador del enemigo
    character_controller enemy;
    // Score p2
    score scoreEnemy = new score();
    // Número de rondas finalizadas
    int roundCounter;
    // Puntos de ronda ganados
    int playerScore = 0;
    int enemyScore = 0;
    // Vida antigua de los jugadores
    int playerOldHp;
    int enemyOldHp;
    // Resultado de la pelea (true = victoria)
    boolean playerWin;
    // Ha acabado la pelea (true = acabada)
    boolean hasEnded;
    // No hay timer
    boolean noTimer;
    // Pelea del mismo personaje
    boolean mirrorFight;
    // Si es contra IA
    boolean vsIa = false;
    // Interfaz
    displayTimer timer = new displayTimer();
    Image bar_player, bar_enemy;
    Image name_player, name_enemy;
    Image indicator_player, indicator_enemy;
    roundIndicators bubbles = new roundIndicators();

    // Constructor (empieza la primera ronda)
    public fight_controller(character_controller p, character_controller e) {
        player = p;
        enemy = e;
        mirrorFight = player.getPlayer().getCharac() == enemy.getPlayer().getCharac();
        roundCounter = 0;
        playerScore = 0;
        enemyScore = 0;
        results = new ArrayList<>();
        hasEnded = false;
        noTimer = false;
        bar_player = new ImageIcon(path+"/hp_bars/player1_frame.png").getImage();
        bar_enemy = new ImageIcon(path+"/hp_bars/player2_frame.png").getImage();
        switch (player.getPlayer().getCharac()) {
            case TERRY:
                name_player = new ImageIcon(path+"/char_names/terry_blue.png").getImage();
                break;
            case MAI:
                break;
            case ANDY:
                break;
        }
        switch (enemy.getPlayer().getCharac()) {
            case TERRY:
                if (mirrorFight) { name_enemy = new ImageIcon(path+"/char_names/terry_red.png").getImage(); }
                else { name_enemy = new ImageIcon(path+"/char_names/terry_blue.png").getImage(); }
                break;
            case MAI:
                break;
            case ANDY:
                break;
        }
        indicator_player = new ImageIcon(path+"/1p.png").getImage();
        indicator_enemy = new ImageIcon(path+"/2p.png").getImage();
        currentRound = new round(player,enemy,roundTime, scorePlayer, scoreEnemy);
        currentRound.addListener(this);
        currentRound.startRound(true);
    }

    // Parar la pelea
    public void pauseFight() {
        currentRound.pause();
    }

    // Retomar la pelea
    public void resumeFight() {
        currentRound.resume();
    }

    // Empezar ronda nueva
    void startNewRound(boolean hasEnd) {
        player.reset();
        enemy.reset();
        currentRound = new round(player,enemy,roundTime, scorePlayer, scoreEnemy);
        currentRound.addListener(this);
        currentRound.startRound(hasEnd);
    }

    // Añadir puntuación en base a resultados de rondas
    public void updateScores(Round_Results r) {
        if (r == Round_Results.WIN) {
            ++playerScore;
        } else if (r == Round_Results.LOSE) {
            ++enemyScore;
        }
    }

    // Gestión de rondas, se llama cuando la ronda actual termina
    @Override
    public void roundEnded() {
        // Gestión puntos
        if(currentRound.getResult() == Round_Results.WIN){
            scorePlayer.applyBonus(currentRound.getPlayer().getPlayer().getLife(),currentRound.getTimeLeft());
        }
        else if (currentRound.getResult() == Round_Results.LOSE){
            scoreEnemy.applyBonus(currentRound.getEnemy().getPlayer().getLife(),currentRound.getTimeLeft());
        }

        ++roundCounter;
        results.add(currentRound.getResult());
        // Segunda ronda
        if (roundCounter == 1) {
            Round_Results lastResult = results.get(0);
            updateScores(lastResult);
            startNewRound(true);
        }
        // Se calcula si se necesita una tercera ronda
        else if (roundCounter == 2) {
            Round_Results lastResult = results.get(1);
            updateScores(lastResult);
            // Uno de los dos ha ganado
            if (Math.abs(playerScore - enemyScore) == 2) {
                playerWin = (playerScore == 2);
                endAudio();
                hasEnded = true;
            }
            // Se necesita tercera ronda
            else {
                startNewRound(true);
            }
        }
        // Se calcula si se necesita ronda extra, sin tiempo límite
        else if (roundCounter == 3){
            Round_Results lastResult = results.get(2);
            updateScores(lastResult);
            // Uno de los dos ha ganado
            if (playerScore != enemyScore) {
                playerWin = (playerScore > enemyScore);
                endAudio();
                hasEnded = true;
            }
            // Se necesita ronda extra
            else {
                startNewRound(false);
                noTimer = true;
            }
        }
        // Resultado de la ronda extra
        else {
            Round_Results lastResult = results.get(3);
            updateScores(lastResult);
            playerWin = (playerScore > enemyScore);
            endAudio();
            hasEnded = true;
        }
    }

    // Reset del audio
    private void endAudio() {
        player.getPlayer().voices.endCharacterVoices();
        enemy.getPlayer().voices.endCharacterVoices();
    }

    // Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        // TIMER
        if (noTimer) {
            screenObjects.remove(Item_Type.TIMER2);
            List<screenObject> timerObjects = timer.getTimer();
            screenObjects.put(Item_Type.TIMER1,timerObjects.get(0));
            screenObjects.put(Item_Type.TIMERFRAME,timerObjects.get(1));
        }
        else {
            List<screenObject> timerObjects = timer.getTimer(currentRound.getTimeLeft());
            screenObjects.put(Item_Type.TIMER1,timerObjects.get(0));
            screenObjects.put(Item_Type.TIMER2,timerObjects.get(1));
            screenObjects.put(Item_Type.TIMERFRAME,timerObjects.get(2));
        }
        // FRAMES DE BARRAS DE VIDA
        screenObjects.put(Item_Type.HPBAR1,new screenObject(136,58,414,30,bar_player, Item_Type.HPBAR1));
        screenObjects.put(Item_Type.HPBAR2,new screenObject(730,58,414,30,bar_enemy, Item_Type.HPBAR2));
        // NOMBRES DE LOS PERSONAJES
        screenObjects.put(Item_Type.NAME1,new screenObject(136,89,414,30,name_player, Item_Type.NAME1));
        screenObjects.put(Item_Type.NAME2,new screenObject(730,89,414,30,name_enemy, Item_Type.NAME2));
        // 1P Y 2P
        screenObjects.put(Item_Type.INDICATOR1,new screenObject(136,20,90,38,indicator_player, Item_Type.INDICATOR1));
        screenObjects.put(Item_Type.INDICATOR2,new screenObject(730,20,90,38,indicator_enemy, Item_Type.INDICATOR2));
        // BURBUJAS DE RONDA
        boolean point1_player = false, point2_player = false, point1_enemy = false, point2_enemy = false;
        if (playerScore >= 1) { point1_player = true; }
        if (playerScore >= 2) { point2_player = true; }
        if (enemyScore >= 1) { point1_enemy = true; }
        if (enemyScore >= 2) { point2_enemy = true; }
        List<screenObject> playerBubbles = bubbles.getFramesPlayer1(point1_player,point2_player);
        List<screenObject> enemyBubbles = bubbles.getFramesPlayer2(point1_enemy,point2_enemy);
        screenObjects.put(Item_Type.BUBBLE1,playerBubbles.get(0));
        screenObjects.put(Item_Type.BUBBLE2,playerBubbles.get(1));
        screenObjects.put(Item_Type.BUBBLE3,enemyBubbles.get(0));
        screenObjects.put(Item_Type.BUBBLE4,enemyBubbles.get(1));
        // RONDA
        currentRound.getAnimation(screenObjects);
    }

    // Dibujar barras de vida
    void drawHpBarPlayer(Graphics2D g) {
        // x = 140, y = 62, w = 406, h = 22
        g.setColor(Color.YELLOW);
        int actualHP = player.getPlayer().getLife();
        if (actualHP == 100) {
            g.fillRect(140,62,406,22);
        }
        else {
            int w = 407 * actualHP / 100;
            g.fillRect(140,62,w,22);
        }
    }
    void drawHpBarEnemy(Graphics2D g) {
        // x = 734, y = 52, w = 406, h = 22
        g.setColor(Color.BLACK);
        int actualHP = enemy.getPlayer().getLife();
        if (actualHP != 100) {
            int damage = 100 - actualHP;
            int w = 407 * damage / 100;
            g.fillRect(734,62,w,22);
        }
    }

    // Devuelve si ha terminado la pelea
    public boolean getEnd() {
        return hasEnded;
    }

    public boolean isVsIa() {
        return vsIa;
    }

    public void setVsIa(boolean vsIa) {
        this.vsIa = vsIa;
    }

    public int getRoundTime() {
        return roundTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(round currentRound) {
        this.currentRound = currentRound;
    }

    public List<Round_Results> getResults() {
        return results;
    }

    public void setResults(List<Round_Results> results) {
        this.results = results;
    }

    public character_controller getPlayer() {
        return player;
    }

    public void setPlayer(character_controller player) {
        this.player = player;
    }

    public score getScorePlayer() {
        return scorePlayer;
    }

    public void setScorePlayer(score scorePlayer) {
        this.scorePlayer = scorePlayer;
    }

    public character_controller getEnemy() {
        return enemy;
    }

    public void setEnemy(character_controller enemy) {
        this.enemy = enemy;
    }

    public score getScoreEnemy() {
        return scoreEnemy;
    }

    public void setScoreEnemy(score scoreEnemy) {
        this.scoreEnemy = scoreEnemy;
    }

    public int getRoundCounter() {
        return roundCounter;
    }

    public void setRoundCounter(int roundCounter) {
        this.roundCounter = roundCounter;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getEnemyScore() {
        return enemyScore;
    }

    public void setEnemyScore(int enemyScore) {
        this.enemyScore = enemyScore;
    }

    public int getPlayerOldHp() {
        return playerOldHp;
    }

    public void setPlayerOldHp(int playerOldHp) {
        this.playerOldHp = playerOldHp;
    }

    public int getEnemyOldHp() {
        return enemyOldHp;
    }

    public void setEnemyOldHp(int enemyOldHp) {
        this.enemyOldHp = enemyOldHp;
    }

    public boolean isPlayerWin() {
        return playerWin;
    }

    public void setPlayerWin(boolean playerWin) {
        this.playerWin = playerWin;
    }

    public boolean isHasEnded() {
        return hasEnded;
    }

    public void setHasEnded(boolean hasEnded) {
        this.hasEnded = hasEnded;
    }

    public boolean isNoTimer() {
        return noTimer;
    }

    public void setNoTimer(boolean noTimer) {
        this.noTimer = noTimer;
    }

    public boolean isMirrorFight() {
        return mirrorFight;
    }

    public void setMirrorFight(boolean mirrorFight) {
        this.mirrorFight = mirrorFight;
    }

    public displayTimer getTimer() {
        return timer;
    }

    public void setTimer(displayTimer timer) {
        this.timer = timer;
    }

    public Image getBar_player() {
        return bar_player;
    }

    public void setBar_player(Image bar_player) {
        this.bar_player = bar_player;
    }

    public Image getBar_enemy() {
        return bar_enemy;
    }

    public void setBar_enemy(Image bar_enemy) {
        this.bar_enemy = bar_enemy;
    }

    public Image getName_player() {
        return name_player;
    }

    public void setName_player(Image name_player) {
        this.name_player = name_player;
    }

    public Image getName_enemy() {
        return name_enemy;
    }

    public void setName_enemy(Image name_enemy) {
        this.name_enemy = name_enemy;
    }

    public Image getIndicator_player() {
        return indicator_player;
    }

    public void setIndicator_player(Image indicator_player) {
        this.indicator_player = indicator_player;
    }

    public Image getIndicator_enemy() {
        return indicator_enemy;
    }

    public void setIndicator_enemy(Image indicator_enemy) {
        this.indicator_enemy = indicator_enemy;
    }

}
