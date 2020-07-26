package com.server;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.util.Collections;
import java.util.Random;

public class Regression {
    private String data = System.getProperty("user.dir") + "/.files/qTable.csv";
    private String modelPath =  System.getProperty("user.dir") + "/.files/regressionModel.model";
    private Instances dataSet;
    private Instances trainDataSet;
    private Instances validateDataSet;
    private boolean simple = true;
    private int grade = 1, maxGrade = 1, folds = 5, foldsCV = 10;
    private  qTableToCSV csvGenerator;
    private LinearRegression finalModel;

    public Regression(){}

    public Regression(int folds, int grade, String t, String q, int fromTraining) throws IOException {
        csvGenerator =  new qTableToCSV(t,q, fromTraining);

        this.folds  = folds;
        this.maxGrade = grade;
        this.grade = grade;
        this.simple = true;
    }

    public Regression(int folds, int grade, int maxGrade, String t, String q, int fromTraining) throws IOException {
        csvGenerator =  new qTableToCSV(t,q, fromTraining);

        this.folds  = folds;
        this.grade = grade;
        this.maxGrade = maxGrade;
        this.simple = false;
    }

    private void loadDataset() throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setFieldSeparator(",");
        loader.setSource(new File(data));
        Instances dataSet = loader.getDataSet();
        Collections.shuffle(dataSet);
        dataSet.setClassIndex(1);

        this.dataSet = dataSet;
        int f = (int) (Math.random()*folds)%folds;
        this.trainDataSet = dataSet.trainCV(folds,f);
        this.validateDataSet = dataSet.testCV(folds,f);
    }

    public void calculateModel() throws Exception {
        if(simple){
            simpleRegression();
        }
        else{
            kFoldCrossValidationPolynomialRegression();
        }
    }

    public void kFoldCrossValidationPolynomialRegression() throws Exception {
        double betterError = 1000.0;
        int betterGrade = 1;
        Evaluation betterEval = null;// = new Evaluation(trainDataSet);
        LinearRegression betterModel = null; // new LinearRegression();;

        for(int i = grade; i <=  maxGrade; ++i){
            csvGenerator.generateCSV(i);
            loadDataset();

            LinearRegression model = new LinearRegression();
            model.buildClassifier(trainDataSet);

            Evaluation eval = new Evaluation(trainDataSet);
            eval.crossValidateModel(model, validateDataSet, foldsCV, new Random(1));

            //System.out.println("Grade: " + i + " Relative absolute error: " + eval.relativeAbsoluteError());

            if(eval.relativeAbsoluteError() < betterError){
                betterError = eval.relativeAbsoluteError();
                betterGrade = i;
                betterEval = eval;
                betterModel = model;
            }
        }

        /*System.out.println("\nBetter grade: " + betterGrade);
        System.out.println("** Linear Regression Evaluation with Datasets **");
        System.out.println(betterEval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(betterModel);*/
        saveModel(betterModel);
        finalModel  = betterModel;
    }

    public void simpleRegression() throws Exception {
        csvGenerator.generateCSV(grade);
        loadDataset();

        LinearRegression model = new LinearRegression();
        model.buildClassifier(trainDataSet);

        Evaluation eval = new Evaluation(trainDataSet);
        eval.crossValidateModel(model, validateDataSet, foldsCV, new Random(1));

        /*System.out.println("** Linear Regression Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(model);*/

        saveModel(model);
        finalModel = model;
    }

    private void saveModel(LinearRegression model) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(modelPath));
        oos.writeObject(model);
        oos.flush();
        oos.close();
    }

    public void loadModel()  {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(modelPath));
            finalModel = (LinearRegression) ois.readObject();
            ois.close();
        }catch (Exception e){
            finalModel = null;
            e.printStackTrace();
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public Instances getDataSet() {
        return dataSet;
    }

    public void setDataSet(Instances dataSet) {
        this.dataSet = dataSet;
    }

    public Instances getTrainDataSet() {
        return trainDataSet;
    }

    public void setTrainDataSet(Instances trainDataSet) {
        this.trainDataSet = trainDataSet;
    }

    public Instances getValidateDataSet() {
        return validateDataSet;
    }

    public void setValidateDataSet(Instances validateDataSet) {
        this.validateDataSet = validateDataSet;
    }

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(int maxGrade) {
        this.maxGrade = maxGrade;
    }

    public int getFolds() {
        return folds;
    }

    public void setFolds(int folds) {
        this.folds = folds;
    }

    public int getFoldsCV() {
        return foldsCV;
    }

    public void setFoldsCV(int foldsCV) {
        this.foldsCV = foldsCV;
    }

    public qTableToCSV getCsvGenerator() {
        return csvGenerator;
    }

    public void setCsvGenerator(qTableToCSV csvGenerator) {
        this.csvGenerator = csvGenerator;
    }

    public LinearRegression getFinalModel() {
        return finalModel;
    }

    public void setFinalModel(LinearRegression finalModel) {
        this.finalModel = finalModel;
    }
}
