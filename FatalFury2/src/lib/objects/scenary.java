package lib.objects;

import lib.Enums.Scenario_time;
import lib.Enums.Scenario_type;

// Clase que representa un escenario y su decoración
public class scenary {
    // Tiempo del escenario
    Scenario_time currentTime;
    // Animaciones del escenario y su decoración
    animation anim1_dawn, anim2_dawn;
    animation anim1_night, anim2_night;
    animation anim1_sunset, anim2_sunset;
    // Coordenadas del escenario y su decoración
    int x = -145, y = 0;
    int x2 = -145, y2 = 398;

    public scenary(){}

    public scenary(Scenario_type type) {
        switch (type) {
            case USA:
                loadUsa();
                break;
            case CHINA:
                loadChina();
                break;
            case AUSTRALIA:
                loadAustralia();
                break;
        }
        this.currentTime = Scenario_time.DAWN;
    }

    // Cargar animaciones de USA
    public void loadUsa() {
        usa Usa;
        anim1_dawn = usa.generateAnimation1(Scenario_time.DAWN);
        anim1_night = usa.generateAnimation1(Scenario_time.NIGHT);
        anim1_sunset = usa.generateAnimation1(Scenario_time.SUNSET);
        anim2_dawn = usa.generateAnimation2();
        anim2_night = usa.generateAnimation2();
        anim2_sunset = usa.generateAnimation2();   
    }

    // Cargar animaciones de CHINA
    public void loadChina() {

    }

    // Cargar animaciones de AUSTRALIA
    public void loadAustralia() {

    }
    // Obtener frame del escenario
    public screenObject getFrame1(){
        screenObject frame;
        switch (currentTime) {
            case DAWN:
                frame = anim1_dawn.getFrame(x,y, 1);
                break;
            case SUNSET:
                frame = anim1_sunset.getFrame(x,y, 1);
                break;
            case NIGHT:
                frame = anim1_night.getFrame(x,y, 1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentTime);
        }
        return frame;
    }

    // Obtener frame de la decoración
    public screenObject getFrame2(){
        screenObject frame;
        switch (currentTime) {
            case DAWN:
                frame = anim2_dawn.getFrame(x2,y2, 1);
                break;
            case SUNSET:
                frame = anim2_sunset.getFrame(x2,y2, 1);
                break;
            case NIGHT:
                frame = anim2_night.getFrame(x2,y2, 1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentTime);
        }
        return frame;
    }

    // Getters y setters
    public Scenario_time getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Scenario_time currentTime) {
        this.currentTime = currentTime;
    }

    public animation getAnim1_dawn() {
        return anim1_dawn;
    }

    public void setAnim1_dawn(animation anim1_dawn) {
        this.anim1_dawn = anim1_dawn;
    }

    public animation getAnim2_dawn() {
        return anim2_dawn;
    }

    public void setAnim2_dawn(animation anim2_dawn) {
        this.anim2_dawn = anim2_dawn;
    }

    public animation getAnim1_night() {
        return anim1_night;
    }

    public void setAnim1_night(animation anim1_night) {
        this.anim1_night = anim1_night;
    }

    public animation getAnim2_night() {
        return anim2_night;
    }

    public void setAnim2_night(animation anim2_night) {
        this.anim2_night = anim2_night;
    }

    public animation getAnim1_sunset() {
        return anim1_sunset;
    }

    public void setAnim1_sunset(animation anim1_sunset) {
        this.anim1_sunset = anim1_sunset;
    }

    public animation getAnim2_sunset() {
        return anim2_sunset;
    }

    public void setAnim2_sunset(animation anim2_sunset) {
        this.anim2_sunset = anim2_sunset;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }
}
