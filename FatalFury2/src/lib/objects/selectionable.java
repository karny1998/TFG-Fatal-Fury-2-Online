package lib.objects;

import lib.Enums.Selectionable;

public class selectionable{
    private Selectionable type;
    private animation anim;
    private menu men = null;

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