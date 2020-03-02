package lib.objects;
import java.awt.Image;

//Tipos de elementos que se van a imprimir por pantalla
enum type{none, scenary, player, enemy, playerThrowable, enemyThrowable}

//Elemento que se acabará mostrando por pantalla
public class screenObject {
    //Tipo de elemento
    private type objectType = type.none;
    //Posición
    private int x = 1, y = 1;
    //Dimensiones
    private int width = 1, height = 1;
    //Imagen
    private Image img;

    // Getters y setters
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Image getImg() {
        return img;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public type getObjectType() {
        return objectType;
    }

    public void setObjectType(type objectType) {
        this.objectType = objectType;
    }

    //Constructor principal completo
    public screenObject(int _x, int _y, int _width, int _height, Image _img, type _t){
        x = _x; y = _y; img = _img;
        width = _width; height = _height;
        objectType = _t;
    }

    // Constructor mínimo
    public screenObject(Image _img){
        img = _img;
    }

}
