package lib.objects;

// Clase que representa un escenario y su decoración
public class scenary {
    // Animaciones del escenario y su decoración
    animation anim1, anim2;
    // Coordenadas del escenario y su decoración
    int x = -145, y = 0;
    int x2 = -145, y2 = 398;

    public scenary(){}

    public scenary(int x, int y, animation anim1, animation anim2) {
        this.anim1 = anim1;
        this.anim2 = anim2;
        this.x = x;
        this.y = y;
    }

    // Obtener frame del escenario
    public screenObject getFrame1(){
        return anim1.getFrame(x,y, 1);
    }

    // Obtener frame de la decoración
    public screenObject getFrame2(){
        return anim2.getFrame(x2,y2, 1);
    }

    // Getters y setters
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

    public animation getAnim1() {
        return anim1;
    }

    public void setAnim1(animation anim1) {
        this.anim1 = anim1;
    }

    public animation getAnim2() {
        return anim2;
    }

    public void setAnim2(animation anim2) {
        this.anim2 = anim2;
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

}