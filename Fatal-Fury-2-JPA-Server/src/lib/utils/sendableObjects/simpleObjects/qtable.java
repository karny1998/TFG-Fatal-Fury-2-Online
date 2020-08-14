package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

public class qtable extends sendableObject {
    private Double table[][];

    public qtable(Double[][] table) {
        this.table = table;
    }

    public Double[][] getTable() {
        return table;
    }

    public void setTable(Double[][] table) {
        this.table = table;
    }
}
