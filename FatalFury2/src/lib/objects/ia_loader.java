package lib.objects;

import javafx.util.Pair;
import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.Enums.ia_type;
import lib.objects.ia_processors.processor_tendencies_player_based;

public class ia_loader {
    public ia_loader(){}

    public static Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>> loadIA(Playable_Character c){
        russian_roulette rr = new russian_roulette();
        rr.addComponent(30.0, Movement.STANDING);
        rr.addComponent(60.0, Movement.WALKING_BACK);
        rr.addComponent(15.0, Movement.HARD_PUNCH);
        rr.addComponent(15.0, Movement.SOFT_PUNCH);
        rr.addComponent(10.0, Movement.THROW);
        rr.addComponent(10.0, Movement.JUMP_ROLL_RIGHT);
        rr.addComponent(50.0, Movement.WALKING);
        rr.addComponent(10.0, Movement.NORMAL_JUMP);
        rr.addComponent(5.0, Movement.JUMP_PUNCH_DOWN);
        rr.addComponent(5.0, Movement.JUMP_ROLL_PUNCH_DOWN);
        rr.fillRoulette();
        ia_type m[][] = {{ia_type.AGRESSIVE, ia_type.DEFENSIVE}, {ia_type.DEFENSIVE, ia_type.AGRESSIVE}, {ia_type.AGRESSIVE, ia_type.DEFENSIVE}, {ia_type.DEFENSIVE,ia_type.AGRESSIVE}};
        Double aux[][]  = {{0.35, 0.15, 0.15, 0.25, 0.2}, {0.35, 0.15, 0.15, 0.25, 0.2},{0.35, 0.15, 0.15, 0.25, 0.2},{0.35, 0.15, 0.15, 0.25, 0.2}};
        //ia_processor ips[] = {new processor_life_round_based()};
        ia_processor ips[] = {new processor_tendencies_player_based()};
        return new Pair<Pair<ia_processor[],russian_roulette>, Pair<ia_type[][], Double[][]>>(new Pair<>(ips, rr), new Pair<ia_type[][], Double[][]>(m, aux));
    }
}
