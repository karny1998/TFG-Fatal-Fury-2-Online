package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

public class qtable extends sendableObject {
    private static final long serialVersionUID = 7617345688754547719L;
    private double table[][];
    //private ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>> transitions;

    public qtable(double[][] table){//}, ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>> transitions) {
        this.table = table;
        //this.transitions = transitions;
    }

    public qtable(Double[][] table){//}, ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>> transitions) {
        this.table = new double[table.length][table[0].length];
        for(int i = 0; i < table.length; ++i){
            for(int j = 0; j < table[0].length; ++j){
                this.table[i][j] = table[i][j];
            }
        }
        //this.transitions = transitions;
    }

    public double[][] getTable() {
        return table;
    }

    public Double[][] getTableDouble() {
        Double[][] aux = new Double[table.length][table[0].length];
        for(int i = 0; i < table.length; ++i){
            for(int j = 0; j < table[0].length; ++j){
                aux[i][j] = table[i][j];
            }
        }
        return aux;
    }

    public void setTable(double[][] table) {
        this.table = table;
    }

    /*public  ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>> getTransitions() {
        return transitions;
    }

    public void setTransitions( ArrayList<Pair<Pair<state, Movement>, Pair<Double, state>>> transitions) {
        this.transitions = transitions;
    }*/
}

