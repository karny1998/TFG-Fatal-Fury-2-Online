import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class main {
    public static void main(String[] args){
        try {
            // XML file path
            File file = new File("ia_stadistics.xml");

            // create an instance of `JAXBContext`
            JAXBContext context = JAXBContext.newInstance(history_stats.class);

            // create an instance of `Unmarshaller`
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // convert XML file to user object
            history_stats user = (history_stats) unmarshaller.unmarshal(file);

            ia_stats aux = random();
            aux.saveUpdatedHistory();

            // print user object
            System.out.println(user);


        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }

    private static ia_stats random(){
        int x = 100;
        ia_stats aux = new ia_stats();
        for(int i = 0; i < x; ++i){
            for(int j = 0; j < 4; ++j){
                round_stats r = aux.getActualFight().currentRound();
                int auasdx = (int) (Math.random()*100.0);
                System.out.println(auasdx);
                r.setBlocked_hits((int) (Math.random()*100.0));
                r.setReceived_hits((int) (Math.random()*100.0));
                r.setRealized_hits((int) (Math.random()*100.0));
                r.setRemaining_life((int) (Math.random()*100.0));
                r.setPlayer_remaining_life((int) (Math.random()*100.0));
                r.setSuccessful_hits((int) (Math.random()*100.0));
                r.setResult((int) (Math.random()*3.0));
                if(j != 3){aux.getActualFight().nextRound();}
            }
            if(i != x-1){aux.nextFight();}
        }
        return aux;
    }
}
