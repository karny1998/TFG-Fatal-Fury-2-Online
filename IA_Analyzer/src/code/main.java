public class main {
    public static void main(String[] args){
        boolean simple = true;
        stadistics s = new stadistics("ia_stadistics.xml");
        s.printEfectivity(simple);
        s.printAccumulatedReward(simple);
        s.printStadistics(simple);
        s.printStadistics2(simple);
        s.printAccumulatedWinsGeneral();
    }
}
