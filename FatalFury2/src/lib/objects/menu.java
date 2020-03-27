package lib.objects;

import javafx.util.Pair;
import lib.Enums.Selectionable;

import java.util.HashMap;
import java.util.Map;

public class menu {
    private Selectionable orden[];
    private int sel = 0;
    private Map<Selectionable, selectionable> selectionables = new HashMap<Selectionable, selectionable>();

    public menu(){}

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

    public screenObject getFrame(controlKey key){
        if(key == controlKey.UP && sel > 0){
            sel--;
        }
        else if(key == controlKey.DOWN && sel < orden.length-1){
            sel++;
        }
        return selectionables.get(orden[sel]).getAnim().getFrame(0,0,1);
    }

    public Pair<menu, Selectionable> select(){
        return new Pair<>(selectionables.get(orden[sel]).getMen(), orden[sel]);
    }
}
