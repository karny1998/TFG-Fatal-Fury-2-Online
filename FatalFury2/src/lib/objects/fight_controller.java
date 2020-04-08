package lib.objects;

import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.Enums.Round_Results;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Clase que representa el controlador encargado de la gestión de una pelea
public class fight_controller implements roundListener {
    // Segundos que dura una ronda
    final int roundTime = 10;
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
    // Resultado de la pelea (true = victoria)
    boolean playerWin;
    // Ha acabado la pelea (true = acabada)
    boolean hasEnded;

    // Constructor (empieza la primera ronda)
    public fight_controller(character_controller p, character_controller e) {
        this.player = p;
        this.enemy = e;
        this.roundCounter = 0;
        this.playerScore = 0;
        this.enemyScore = 0;
        this.results = new ArrayList<>();
        this.hasEnded = false;
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
        currentRound.getAnimation(screenObjects);
    }

    // Devuelve si ha terminado la pelea
    public boolean getEnd() {
        return hasEnded;
    }

}
