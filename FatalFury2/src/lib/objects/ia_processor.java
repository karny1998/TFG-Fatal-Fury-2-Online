package lib.objects;

public abstract class ia_processor {
    public ia_processor(){}

    public abstract void updateRoulette(russian_roulette roulette, int lvl, character player, character enemy, int time, int round);
}
