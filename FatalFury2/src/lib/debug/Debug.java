package lib.debug;

import java.awt.*;

/**
 * The type Debug.
 */
//Clase encargada de mostrar los Frames por segundo para realizar tareas de depuración
public class Debug {
    /**
     * The Debug on.
     */
    private Boolean debugOn;
    /**
     * The Width.
     */
    private int width;
    /**
     * The Height.
     */
    private int height;
    /**
     * The Update time ns.
     */
    private long updateTime_ns;

    /**
     * The constant lastFpsCheck.
     */
    private static long lastFpsCheck = 0;
    /**
     * The constant currentFps.
     */
    private static int currentFps = 0;
    /**
     * The constant totalFrames.
     */
    private static int totalFrames = 0;
    /**
     * The constant nsInASecond.
     */
    private static long nsInASecond = 1/*s*/*1000/*ms*/*1000/*us*/*1000/*ns*/;

    /**
     * Instantiates a new Debug.
     *
     * @param on     the on
     * @param w      the w
     * @param h      the h
     * @param update the update
     */
// Inicializa los valores
    public Debug(Boolean on, int w, int h, long update){
        debugOn = on;
        width = w;
        height = h;
        updateTime_ns = update*1000*1000;
    }

    /**
     * Current fps int.
     *
     * @return the int
     */
// Calcula los frames por segundo actuales
    public int currentFPS(){
        totalFrames++;
            if (System.nanoTime() > lastFpsCheck + updateTime_ns ) {
                lastFpsCheck = System.nanoTime();
                currentFps = (int) Math.ceil(totalFrames / ( (float)  updateTime_ns / (float) nsInASecond ));
                totalFrames = 0;
            }
            return currentFps;
    }

    /**
     * Draw fps.
     *
     * @param g the g
     */
// Muestra por pantalla los frames por segundo calculados
    public void drawFPS(Graphics g){
        if(debugOn) {
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(this.currentFPS()), 2, 10);
        }
    }





}
