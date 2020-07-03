package lib.analysis;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

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
        return fStats.winRatio() * 0.3 + fStats.blockRatio()*0.1 + fStats.hitRatio()*0.1
                + (fStats.remainingLifeMean()/100)*0.1 + ((100-fStats.remainingPlayerLifeMean())/100)*0.15
                + (fStats.remainingTimeMean()/90)*0.1 + fStats.successfulHitPerReceivedRatio()*0.15;
    }

    /**
     * Print efectivity.
     */
    public void printEfectivity(){
        DefaultXYDataset dataset = new DefaultXYDataset();
        int dim = stats.getHistory().getFights().size();
        double mat[][] = new double[2][dim];
        for(int i = 1; i <= dim; ++i){
            mat[0][i-1] = i;
            mat[1][i-1] = 100*efectivity(stats.getHistory().getFights().get(i-1));
        }
        dataset.addSeries("Efectivity", mat);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2));
        JFreeChart chart = ChartFactory.createXYLineChart("IA Efectivity", "Iteration", "Efectivity", dataset, PlotOrientation.VERTICAL, true, false, false);
        chart.getXYPlot().getRangeAxis().setRange(0, 100);
        ((NumberAxis) chart.getXYPlot().getRangeAxis()).setNumberFormatOverride(new DecimalFormat("#'%'"));
        chart.getXYPlot().setRenderer(renderer);

        BufferedImage image = chart.createBufferedImage(600, 400);
        try {
            ImageIO.write(image, "png", new File("Efectivity.png"));
        }catch (Exception e){}
    }

    /**
     * Print stadistics.
     */
    public void printStadistics(){
        DefaultXYDataset dataset = new DefaultXYDataset();
        int dim = stats.getHistory().getFights().size();
        double win[][] = new double[2][dim];
        double block[][] = new double[2][dim];
        double hit[][] = new double[2][dim];
        double life[][] = new double[2][dim];
        double playerLife[][] = new double[2][dim];
        double time[][] = new double[2][dim];
        double hitPerReceived[][] = new double[2][dim];
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
        }
        dataset.addSeries("Round win ratio", win);
        dataset.addSeries("Block ratio", block);
        dataset.addSeries("Succesful hit ratio", hit);
        dataset.addSeries("Remaining life", life);
        dataset.addSeries("Player remainig life", playerLife);
        dataset.addSeries("Remaining time", time);
        dataset.addSeries("Successful hit per received", hitPerReceived);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
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
}
