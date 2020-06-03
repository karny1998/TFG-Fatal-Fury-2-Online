package lib.objects;

import java.awt.*;

/**
 * The enum Box type.
 */
enum box_type{
    /**
     * Hitbox box type.
     */
    HITBOX,
    /**
     * Hurtbox box type.
     */
    HURTBOX,
    /**
     * Coverbox box type.
     */
    COVERBOX}

/**
 * The type Hit box.
 */
public class hitBox {
    /**
     * The X.
     */
// Coordenadas dentro del sprite (suponiendo la esquina
    // del sprite como 0,0)
    private int x, /**
     * The Y.
     */
    y;
    /**
     * The Width.
     */
// Ancho y alto de la caja
    private int width, /**
     * The Height.
     */
    height;
    /**
     * The Type.
     */
// Si es hitbox, hurtbox o coverbox
    private box_type type = box_type.HURTBOX;

    /**
     * Instantiates a new Hit box.
     */
    public hitBox(){}

    /**
     * Instantiates a new Hit box.
     *
     * @param originX the origin x
     * @param originY the origin y
     * @param width   the width
     * @param height  the height
     * @param type    the type
     */
    public hitBox(int originX, int originY, int width, int height, box_type type) {
        this.x = originX;               this.y = originY;
        this.width = width;             this.height = height;
        this.type = type;
    }

    /**
     * Update hit box.
     *
     * @param newX      the new x
     * @param newY      the new y
     * @param newWidth  the new width
     * @param newHeight the new height
     */
    public void updateHitBox(int newX, int newY, int newWidth, int newHeight) {
        this.x = newX;                  this.y = newY;
        this.width = newWidth;          this.height = newHeight;
    }

    /**
     * Collides boolean.
     *
     * @param other the other
     * @return the boolean
     */
// Devuelve cierto si esta Hitbox colisiona con otra
    public Boolean collides(hitBox other) {
        if (this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y) {
            return true;
        }
        return false;
    }

    /**
     * Draw hit box.
     *
     * @param g the g
     */
// Muestra la hitbox por pantalla
    public void drawHitBox(Graphics g) {
        if (this.type == box_type.HITBOX) {
            g.setColor(new Color(1,0,0, (float) 0.5));
        }
        else if (this.type == box_type.COVERBOX){
            g.setColor(new Color(0,1,0, (float) 0.5));
        }
        else {
            g.setColor(new Color(0,0,1, (float) 0.5));
        }
        g.fillRect(this.x, this.y, this.width, this.height);
        g.setColor(Color.WHITE);
        g.drawString(""+x+" "+ y, x, y);
    }

    /**
     * Is hitbox boolean.
     *
     * @return the boolean
     */
    public Boolean isHitbox() { return type == box_type.HITBOX; }

    /**
     * Is hurtbox boolean.
     *
     * @return the boolean
     */
    public Boolean isHurtbox() { return type == box_type.HURTBOX; }

    /**
     * Is coverbox boolean.
     *
     * @return the boolean
     */
    public Boolean isCoverbox() { return type == box_type.COVERBOX; }

    /**
     * Gets width.
     *
     * @return the width
     */
    public int getWidth() { return width; }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
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
     * Sets width.
     *
     * @param width the width
     */
    public void setWidth(int width) {
        this.width = width;
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
     * Gets type.
     *
     * @return the type
     */
    public box_type getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(box_type type) {
        this.type = type;
    }

    /**
     * Gets height.
     *
     * @return the height
     */
    public int getHeight() { return height; }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() { return x; }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() { return y; }

}
