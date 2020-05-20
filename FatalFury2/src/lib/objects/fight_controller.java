package lib.objects;

import lib.Enums.Fight_Results;
import lib.Enums.Item_Type;
import lib.Enums.Round_Results;
import lib.Enums.Scenario_time;
import lib.maps.scenary;
import lib.sound.audio_manager;
import lib.sound.fight_audio;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

import static java.lang.Math.abs;
import static lib.Enums.Item_Type.*;

// Clase que representa el controlador encargado de la gestión de una pelea
public class fight_controller implements roundListener {
    // Segundos que dura una ronda
    final int roundTime = 90;
    // Milisegundos que aparecen los textos entre rondas
    final int announcementTime = 2000;
    // Milisegundos que aparecen los textos del score entre rondas
    final int scoreTime = 2000;
    // Path de sprites para la interfaz
    String path = "/assets/sprites/fight_interface";
    // Ronda actual de la pelea
    round currentRound;
    // Lista de resultados de todas las rondas
    List<Round_Results> results;
    // Controlador del usuario
    character_controller player;
    // Escenario
    scenary scene;
    // Para gestión de límites de mapa
    hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);
    // Score p1
    score scorePlayer = new score(ia_loader.dif.EASY);
    // Controlador del enemigo
    character_controller enemy;
    // Score p2
    score scoreEnemy = new score(ia_loader.dif.EASY);
    // Número de rondas finalizadas
    int roundCounter;
    // Puntos de ronda ganados
    int playerScore = 0;
    int enemyScore = 0;
    // Resultado de la pelea
    Fight_Results fight_result;
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
    // La anterior ronda fue double KO
    boolean wasDoubleKO;
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
    Image perfect, you_win, you_lost, time_up, draw_game, double_ko;
    Image terry_win, mai_win, andy_win;
    displayScores displayscores = new displayScores();
    // Booleanos anuncios
    boolean startedFightAnimation;
    boolean startedVictoryAnimation;
    boolean showIntro = false;
    long introTimeStamp;
    boolean showOutro = false;
    long outroTimeStamp;
    boolean showScores = false;
    long scoresTimestamp;
    boolean showedScore = false;
    boolean showedBonus = false;
    boolean showedLife = false;
    boolean showedTime = false;
    // Loader ia
    ia_loader.dif iaLvl = ia_loader.dif.EASY;
    // Puntos anteriores
    private int lastScore = 0;
    // Booleanos sonidos de scores
    boolean playedScore = false;
    boolean playedBonus = false;
    boolean playedLife = false;
    boolean playedTime = false;
    boolean playedTotal = false;


    boolean audio_ready = false, audio_round = false, audio_fight = false;
    boolean audio_timeOut = false, audio_perfect = false, audio_double_ko = false, audio_draw_game = false;
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
        wasDoubleKO = false;
        noTimer = false;
        startedFightAnimation = false;
        startedVictoryAnimation = false;
        bar_player = new ImageIcon(this.getClass().getResource(path+"/hp_bars/player1_frame.png")).getImage();
        bar_enemy = new ImageIcon(this.getClass().getResource(path+"/hp_bars/player2_frame.png")).getImage();
        switch (player.getPlayer().getCharac()) {
            case TERRY:
                name_player = new ImageIcon(this.getClass().getResource(path+"/char_names/terry_blue.png")).getImage();
                you_win = new ImageIcon(this.getClass().getResource(path+"/announcer/terry_win.png")).getImage();
                break;
            case MAI:
                name_player = new ImageIcon(this.getClass().getResource(path+"/char_names/mai_blue.png")).getImage();
                break;
            case ANDY:
                name_player = new ImageIcon(this.getClass().getResource(path+"/char_names/andy_blue.png")).getImage();
                break;
        }
        switch (enemy.getPlayer().getCharac()) {
            case TERRY:
                if (mirrorFight) { name_enemy = new ImageIcon(this.getClass().getResource(path+"/char_names/terry_red.png")).getImage(); }
                else { name_enemy = new ImageIcon(this.getClass().getResource(path+"/char_names/terry_blue.png")).getImage(); }
                break;
            case MAI:
                if (mirrorFight) { name_enemy = new ImageIcon(this.getClass().getResource(path+"/char_names/mai_red.png")).getImage(); }
                else { name_enemy = new ImageIcon(this.getClass().getResource(path+"/char_names/mai_blue.png")).getImage(); }
                break;
            case ANDY:
                if (mirrorFight) { name_enemy = new ImageIcon(this.getClass().getResource(path+"/char_names/andy_red.png")).getImage(); }
                else { name_enemy = new ImageIcon(this.getClass().getResource(path+"/char_names/andy_blue.png")).getImage(); }
                break;
        }
        indicator_player = new ImageIcon(this.getClass().getResource(path+"/1p.png")).getImage();
        indicator_enemy = new ImageIcon(this.getClass().getResource(path+"/2p.png")).getImage();
        you_lost = new ImageIcon(this.getClass().getResource(path+"/announcer/you_lost.png")).getImage();
        time_up = new ImageIcon(this.getClass().getResource(path+"/announcer/time_up.png")).getImage();
        draw_game = new ImageIcon(this.getClass().getResource(path+"/announcer/draw_game.png")).getImage();
        match_play = new ImageIcon(this.getClass().getResource(path+"/announcer/match_play.png")).getImage();
        perfect = new ImageIcon(this.getClass().getResource(path+"/announcer/perfect.png")).getImage();
        round_1 = new ImageIcon(this.getClass().getResource(path+"/announcer/round1.png")).getImage();
        round_2 = new ImageIcon(this.getClass().getResource(path+"/announcer/round2.png")).getImage();
        round_3 = new ImageIcon(this.getClass().getResource(path+"/announcer/round3.png")).getImage();
        round_extra = new ImageIcon(this.getClass().getResource(path+"/announcer/final_round.png")).getImage();
        double_ko = new ImageIcon(this.getClass().getResource(path+"/announcer/double_ko.png")).getImage();
        terry_win = new ImageIcon(this.getClass().getResource(path+"/announcer/terry_win.png")).getImage();
        andy_win = new ImageIcon(this.getClass().getResource(path+"/announcer/andy_win.png")).getImage();
        mai_win = new ImageIcon(this.getClass().getResource(path+"/announcer/mai_win.png")).getImage();
        player.startStandBy();
        enemy.startStandBy();
        fight_result = Fight_Results.UNFINISHED;
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

    // Enseñar la pantalal de scores
    public void showScores() {
        Date date = new Date();
        scoresTimestamp = date.getTime();
        showScores = true;
    }

    // Parar la pelea
    public void pauseFight() {
        currentRound.pause();
    }

    // Retomar la pelea
    public void resumeFight() {
        introTimeStamp = System.currentTimeMillis();
        outroTimeStamp = System.currentTimeMillis();
        scoresTimestamp = System.currentTimeMillis();
        if (showedScore) { scoresTimestamp = scoresTimestamp - scoreTime; }
        if (showedBonus) { scoresTimestamp = scoresTimestamp - scoreTime; }
        if (showedLife) { scoresTimestamp = scoresTimestamp - scoreTime; }
        if (showedTime) { scoresTimestamp = scoresTimestamp - scoreTime; }
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
    Random random = new Random();
    // Gestión de rondas, se llama cuando la ronda actual termina
    @Override
    public void roundEnded() {
        playedScore = false;
        playedBonus = false;
        playedLife = false;
        playedTime = false;
        playedTotal = false;
        player.startStandBy();
        enemy.startStandBy();
        // Gestión puntos
        lastScore = scorePlayer.getScore();
        if(currentRound.getResult() == Round_Results.WIN){
            scorePlayer.applyBonus(currentRound.getPlayer().getPlayer().getLife(),currentRound.getTimeLeft());
        }
        else if (currentRound.getResult() == Round_Results.LOSE){
            scoreEnemy.applyBonus(currentRound.getEnemy().getPlayer().getLife(),currentRound.getTimeLeft());
        }
        wasPerfect = currentRound.isPerfect();
        wasTimedOut = currentRound.isTimeOut();
        if(!wasTimedOut){
            int sonido = abs(random.nextInt() % 3);
            switch (sonido){
                case 0:
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Final_hit_1);
                    break;
                case 1:
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Final_hit_2);
                    break;
                case 2:
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Final_hit_3);
                    break;
            }

        }

        wasDoubleKO = currentRound.isDoubleKO;
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
            if (abs(playerScore - enemyScore) == 2) {
                if (playerScore == 2) {
                    fight_result = Fight_Results.PLAYER1_WIN;
                }
                else if (enemyScore == 2) {
                    fight_result = Fight_Results.PLAYER2_WIN;
                }
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
                if (playerScore > enemyScore) {
                    fight_result = Fight_Results.PLAYER1_WIN;
                }
                else {
                    fight_result = Fight_Results.PLAYER2_WIN;
                }
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
            if (playerScore > enemyScore) {
                fight_result = Fight_Results.PLAYER1_WIN;
            }
            else if (enemyScore > playerScore) {
                fight_result = Fight_Results.PLAYER2_WIN;
            }
            else {
                fight_result = Fight_Results.TIE;
            }
            newRound = false;
        }
    }

    // Asigna a screenObjects las cosas a mostrar, relacionadas con la pelea
    public void getAnimation(Map<Item_Type, screenObject> screenObjects) {
        // Actualizar valores de la ia
        if(vsIa){
            ia_controller iaAux = enemy.getIa();
            iaAux.setDif(iaLvl);
            iaAux.setRound(roundCounter+1);
            iaAux.setTime(currentRound.getTimeLeft());
            iaAux.setpWins(playerScore);
        }

        // ESCENARIO DE LA PELEA incluidos desplazamientos
        screenObject s1 = scene.getFrame1().cloneSO();
        s1.setX(s1.getX()+currentRound.getScenaryOffset());
        s1.setY(s1.getY()+currentRound.getScenaryOffsetY());
        screenObject s2 = scene.getFrame2().cloneSO();
        s2.setX(s2.getX()+currentRound.getScenaryOffset()/3);
        s2.setY(s2.getY()-currentRound.getScenaryOffsetY());
        screenObjects.put(Item_Type.SCENARY_1, s1);
        screenObjects.put(Item_Type.SCENARY_2, s2);
        // TIMER
        if (noTimer && !showOutro) {
            screenObjects.remove(Item_Type.TIMER2);
            List<screenObject> timerObjects = timer.getTimer();
            screenObjects.put(Item_Type.TIMER1,timerObjects.get(0));
            screenObjects.put(Item_Type.TIMERFRAME,timerObjects.get(1));
        }
        else {
            List<screenObject> timerObjects = timer.getTimer(currentRound.getTimeLeft());
            screenObjects.put(Item_Type.TIMER1, timerObjects.get(0));
            screenObjects.put(Item_Type.TIMER2, timerObjects.get(1));
            screenObjects.put(Item_Type.TIMERFRAME, timerObjects.get(2));
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
            List<screenObject> timerObjects;
            if (noTimer) { timerObjects = timer.getTimer(); }
            else { timerObjects = timer.getTimer(roundTime); }
            screenObjects.put(Item_Type.TIMER1,timerObjects.get(0));
            screenObjects.put(Item_Type.TIMER2,timerObjects.get(1));
            if (!noTimer) { screenObjects.put(Item_Type.TIMERFRAME, timerObjects.get(2)); }
            Date date = new Date();
            long timePast = date.getTime() - introTimeStamp;
            if (timePast <= announcementTime && roundCounter == 0) {
                if(!audio_ready){
                    audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Ready);
                    audio_ready = true;
                }
                //match play
                screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,match_play, Item_Type.ANNOUNCEMENT));
            }
            else if (timePast <= announcementTime * 2 && roundCounter == 0 || timePast <= announcementTime && roundCounter > 0) {
                //round number
                switch (roundCounter) {
                    case 0:
                        if(!audio_round){
                            audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Round_One);
                            audio_round = true;
                        }
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_1, Item_Type.ANNOUNCEMENT));
                        break;
                    case 1:
                        if(!audio_round){
                            audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Round_Two);
                            audio_round = true;
                        }
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_2, Item_Type.ANNOUNCEMENT));
                        break;
                    case 2:
                        if(!audio_round){
                            audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Round_Three);
                            audio_round = true;
                        }
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_3, Item_Type.ANNOUNCEMENT));
                        break;
                    case 3:
                        if(!audio_round){
                            audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Final_Round);
                            audio_round = true;
                        }
                        screenObjects.put(Item_Type.ANNOUNCEMENT,new screenObject(440,345,400,40,round_extra, Item_Type.ANNOUNCEMENT));
                        break;
                }
            }
            else if (timePast <= announcementTime + 700 && roundCounter > 0 || timePast <= announcementTime * 2 + 700 && roundCounter == 0) {
                if(!audio_fight){
                    audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Fight);
                    audio_fight = true;
                }
                if (!startedFightAnimation) {
                    startedFightAnimation = true;
                    fightAnnouncement.start();
                }
                screenObjects.put(Item_Type.ANNOUNCEMENT,fightAnnouncement.getFrame());
            }
            else {
                audio_ready = false;
                audio_round = false;
                audio_fight = false;
                showIntro = false;
                screenObjects.remove(Item_Type.ANNOUNCEMENT);
                startedFightAnimation = false;
                startedVictoryAnimation = false;
                player.endStandBy();
                enemy.endStandBy();
                newRound = false;
                startNewRound(roundCounter != 3);
            }
        }
        if (showOutro) {
            if (roundCounter == 4) {
                screenObjects.remove(Item_Type.TIMER2);
                List<screenObject> timerObjects = timer.getTimer();
                screenObjects.put(Item_Type.TIMER1, timerObjects.get(0));
                screenObjects.put(Item_Type.TIMERFRAME, timerObjects.get(1));
            }
            // Pose de victoria de ronda
            if (!startedVictoryAnimation) {
                switch (results.get(roundCounter-1)) {
                    // Ha ganado el jugador 1
                    case WIN:
                        if (!newRound) { player.getPlayer().setVictory(2); }
                        else { player.getPlayer().setVictory(1); }
                        enemy.getPlayer().setDefeat();
                        break;
                    // Ha ganado el jugador 2
                    case LOSE:
                        if (!newRound) { enemy.getPlayer().setVictory(2); }
                        else { enemy.getPlayer().setVictory(1); }
                        player.getPlayer().setDefeat();
                        break;
                    // Empate
                    case TIE:
                        if (!wasDoubleKO) {
                            if (!newRound) {
                                player.getPlayer().setVictory(2);
                                enemy.getPlayer().setVictory(2);
                            } else {
                                player.getPlayer().setVictory(1);
                                enemy.getPlayer().setVictory(1);
                            }
                        }
                        else {
                                player.getPlayer().setDefeat();
                                enemy.getPlayer().setDefeat();
                        }
                        break;
                }
                startedVictoryAnimation = true;
            }
            // Pos de victoria de pelea
            Date date = new Date();
            long timePast = date.getTime() - outroTimeStamp;
            Round_Results lastResult = results.get(results.size()-1);
            if (timePast <= announcementTime) {
                if (wasTimedOut) {
                    if(!audio_timeOut){
                        audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Time_Up);
                        audio_timeOut = true;
                    }
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, time_up, Item_Type.ANNOUNCEMENT));
                }
                else if (wasPerfect) {
                    if(!audio_perfect) {
                        audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Perfect);
                        audio_perfect = true;
                    }
                    screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, perfect, Item_Type.ANNOUNCEMENT));
                }
                else if (lastResult == Round_Results.WIN) {
                    if (vsIa) {
                        screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_win, Item_Type.ANNOUNCEMENT));
                    }
                    else {
                        switch (player.getPlayer().getCharac()) {
                            case TERRY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, terry_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case MAI:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, mai_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case ANDY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, andy_win, Item_Type.ANNOUNCEMENT));
                                break;
                        }
                    }
                }
                else if (lastResult == Round_Results.LOSE) {
                    if (vsIa) {
                        screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_lost, Item_Type.ANNOUNCEMENT));
                    }
                    else {
                        switch (enemy.getPlayer().getCharac()) {
                            case TERRY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, terry_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case MAI:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, mai_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case ANDY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, andy_win, Item_Type.ANNOUNCEMENT));
                                break;
                        }
                    }
                }
            }
            else if (timePast <= announcementTime * 2) {
                if (lastResult == Round_Results.TIE) {
                    if (wasDoubleKO) {
                        if (!audio_double_ko){
                            audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Double_KO);
                            audio_double_ko = true;
                        }
                        screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, double_ko, Item_Type.ANNOUNCEMENT));
                    }
                    else {
                        if(!audio_draw_game){
                            audio_manager.fight.playAnnouncer(fight_audio.announcer_indexes.Draw_Game);
                            audio_draw_game = true;
                        }
                        screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, draw_game, Item_Type.ANNOUNCEMENT));
                    }
                }
                else if ((wasPerfect || wasTimedOut) && lastResult == Round_Results.WIN) {
                    if (vsIa) {
                        screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_win, Item_Type.ANNOUNCEMENT));
                    }
                    else {
                        switch (player.getPlayer().getCharac()) {
                            case TERRY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, terry_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case MAI:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, mai_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case ANDY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, andy_win, Item_Type.ANNOUNCEMENT));
                                break;
                        }
                    }
                }
                else if ((wasPerfect || wasTimedOut) && lastResult == Round_Results.LOSE) {
                    if (vsIa) {
                        screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, you_lost, Item_Type.ANNOUNCEMENT));
                    }
                    else {
                        switch (enemy.getPlayer().getCharac()) {
                            case TERRY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, terry_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case MAI:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, mai_win, Item_Type.ANNOUNCEMENT));
                                break;
                            case ANDY:
                                screenObjects.put(Item_Type.ANNOUNCEMENT, new screenObject(440, 345, 400, 40, andy_win, Item_Type.ANNOUNCEMENT));
                                break;
                        }
                    }
                }
                else {
                    showOutro = false;
                    audio_timeOut = false;
                    audio_perfect = false;
                    audio_double_ko = false;
                    audio_draw_game = false;
                    screenObjects.remove(Item_Type.ANNOUNCEMENT);
                    if (!vsIa) {
                        if (newRound) {
                            player.reset();
                            enemy.reset();
                            currentRound.setScenaryOffset(0);
                            if (roundCounter == 1) {
                                scene.setCurrentTime(Scenario_time.SUNSET);
                            } else if (roundCounter >= 2) {
                                scene.setCurrentTime(Scenario_time.NIGHT);
                            }
                            enemy.setRival(player.getPlayer());
                            enemy.getPlayer().setMapLimit(mapLimit);
                            player.setRival(enemy.getPlayer());
                            player.getPlayer().setMapLimit(mapLimit);
                            showIntro();
                        }
                        else { hasEnded = true; }
                    }
                    else {
                        showScores();
                    }

                }
            }
            else {
                showOutro = false;
                audio_timeOut = false;
                audio_perfect = false;
                audio_double_ko = false;
                audio_draw_game = false;
                screenObjects.remove(Item_Type.ANNOUNCEMENT);
                if (!vsIa) {
                    if (newRound) {
                        player.reset();
                        enemy.reset();
                        currentRound.setScenaryOffset(0);
                        if (roundCounter == 1) {
                            scene.setCurrentTime(Scenario_time.SUNSET);
                        } else if (roundCounter >= 2) {
                            scene.setCurrentTime(Scenario_time.NIGHT);
                        }
                        enemy.setRival(player.getPlayer());
                        enemy.getPlayer().setMapLimit(mapLimit);
                        player.setRival(enemy.getPlayer());
                        player.getPlayer().setMapLimit(mapLimit);
                        showIntro();
                    }
                    else { hasEnded = true; }
                }
                else {
                    showScores();
                }
            }
        }
        if (showScores) {
            Round_Results lastResult = results.get(results.size()-1);
            screenObject objectFrame = displayscores.getFrame();
            screenObjects.put(objectFrame.getObjectType(),objectFrame);
            Date date = new Date();
            long timePast = date.getTime() - scoresTimestamp;
            int lifeScore = 0;
            int timeScore = 0;
            if (lastResult == Round_Results.WIN) {
                lifeScore = player.getPlayer().getLife() * 100;
                if (!noTimer) { timeScore = currentRound.getTimeLeft() * 100; }
            }
            int totalScore = lastScore + lifeScore + timeScore;
            if (totalScore > 99999){
                totalScore = 99999;
            }
            if (timePast <= scoreTime) {
                List<screenObject> scoreObjects = displayscores.getScore(lastScore);
                for (screenObject scoreObject : scoreObjects) {
                    screenObjects.put(scoreObject.getObjectType(),scoreObject);
                }
                if (!playedScore) {
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Move_cursor);
                    playedScore = true;
                }
            }
            else if (timePast <= scoreTime * 2) {
                showedScore = true;
                screenObject bonusObject = displayscores.getBonusTitle();
                screenObjects.put(bonusObject.getObjectType(),bonusObject);
                if (!playedBonus) {
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Move_cursor);
                    playedBonus = true;
                }
            }
            else if (timePast <= scoreTime * 3) {
                showedBonus = true;
                List<screenObject> lifeObjects = displayscores.getLife(lifeScore);
                for (screenObject lifeObject : lifeObjects) {
                    screenObjects.put(lifeObject.getObjectType(),lifeObject);
                }
                if (!playedLife) {
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Move_cursor);
                    playedLife = true;
                }
            }
            else if (timePast <= scoreTime * 4) {
                showedLife = true;
                List<screenObject> timeObjects = displayscores.getTime(timeScore);
                for (screenObject timeObject : timeObjects) {
                    screenObjects.put(timeObject.getObjectType(),timeObject);
                }
                if (!playedTime) {
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Move_cursor);
                    playedTime = true;
                }
            }
            else if (timePast <= scoreTime * 5) {
                showedTime = true;
                List<screenObject> totalObjects = displayscores.getTotal(totalScore);
                for (screenObject totalObject : totalObjects) {
                    screenObjects.put(totalObject.getObjectType(),totalObject);
                }
                if (!playedTotal) {
                    audio_manager.fight.playSfx(fight_audio.sfx_indexes.Option_selected);
                    playedTotal = true;
                }
            }
            else {
                showedScore = false;
                showedBonus = false;
                showedLife = false;
                showedTime = false;
                showScores = false;
                clearScores(screenObjects);
                if (newRound) {
                    player.reset();
                    enemy.reset();
                    currentRound.setScenaryOffset(0);
                    if (roundCounter == 1) {
                        scene.setCurrentTime(Scenario_time.SUNSET);
                    } else if (roundCounter >= 2) {
                        scene.setCurrentTime(Scenario_time.NIGHT);
                    }
                    enemy.setRival(player.getPlayer());
                    enemy.getPlayer().setMapLimit(mapLimit);
                    player.setRival(enemy.getPlayer());
                    player.getPlayer().setMapLimit(mapLimit);
                    showIntro();
                }
                else { hasEnded = true; }
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

    // Borra todos los elementos de la interfaz de score
    void clearScores(Map<Item_Type, screenObject> screenObjects) {
        Item_Type[] types = new Item_Type[]{    SCORE_TEXT, SCOREN1, SCOREN2, SCOREN3, SCOREN4, SCOREN5,
                                                 LIFE_TEXT, LIFEN1, LIFEN2, LIFEN3, LIFEN4, LIFEN5,
                                                 TIME_TEXT, TIMEN1, TIMEN2, TIMEN3, TIMEN4, TIMEN5,
                                                 TOTAL_TEXT, TOTALN1, TOTALN2, TOTALN3, TOTALN4, TOTALN5,
                                                 BONUS, SCORE_FRAME };
        for (Item_Type i : types) {
            screenObjects.remove(i);
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

    public Fight_Results getFight_result() {
        return fight_result;
    }

    public void setFight_result(Fight_Results fight_result) {
        this.fight_result = fight_result;
    }

    public ia_loader.dif getIaLvl() {
        return iaLvl;
    }

    public void setIaLvl(ia_loader.dif iaLvl) {
        this.scorePlayer = new score(iaLvl);
        this.iaLvl = iaLvl;
    }

    public hitBox getMapLimit() {
        return mapLimit;
    }

    public void setMapLimit(hitBox mapLimit) {
        this.mapLimit = mapLimit;
    }
}
