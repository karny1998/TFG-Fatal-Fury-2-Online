package lib.objects;

import lib.Enums.Selectionable;
import lib.menus.menu;

/**
 * The type Selectionable.
 */
// Una de las opciones seleccionables de un menú
public class selectionable{
    /**
     * The Type.
     */
// Tipo de selección
    private Selectionable type;
    /**
     * The Anim.
     */
// Animación de cuando el cursor está en la opción
    private animation anim;
    /**
     * The Men.
     */
// Menú al que lleva (si es null es que no continua la navegación)
    private menu men = null;

    /**
     * Instantiates a new Selectionable.
     */
    public selectionable(){}

    /**
     * Instantiates a new Selectionable.
     *
     * @param type the type
     * @param anim the anim
     * @param men  the men
     */
    public selectionable(Selectionable type, animation anim, menu men) {
        this.type = type;
        this.anim = anim;
        this.men = men;
    }

    /**
     * Instantiates a new Selectionable.
     *
     * @param type the type
     * @param anim the anim
     */
    public selectionable(Selectionable type, animation anim) {
        this.type = type;
        this.anim = anim;
    }

    /**
     * Gets men.
     *
     * @return the men
     */
// Getters y setters
    public menu getMen() {
        return men;
    }

    /**
     * Sets men.
     *
     * @param men the men
     */
    public void setMen(menu men) {
        this.men = men;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Selectionable getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(Selectionable type) {
        this.type = type;
    }

    /**
     * Gets anim.
     *
     * @return the anim
     */
    public animation getAnim() {
        return anim;
    }

    /**
     * Sets anim.
     *
     * @param anim the anim
     */
    public void setAnim(animation anim) {
        this.anim = anim;
    }
}