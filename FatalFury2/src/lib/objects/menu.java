package lib.objects;

import javafx.util.Pair;
import lib.Enums.Selectionable;

import java.util.HashMap;
import java.util.Map;

public class menu {
    private Selectionable orden[];
    private int sel = 0;
    private Map<Selectionable, selectionable> selectionables = new HashMap<Selectionable, selectionable>();
    private long referenceTime = System.currentTimeMillis();
    private menu father;
    private int x = 0, y = 0;

    public menu(){}

    public menu getFather() {
        return father;
    }

    public void setFather(menu father) {
        this.father = father;
    }

    public menu(Selectionable[] orden, Map<Selectionable, selectionable> selectionables) {
        this.orden = orden;
        this.selectionables = selectionables;
    }

    public Selectionable[] getOrden() {
        return orden;
    }

    public void setOrden(Selectionable[] orden) {
        this.orden = orden;
    }

    public Map<Selectionable, selectionable> getSelectionables() {
        return selectionables;
    }

    public void setSelectionables(Map<Selectionable, selectionable> selectionables) {
        this.selectionables = selectionables;
    }

    public void updateTime(){referenceTime = System.currentTimeMillis();}

    public screenObject getFrame(controlKey key){
        long current = System.currentTimeMillis();
        if(current - referenceTime > 300.0){
            if(key == controlKey.UP && sel > 0){
                sel--;
                referenceTime = current;
            }
            else if(key == controlKey.DOWN && sel < orden.length-1){
                sel++;
                referenceTime = current;
            }
        }
        return selectionables.get(orden[sel]).getAnim().getFrame(x,y,1);
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

    public Pair<menu, Selectionable> select(){
        long current = System.currentTimeMillis();
        if(current - referenceTime > 300.0){
            return new Pair<>(selectionables.get(orden[sel]).getMen(), orden[sel]);
        }
        else{
            return new Pair<>(this, Selectionable.NONE);
        }
    }
}
