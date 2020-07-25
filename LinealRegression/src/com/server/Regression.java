package com.server;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

    public Regression(int folds, int grade, String t, String q, boolean fromTraining) throws IOException {
        csvGenerator =  new qTableToCSV(t,q, fromTraining);

        this.folds  = folds;
        this.maxGrade = grade;
        this.grade = grade;
        this.simple = true;

        loadDataset();
    }

    public Regression(int folds, int grade, int maxGrade, String t, String q, boolean fromTraining) throws IOException {
        csvGenerator =  new qTableToCSV(t,q, fromTraining);

        this.folds  = folds;
        this.grade = grade;
        this.maxGrade = maxGrade;
        this.simple = false;

        loadDataset();
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
        Evaluation betterEval = new Evaluation(trainDataSet);
        LinearRegression betterModel = new LinearRegression();;

        for(int i = grade; i <=  maxGrade; ++i){
            csvGenerator.generateCSV(i);
            loadDataset();

            LinearRegression model = new LinearRegression();
            model.buildClassifier(trainDataSet);

            Evaluation eval = new Evaluation(trainDataSet);
            eval.crossValidateModel(model, validateDataSet, foldsCV, new Random(1));

            System.out.println("Grade: " + i + " Relative absolute error: " + eval.relativeAbsoluteError());

            if(eval.relativeAbsoluteError() < betterError){
                betterError = eval.relativeAbsoluteError();
                betterGrade = i;
                betterEval = eval;
                betterModel = model;
            }
        }

        System.out.println("\nBetter grade: " + betterGrade);
        System.out.println("** Linear Regression Evaluation with Datasets **");
        System.out.println(betterEval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(betterModel);
        saveModel(betterModel);
    }

    public void simpleRegression() throws Exception {
        csvGenerator.generateCSV(grade);

        LinearRegression model = new LinearRegression();
        model.buildClassifier(trainDataSet);

        Evaluation eval = new Evaluation(trainDataSet);
        eval.crossValidateModel(model, validateDataSet, foldsCV, new Random(1));

        System.out.println("** Linear Regression Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(model);

        saveModel(model);
    }

    private void saveModel(LinearRegression model) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(modelPath));
        oos.writeObject(model);
        oos.flush();
        oos.close();
    }
}
