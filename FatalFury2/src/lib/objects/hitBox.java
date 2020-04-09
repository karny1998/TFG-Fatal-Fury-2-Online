package lib.objects;

import java.awt.*;

public class hitBox {
    // Coordenadas dentro del sprite (suponiendo la esquina
    // del sprite como 0,0)
    private int x, y;
    // Ancho y alto de la caja
    private int width, height;
    // Si es hitbox o hurtbox
    private Boolean hitbox;

    public hitBox(){}

    public hitBox(int originX, int originY, int width, int height, Boolean hitbox) {
        this.x = originX;               this.y = originY;
        this.width = width;             this.height = height;
        this.hitbox = hitbox;
    }

    public void updateHitBox(int newX, int newY, int newWidth, int newHeight) {
        this.x = newX;                  this.y = newY;
        this.width = newWidth;          this.height = newHeight;
    }

    public Boolean collides(hitBox other) {
        if (this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y) {
            return true;
        }
        return false;
    }

    public void drawHitBox(Graphics g) {
        if (this.hitbox) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLUE);
        }
        g.drawRect(this.x, this.y, this.width, this.height);
        g.setColor(Color.WHITE);
        g.drawString(""+x+" "+ y, x, y);
    }

    public Boolean isHitbox() { return hitbox; }

    public Boolean isHurtbox() { return !hitbox; }

    public int getWidth() { return width; }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Boolean getHitbox() {
        return hitbox;
    }

    public void setHitbox(Boolean hitbox) {
        this.hitbox = hitbox;
    }

    public int getHeight() { return height; }
    public int getX() { return x; }
    public int getY() { return y; }

}
