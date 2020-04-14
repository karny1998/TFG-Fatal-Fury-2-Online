package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Round_Results;
import lib.Enums.Scenario_time;
import lib.Enums.Scenario_type;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

// Clase que representa el controlador encargado de la gestión de una pelea
public class fight_controller implements roundListener {
    // Segundos que dura una ronda
    final int roundTime = 90;
    // Milisegundos que aparecen los textos entre rondas
    final int announcementTime = 2000;
    // Path
    String path = "assets/sprites/fight_interface/";
    // Ronda actual de la pelea
    round currentRound;
    // Lista de resultados de todas las rondas
    List<Round_Results> results;
    // Controlador del usuario
    character_controller player;
    // Escenario
    scenary scene;
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
    // La anterior ronda fue perfect
    boolean wasPerfect;
    // La anterior ronda se acabó el tiempo
    boolean wasTimedOut;
    // Es necesaria una nueva ronda
    boolean newRound;
    // Interfaz
    displayTimer timer = new displayTimer();
    Image bar_player, bar_enemy;
    Image name_player, name_enemy;
    Image indicator_player, indicator_enemy;
    roundIndicators bubbles = new roundIndicators();
    announcerAnimation fightAnnouncement = new announcerAnimation();
    Image match_play, round_1, round_2, round_3, round_extra;
    Image perfect, you_win, you_lost, time_up, draw_game;
    // Booleanos anuncios
    boolean startedFightAnimation;
    boolean showIntro = false;
    long introTimeStamp;
    boolean showOutro = false;
    long outroTimeStamp;

    // Constructor (empieza la primera ronda)
    public fight_controller(character_controller p, character_controller e, scenary s) {
        scene = s;
        player = p;
        enemy = e;
        mirrorFight = player.getPlayer().getCharac() == enemy.getPlayer().getCharac();
        roundCounter = 0;
        playerScore = 0;
        enemyScore = 0;
        results = new ArrayList<>();
        hasEnded = false;
        wasPerfect = false;
        wasTimedOut = false;
        noTimer = false;
        startedFightAnimation = false;
        bar_player = new ImageIcon(path+"/hp_bars/player1_frame.png").getImage();
        bar_enemy = new ImageIcon(path+"/hp_bars/player2_frame.png").getImage();
        switch (player.getPlayer().getCharac()) {
            case TERRY:
                name_player = new ImageIcon(path+"/char_names/terry_blue.png").getImage();
                you_win = new ImageIcon(path+"/announcer/terry_win.png").getImage();
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
        you_lost = new ImageIcon(path+"/announcer/you_lost.png").getImage();
        time_up = new ImageIcon(path+"/announcer/time_up.png").getImage();
        draw_game = new ImageIcon(path+"/announcer/draw_game.png").getImage();
        match_play = new ImageIcon(path+"/announcer/match_play.png").getImage();
        perfect = new ImageIcon(path+"/announcer/perfect.png").getImage();
        round_1 = new ImageIcon(path+"/announcer/round1.png").getImage();
        round_2 = new ImageIcon(path+"/announcer/round2.png").getImage();
        round_3 = new ImageIcon(path+"/announcer/round3.png").getImage();
        round_extra = new ImageIcon(path+"/announcer/final_round.png").getImage();
        player.startStandBy();
        enemy.startStandBy();
        currentRound = new round(player,enemy,roundTime, scorePlayer, scoreEnemy);
        showIntro();
    }

    // Enseñar la intro de la ronda
    public void showIntro() {
        Date date = new Date();
        introTimeStamp = date.getTime();
        showIntro = true;
    }

    // Enseñar el final de la ronda
    public void showOutro() {
        Date date = new Date();
        outroTimeStamp = date.getTime();
        showOutro = true;
    }

    // Parar la pelea
    public void pauseFight() {
        currentRound.pause();
    }

    // Retomar la pelea
    public void resumeFight() {
        introTimeStamp = System.currentTimeMillis();
        outroTimeStamp = System.currentTimeMillis();
        currentRound.resume();
    }

    // Empezar ronda nueva
    void startNewRound(boolean hasEnd) {
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
        player.startStandBy();
        enemy.startStandBy();
        // Gestión puntos
        if(currentRound.getResult() == Round_Results.WIN){
            scorePlayer.applyBonus(currentRound.getPlayer().getPlayer().getLife(),currentRound.getTimeLeft());
        }
        else if (currentRound.getResult() == Round_Results.LOSE){
            scoreEnemy.applyBonus(currentRound.getEnemy().getPlayer().getLife(),currentRound.getTimeLeft());
        }
        wasPerfect = currentRound.isPerfect();
        wasTimedOut = currentRound.isTimeOut();
        ++roundCounter;
        results.add(currentRound.getResult());
        showOutro();
        // Segunda ronda
        if (roundCounter == 1) {
            Round_Results lastResult = results.get(0);
            updateScores(lastResult);
            newRound = true;
        }
        // Se calcula si se necesita una tercera ronda
        else if (roundCounter == 2) {
            Round_Results lastResult = results.get(1);
            updateScores(lastResult);
            // Uno de los dos ha ganado
            if (Math.abs(playerScore - enemyScore) == 2) {
                playerWin = (playerScore == 2);
                newRound = false;
            }
            // Se necesita tercera ronda
            else {
                newRound = true;
            }
        }
        // Se calcula si se necesita ronda extra, sin tiempo límite
        else if (roundCounter == 3){
            Round_Results lastResult = results.get(2);
            updateScores(lastResult);
            // Uno de los dos ha ganado
            if (playerScore != enemyScore) {
                playerWin = (playerScore > enemyScore);
                newRound = false;
            }
            // Se necesita ronda extra
            else {
                newRound = true;
                noTimer = true;
            }
        }
        // Resultado de la ronda extra
        else {
            Round_Results lastResult = results.get(3);
            updateScores(lastResult);
            playerWin = (playerScore > enemyScore);
            newRound = false;
        }
    }

    // Reset del audio
    private void endAudio() {
        player.getPlayer().voices.endCharacterVoices();
        enemy.getPlayer().voices.endCharacterVoices();
    }

    // Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        // ESCENARIO DE LA PELEA
        screenObjects.put(Item_Type.SCENARY_1, scene.getFrame1());
        screenObjects.put(Item_Type.SCENARY_2, scene.getFrame2());
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
        // ANUNCIOS ENTRE RONDAS
        if (showIntro) {
            Date date = new Date();
            long timePast = date.getTime() - introTimeStamp;
            if (timePast <= announcementTime && roundCounter == 0) {
                screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,match_play, Item_Type.ANNOUNCEMENT));
            }
            else if (timePast <= announcementTime * 2 && roundCounter == 0 || timePast <= announcementTime && roundCounter > 0) {
                switch (roundCounter) {
                    case 0:
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_1, Item_Type.ANNOUNCEMENT));
                        break;
                    case 1:
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_2, Item_Type.ANNOUNCEMENT));
                        break;
                    case 2:
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_3, Item_Type.ANNOUNCEMENT));
                        break;
                    case 3:
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_extra, Item_Type.ANNOUNCEMENT));
                        break;
                }
            }
            else if (timePast <= announcementTime + 700 && roundCounter > 0 || timePast <= announcementTime * 2 + 700 && roundCounter == 0) {
                if (!startedFightAnimation) {
                    startedFightAnimation = true;
                    fightAnnouncement.start();
                }
                screenObjects.put(Item_Type.ANNOUNCEMENT,fightAnnouncement.getFrame());
            }
            else {
                showIntro = false;
                screenObjects.remove(Item_Type.ANNOUNCEMENT);
                startedFightAnimation = false;
                player.endStandBy();
                enemy.endStandBy();
                startNewRound(roundCounter != 3);
            }
        }
        if (showOutro) {
            Date date = new Date();
            long timePast = date.getTime() - outroTimeStamp;
            Round_Results lastResult = results.get(results.size()-1);
            if (timePast <= announcementTime) {
                if (wasTimedOut) {
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, time_up, Item_Type.ANNOUNCEMENT));
                }
                else if (wasPerfect) {
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, perfect, Item_Type.ANNOUNCEMENT));
                }
                else if (lastResult == Round_Results.WIN) {
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_win, Item_Type.ANNOUNCEMENT));
                }
                else if (lastResult == Round_Results.LOSE) {
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_lost, Item_Type.ANNOUNCEMENT));
                }
            }
            else if (timePast <= announcementTime * 2) {
                if (lastResult == Round_Results.TIE) {
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, draw_game, Item_Type.ANNOUNCEMENT));
                }
                else if ((wasPerfect || wasTimedOut) && lastResult == Round_Results.WIN) {
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_win, Item_Type.ANNOUNCEMENT));
                }
                else if ((wasPerfect || wasTimedOut) && lastResult == Round_Results.LOSE) {
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_lost, Item_Type.ANNOUNCEMENT));
                }
                else {
                    showOutro = false;
                    screenObjects.remove(Item_Type.ANNOUNCEMENT);
                    if (newRound) {
                        player.reset();
                        enemy.reset();
                        if (roundCounter == 1) {
                            scene.setCurrentTime(Scenario_time.SUNSET);
                        }
                        else if (roundCounter >= 2) {
                            scene.setCurrentTime(Scenario_time.NIGHT);
                        }
                        enemy.setRival(player.getPlayer());
                        showIntro();
                    }
                    else {
                        endAudio();
                        hasEnded = true;
                    }

                }
            }
            else {
                showOutro = false;
                screenObjects.remove(Item_Type.ANNOUNCEMENT);
                if (newRound) {
                    player.reset();
                    enemy.reset();
                    if (roundCounter == 1) {
                        scene.setCurrentTime(Scenario_time.SUNSET);
                    }
                    else if (roundCounter >= 2) {
                        scene.setCurrentTime(Scenario_time.NIGHT);
                    }
                    enemy.setRival(player.getPlayer());
                    showIntro();
                }
                else {
                    endAudio();
                    hasEnded = true;
                }
            }
        }
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
