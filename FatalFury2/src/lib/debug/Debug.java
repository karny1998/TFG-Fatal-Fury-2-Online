package lib.debug;

import java.awt.*;

public class Debug {
    private Boolean debugOn;
    private int width;
    private int height;
    private long updateTime_ns;

    private static long lastFpsCheck = 0;
    private static int currentFps = 0;
    private static int totalFrames = 0;
    private static long nsInASecond = 1/*s*/*1000/*ms*/*1000/*us*/*1000/*ns*/;

    public Debug(Boolean on, int w, int h, long update){
        debugOn = on;
        width = w;
        height = h;
        updateTime_ns = update*1000*1000;
    }

    public int currentFPS(){
        totalFrames++;
            if (System.nanoTime() > lastFpsCheck + updateTime_ns ) {
                lastFpsCheck = System.nanoTime();
                currentFps = (int) Math.ceil(totalFrames / ( (float)  updateTime_ns / (float) nsInASecond ));
                totalFrames = 0;
            }
            return currentFps;
    }

    public void drawFPS(Graphics g){
        if(debugOn) {
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(this.currentFPS()), 2, 10);
        }
    }


}
