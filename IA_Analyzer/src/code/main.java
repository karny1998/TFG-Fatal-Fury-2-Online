public class main {
    public static void main(String[] args){
        boolean simple = false;
        stadistics s = new stadistics("q_learning_statsAleatoryRegresionYes6.xml");
        s.printEfectivity(simple);
        s.printAccumulatedReward(simple);
        s.printStadistics(simple);
        s.printStadistics2(simple);
        s.printAccumulatedWinsGeneral();
    }
}
