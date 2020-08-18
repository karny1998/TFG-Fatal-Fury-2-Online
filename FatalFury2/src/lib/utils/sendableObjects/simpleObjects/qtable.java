package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

public class qtable extends sendableObject {
    private static final long serialVersionUID = 7617345688754547714L;
    private double table[][];
    private String transitions;

    public qtable(double[][] table, String transitions) {
        this.table = table;
        this.transitions = transitions;
    }

    public qtable(Double[][] table, String transitions) {
        this.table = new double[table.length][table[0].length];
        for(int i = 0; i < table.length; ++i){
            for(int j = 0; j < table[0].length; ++j){
                this.table[i][j] = table[i][j];
            }
        }
        this.transitions = transitions;
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

    public  String getTransitions() {
        return transitions;
    }

    public void setTransitions(String transitions) {
        this.transitions = transitions;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
