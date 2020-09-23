package lib.maps;

import lib.Enums.Scenario_time;
import lib.Enums.Scenario_type;
import lib.objects.animation;
import lib.objects.screenObject;

/**
 * The type Scenary.
 */
// Clase que representa un escenario y su decoración
public class scenary {
    /**
     * The Current time.
     */
// Tiempo del escenario
    Scenario_time currentTime;
    /**
     * The Anim 1 dawn.
     */
// Animaciones del escenario y su decoración
    animation anim1_dawn, /**
     * The Anim 2 dawn.
     */
    anim2_dawn;
    /**
     * The Anim 1 night.
     */
    animation anim1_night, /**
     * The Anim 2 night.
     */
    anim2_night;
    /**
     * The Anim 1 sunset.
     */
    animation anim1_sunset, /**
     * The Anim 2 sunset.
     */
    anim2_sunset;
    /**
     * The X.
     */
// Coordenadas del escenario y su decoración
    int x, /**
     * The Y.
     */
    y;
    /**
     * The X 2.
     */
    int x2, /**
     * The Y 2.
     */
    y2;

    /**
     * The Scenario.
     */
    Scenario_type scenario;

    /**
     * Get scenario scenario type.
     *
     * @return the scenario type
     */
    public Scenario_type getScenario(){
        return scenario;
    }

    /**
     * Instantiates a new Scenary.
     */
    public scenary(){}

    /**
     * Instantiates a new Scenary.
     *
     * @param type the type
     */
    public scenary(Scenario_type type) {
        scenario = type;
        switch (type) {
            case USA:
                x = -145;
                y = -60;
                x2 = -145;
                y2 = 398;
                loadUsa();
                break;
            case CHINA:
                x = -145;
                y = -60;
                x2 = -145;
                y2 = 57;
                loadChina();
                break;
            case AUSTRALIA:
                x = -145;
                y = -60;
                x2 = -145;
                y2 = 90;
                loadAustralia();
                break;
        }
        this.currentTime = Scenario_time.DAWN;
    }

    /**
     * Load usa.
     */
// Cargar animaciones de USA
    public void loadUsa() {
        anim1_dawn = usa.generateAnimation1(Scenario_time.DAWN);
        anim1_night = usa.generateAnimation1(Scenario_time.NIGHT);
        anim1_sunset = usa.generateAnimation1(Scenario_time.SUNSET);
        anim2_dawn = usa.generateAnimation2();
        anim2_night = usa.generateAnimation2();
        anim2_sunset = usa.generateAnimation2();   
    }

    /**
     * Load china.
     */
// Cargar animaciones de CHINA
    public void loadChina() {
        anim1_dawn = china.generateAnimation1(Scenario_time.DAWN);
        anim1_night = china.generateAnimation1(Scenario_time.NIGHT);
        anim1_sunset = china.generateAnimation1(Scenario_time.SUNSET);
        anim2_dawn = china.generateAnimation2(Scenario_time.DAWN);
        anim2_night = china.generateAnimation2(Scenario_time.NIGHT);
        anim2_sunset = china.generateAnimation2(Scenario_time.SUNSET);
    }

    /**
     * Load australia.
     */
// Cargar animaciones de AUSTRALIA
    public void loadAustralia() {
        anim1_dawn = australia.generateAnimation1(Scenario_time.DAWN);
        anim1_night = australia.generateAnimation1(Scenario_time.NIGHT);
        anim1_sunset = australia.generateAnimation1(Scenario_time.SUNSET);
        anim2_dawn = australia.generateAnimation2(Scenario_time.DAWN);
        anim2_night = australia.generateAnimation2(Scenario_time.NIGHT);
        anim2_sunset = australia.generateAnimation2(Scenario_time.SUNSET);
    }

    /**
     * Get frame 1 screen object.
     *
     * @return the screen object
     */
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

    /**
     * Get frame 2 screen object.
     *
     * @return the screen object
     */
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

    /**
     * Gets current time.
     *
     * @return the current time
     */
// Getters y setters
    public Scenario_time getCurrentTime() {
        return currentTime;
    }

    /**
     * Sets current time.
     *
     * @param currentTime the current time
     */
    public void setCurrentTime(Scenario_time currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * Gets anim 1 dawn.
     *
     * @return the anim 1 dawn
     */
    public animation getAnim1_dawn() {
        return anim1_dawn;
    }

    /**
     * Sets anim 1 dawn.
     *
     * @param anim1_dawn the anim 1 dawn
     */
    public void setAnim1_dawn(animation anim1_dawn) {
        this.anim1_dawn = anim1_dawn;
    }

    /**
     * Gets anim 2 dawn.
     *
     * @return the anim 2 dawn
     */
    public animation getAnim2_dawn() {
        return anim2_dawn;
    }

    /**
     * Sets anim 2 dawn.
     *
     * @param anim2_dawn the anim 2 dawn
     */
    public void setAnim2_dawn(animation anim2_dawn) {
        this.anim2_dawn = anim2_dawn;
    }

    /**
     * Gets anim 1 night.
     *
     * @return the anim 1 night
     */
    public animation getAnim1_night() {
        return anim1_night;
    }

    /**
     * Sets anim 1 night.
     *
     * @param anim1_night the anim 1 night
     */
    public void setAnim1_night(animation anim1_night) {
        this.anim1_night = anim1_night;
    }

    /**
     * Gets anim 2 night.
     *
     * @return the anim 2 night
     */
    public animation getAnim2_night() {
        return anim2_night;
    }

    /**
     * Sets anim 2 night.
     *
     * @param anim2_night the anim 2 night
     */
    public void setAnim2_night(animation anim2_night) {
        this.anim2_night = anim2_night;
    }

    /**
     * Gets anim 1 sunset.
     *
     * @return the anim 1 sunset
     */
    public animation getAnim1_sunset() {
        return anim1_sunset;
    }

    /**
     * Sets anim 1 sunset.
     *
     * @param anim1_sunset the anim 1 sunset
     */
    public void setAnim1_sunset(animation anim1_sunset) {
        this.anim1_sunset = anim1_sunset;
    }

    /**
     * Gets anim 2 sunset.
     *
     * @return the anim 2 sunset
     */
    public animation getAnim2_sunset() {
        return anim2_sunset;
    }

    /**
     * Sets anim 2 sunset.
     *
     * @param anim2_sunset the anim 2 sunset
     */
    public void setAnim2_sunset(animation anim2_sunset) {
        this.anim2_sunset = anim2_sunset;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets x 2.
     *
     * @return the x 2
     */
    public int getX2() {
        return x2;
    }

    /**
     * Sets x 2.
     *
     * @param x2 the x 2
     */
    public void setX2(int x2) {
        this.x2 = x2;
    }

    /**
     * Gets y 2.
     *
     * @return the y 2
     */
    public int getY2() {
        return y2;
    }

    /**
     * Sets y 2.
     *
     * @param y2 the y 2
     */
    public void setY2(int y2) {
        this.y2 = y2;
    }
}
