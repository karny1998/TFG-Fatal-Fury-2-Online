package lib.objects;
import java.awt.Image;

public class screenObject {
    private int x = 0, y = 0;
    private Image img;

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

    public screenObject(int _x, int _y, Image _img){x = _x; y = _y; img = _img;}

}
