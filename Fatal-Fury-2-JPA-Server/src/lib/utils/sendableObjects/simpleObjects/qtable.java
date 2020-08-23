package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

/**
 * The type Qtable.
 */
public class qtable extends sendableObject {
    private static final long serialVersionUID = 7617345688754547714L;
    private double table[][];
    private String transitions;

    /**
     * Instantiates a new Qtable.
     *
     * @param table       the table
     * @param transitions the transitions
     */
    public qtable(double[][] table, String transitions) {
        this.table = table;
        this.transitions = transitions;
    }

    /**
     * Instantiates a new Qtable.
     *
     * @param table       the table
     * @param transitions the transitions
     */
    public qtable(Double[][] table, String transitions) {
        this.table = new double[table.length][table[0].length];
        for(int i = 0; i < table.length; ++i){
            for(int j = 0; j < table[0].length; ++j){
                this.table[i][j] = table[i][j];
            }
        }
        this.transitions = transitions;
    }

    /**
     * Get table double [ ] [ ].
     *
     * @return the double [ ] [ ]
     */
    public double[][] getTable() {
        return table;
    }

    /**
     * Get table double double [ ] [ ].
     *
     * @return the double [ ] [ ]
     */
    public Double[][] getTableDouble() {
        Double[][] aux = new Double[table.length][table[0].length];
        for(int i = 0; i < table.length; ++i){
            for(int j = 0; j < table[0].length; ++j){
                aux[i][j] = table[i][j];
            }
        }
        return aux;
    }

    /**
     * Sets table.
     *
     * @param table the table
     */
    public void setTable(double[][] table) {
        this.table = table;
    }

    /**
     * Gets transitions.
     *
     * @return the transitions
     */
    public  String getTransitions() {
        return transitions;
    }

    /**
     * Sets transitions.
     *
     * @param transitions the transitions
     */
    public void setTransitions(String transitions) {
        this.transitions = transitions;
    }

    /**
     * Gets serial version uid.
     *
     * @return the serial version uid
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
