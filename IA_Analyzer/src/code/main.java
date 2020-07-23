public class main {
    public static void main(String[] args){
        //random().saveUpdatedHistory();
        stadistics s = new stadistics("ia_stadistics.xml");
        s.printEfectivity();
        s.printAccumulatedReward();
        s.printStadistics();
        s.printStadistics2();
    }
}
