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
import java.io.IOException;
import java.util.Collections;
import java.util.Random;

public class Regression {
    private static final String data = System.getProperty("user.dir") + "/.files/qTable.csv";
    private Instances dataSet;
    private Instances trainDataSet;
    private Instances validateDataSet;

    public Regression(int folds) throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setFieldSeparator(",");
        loader.setSource(new File(data));
        Instances dataSet = loader.getDataSet();
        Collections.shuffle(dataSet);
        dataSet.setClassIndex(1);
        this.dataSet = dataSet;
        this.trainDataSet = dataSet.trainCV(folds,0);
        this.validateDataSet = dataSet.testCV(folds,0);
        System.out.println();
    }

    public void calculateModel() throws Exception {
        LinearRegression model = new LinearRegression();
        model.buildClassifier(trainDataSet);

        Evaluation eval = new Evaluation(trainDataSet);
        eval.evaluateModel(model, validateDataSet);

        System.out.println("** Linear Regression Evaluation with Datasets **");
        System.out.println(eval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(model);
    }
}
