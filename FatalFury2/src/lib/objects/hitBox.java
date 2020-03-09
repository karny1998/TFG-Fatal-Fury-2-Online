package lib.objects;

import java.awt.*;

public class hitBox {

    private int x, y;
    private int width, height;
    private Boolean hitbox;

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
    }


    public Boolean isHitbox() { return hitbox; }
    public Boolean isHurtbox() { return !hitbox; }



    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getX() { return x; }
    public int getY() { return y; }


    }
