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

    private int grade = 5;

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
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(grade);
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
        /*double mat[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{288.0,
                321.0,
                172.0,
                308.0,
                336.0,
                251.0,
                260.0,
                241.0,
                178.0,
                207.0,
                220.0,
                227.0,
                60.0,
                207.0,
                241.0,
                244.0,
                368.0,
                362.0,
                300.0,
                88.0,
                387.0,
                375.0}};*/
        for(int i = 1; i <= dim; ++i){
            mat[0][i-1] = i;
            mat[1][i-1] = stats.getHistory().getFights().get(i-1).getAccumulatedReward();
            obs.add(i, mat[1][i-1]);
        }

        printTable(dim,mat,"REWARD");

        if(simplified) {
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(grade);
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
        JFreeChart chart = ChartFactory.createXYLineChart("Recompensa acumulada durante las partidas", "Iteración", "Recompensa acumulada", dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.getXYPlot().getRangeAxis().setRange(-1000, 1000);
        ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat());
        chart.getXYPlot().setRenderer(renderer);

        BufferedImage image = chart.createBufferedImage(1000, 800);
        try {
            ImageIO.write(image, "png", new File("AccumulatedReward.png"));
        }catch (Exception e){}
    }

    public void printTable(int t, double table[][], String name){
        System.out.println(name);
        for(int i = 0; i < t; ++i){
            System.out.println("{"+table[0][i] + "," + table[1][i] + "},");
        }
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
        /*double win[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{100.0,
                94.4444444444,
                100.0,
                93.3333333333,
                100.0,
                91.6666666667,
                100.0,
                77.7777777778,
                100.0,
                100.0,
                100.0,
                100.0,
                83.3333333333,
                100.0,
                100.0,
                100.0,
                100.0,
                100.0,
                83.3333333333,
                74.333333,
                100.0,
                100.0}};
        double block[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{3.75,
                0.0,
                0.0,
                2.0,
                0.0,
                0.0,
                2.08333333333,
                6.66666666667,
                0.0,
                0.0,
                0.0,
                0.0,
                2.08333333333,
                0.0,
                0.0,
                8.33333333333,
                0.0,
                0.0,
                6.81818181818,
                0.0,
                0.0,
                0.0}};
        double hit[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{48.2084323628,
                51.1251820791,
                40.5871651497,
                47.0436932349,
                63.9039953102,
                48.1643356643,
                54.7194272446,
                44.8091086386,
                55.9866984867,
                57.5757575758,
                45.931372549,
                48.0965909091,
                46.4577497666,
                53.9204545455,
                52.0698051948,
                57.0512820513,
                46.1217948718,
                38.6887254902,
                58.51795263559969,
                30.1948051948,
                60.256410256410255,
                77.08333333333333}};
        double life[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{62.9166666667,
                50.8055555556,
                38.0833333333,
                47.2666666667,
                52,
                71.5,
                46.4583333333,
                40.0,
                54.8333333333,
                49.25,
                50.25,
                49.5,
                42.25,
                44.74,
                65.25,
                62.25,
                55.0,
                51.75,
                35.5833333333,
                31.5,
                61.0,
                59.0}};
        double playerLife[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{0.0,
                2.33333333333,
                15,
                0.4,
                0.0,
                6.58333333333,
                0.0,
                4.11111111111,
                0.0,
                0.0,
                0.0,
                0.0,
                0.16666666666,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                2.33333333333,
                27.666,
                0.0,
                0.0}};
        double time[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{87.4074074074,
                87.9012345679,
                81.0185185185,
                85.8518518519,
                90.5555555556,
                87.8240740741,
                85.2777777778,
                83.024691358,
                86.1111111111,
                88.8888888889,
                87.5,
                86.1111111111,
                85.6481481481,
                88.8888888889,
                89.4444444444,
                87.7777777778,
                86.9444444444,
                83.0555555556,
                81.3888888889,
                69.4444444444,
                91.11111111111111,
                90.55555555555556}};
        double hitPerReceived[][] = {{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0},{59.6199333488,
                54.8312040263,
                45.1038401254,
                55.802378298,
                62.5761728737,
                54.1520755459,
                51.6438756428,
                46.5210321668,
                58.1290767827,
                53.2608695652,
                52.380952381,
                54.0,
                53.869047619,
                53.2727272727,
                61.9883040936,
                58.5129310345,
                57.8063241107,
                59.7222222222,
                46.6666666667,
                34.0909090909,
                61.904761904761905,
                65.0}};*/
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


        //////////////////////////////////////
        printTable(dim, win,"WINS");
        printTable(dim, block,"BLOCK");
        printTable(dim, hit,"HIT");
        printTable(dim, life,"LIFE");
        printTable(dim, playerLife,"PLAYERLIFE");
        printTable(dim, time,"TIME");
        printTable(dim, hitPerReceived,"HITPR");
        //////////////////////////////////////

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        if(simplified){
            String series[] = {"Ganar una ronda","Bloquear un ataque","Golpear a un enemigo con un ataque",
                    "Vida restante al final de una ronda","Vida restante del enemigo al final de una ronda",
                    "Tiempo restante al final de una ronda","Ataques exitosos de la IA sobre el total de exitosos"};
            WeightedObservedPoints obs[] = {obs1,obs2,obs3,obs4,obs5,obs6,obs7};
            for(int j = 0; j < 7;++j) {
                PolynomialCurveFitter fitter = PolynomialCurveFitter.create(grade);
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
        JFreeChart chart = ChartFactory.createXYLineChart("Resumen del aprendizaje", "Iteración", "Promedio/Probabilidad", dataset, PlotOrientation.VERTICAL, true, false, false);
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
            PolynomialCurveFitter fitter = PolynomialCurveFitter.create(grade);
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

        printGraphic(simplified, win, obs1,"Won rounds", Color.BLUE, "Rondas ganadas por partida", "Iteración", "Rondas ganadas", "RoundWinRatio");
        printGraphic(simplified, block, obs2,"Blocked attacks", Color.YELLOW, "Ataques bloqueados del total de recibidos", "Iteración", "Ataques bloqueados", "BlockRatio");
        printGraphic(simplified, hit, obs3,"Succesful attacks", Color.ORANGE, "Ataques que golpearon al enemigo del total realizados", "Iteración", "Ataques exitosos", "SuccessfulHitRatio");
        printGraphic(simplified, life, obs4,"Remaining AI Life", Color.RED, "Vida media restante al final de una ronda", "Iteración", "Vida media restante", "RemainingLife");
        printGraphic(simplified, playerLife, obs5,"Remaining enemy Life", Color.GREEN, "Vida media restante del enemigo al final de una ronda", "Iteración", "Vida media restante del enemigo", "EnemyRemainingLife");
        printGraphic(simplified, time, obs6,"Remaining time", Color.PINK, "Tiempo medio restante al final de una ronda", "Iteración", "Tiempo medio restante", "RemainingTime");
        printGraphic(simplified, hitPerReceived, obs7,"Successful attack per received", Color.MAGENTA, "Ataques exitosos realizados por la IA del total de ataques exitosos", "Iteración", "Attack per received", "HitPerReceivedRatio");
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

        printTable(dim,iaWins,"IA WINS");
        printTable(dim,enemyWins,"ENEMY WINS");

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
