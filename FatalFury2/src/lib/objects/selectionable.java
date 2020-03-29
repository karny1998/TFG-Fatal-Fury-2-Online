package lib.objects;

import lib.Enums.Selectionable;

// Una de las opciones seleccionables de un menú
public class selectionable{
    // Tipo de selección
    private Selectionable type;
    // Animación de cuando el cursor está en la opción
    private animation anim;
    // Menú al que lleva (si es null es que no continua la navegación)
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

    // Getters y setters
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