package lib.objects;
import lib.Enums.Item_Type;

import java.awt.*;

/**
 * The type Screen object.
 */
// Elemento que se acabará mostrando por pantalla
public class screenObject {
    /**
     * The Object type.
     */
//Tipo de elemento
    private Item_Type objectType = Item_Type.NONE;
    /**
     * The X.
     */
//Posición
    private int x = 1, /**
     * The Y.
     */
    y = 1;
    /**
     * The Width.
     */
//Dimensiones
    private int width = 1, /**
     * The Height.
     */
    height = 1;
    /**
     * The Img.
     */
//Imagen
    private Image img;

    /**
     * Instantiates a new Screen object.
     *
     * @param _x      the x
     * @param _y      the y
     * @param _width  the width
     * @param _height the height
     * @param _img    the img
     * @param _t      the t
     */
//Constructor principal completo
    public screenObject(int _x, int _y, int _width, int _height, Image _img, Item_Type _t){
        x = _x; y = _y; img = _img;
        width = _width; height = _height;
        objectType = _t;
    }

    /**
     * Instantiates a new Screen object.
     *
     * @param _img the img
     */
// Constructor mínimo
    public screenObject(Image _img){
        img = _img;
    }

    /**
     * Clone so screen object.
     *
     * @return the screen object
     */
// Clonación de un screenObject
    public screenObject cloneSO(){
        return new screenObject(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getImg(), this.getObjectType());
    }

    /**
     * Gets width.
     *
     * @return the width
     */
// Getters y setters
    public int getWidth() {
        return width;
    }

    /**
     * Sets width.
     *
     * @param width the width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets height.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets height.
     *
     * @param height the height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets img.
     *
     * @param img the img
     */
    public void setImg(Image img) {
        this.img = img;
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
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets img.
     *
     * @return the img
     */
    public Image getImg() {
        return img;
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
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Gets object type.
     *
     * @return the object type
     */
    public Item_Type getObjectType() {
        return objectType;
    }

    /**
     * Sets object type.
     *
     * @param objectType the object type
     */
    public void setObjectType(Item_Type objectType) {
        this.objectType = objectType;
    }
}
