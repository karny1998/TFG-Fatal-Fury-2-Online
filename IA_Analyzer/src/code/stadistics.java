import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;

/**
 * Clase para calcular estadísticas y su representación gráfica
 */
public class stadistics {
    /**
     * Datos de la IA a analizar
     */
    private ia_stats stats;

    /**
     * Instantiates a new Stadistics.
     *
     * @param file the file
     */
    public  stadistics(String file){
        stats = new ia_stats();
        stats.setFilename(file);
        stats.loadHistory();
    }

    /**
     * Load the history from file.
     *
     * @param file the file
     */
    public void load(String file){
        stats.setFilename(file);
        stats.loadHistory();
    }

    /**
     * Efectivity double.
     *
     * @param fStats the f stats
     * @return the double
     */
    public double efectivity(fight_stats fStats){
        return fStats.winRatio() * 0.6 + fStats.blockRatio()*0.05 + fStats.hitRatio()*0.05
                + (fStats.remainingLifeMean()/100)*0.1 + ((100-fStats.remainingPlayerLifeMean())/100)*0.1
                + (fStats.remainingTimeMean()/90)*0.05 + fStats.successfulHitPerReceivedRatio()*0.05;
    }

    /**
     * Print efectivity.
     */
    public void printEfectivity(boolean simplified){
        DefaultXYDataset dataset = new DefaultXYDataset();
        WeightedObservedPoints obs = new WeightedObservedPoints();
        int dim = stats.getHistory().getFights().size();
        double mat[][] = new double[2][dim];
        for(int i = 1; i <= dim; ++i){
            mat[0][i-1] = i;
            mat[1][i-1] = 100*efectivity(stats.getHistory().getFights().get(i-1));
            obs.add(i, mat[1][i-1]);
        }

        if(simplified) {
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(5);
            PolynomialFunction fitted = new PolynomialFunction(fitter.fit(obs.toList()));

            double mat2[][] = new double[2][dim];
            for (int i = 1; i <= dim; ++i) {
                mat2[0][i - 1] = i;
                mat2[1][i - 1] = fitted.value(i);
            }
            dataset.addSeries("Efectivity", mat2);
        }
        else{
            dataset.addSeries("Efectivity", mat);
        }

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2));
        JFreeChart chart = ChartFactory.createXYLineChart("IA Efectivity", "Iteration", "Efectivity", dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.getXYPlot().getRangeAxis().setRange(0, 100);
        ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#'%'"));
        chart.getXYPlot().setRenderer(renderer);

        BufferedImage image = chart.createBufferedImage(1000, 800);
        try {
            ImageIO.write(image, "png", new File("Efectivity.png"));
        }catch (Exception e){}
    }

    /**
     * Print accumulatedReward.
     */
    public void printAccumulatedReward(boolean simplified){
        DefaultXYDataset dataset = new DefaultXYDataset();
        WeightedObservedPoints obs = new WeightedObservedPoints();
        int dim = stats.getHistory().getFights().size();
        double mat[][] = new double[2][dim];
        for(int i = 1; i <= dim; ++i){
            mat[0][i-1] = i;
            mat[1][i-1] = stats.getHistory().getFights().get(i-1).getAccumulatedReward();
            obs.add(i, mat[1][i-1]);
        }

        if(simplified) {
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(5);
            PolynomialFunction fitted = new PolynomialFunction(fitter.fit(obs.toList()));

            double mat2[][] = new double[2][dim];
            for (int i = 1; i <= dim; ++i) {
                mat2[0][i - 1] = i;
                mat2[1][i - 1] = fitted.value(i);
            }
            dataset.addSeries("Accumulate reward", mat2);
        }
        else{
            dataset.addSeries("Accumulate reward", mat);
        }

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2));
        JFreeChart chart = ChartFactory.createXYLineChart("IA Accumulated reward during the games", "Iteration", "Accumulated reward in a game", dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.getXYPlot().getRangeAxis().setRange(-1000, 1000);
        ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat());
        chart.getXYPlot().setRenderer(renderer);

        BufferedImage image = chart.createBufferedImage(1000, 800);
        try {
            ImageIO.write(image, "png", new File("AccumulatedReward.png"));
        }catch (Exception e){}
    }

    /**
     * Print stadistics.
     */
    public void printStadistics(boolean simplified){
        DefaultXYDataset dataset = new DefaultXYDataset();
        int dim = stats.getHistory().getFights().size();
        double win[][] = new double[2][dim];
        double block[][] = new double[2][dim];
        double hit[][] = new double[2][dim];
        double life[][] = new double[2][dim];
        double playerLife[][] = new double[2][dim];
        double time[][] = new double[2][dim];
        double hitPerReceived[][] = new double[2][dim];
        WeightedObservedPoints obs1 = new WeightedObservedPoints();
        WeightedObservedPoints obs2 = new WeightedObservedPoints();
        WeightedObservedPoints obs3 = new WeightedObservedPoints();
        WeightedObservedPoints obs4 = new WeightedObservedPoints();
        WeightedObservedPoints obs5 = new WeightedObservedPoints();
        WeightedObservedPoints obs6 = new WeightedObservedPoints();
        WeightedObservedPoints obs7 = new WeightedObservedPoints();
        for(int i = 1; i <= dim; ++i){
            win[0][i-1] = i;
            block[0][i-1] = i;
            hit[0][i-1] = i;
            life[0][i-1] = i;
            playerLife[0][i-1] = i;
            time[0][i-1] = i;
            hitPerReceived[0][i-1] = i;
            win[1][i-1] = 100*stats.getHistory().getFights().get(i-1).winRatio();
            block[1][i-1] = 100*stats.getHistory().getFights().get(i-1).blockRatio();
            hit[1][i-1] = 100*stats.getHistory().getFights().get(i-1).hitRatio();
            life[1][i-1] = stats.getHistory().getFights().get(i-1).remainingLifeMean();
            playerLife[1][i-1] = stats.getHistory().getFights().get(i-1).remainingPlayerLifeMean();
            time[1][i-1] = 100*stats.getHistory().getFights().get(i-1).remainingTimeMean()/90;
            hitPerReceived[1][i-1] = 100*stats.getHistory().getFights().get(i-1).successfulHitPerReceivedRatio();
            obs1.add(i, win[1][i-1]);
            obs2.add(i, block[1][i-1]);
            obs3.add(i, hit[1][i-1]);
            obs4.add(i, life[1][i-1]);
            obs5.add(i, playerLife[1][i-1]);
            obs6.add(i, time[1][i-1]);
            obs7.add(i, hitPerReceived[1][i-1]);
        }

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        if(simplified){
            String series[] = {"Won rounds in a game","Block ratio","Succesful hit ratio",
                    "Remaining life","Player remainig life","Remaining time","Successful hit per received"};
            WeightedObservedPoints obs[] = {obs1,obs2,obs3,obs4,obs5,obs6,obs7};
            for(int j = 0; j < 7;++j) {
                PolynomialCurveFitter fitter = PolynomialCurveFitter.create(5);
                PolynomialFunction fitted = new PolynomialFunction(fitter.fit(obs[j].toList()));

                double mat2[][] = new double[2][dim];
                for (int i = 1; i <= dim; ++i) {
                    mat2[0][i - 1] = i;
                    mat2[1][i - 1] = fitted.value(i);
                }
                dataset.addSeries(series[j], mat2);
            }
        }
        else {
            dataset.addSeries("Won rounds in a game", win);
            dataset.addSeries("Block ratio", block);
            dataset.addSeries("Succesful hit ratio", hit);
            dataset.addSeries("Remaining life", life);
            dataset.addSeries("Player remainig life", playerLife);
            dataset.addSeries("Remaining time", time);
            dataset.addSeries("Successful hit per received", hitPerReceived);
        }

        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.YELLOW);
        renderer.setSeriesPaint(2, Color.ORANGE);
        renderer.setSeriesPaint(3, Color.RED);
        renderer.setSeriesPaint(4, Color.GREEN);
        renderer.setSeriesPaint(5, Color.PINK);
        renderer.setSeriesPaint(6, Color.MAGENTA);
        renderer.setSeriesStroke(0, new BasicStroke(2));
        renderer.setSeriesStroke(1, new BasicStroke(2));
        renderer.setSeriesStroke(2, new BasicStroke(2));
        renderer.setSeriesStroke(3, new BasicStroke(2));
        renderer.setSeriesStroke(4, new BasicStroke(2));
        renderer.setSeriesStroke(5, new BasicStroke(2));
        renderer.setSeriesStroke(6, new BasicStroke(2));
        JFreeChart chart = ChartFactory.createXYLineChart("IA Efectivity", "Iteration", "Ratio", dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.getXYPlot().getRangeAxis().setRange(0, 100);
        ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#'%'"));
        chart.getXYPlot().setRenderer(renderer);

        BufferedImage image = chart.createBufferedImage(1000, 800);
        try {
            ImageIO.write(image, "png", new File("Stadistics.png"));
        }catch (Exception e){}
    }

    public void printGraphic(boolean simplified, double values[][], WeightedObservedPoints obs, String serie, Color color, String title, String x, String y, String img){
        DefaultXYDataset dataset = new DefaultXYDataset();
        int dim = values[0].length;
        if(simplified) {
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(5);
            PolynomialFunction fitted = new PolynomialFunction(fitter.fit(obs.toList()));

            double mat2[][] = new double[2][dim];
            for (int i = 1; i <= dim; ++i) {
                mat2[0][i - 1] = i;
                mat2[1][i - 1] = fitted.value(i);
            }
            dataset.addSeries(serie, mat2);
        }
        else{
            dataset.addSeries(serie, values);
        }

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, color);
        renderer.setSeriesStroke(0, new BasicStroke(2));
        JFreeChart chart = ChartFactory.createXYLineChart(title, x, y, dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.getXYPlot().getRangeAxis().setRange(0, 100);
        ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#'%'"));
        chart.getXYPlot().setRenderer(renderer);
        BufferedImage image = chart.createBufferedImage(1000, 800);
        try {
            ImageIO.write(image, "png", new File(img+".png"));
        }catch (Exception e){}
    }

    /**
     * Print stadistics.
     */
    public void printStadistics2(boolean simplified){
        DefaultXYDataset dataset = new DefaultXYDataset();
        int dim = stats.getHistory().getFights().size();
        double win[][] = new double[2][dim];
        double block[][] = new double[2][dim];
        double hit[][] = new double[2][dim];
        double life[][] = new double[2][dim];
        double playerLife[][] = new double[2][dim];
        double time[][] = new double[2][dim];
        double hitPerReceived[][] = new double[2][dim];
        WeightedObservedPoints obs1 = new WeightedObservedPoints();
        WeightedObservedPoints obs2 = new WeightedObservedPoints();
        WeightedObservedPoints obs3 = new WeightedObservedPoints();
        WeightedObservedPoints obs4 = new WeightedObservedPoints();
        WeightedObservedPoints obs5 = new WeightedObservedPoints();
        WeightedObservedPoints obs6 = new WeightedObservedPoints();
        WeightedObservedPoints obs7 = new WeightedObservedPoints();
        for(int i = 1; i <= dim; ++i){
            win[0][i-1] = i;
            block[0][i-1] = i;
            hit[0][i-1] = i;
            life[0][i-1] = i;
            playerLife[0][i-1] = i;
            time[0][i-1] = i;
            hitPerReceived[0][i-1] = i;
            win[1][i-1] = 100*stats.getHistory().getFights().get(i-1).winRatio();
            block[1][i-1] = 100*stats.getHistory().getFights().get(i-1).blockRatio();
            hit[1][i-1] = 100*stats.getHistory().getFights().get(i-1).hitRatio();
            life[1][i-1] = stats.getHistory().getFights().get(i-1).remainingLifeMean();
            playerLife[1][i-1] = stats.getHistory().getFights().get(i-1).remainingPlayerLifeMean();
            time[1][i-1] = 100*stats.getHistory().getFights().get(i-1).remainingTimeMean()/90;
            hitPerReceived[1][i-1] = 100*stats.getHistory().getFights().get(i-1).successfulHitPerReceivedRatio();
            obs1.add(i, win[1][i-1]);
            obs2.add(i, block[1][i-1]);
            obs3.add(i, hit[1][i-1]);
            obs4.add(i, life[1][i-1]);
            obs5.add(i, playerLife[1][i-1]);
            obs6.add(i, time[1][i-1]);
            obs7.add(i, hitPerReceived[1][i-1]);
        }

        printGraphic(simplified, win, obs1,"Won rounds", Color.BLUE, "Won rounds in a game", "Iteration", "Won rounds", "RoundWinRatio");
        printGraphic(simplified, block, obs2,"Blocked attacks", Color.YELLOW, "Proportion between received and blocked attacks", "Iteration", "Blocked attacks", "BlockRatio");
        printGraphic(simplified, hit, obs3,"Succesful attacks", Color.ORANGE, "Proportion between realized and successful attacks", "Iteration", "Susccessful attacks", "SuccessfulHitRatio");
        printGraphic(simplified, life, obs4,"Remaining AI Life", Color.RED, "Average remaining AI mean life in a game", "Iteration", "Average AI remaining life", "RemainingLife");
        printGraphic(simplified, playerLife, obs5,"Remaining enemy Life", Color.GREEN, "Average remaining enemy mean life in a game", "Iteration", "Average enemy remaining life", "EnemyRemainingLife");
        printGraphic(simplified, time, obs6,"Remaining time", Color.PINK, "Average remaining time in a game", "Iteration", "Average remaining time", "RemainingTime");
        printGraphic(simplified, hitPerReceived, obs7,"Successful attack per received", Color.MAGENTA, "Proportion between realized and received attacks", "Iteration", "Attack per received", "HitPerReceivedRatio");
    }


    /**
     * Print accumulatedReward.
     */
    public void printAccumulatedWinsGeneral(){
        DefaultXYDataset dataset = new DefaultXYDataset();
        int dim = stats.getHistory().getFights().size();
        double iaWins[][] = new double[2][dim];
        double enemyWins[][] = new double[2][dim];
        double iawins = 0, enemywins = 0;
        for(int i = 1; i <= dim; ++i){
            iaWins[0][i-1] = i;
            enemyWins[0][i-1] = i;
            if(stats.getHistory().getFights().get(i-1).getResult() == 1){
                ++iawins;
            }
            else{
                ++enemywins;
            }
            iaWins[1][i-1] = iawins;
            enemyWins[1][i-1] = enemywins;
        }
        dataset.addSeries("Ia in training wins", iaWins);
        dataset.addSeries("Enemy wins", enemyWins);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2));
        renderer.setSeriesStroke(1, new BasicStroke(2));
        JFreeChart chart = ChartFactory.createXYLineChart("Accumulated wins", "Iteration", "Accumulated wins", dataset, PlotOrientation.VERTICAL, true, false, false);
        //chart.getXYPlot().getRangeAxis().setRange(0, 1000);
        ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat());
        chart.getXYPlot().setRenderer(renderer);

        BufferedImage image = chart.createBufferedImage(1000, 800);
        try {
            ImageIO.write(image, "png", new File("AccumulatedWins.png"));
        }catch (Exception e){}
    }
}
