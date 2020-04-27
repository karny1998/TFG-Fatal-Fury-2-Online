package lib.objects;

import javafx.util.Pair;
import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.Enums.moods;
import lib.objects.ia_processors.processor_life_round_based;

public class ia_loader {
    public ia_loader(){}

    public static Pair<ia_processor,russian_roulette> loadIA(Playable_Character c, moods mood[]){
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
        return new Pair<>(new processor_life_round_based(), rr);
    }
}
