package com.server;

public class Main {

    public static void main(String[] args) {
        try {
            Regression reg = new Regression(5,1,10,"trainingRegister.txt","qTable.txt", false);
            //Regression reg = new Regression(5,2,"trainingRegister.txt","qTable.txt", false);
            reg.calculateModel();
        }catch (Exception e){e.printStackTrace();}
    }
}
