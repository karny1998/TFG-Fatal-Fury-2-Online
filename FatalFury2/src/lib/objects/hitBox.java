package lib.objects;

import java.awt.*;

enum box_type{HITBOX, HURTBOX, COVERBOX}

public class hitBox {
    // Coordenadas dentro del sprite (suponiendo la esquina
    // del sprite como 0,0)
    private int x, y;
    // Ancho y alto de la caja
    private int width, height;
    // Si es hitbox, hurtbox o coverbox
    private box_type type = box_type.HURTBOX;
    public hitBox(){}

    public hitBox(int originX, int originY, int width, int height, box_type type) {
        this.x = originX;               this.y = originY;
        this.width = width;             this.height = height;
        this.type = type;
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

    public Boolean isHitbox() { return type == box_type.HITBOX; }

    public Boolean isHurtbox() { return type == box_type.HURTBOX; }

    public Boolean isCoverbox() { return type == box_type.COVERBOX; }

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

    public box_type getType() {
        return type;
    }

    public void setType(box_type type) {
        this.type = type;
    }

    public int getHeight() { return height; }
    public int getX() { return x; }
    public int getY() { return y; }

}
