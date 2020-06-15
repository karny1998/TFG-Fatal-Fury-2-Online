package lib.training;

import lib.Enums.Movement;
import lib.objects.character;
import lib.utils.Pair;
import netscape.javascript.JSObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Agent.
 */
public class agent{
    /**
     * Tabla de valores Q, es decir, la tabla de recompensas para las acciones en cada estado.
     */
    private Double qTable[][];
    /**
     * El buffer para el experience replay, es decir, para poder "revivir" transiciones pasadas.
     */
    private List<Pair<Pair<state,Movement>,Pair<Double,state>>> experienceBuffer = new ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>>();
    /**
     * El número máximo de experiencias a tener en el buffer.
     */
    private int maxExperience = 1000;
    /**
     * El resgistro de todas las transiciones desde que comenzó el entrenamiento.
     */
    private List<Pair<Pair<state,Movement>,Pair<Double,state>>> trainingRegister = new ArrayList<>();
    /**
     * Frecuencia con la que se "reviven" experiencias.
     */
    private double experienceFrequency = 0.1;
    /**
     * The Previous state.
     */
    private state previousState = new state(-100,-100, Movement.SOFT_PUNCH, -700, -1, -1, -1, -1);
    /**
     * The Executed action.
     */
    private Movement executedAction = Movement.STANDING;
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

    private boolean waitingResult = false;

    /**
     * Instantiates a new Agent.
     *
     * @param initialS the initial s
     * @param epsilon  the epsilon
     * @param ganma    the ganma
     * @param alpha    the alpha
     */
    public agent(state initialS, double epsilon, double ganma, double alpha) {
        this.previousState = initialS;
        this.epsilon = epsilon;
        this.ganma = ganma;
        this.alpha = alpha;
        int x = stateCalculator.getMax();
        int y = stateCalculator.getnActions();
        qTable = new Double[x][y];
        for(int i = 0; i < x; ++i){
            for(int j = 0; j < y; ++j){
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
        trainingEnded = true;
        trainingRegister.clear();
        // Notifica el resultado por si se estaba esperando en train_Q_learning
        notify();
        previousState = initial;
    }

    /**
     * Se ejecuta el algoritmo de entrenamiento de Q-Learning.
     */
    public synchronized void train_Q_Learning(){
        // Mientras no haya terminado el entrenamiento
        if(!trainingEnded) {
            // Selecciona una acción a ejecutar
            if(!waitingResult){
                actionToExecute = selectAction();
            }
            // Espera por el resultado de la acción
            if(!resultAssigned) {
                waitingResult = true;
                return;
            }
            resultAssigned = false;
            waitingResult = false;
            // Si se le indica que ha terminado el entrenamiento sale
            if(trainingEnded){
                return;
            }
            // Se da la recompensa correspondiente a la acción ejecutada en base al resultado
            giveReward(result);
            // Se actualiza el estado origen
            previousState = result;
        }
    }

    /**
     * Notify result.
     *
     * @param r the r
     */
    public synchronized void notifyResult(state r){
        result = r;
        resultAssigned = true;
        // Notifica que el resultado ha sido asignado
        notify();
    }

    /**
     * Da una recompensa en base al nuevo estado newS, a la acción ejecutada en el estado anterior.
     *
     * @param newS the new s
     */
    public void giveReward(state newS){
        // Da la recompensa a la acción executedAction previousState en base a newS
        giveReward(previousState, newS, executedAction, false);
        // Aleatoriamente se comprueba si toca revivir alguna experiencia
        if(Math.random() > 1-experienceFrequency){
            // Se reviven 5 experiencias aleatorias del buffer
            for(int h = 0; h < 5; ++h){
                Pair<Pair<state,Movement>,Pair<Double,state>> aux = experienceBuffer.get((int) (experienceBuffer.size()*Math.random()));
                giveReward(aux.first.first, aux.second.second, aux.first.second, true);
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
     */
    private void giveReward(state ini, state newS, Movement action, boolean experience){
        // Se hallan los índices de la tabla en base al estado y la acción
        int i = ini.getStateNum(), j = stateCalculator.idAction(action);
        double reward = 0;
        // Recompensa futura máxima del estado newS (si newS es terminal de la pelea no tiene recompensa futura)
        double futureReward = 0.0;
        // Si es terminal de la pelea, se recompensa mucho la victoria, se penaliza mucho la derrota, y
        // ni de compensa ni penaliza el empate
        if(newS.isFightTerminal()){
            if(newS.getIaVictories() == 2){
                reward = 200;
            }
            else if(newS.getPlayerVictories() == 2){
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
            aux.setRound(aux.getRound()+1);
            aux.setRemainingTime(90);
            aux.setLife(100);
            aux.setPlayerLife(100);
            aux.setPlayerState(Movement.STANDING);
            // Recompensa del estado inicial futuro
            futureReward = maxFutureReward(aux);
        }
        // Caso genérico
        else {
            // Recompensa futura
            futureReward = maxFutureReward(newS);
            // Si no han cambiado las vidas
            if (newS.getLife() == ini.getLife() && newS.getPlayerLife() == ini.getPlayerLife()) {
                // Si el jugador estaba atacando y la ia se defencio
                if (character.isAttack(ini.getPlayerState()) && (action == Movement.WALKING || action == Movement.CROUCHED_BLOCK)) {
                    reward = 5;
                }
                // Si atacó al aire
                else if (character.isAttack(action)) {
                    reward = -5;
                }
            }
            // Si a alguno de los dos le bajó la visa
            else if (newS.getLife() != ini.getLife() || newS.getPlayerLife() != ini.getPlayerLife()) {
                // Si se estaba cubriento, se recompensa con el daño recibido (este es reducido)
                if (character.isAttack(ini.getPlayerState()) && (action == Movement.WALKING || action == Movement.CROUCHED_BLOCK)) {
                    reward = (newS.getLife() - ini.getLife())/2;
                }
                // Sino, recompensa con la diferencia entre el daño infligido y el recibido
                else {
                    reward = ((ini.getLife() - newS.getLife()) + (newS.getPlayerLife() - ini.getPlayerLife()));
                }
            }
        }

        // Función de actualización de la qTable
        qTable[i][j] = qTable[i][j] + alpha * (reward + ganma * futureReward - qTable[i][j]);

        // Registra la transición
        trainingRegister.add(new Pair<>(new Pair<>(ini, action), new Pair<>(reward, newS)));

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
            int m = (int) (Math.random() * n);
            m = m % stateCalculator.getnActions();
            a = stateCalculator.actionById(m);
        }
        else{
            int best = 0, s = previousState.getStateNum();
            double max = qTable[s][0];
            for(int i = 1; i < n; ++i){
                if(qTable[s][i] > max){
                    max = qTable[s][i];
                    best = i;
                }
            }
            a = stateCalculator.actionById(best);
        }
        return a;
    }

    /**
     * Selecciona una acción "óptima"
     *
     * @param st the st
     * @return the movement
     */
    public Movement selectAction(state st){
        Movement a = Movement.STANDING;
        int n = stateCalculator.getnActions();
        int best = 0, s = st.getStateNum();
        double max = qTable[s][0];
        boolean allZeros = true;
        for(int i = 1; i < n; ++i){
            allZeros = allZeros && qTable[s][i] == 0.0;
            if(qTable[s][i] > max){
                max = qTable[s][i];
                best = i;
            }
        }
        if(allZeros){
            a = Movement.NONE;
        }
        else{
            a = stateCalculator.actionById(best);
        }
        return a;
    }

    /**
     * Escribe la tabla y el registro del entrenamiento
     */
    public void writeQTableAndRegister(){
        String path1 =  System.getProperty("user.dir") + "/.files/qTable.txt";
        String path2 =  System.getProperty("user.dir") + "/.files/trainingRegister.txt";
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

            f = new File(path2);
            fos = new FileOutputStream(f);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
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
            trainingRegister.clear();
        }
        catch (Exception e){}
    }

    /**
     * Carga la tabla q del fichero correspondiente.
     */
    public void loadQtable(){
        String path =  System.getProperty("user.dir") + "/.files/trainingRegister.txt";
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
        }catch (Exception e){}
    }

    /**
     * Load training.
     */
    public void loadTraining(){
        String path =  System.getProperty("user.dir") + "/.files/trainingRegister.txt";
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            while((aux = b.readLine()) != null) {
                while ((aux = b.readLine()) != null) {
                    aux = b.readLine();
                    aux = b.readLine();
                    aux = b.readLine();
                    String estado[] = aux.split(",");
                    state s = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                            Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[5]),
                            Integer.parseInt(estado[6]), Integer.parseInt(estado[7]));
                    aux = b.readLine();
                    aux = b.readLine();
                    Movement action = Movement.valueOf(aux);
                    aux = b.readLine();
                    aux = b.readLine();
                    aux = b.readLine();
                    aux = b.readLine();
                    estado = aux.split(",");
                    state s2 = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                            Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[5]),
                            Integer.parseInt(estado[6]), Integer.parseInt(estado[7]));
                    giveReward(s, s2, action, false);
                }
            }
            b.close();
        }catch (Exception e){}
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
     * Gets executed action.
     *
     * @return the executed action
     */
    public Movement getExecutedAction() {
        return executedAction;
    }

    /**
     * Sets executed action.
     *
     * @param executedAction the executed action
     */
    public void setExecutedAction(Movement executedAction) {
        this.executedAction = executedAction;
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
}
