package lib.training;

import lib.Enums.Movement;
import lib.utils.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Agent.
 */
public class agent {
    /**
     * Tabla de valores Q, es decir, la tabla de recompensas para las acciones en cada estado.
     */
    private Double qTable[][];
    /**
     * El buffer para el experience replay, es decir, para poder "revivir" transiciones pasadas.
     */
    private List<Pair<Pair<state, Movement>, Pair<Double, state>>> experienceBuffer = new ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>>();
    /**
     * El número máximo de experiencias a tener en el buffer.
     */
    private int maxExperience = 1000;
    /**
     * El resgistro de todas las transiciones desde que comenzó el entrenamiento.
     */
    private List<Pair<Pair<state, Movement>, Pair<Double, state>>> trainingRegister = new ArrayList<>();
    /**
     * Frecuencia con la que se "reviven" experiencias.
     */
    private double experienceFrequency = 0.00;
    /**
     * The Previous state.
     */
    private state previousState = new state(-100,-100, Movement.SOFT_PUNCH, -700, -1, -1, -1, -1, false);
    /**
     * Epsilon, probabilidad de exploración.
     */
    private double epsilon = 0.5;
    /**
     * Ganma, valoración de acciones futuras.
     */
    private double ganma = 0.75;
    /**
     * Alpha, factor de aprendizaje.
     */
    private double alpha = 0.25;
    /**
     * Resultado de la acción a ejecutar.
     */
    private state result;
    /**
     * The Action to execute.
     */
    private Movement actionToExecute = Movement.STANDING;
    /**
     * The Training ended.
     */
    private boolean trainingEnded = false;

    /**
     * The Result assigned (for sincronization).
     */
    private boolean resultAssigned = false;

    /**
     * The Waiting result.
     */
    private boolean waitingResult = false;

    /**
     * The Accumulated reward.
     */
    private double accumulatedReward = 0;

    /**
     * The Optimal decision.
     */
    private double optimalDecision = 0.8;

    /**
     * The Use regression.
     */
    private boolean useRegression = false;

    /**
     * The Regression.
     */
    private Regression regression = null;

    /**
     * The Model.
     */
    private double model[];

    private String user;

    /**
     * Instantiates a new Agent.
     *
     * @param user the user
     */
    public agent(String user){
        this.user = user;
        int x = stateCalculator.getMax();
        int y = stateCalculator.getnActions();
        qTable = new Double[x][y];
        if(!loadQtable()) {
            for (int i = 0; i < x; ++i) {
                for (int j = 0; j < y; ++j) {
                    qTable[i][j] = 0.0;
                }
            }
        }
    }

    /**
     * Instantiates a new Agent.
     *
     * @param user     the user
     * @param initialS the initial s
     * @param epsilon  the epsilon
     * @param ganma    the ganma
     * @param alpha    the alpha
     */
    public agent(String user, state initialS, double epsilon, double ganma, double alpha) {
        this.user = user;
        this.previousState = initialS;
        this.epsilon = epsilon;
        this.ganma = ganma;
        this.alpha = alpha;
        int x = stateCalculator.getMax();
        int y = stateCalculator.getnActions();
        qTable = new Double[x][y];
        if(!loadQtable()) {
            for (int i = 0; i < x; ++i) {
                for (int j = 0; j < y; ++j) {
                    qTable[i][j] = 0.0;
                }
            }
        }
    }

    /**
     * Clear qtable.
     */
    public void clearQtable(){
        int x = stateCalculator.getMax();
        int y = stateCalculator.getnActions();
        for (int i = 0; i < x; ++i) {
            for (int j = 0; j < y; ++j) {
                qTable[i][j] = 0.0;
            }
        }
    }

    /**
     * Restart.
     *
     * @param initial the initial
     */
    public synchronized void restart(state initial){
        trainingEnded = false;
        waitingResult = false;
        resultAssigned = false;
        trainingRegister.clear();
        previousState = initial;
        accumulatedReward = 0;
    }

    /**
     * Notify result.
     *
     * @param r the r
     */
    public synchronized void notifyResult(state r){
        result = r;
        // Se da la recompensa correspondiente a la acción ejecutada en base al resultado
        giveReward(result);
        resultAssigned = true;

        ////////////////////////////////////
        previousState = result;
        actionToExecute = selectAction();
    }

    /**
     * Da una recompensa en base al nuevo estado newS, a la acción ejecutada en el estado anterior.
     *
     * @param newS the new s
     */
    public void giveReward(state newS){
        // Da la recompensa a la acción actionToExecute previousState en base a newS
        giveReward(previousState, newS, actionToExecute, false, false);
        // Aleatoriamente se comprueba si toca revivir alguna experiencia
        if(Math.random() > 1-experienceFrequency){
            // Se reviven 5 experiencias aleatorias del buffer
            for(int h = 0; h < 5; ++h){
                Pair<Pair<state, Movement>, Pair<Double, state>> aux = experienceBuffer.get((int) (experienceBuffer.size()*Math.random()));
                giveReward(aux.first.first, aux.second.second, aux.first.second, true, false);
            }
        }
    }

    /**
     * Da la recompensa correspondiente en base al estado newS, a la accion action en el estado ini.
     *
     * @param ini        the ini
     * @param newS       the new s
     * @param action     the action
     * @param experience the experience (indica si viene de experience replay o no)
     * @param loading    the loading
     */
    private void giveReward(state ini, state newS, Movement action, boolean experience, boolean loading){
        // Se hallan los índices de la tabla en base al estado y la acción
        int i = ini.getStateNum(), j = stateCalculator.idAction(action);
        double reward = 0;
        // Recompensa futura máxima del estado newS (si newS es terminal de la pelea no tiene recompensa futura)
        double futureReward = 0.0;
        // Si es terminal de la pelea, se recompensa mucho la victoria, se penaliza mucho la derrota, y
        // ni de compensa ni penaliza el empate
        if(newS.isFightTerminal()){
            if(newS.getIaVictories() == 1 && newS.getPlayerLife() == 0){
                reward = 200;
            }
            else if(newS.getPlayerVictories() == 1 && newS.getLife() == 0){
                reward = -200;
            }
        }
        // Si es terminal de la ronda, se recompensa bastante la victoria, se penaliza bastante la derrota, y
        // ni de compensa ni penaliza el empate
        else if(newS.isRoundTerminal()){
            state aux = newS.clone();
            if(newS.getLife() > newS.getPlayerLife()){
                reward = 100;
                aux.setIaVictories(aux.getIaVictories()+1);
            }
            else if(newS.getLife() < newS.getPlayerLife()){
                reward = -100;
                aux.setPlayerVictories(aux.getPlayerVictories()+1);
            }
            if(!stateCalculator.isSimple()) {
                aux.setRound(aux.getRound() + 1);
                aux.setRemainingTime(90);
                aux.setLife(100);
                aux.setPlayerLife(100);
                aux.setPlayerState(Movement.STANDING);
                // Recompensa del estado inicial futuro
                futureReward = maxFutureReward(aux);
            }
        }
        // Caso genérico
        else {
            // Recompensa futura
            futureReward = maxFutureReward(newS);
            // Si no han cambiado las vidas
            if (newS.getLife() == ini.getLife() && newS.getPlayerLife() == ini.getPlayerLife()) {
                // Si el jugador estaba atacando y la ia se defencio
                if (character.isAttack(ini.getPlayerState()) && (action == Movement.WALKING || action == Movement.CROUCHED_BLOCK)) {
                    if(ini.getDis() > 200 && newS.getDis() > 200){
                        reward = -1;
                    }
                    else {
                        reward = 5;
                    }
                }
                // Si atacó al aire
                else if (character.isAttack(action) && newS.getDis() > 200 && ini.getDis() > 200) {
                    reward = -1;
                }
            }
            // Si a alguno de los dos le bajó la vida
            else if (newS.getLife() != ini.getLife() || newS.getPlayerLife() != ini.getPlayerLife()) {
                // Si se estaba cubriento, se recompensa con el daño recibido (este es reducido)
                if (newS.getLife() < ini.getLife()  && (action == Movement.WALKING || action == Movement.CROUCHED_BLOCK)) {
                    reward = 30.0*(((double) ini.getLife()-(double) newS.getLife())/2)/(double) ini.getLife();
                }
                // Sino, recompensa con la diferencia entre el daño infligido y el recibido
                else {
                    reward = (30.0*((double) newS.getLife() - (double) ini.getLife())/(double) ini.getLife() + 30.0*((double) ini.getPlayerLife()-(double) newS.getPlayerLife())/(double) ini.getPlayerLife());
                }
            }
        }

        // Función de actualización de la qTable
        qTable[i][j] = qTable[i][j] + alpha * (reward + ganma * futureReward - qTable[i][j]);

        //System.out.println("action: " + action.toString() + " reward: " + reward + " future reward: " + futureReward + " q value: "+ qTable[i][j]);

        accumulatedReward += reward;
        //System.out.println("accumulated Reward: " + accumulatedReward);

        // Registra la transición
        if(!loading) {
            trainingRegister.add(new Pair<>(new Pair<>(ini, action), new Pair<>(reward, newS)));
        }

        // SI no viene de esperience replay, se guarda en el buffer
        if(!experience) {
            experienceBuffer.add(new Pair<>(new Pair<>(ini, action), new Pair<>(reward, newS)));
            if(experienceBuffer.size() > maxExperience){
                experienceBuffer.remove(0);
            }
        }
    }

    /**
     * Halla la máxima recompensa futura del estado s
     *
     * @param s the s
     * @return the double
     */
    public double maxFutureReward(state s){
        int x = s.getStateNum();
        double r = qTable[x][0];
        for(int i = 1; i < stateCalculator.getnActions(); ++i){
            if(qTable[x][i] > r){
                r = qTable[x][i];
            }
        }
        return r;
    }

    /**
     * Selecciona una acción aleatoria con probabilidad epsilon, y una acción "óptima" con probabilidad 1-epsilon
     *
     * @return the movement
     */
    public Movement selectAction(){
        Movement a = Movement.STANDING;
        int n = stateCalculator.getnActions();
        if(Math.random() <= epsilon) {
            if(previousState.isJumping()){
                Movement aux[] = {Movement.HARD_PUNCH, Movement.SOFT_PUNCH, Movement.SOFT_KICK,
                        Movement.HARD_KICK, Movement.STANDING};
                a = aux[(int) ((Math.random() * 4.0)+0.5)];
            }
            else {
                int m = (int) (Math.random() * n);
                m = m % stateCalculator.getnActions();
                a = stateCalculator.actionById(m);
            }
        }
        else{
            a = selectAction(previousState);
        }
        //System.out.println(a + " seleccionada");
        return a;
    }

    /**
     * Selecciona una acción "óptima"
     *
     * @param st the st
     * @return the movement
     */
    public Movement selectAction(state st){
        boolean allZeros = true;
        int n = stateCalculator.getnActions(), s = st.getStateNum();
        if(useRegression && regression != null) {
            for (int i = 1; allZeros && i < n; ++i) {
                allZeros = qTable[s][i] == 0.0;
            }
            if (allZeros) {
                double max = evalueWithRegression(st,0);
                int best = 0;
                for (int i = 1; i < n; ++i) {
                    double val = evalueWithRegression(st,i);
                    if (val > max) {
                        max = val;
                        best = i;
                    }
                }
                Movement a = stateCalculator.actionById(best);
                if (st.isJumping() &&
                        (a != Movement.HARD_PUNCH && a != Movement.SOFT_PUNCH && a != Movement.SOFT_KICK && a != Movement.HARD_KICK)) {
                    a = Movement.STANDING;
                }
                return a;
            }
        }

        if(Math.random() <= optimalDecision) {
            Movement a = Movement.STANDING;
            int best = 0;
            double max = qTable[s][0];
            for (int i = 1; i < n; ++i) {
                if (qTable[s][i] > max) {
                    max = qTable[s][i];
                    best = i;
                }
            }
            a = stateCalculator.actionById(best);

            if (st.isJumping() &&
                    (a != Movement.HARD_PUNCH && a != Movement.SOFT_PUNCH && a != Movement.SOFT_KICK && a != Movement.HARD_KICK)) {
                a = Movement.STANDING;
            }
            return a;
        }
        else{
            Movement a = Movement.STANDING;
            int best = 0;
            double max = qTable[s][0];

            List<Pair<Integer, Double>> top4 = new ArrayList<>();

            for (int i = 1; i < n; ++i) {
                if(top4.size() < 4 && qTable[s][i] > 0){
                    top4.add(new Pair<Integer, Double>(i,qTable[s][i]));
                }
                else {
                    boolean done = false;
                    for (int j = 0; !done && j < top4.size(); ++j) {
                        if (qTable[s][i] > top4.get(j).second) {
                            top4.remove(j);
                            top4.add(new Pair<Integer, Double>(i,qTable[s][i]));
                            done = true;
                        }
                    }
                }
                if (qTable[s][i] > max) {
                    max = qTable[s][i];
                    best = i;
                }
            }

            boolean done = false;
            for(int i = 0; !done && i < top4.size(); ++i){
                if(top4.get(i).first == best){
                    done = true;
                    top4.remove(i);
                }
            }
            if(top4.size() == 0){
                a = stateCalculator.actionById(best);;
            }
            else {
                try {
                    int idAct = top4.get(((int) (Math.random() * top4.size())) % top4.size()).first;
                    a = stateCalculator.actionById(idAct);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (st.isJumping() &&
                    (a != Movement.HARD_PUNCH && a != Movement.SOFT_PUNCH && a != Movement.SOFT_KICK && a != Movement.HARD_KICK)) {
                a = Movement.STANDING;
            }
            return a;
        }
    }

    /**
     * Training to string string.
     *
     * @return the string
     */
    public String trainingToString(){
        String train = "";

        train += "#-----------#\n";
        train += "# NEW FIGHT #\n";
        train += "#-----------#\n";
        int x = trainingRegister.size();
        for(int i = 0; i < x; ++i) {
            Pair<Pair<state, Movement>, Pair<Double, state>> aux = trainingRegister.get(i);
            train += "# Source state #\n";
            train += aux.first.first.toString()+"\n";
            train += "# Action #\n";
            train += aux.first.second.toString()+"\n";
            train += "# Reward #\n";
            train += aux.second.first.toString()+"\n";
            train += "# Destiny state #\n";
            train += aux.second.second.toString()+"\n";
        }

        return train;
    }

    /**
     * Load training from string.
     *
     * @param t the t
     */
    public void loadTrainingFromString(String t){
        String train[] = t.split("\n");
        int i = 3;
        while(i < train.length){
            ++i;
            String estado[] = train[i].split(",");
            state s = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                    Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[6]),
                    Integer.parseInt(estado[7]), Integer.parseInt(estado[8]), Boolean.parseBoolean(estado[9]));
            ++i;++i;
            Movement action = Movement.valueOf(train[i]);
            ++i;++i;++i;++i;
            estado = train[i].split(",");
            state s2 = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                    Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[6]),
                    Integer.parseInt(estado[7]), Integer.parseInt(estado[8]), Boolean.parseBoolean(estado[9]));
            giveReward(s, s2, action, false, true);
            ++i;
        }
    }

    /**
     * Escribe la tabla y el registro del entrenamiento
     */
    public void writeQTableAndRegister(){
        String path1 =  System.getProperty("user.dir") + "/.files/qTable"+user+".txt";
        String path2 =  System.getProperty("user.dir") + "/.files/trainingRegister"+user+".txt";
        File f= new File(path1);
        f.delete();
        f = new File(path1);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            int aux1 = stateCalculator.getnActions();
            int aux2 = stateCalculator.getMax();
            for(int i = 0; i < aux2; ++i){
                for(int j = 0; j < aux1; ++j){
                    bw.write(Double.toString(qTable[i][j]));
                    if(j < aux1-1){
                        bw.write(" ");
                    }
                    else if(i < aux2-1){
                        bw.write("\n");
                    }
                }
            }
            bw.close();
            fos.close();

            f = new File(path2);
            FileWriter fr = new FileWriter(path2, true);
            //fos = new FileOutputStream(fr);
            bw = new BufferedWriter(fr);
            PrintWriter out = new PrintWriter(bw);
            out.println("#-----------#");
            out.println("# NEW FIGHT #");
            out.println("#-----------#");
            int aux3 = trainingRegister.size();
            for(int i = 0; i < aux3; ++i) {
                Pair<Pair<state, Movement>, Pair<Double, state>> aux = trainingRegister.get(i);
                out.println("# Source state #");
                out.println(aux.first.first.toString());
                out.println("# Action #");
                out.println(aux.first.second.toString());
                out.println("# Reward #");
                out.println(aux.second.first.toString());
                out.println("# Destiny state #");
                out.println(aux.second.second.toString());
            }
            bw.close();
            fr.close();
            out.close();
            trainingRegister.clear();
        }
        catch (Exception e){}
    }

    /**
     * Carga la tabla q del fichero correspondiente.
     *
     * @return the boolean
     */
    public boolean loadQtable(){
        String path =  System.getProperty("user.dir") + "/.files/qTable"+user+".txt";
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            int i = 0;
            while((aux = b.readLine()) != null){
                String values[] = aux.split(" ");
                for(int j = 0; j < values.length; ++j){
                    qTable[i][j] = Double.parseDouble(values[j]);
                }
                ++i;
            }
            b.close();
        }catch (Exception e){
            System.out.println("No se ha encontrado la q table, por lo que se empezará de 0");
            return false;
        }
        return true;
    }

    /**
     * Load training.
     *
     * @param file the file
     */
    public void loadTraining(String file){
        String path =  System.getProperty("user.dir") + "/.files/"+file;
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            boolean nextFight = false;
            aux = b.readLine();
            while(aux != null) {
                nextFight = false;
                aux = b.readLine();
                aux = b.readLine();
                while (!nextFight) {
                    aux = b.readLine();
                    if(aux == null || aux.equals("#-----------#")){nextFight = true;}
                    else {
                        aux = b.readLine();
                        String estado[] = aux.split(",");
                        state s = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                                Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[6]),
                                Integer.parseInt(estado[7]), Integer.parseInt(estado[8]), Boolean.parseBoolean(estado[9]));
                        aux = b.readLine();
                        aux = b.readLine();
                        Movement action = Movement.valueOf(aux);
                        aux = b.readLine();
                        aux = b.readLine();
                        aux = b.readLine();
                        aux = b.readLine();
                        estado = aux.split(",");
                        state s2 = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                                Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[6]),
                                Integer.parseInt(estado[7]), Integer.parseInt(estado[8]), Boolean.parseBoolean(estado[9]));
                        giveReward(s, s2, action, false, true);
                    }
                }
            }
            b.close();
        }catch (Exception e){
            System.out.println("No se ha encontrado el fichero de entrenamiento");
        }
    }

    /**
     * Evalue with regression double.
     *
     * @param s the s
     * @param a the a
     * @return the double
     */
    public double evalueWithRegression(state s, int a){
        int grade = (model.length -1)/6;
        List<Double> values = new ArrayList<>();
        for (double i = 1; i <= grade; ++i) {
            values.add(Math.pow(a,i));
            double j = 0;
            if (s.isJumping()) {
                j = 1;
            }
            values.add(Math.pow(j,i));
            values.add(Math.pow((double) stateCalculator.getIdMov().get(s.getPlayerState()),i));
            values.add(Math.pow(s.getSimpleDistance(),i));
            values.add(Math.pow(s.getSimpleLife(),i));
            values.add(Math.pow(s.getSimplePlayerLife(),i));
        }
        values.add(1,0.0);
        double reward  = 0.0;
        for(int i = 0; i < model.length-1;++i){
            reward += (values.get(i)*model[i]);
        }
        reward += model[model.length-1];
        if(reward > 200.0){
            reward = 200.0;
        }
        return reward;
    }

    /**
     * Train regression.
     */
    public void trainRegression(){
        if(useRegression){
            try {
                if (regression == null) {
                    regression = new Regression(5, 1, 10, user, "trainingRegister"+user+".txt", "qTable"+user+".txt", 0);
                }
                regression.calculateModel();
                regression.loadModel();
                model = regression.getFinalModel().coefficients();
            }catch (Exception e){
                regression = null;
                e.printStackTrace();
            }
        }
    }

    /**
     * Getq table double [ ] [ ].
     *
     * @return the double [ ] [ ]
     */
    public Double[][] getqTable() {
        return qTable;
    }

    /**
     * Sets table.
     *
     * @param qTable the q table
     */
    public void setqTable(Double[][] qTable) {
        this.qTable = qTable;
    }

    /**
     * Gets previous state.
     *
     * @return the previous state
     */
    public state getPreviousState() {
        return previousState;
    }

    /**
     * Sets previous state.
     *
     * @param previousState the previous state
     */
    public void setPreviousState(state previousState) {
        this.previousState = previousState;
        this.actionToExecute = selectAction();
    }

    /**
     * Gets epsilon.
     *
     * @return the epsilon
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * Sets epsilon.
     *
     * @param epsilon the epsilon
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Gets ganma.
     *
     * @return the ganma
     */
    public double getGanma() {
        return ganma;
    }

    /**
     * Sets ganma.
     *
     * @param ganma the ganma
     */
    public void setGanma(double ganma) {
        this.ganma = ganma;
    }

    /**
     * Gets alpha.
     *
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Sets alpha.
     *
     * @param alpha the alpha
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Gets experience buffer.
     *
     * @return the experience buffer
     */
    public List<Pair<Pair<state, Movement>, Pair<Double, state>>> getExperienceBuffer() {
        return experienceBuffer;
    }

    /**
     * Sets experience buffer.
     *
     * @param experienceBuffer the experience buffer
     */
    public void setExperienceBuffer(List<Pair<Pair<state, Movement>, Pair<Double, state>>> experienceBuffer) {
        this.experienceBuffer = experienceBuffer;
    }

    /**
     * Gets max experience.
     *
     * @return the max experience
     */
    public int getMaxExperience() {
        return maxExperience;
    }

    /**
     * Sets max experience.
     *
     * @param maxExperience the max experience
     */
    public void setMaxExperience(int maxExperience) {
        this.maxExperience = maxExperience;
    }

    /**
     * Gets experience frequency.
     *
     * @return the experience frequency
     */
    public double getExperienceFrequency() {
        return experienceFrequency;
    }

    /**
     * Sets experience frequency.
     *
     * @param experienceFrequency the experience frequency
     */
    public void setExperienceFrequency(double experienceFrequency) {
        this.experienceFrequency = experienceFrequency;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public state getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(state result) {
        this.result = result;
    }

    /**
     * Gets action to execute.
     *
     * @return the action to execute
     */
    public Movement getActionToExecute() {
        return actionToExecute;
    }

    /**
     * Sets action to execute.
     *
     * @param actionToExecute the action to execute
     */
    public void setActionToExecute(Movement actionToExecute) {
        this.actionToExecute = actionToExecute;
    }

    /**
     * Is training ended boolean.
     *
     * @return the boolean
     */
    public boolean isTrainingEnded() {
        return trainingEnded;
    }

    /**
     * Sets training ended.
     *
     * @param trainingEnded the training ended
     */
    public void setTrainingEnded(boolean trainingEnded) {
        this.trainingEnded = trainingEnded;
    }

    /**
     * Gets training register.
     *
     * @return the training register
     */
    public List<Pair<Pair<state, Movement>, Pair<Double, state>>> getTrainingRegister() {
        return trainingRegister;
    }

    /**
     * Sets training register.
     *
     * @param trainingRegister the training register
     */
    public void setTrainingRegister(List<Pair<Pair<state, Movement>, Pair<Double, state>>> trainingRegister) {
        this.trainingRegister = trainingRegister;
    }

    /**
     * Is result assigned boolean.
     *
     * @return the boolean
     */
    public boolean isResultAssigned() {
        return resultAssigned;
    }

    /**
     * Sets result assigned.
     *
     * @param resultAssigned the result assigned
     */
    public void setResultAssigned(boolean resultAssigned) {
        this.resultAssigned = resultAssigned;
    }

    /**
     * Is waiting result boolean.
     *
     * @return the boolean
     */
    public boolean isWaitingResult() {
        return waitingResult;
    }

    /**
     * Sets waiting result.
     *
     * @param waitingResult the waiting result
     */
    public void setWaitingResult(boolean waitingResult) {
        this.waitingResult = waitingResult;
    }

    /**
     * Gets accumulated reward.
     *
     * @return the accumulated reward
     */
    public double getAccumulatedReward() {
        return accumulatedReward;
    }

    /**
     * Sets accumulated reward.
     *
     * @param accumulatedReward the accumulated reward
     */
    public void setAccumulatedReward(double accumulatedReward) {
        this.accumulatedReward = accumulatedReward;
    }

    /**
     * Gets optimal decision.
     *
     * @return the optimal decision
     */
    public double getOptimalDecision() {
        return optimalDecision;
    }

    /**
     * Sets optimal decision.
     *
     * @param optimalDecision the optimal decision
     */
    public void setOptimalDecision(double optimalDecision) {
        this.optimalDecision = optimalDecision;
    }

    /**
     * Is use regression boolean.
     *
     * @return the boolean
     */
    public boolean isUseRegression() {
        return useRegression;
    }

    /**
     * Sets use regression.
     *
     * @param useRegression the use regression
     */
    public void setUseRegression(boolean useRegression) {
        this.useRegression = useRegression;
        if(useRegression){
            try {
                regression = new Regression(5, 1, 10, user, "trainingRegister"+user+".txt", "qTable"+user+".txt", 0);
                regression.loadModel();
                model = regression.getFinalModel().coefficients();
            }catch (Exception e){
                //e.printStackTrace();
                regression = null;
            }
        }
        else{
            regression = null;
            model = null;
        }
    }

    /**
     * Gets regression.
     *
     * @return the regression
     */
    public Regression getRegression() {
        return regression;
    }

    /**
     * Sets regression.
     *
     * @param regression the regression
     */
    public void setRegression(Regression regression) {
        this.regression = regression;
    }

    /**
     * Get model double [ ].
     *
     * @return the double [ ]
     */
    public double[] getModel() {
        return model;
    }

    /**
     * Sets model.
     *
     * @param model the model
     */
    public void setModel(double[] model) {
        this.model = model;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(String user) {
        this.user = user;
    }
}
