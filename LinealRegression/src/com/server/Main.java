package com.server;

public class Main {

    public static void main(String[] args) {
        qTableToCSV aux = new qTableToCSV("trainingRegister.txt","qTable.txt", true);
        aux.generateCSV(2);
        try {
            Regression reg = new Regression(5);
            reg.calculateModel();
        }catch (Exception e){e.printStackTrace();}
    }
}
