package lib.training;

import lib.Enums.Movement;
import lib.objects.character;
import lib.utils.Pair;
import netscape.javascript.JSObject;

import java.io.*;
import java.util.List;

public class agent{
    private Double qTable[][];
    private List<state> transitionTable[][];
    private List<Pair<Pair<state,Movement>,Pair<Double,state>>> experienceBuffer;
    private List<Pair<Pair<state,Movement>,Pair<Double,state>>> trainingRegister;
    private int maxExperience = 1000;
    private double experienceFrequency = 0.1;
    private state previousState = new state(-100,-100, Movement.SOFT_PUNCH, -700, -1, -1);
    private Movement executedAction = Movement.NONE;
    private double epsilon = 0.5;
    private double ganma = 0.5;
    private double alpha = 0.25;
    private state result;
    private Movement actionToExecute = Movement.STANDING;
    private boolean trainingEnded = false;

    public agent(state initialS, double epsilon, double ganma, double alpha) {
        this.previousState = initialS;
        this.epsilon = epsilon;
        this.ganma = ganma;
        this.alpha = alpha;
        int x = stateCalculator.getMax();
        int y = stateCalculator.getnActions();
        qTable = new Double[x][y];
        for(int i = 0; i < x; ++i){
            for(int j = 0; j < x; ++j){
                qTable[i][j] = 0.0;
            }
        }
    }

    public synchronized void restart(state initial){
        trainingEnded = true;
        result.notify();
        previousState = initial;
    }

    public synchronized void train_Q_Learning(){
        while(!trainingEnded) {
            actionToExecute = selectAction();
            try {
                result.wait();
                if(trainingEnded){
                    return;
                }
            } catch (Exception e) {
                train_Q_Learning();
            }

            giveReward(result);
            previousState = result;
        }
    }

    public synchronized void notifyResult(state r){
        result = r;
        result.notify();
    }

    public void giveReward(state newS){
        giveReward(previousState, newS, executedAction, false);

        if(Math.random() > 1-experienceFrequency){
            for(int h = 0; h < 5; ++h){
                Pair<Pair<state,Movement>,Pair<Double,state>> aux = experienceBuffer.get((int) (maxExperience*Math.random()));
                giveReward(aux.first.first, aux.second.second, aux.first.second, true);
            }
        }
    }

    private void giveReward(state ini, state newS, Movement action, boolean experience){
        int i = ini.getStateNum(), j = stateCalculator.idAction(action);
        double reward = 0;

        if(newS.isTerminal()){
            if(newS.getLife() > newS.getPlayerLife()){
                reward = 50;
            }
            else if(newS.getLife() < newS.getPlayerLife()){
                reward = -50;
            }
            qTable[i][j] += (alpha * reward);
        }
        else {
            if (newS.getLife() == ini.getLife() && newS.getPlayerLife() == ini.getPlayerLife()) {
                if (action == Movement.WALKING || action == Movement.CROUCHED_BLOCK) {
                    qTable[i][j] += (alpha * 5);
                    reward = 5;
                } else if (character.isAttack(executedAction)) {
                    qTable[i][j] -= (alpha * 5);
                    reward = -5;
                }
            } else if (newS.getLife() != ini.getLife() || newS.getPlayerLife() != ini.getPlayerLife()) {
                if (action == Movement.WALKING || action == Movement.CROUCHED_BLOCK) {
                    reward = (newS.getLife() - ini.getLife());
                } else {
                    reward = ((newS.getLife() - ini.getLife()) + (ini.getPlayerLife() - newS.getPlayerLife()));
                }
                qTable[i][j] += (alpha * reward);
            }
        }

        trainingRegister.add(new Pair<>(new Pair<>(ini, action), new Pair<>(reward, newS)));
        if(!experience) {
            experienceBuffer.add(new Pair<>(new Pair<>(ini, action), new Pair<>(reward, newS)));
            if(experienceBuffer.size() > maxExperience){
                experienceBuffer.remove(0);
            }
        }
    }

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

            fos = new FileOutputStream(f);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            PrintWriter out = new PrintWriter(bw);
            out.println("#-----------#");
            out.println("# NEW FIGHT #");
            out.println("#-----------#");
            int aux3 = trainingRegister.size();
            for(int i = 0; i < aux3; ++i) {
                Pair<Pair<state, Movement>, Pair<Double, state>> aux = experienceBuffer.get(i);
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
        }
        catch (Exception e){}
    }

    public void loadQtable(){
        String path =  System.getProperty("user.dir") + "/.files/trainingRegister.txt";
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            while((aux = b.readLine()) != null){

            }
            b.close();
        }catch (Exception e){}
    }

    public void loadTraining(){
        String path =  System.getProperty("user.dir") + "/.files/trainingRegister.txt";
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            while((aux = b.readLine()) != null){
                aux = b.readLine();
                aux = b.readLine();
                aux = b.readLine();
                aux = b.readLine();
                String estado[] = aux.split(",");
                state s = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]), Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[5]));
                aux = b.readLine();
                aux = b.readLine();
                Movement action = Movement.valueOf(aux);
                aux = b.readLine();
                aux = b.readLine();
                aux = b.readLine();
                aux = b.readLine();
                estado = aux.split(",");
                state s2 = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]), Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[5]));
                giveReward(s,s2,action, false);
            }
            b.close();
        }catch (Exception e){}
    }

    public Double[][] getqTable() {
        return qTable;
    }

    public void setqTable(Double[][] qTable) {
        this.qTable = qTable;
    }

    public List<state>[][] getTransitionTable() {
        return transitionTable;
    }

    public void setTransitionTable(List<state>[][] transitionTable) {
        this.transitionTable = transitionTable;
    }

    public state getPreviousState() {
        return previousState;
    }

    public void setPreviousState(state previousState) {
        this.previousState = previousState;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public double getGanma() {
        return ganma;
    }

    public void setGanma(double ganma) {
        this.ganma = ganma;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public List<Pair<Pair<state, Movement>, Pair<Double, state>>> getExperienceBuffer() {
        return experienceBuffer;
    }

    public void setExperienceBuffer(List<Pair<Pair<state, Movement>, Pair<Double, state>>> experienceBuffer) {
        this.experienceBuffer = experienceBuffer;
    }

    public int getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(int maxExperience) {
        this.maxExperience = maxExperience;
    }

    public double getExperienceFrequency() {
        return experienceFrequency;
    }

    public void setExperienceFrequency(double experienceFrequency) {
        this.experienceFrequency = experienceFrequency;
    }

    public Movement getExecutedAction() {
        return executedAction;
    }

    public void setExecutedAction(Movement executedAction) {
        this.executedAction = executedAction;
    }

    public state getResult() {
        return result;
    }

    public void setResult(state result) {
        this.result = result;
    }

    public Movement getActionToExecute() {
        return actionToExecute;
    }

    public void setActionToExecute(Movement actionToExecute) {
        this.actionToExecute = actionToExecute;
    }

    public boolean isTrainingEnded() {
        return trainingEnded;
    }

    public void setTrainingEnded(boolean trainingEnded) {
        this.trainingEnded = trainingEnded;
    }
}
