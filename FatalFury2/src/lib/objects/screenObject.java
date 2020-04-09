package lib.objects;
import lib.Enums.Item_Type;

import java.awt.*;

// Elemento que se acabará mostrando por pantalla
public class screenObject {
    //Tipo de elemento
    private Item_Type objectType = Item_Type.NONE;
    //Posición
    private int x = 1, y = 1;
    //Dimensiones
    private int width = 1, height = 1;
    //Imagen
    private Image img;

    //Constructor principal completo
    public screenObject(int _x, int _y, int _width, int _height, Image _img, Item_Type _t){
        x = _x; y = _y; img = _img;
        width = _width; height = _height;
        objectType = _t;
    }

    // Constructor mínimo
    public screenObject(Image _img){
        img = _img;
    }

    // Clonación de un screenObject
    public screenObject cloneSO(){
        return new screenObject(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getImg(), this.getObjectType());
    }

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

    public Item_Type getObjectType() {
        return objectType;
    }

    public void setObjectType(Item_Type objectType) {
        this.objectType = objectType;
    }
}
