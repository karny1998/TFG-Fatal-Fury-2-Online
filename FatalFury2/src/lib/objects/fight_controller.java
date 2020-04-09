package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Round_Results;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    // Controlador del enemigo
    character_controller enemy;
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
    // Interfaz
    displayTimer timer = new displayTimer();
    Image bar_player, bar_enemy;
    Image name_player, name_enemy;
    Image indicator_player, indicator_enemy;

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
        currentRound = new round(player,enemy,roundTime);
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
        currentRound = new round(player,enemy,roundTime);
        currentRound.addListener(this);
        currentRound.startRound(hasEnd);
    }

    // Añadir puntuación en base a resultados de rondas
    public void updateScores(Round_Results r) {
        if (r == Round_Results.WIN) {
            ++playerScore;
        } else if (r == Round_Results.LOSE) {
            ++enemyScore;
        } else if (r == Round_Results.TIE) {
            ++playerScore;
            ++enemyScore;
        }
    }

    // Gestión de rondas, se llama cuando la ronda actual termina
    @Override
    public void roundEnded() {
        ++roundCounter;
        results.add(currentRound.getResult());
        // Segunda ronda
        if (roundCounter == 1) {
            startNewRound(true);
        }
        // Se calcula si se necesita una tercera ronda
        else if (roundCounter == 2) {
            for (Round_Results r : results) {
                updateScores(r);
            }
            // Uno de los dos ha ganado
            if (Math.abs(playerScore - enemyScore) == 2) {
                playerWin = (playerScore == 2);
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
            hasEnded = true;
        }
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

}
