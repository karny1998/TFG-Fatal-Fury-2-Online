package database.models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.sql.Date;
import java.time.LocalDateTime;

/**
 * The type Ranked game.
 */
@Entity(name="RANKEDGAME")
@DiscriminatorValue(value = "RankedGame")
public class RankedGame extends Game{

    /**
     * Instantiates a new Ranked game.
     */
    public RankedGame(){}

    /**
     * Instantiates a new Ranked game.
     *
     * @param player1 the player 1
     * @param player2 the player 2
     * @param result  the result
     */
    public RankedGame(Player player1, Player player2, String character1, String character2, int result){
        super(player1,player2,character1,character2,result);
        ranked = true;
    }

    /**
     * Instantiates a new Ranked game.
     *
     * @param player1    the player 1
     * @param player2    the player 2
     * @param result     the result
     * @param tournament the tournament
     */
    public RankedGame(Player player1, Player player2, String character1, String character2, int result, Tournament tournament){
        super(player1,player2,character1,character2,result, tournament);
        ranked = true;
    }

    /**
     * Instantiates a new Ranked game.
     *
     * @param player1      the player 1
     * @param player2      the player 2
     * @param result       the result
     * @param winnerPoints the winner points
     * @param loserPoints  the loser points
     */
    public RankedGame(Player player1, Player player2, String character1, String character2, int result, int winnerPoints, int loserPoints){
        super(player1,player2,character1,character2,result, winnerPoints, loserPoints);
        ranked = true;
    }

    /**
     * Instantiates a new Ranked game.
     *
     * @param player1 the player 1
     * @param player2 the player 2
     * @param date    the date
     * @param result  the result
     */
    public RankedGame(Player player1, Player player2, String character1, String character2, LocalDateTime date, int result){
        super(player1,player2,character1,character2,date, result);
        ranked = true;
    }

    /**
     * Instantiates a new Ranked game.
     *
     * @param player1    the player 1
     * @param player2    the player 2
     * @param date       the date
     * @param result     the result
     * @param tournament the tournament
     */
    public RankedGame(Player player1, Player player2, String character1, String character2, LocalDateTime date, int result, Tournament tournament){
        super(player1,player2,character1,character2,date,result, tournament);
        ranked = true;
    }

    /**
     * Instantiates a new Ranked game.
     *
     * @param player1      the player 1
     * @param player2      the player 2
     * @param date         the date
     * @param result       the result
     * @param winnerPoints the winner points
     * @param loserPoints  the loser points
     */
    public RankedGame(Player player1, Player player2, String character1, String character2, LocalDateTime date, int result, int winnerPoints, int loserPoints){
        super(player1,player2,character1,character2,date,result,winnerPoints,loserPoints);
        ranked = true;
    }
}
