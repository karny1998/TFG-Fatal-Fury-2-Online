package lib.training;

import lib.Enums.Movement;
import lib.utils.Pair;

import java.util.List;

public class agent{
    private Double qTable[][];
    private List<state> transitionTable[][];
    private List<Pair<Double,state>> experienceBuffer;
    private int maxExperience = 1000;
    private state previousState = new state(-100,-100, Movement.SOFT_PUNCH, -700, -1);
    private double epsilon = 0.5;
    private double ganma = 0.5;
    private double alpha = 0.25;

    public agent(double epsilon, double ganma, double alpha) {
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

    public void giveReward(state newS){

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
}
