package com.server;

public class Main {

    public static void main(String[] args) {
        try {
            Regression reg = new Regression(5,1,10,"","trainingRegister.txt","qTable.txt", 0);
            //Regression reg = new Regression(5,3,"trainingRegister.txt","qTable.txt", 0);
            reg.calculateModel();
            //Regression reg = new Regression();
            //reg.loadModel();
            //System.out.println(reg.getFinalModel().toString());
        }catch (Exception e){e.printStackTrace();}
    }
}
