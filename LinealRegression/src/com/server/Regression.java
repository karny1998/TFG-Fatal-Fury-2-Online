package com.server;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.util.Collections;
import java.util.Random;

/**
 * The type Regression.
 */
public class Regression {
    /**
     * The Data.
     */
    private String data = System.getProperty("user.dir") + "/.files/qTable.csv";
    /**
     * The Model path.
     */
    private String modelPath =  System.getProperty("user.dir") + "/.files/regressionModel.model";
    /**
     * The Data set.
     */
    private Instances dataSet;
    /**
     * The Train data set.
     */
    private Instances trainDataSet;
    /**
     * The Validate data set.
     */
    private Instances validateDataSet;
    /**
     * The Simple.
     */
    private boolean simple = true;
    /**
     * The Grade.
     */
    private int grade = 1, /**
     * The Max grade.
     */
    maxGrade = 1, /**
     * The Folds.
     */
    folds = 5, /**
     * The Folds cv.
     */
    foldsCV = 5;
    /**
     * The Csv generator.
     */
    private  qTableToCSV csvGenerator;
    /**
     * The Final model.
     */
    private LinearRegression finalModel;

    /**
     * The User.
     */
    private String user = "";

    /**
     * Instantiates a new Regression.
     */
    public Regression(){}

    /**
     * Instantiates a new Regression.
     *
     * @param folds        the folds
     * @param grade        the grade
     * @param t            the t
     * @param q            the q
     * @param fromTraining the from training
     * @throws IOException the io exception
     */
    public Regression(int folds, int grade, String t, String q, int fromTraining) throws IOException {
        csvGenerator =  new qTableToCSV(t,q, fromTraining);

        this.folds  = folds;
        this.maxGrade = grade;
        this.grade = grade;
        this.simple = true;
    }

    /**
     * Instantiates a new Regression.
     *
     * @param folds        the folds
     * @param grade        the grade
     * @param maxGrade     the max grade
     * @param user         the user
     * @param t            the t
     * @param q            the q
     * @param fromTraining the from training
     * @throws IOException the io exception
     */
    public Regression(int folds, int grade, int maxGrade, String user, String t, String q, int fromTraining) throws IOException {
        csvGenerator =  new qTableToCSV(t,q, fromTraining);

        this.user = user;
        this.folds  = folds;
        this.grade = grade;
        this.maxGrade = maxGrade;
        this.simple = false;

        this.data = System.getProperty("user.dir") + "/.files/qTable" + user +".csv";
        this.modelPath =  System.getProperty("user.dir") + "/.files/regressionModel" + user +".model";
    }

    /**
     * Load dataset.
     *
     * @throws IOException the io exception
     */
    private void loadDataset() throws IOException {
        CSVLoader loader = new CSVLoader();
        loader.setFieldSeparator(",");
        loader.setSource(new File(data));
        Instances dataSet = loader.getDataSet();
        Collections.shuffle(dataSet);
        dataSet.setClassIndex(1);

        this.dataSet = dataSet;//new Instances(dataSet, 0, 300);
        if(folds > dataSet.size()){
            folds = dataSet.size();
        }
        int f = (int) (Math.random()*folds)%folds;
        this.trainDataSet = this.dataSet.trainCV(folds,f);
        this.validateDataSet = this.dataSet.testCV(folds,f);
    }

    /**
     * Calculate model.
     *
     * @throws Exception the exception
     */
    public void calculateModel() throws Exception {
        if(simple){
            simpleRegression();
        }
        else{
            kFoldCrossValidationPolynomialRegression();
        }
    }

    /**
     * K fold cross validation polynomial regression.
     *
     * @throws Exception the exception
     */
    public void kFoldCrossValidationPolynomialRegression() throws Exception {
        double bestError = 999999.0;
        int bestGrade = 1;
        Evaluation bestEval = null;// = new Evaluation(trainDataSet);
        LinearRegression bestModel = null; // new LinearRegression();;

        for(int i = grade; i <=  maxGrade; ++i){
            csvGenerator.generateCSV(i);
            loadDataset();

            double meanError = 0.0;
            int foldsAux = foldsCV;
            if(foldsCV > validateDataSet.size()){
                foldsAux = validateDataSet.size();
            }
            for(int j = 2; j <= foldsAux; ++j){
                int f = (int) (Math.random()*j)%j;
                Instances trainAux = trainDataSet.trainCV(j,f);
                Instances testAux = trainDataSet.testCV(j,f);
                LinearRegression model = new LinearRegression();
                model.buildClassifier(trainAux);
                Evaluation eval = new Evaluation(trainDataSet);
                eval.evaluateModel(model,testAux);
                meanError += eval.relativeAbsoluteError();
            }

            meanError /= (double)(foldsAux-1);

            System.out.println("Grade: " + i + " Relative absolute error: " + meanError);

            if(meanError < bestError){
                bestError = meanError;
                bestGrade = i;
            }
        }

        csvGenerator.generateCSV(bestGrade);
        loadDataset();
        bestModel = new LinearRegression();
        bestModel.buildClassifier(trainDataSet);
        bestEval = new Evaluation(trainDataSet);
        bestEval.evaluateModel(bestModel,validateDataSet);

        System.out.println("\nBest grade: " + bestGrade);
        System.out.println("** Linear Regression Evaluation with Datasets **");
        System.out.println(bestEval.toSummaryString());
        System.out.print(" the expression for the input data as per alogorithm is ");
        System.out.println(bestModel);
        saveModel(bestModel);
        finalModel  = bestModel;
    }

    /**
     * Simple regression.
     *
     * @throws Exception the exception
     */
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

    /**
     * Save model.
     *
     * @param model the model
     * @throws IOException the io exception
     */
    private void saveModel(LinearRegression model) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(modelPath));
        oos.writeObject(model);
        oos.flush();
        oos.close();
    }

    /**
     * Load model.
     */
    public void loadModel()  {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(modelPath));
            finalModel = (LinearRegression) ois.readObject();
            ois.close();
        }catch (Exception e){
            finalModel = null;
            //e.printStackTrace();
            System.out.println("No se ha podido cargar el modelo, por lo que se empezará de 0");
        }
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets model path.
     *
     * @return the model path
     */
    public String getModelPath() {
        return modelPath;
    }

    /**
     * Sets model path.
     *
     * @param modelPath the model path
     */
    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    /**
     * Gets data set.
     *
     * @return the data set
     */
    public Instances getDataSet() {
        return dataSet;
    }

    /**
     * Sets data set.
     *
     * @param dataSet the data set
     */
    public void setDataSet(Instances dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Gets train data set.
     *
     * @return the train data set
     */
    public Instances getTrainDataSet() {
        return trainDataSet;
    }

    /**
     * Sets train data set.
     *
     * @param trainDataSet the train data set
     */
    public void setTrainDataSet(Instances trainDataSet) {
        this.trainDataSet = trainDataSet;
    }

    /**
     * Gets validate data set.
     *
     * @return the validate data set
     */
    public Instances getValidateDataSet() {
        return validateDataSet;
    }

    /**
     * Sets validate data set.
     *
     * @param validateDataSet the validate data set
     */
    public void setValidateDataSet(Instances validateDataSet) {
        this.validateDataSet = validateDataSet;
    }

    /**
     * Is simple boolean.
     *
     * @return the boolean
     */
    public boolean isSimple() {
        return simple;
    }

    /**
     * Sets simple.
     *
     * @param simple the simple
     */
    public void setSimple(boolean simple) {
        this.simple = simple;
    }

    /**
     * Gets grade.
     *
     * @return the grade
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Sets grade.
     *
     * @param grade the grade
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * Gets max grade.
     *
     * @return the max grade
     */
    public int getMaxGrade() {
        return maxGrade;
    }

    /**
     * Sets max grade.
     *
     * @param maxGrade the max grade
     */
    public void setMaxGrade(int maxGrade) {
        this.maxGrade = maxGrade;
    }

    /**
     * Gets folds.
     *
     * @return the folds
     */
    public int getFolds() {
        return folds;
    }

    /**
     * Sets folds.
     *
     * @param folds the folds
     */
    public void setFolds(int folds) {
        this.folds = folds;
    }

    /**
     * Gets folds cv.
     *
     * @return the folds cv
     */
    public int getFoldsCV() {
        return foldsCV;
    }

    /**
     * Sets folds cv.
     *
     * @param foldsCV the folds cv
     */
    public void setFoldsCV(int foldsCV) {
        this.foldsCV = foldsCV;
    }

    /**
     * Gets csv generator.
     *
     * @return the csv generator
     */
    public qTableToCSV getCsvGenerator() {
        return csvGenerator;
    }

    /**
     * Sets csv generator.
     *
     * @param csvGenerator the csv generator
     */
    public void setCsvGenerator(qTableToCSV csvGenerator) {
        this.csvGenerator = csvGenerator;
    }

    /**
     * Gets final model.
     *
     * @return the final model
     */
    public LinearRegression getFinalModel() {
        return finalModel;
    }

    /**
     * Sets final model.
     *
     * @param finalModel the final model
     */
    public void setFinalModel(LinearRegression finalModel) {
        this.finalModel = finalModel;
    }
}
