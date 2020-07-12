public class main {
    public static void main(String[] args){
        //random().saveUpdatedHistory();
        stadistics s = new stadistics("ia_stadistics.xml");
        s.printEfectivity();
        s.printAccumulatedReward();
        s.printStadistics();
    }

    private static ia_stats proof() {
        int x = 10;
        ia_stats aux = new ia_stats();
        for (int i = 0; i < x; ++i) {
            for (int j = 0; j < 4; ++j) {
                round_stats r = aux.getActualFight().currentRound();
                r.setBlocked_hits(i);
                r.setReceived_hits(i);
                r.setRealized_hits(i*2);
                r.setRemaining_life(i);
                r.setPlayer_remaining_life(100-i);
                r.setSuccessful_hits(i*2);
                r.setResult(1);
                if (j != 3) {
                    aux.getActualFight().nextRound();
                }
            }
            if (i != x - 1) {
                aux.nextFight();
            }
        }
        return aux;
    }


        private static ia_stats random(){
        int x = 100;
        ia_stats aux = new ia_stats();
        for(int i = 0; i < x; ++i){
            for(int j = 0; j < 4; ++j){
                round_stats r = aux.getActualFight().currentRound();
                r.setBlocked_hits((int) (Math.random()*100.0));
                r.setReceived_hits(r.getBlocked_hits() + (int) (Math.random()*100.0));
                r.setSuccessful_hits( (int) (Math.random()*100.0));
                r.setRealized_hits(r.getSuccessful_hits() +(int) (Math.random()*100.0));
                r.setRemaining_life((int) (Math.random()*100.0));
                r.setPlayer_remaining_life((int) (Math.random()*100.0));
                r.setRemaining_time((int) (Math.random()*90.0));
                r.setResult((int) (Math.random()*3.0));
                if(j != 3){aux.getActualFight().nextRound();}
            }
            if(i != x-1){aux.nextFight();}
        }
        return aux;
    }
}
