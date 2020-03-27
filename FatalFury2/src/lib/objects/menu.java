package lib.objects;

import javafx.util.Pair;
import lib.Enums.Selectionable;

import java.util.HashMap;
import java.util.Map;

public class menu {
    public class selectionable{
        Selectionable type;
        animation anim;
        menu men = null;

        public selectionable(){}

        public selectionable(Selectionable type, animation anim, menu men) {
            this.type = type;
            this.anim = anim;
            this.men = men;
        }
        public selectionable(Selectionable type, animation anim) {
            this.type = type;
            this.anim = anim;
        }

        public menu getMen() {
            return men;
        }

        public void setMen(menu men) {
            this.men = men;
        }

        public Selectionable getType() {
            return type;
        }

        public void setType(Selectionable type) {
            this.type = type;
        }

        public animation getAnim() {
            return anim;
        }

        public void setAnim(animation anim) {
            this.anim = anim;
        }
    }

    Selectionable orden[];
    int sel = 0;
    Map<Selectionable, selectionable> selectionables = new HashMap<Selectionable, selectionable>();

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
