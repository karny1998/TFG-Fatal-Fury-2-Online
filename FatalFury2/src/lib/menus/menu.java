package lib.menus;

import lib.utils.Pair;
import lib.Enums.Selectionable;
import lib.input.controlListener;
import lib.objects.screenObject;
import lib.objects.selectionable;
import lib.sound.audio_manager;
import lib.sound.menu_audio;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Menu.
 */
// Clase que representa un menú
public class menu {
    /**
     * The Orden.
     */
// Orden de los tipos de las opciones a seleccionar
    private Selectionable orden[];
    /**
     * The Sel.
     */
// "Cursor"
    private int sel = 0;
    /**
     * The Selectionables.
     */
// Lista de las opciones del menú
    private Map<Selectionable, selectionable> selectionables = new HashMap<Selectionable, selectionable>();
    /**
     * The Reference time.
     */
// Tiempo de referencia (para evitar problemas de que una tecla avance 2 menús)
    private long referenceTime = System.currentTimeMillis();
    /**
     * The X.
     */
// Coordenadas
    private int x = 0, /**
     * The Y.
     */
    y = 0;

    /**
     * Instantiates a new Menu.
     */
    public menu(){}

    /**
     * Instantiates a new Menu.
     *
     * @param orden          the orden
     * @param selectionables the selectionables
     */
    public menu(Selectionable[] orden, Map<Selectionable, selectionable> selectionables) {
        this.orden = orden;
        this.selectionables = selectionables;
    }

    /**
     * Update time.
     */
// Actualizar tiempo de referencia (hacerlo cuando se acabe de seleccionar)
    public void updateTime(){referenceTime = System.currentTimeMillis();}

    /**
     * Get frame screen object.
     *
     * @return the screen object
     */
// Obtener el frame del menú (también sube y baja el cursor)
    public screenObject getFrame(){
        long current = System.currentTimeMillis();
        if(current - referenceTime > 300.0){
            if(( controlListener.getStatus(0, controlListener.AR_INDEX)
                    || controlListener.getStatus(2, controlListener.AR_INDEX)
                    || controlListener.getStatus(1, controlListener.IZ_INDEX)
                    || controlListener.getStatus(2, controlListener.IZ_INDEX)) && sel > 0){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
                sel--;
                referenceTime = current;
            }
            else if(( controlListener.getStatus(1, controlListener.AB_INDEX)
                    || controlListener.getStatus(2, controlListener.AB_INDEX)
                    || controlListener.getStatus(1, controlListener.DE_INDEX)
                    || controlListener.getStatus(2, controlListener.DE_INDEX))&& sel < orden.length-1){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);

                sel++;
                referenceTime = current;
            }
        }
        return selectionables.get(orden[sel]).getAnim().getFrame(x,y,1);
    }

    /**
     * Select pair.
     *
     * @return the pair
     */
// Devuelve el menú (si es que tiene) y el tipo de opción, del seleccionable
    // en el que está el cursor
    public Pair<menu, Selectionable> select(){
        long current = System.currentTimeMillis();
        if(current - referenceTime > 300.0){
            return new Pair<>(selectionables.get(orden[sel]).getMen(), orden[sel]);
        }
        // Si no ha pasado el tiempo requerido para poder navegar
        else{
            return new Pair<>(this, Selectionable.NONE);
        }
    }

    /**
     * Get orden selectionable [ ].
     *
     * @return the selectionable [ ]
     */
// Getters y setters
    public Selectionable[] getOrden() {
        return orden;
    }

    /**
     * Sets orden.
     *
     * @param orden the orden
     */
    public void setOrden(Selectionable[] orden) {
        this.orden = orden;
    }

    /**
     * Gets selectionables.
     *
     * @return the selectionables
     */
    public Map<Selectionable, selectionable> getSelectionables() {
        return selectionables;
    }

    /**
     * Sets selectionables.
     *
     * @param selectionables the selectionables
     */
    public void setSelectionables(Map<Selectionable, selectionable> selectionables) {
        this.selectionables = selectionables;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets sel.
     *
     * @return the sel
     */
    public int getSel() {
        return sel;
    }

    /**
     * Sets sel.
     *
     * @param sel the sel
     */
    public void setSel(int sel) {
        this.sel = sel;
    }

    /**
     * Gets reference time.
     *
     * @return the reference time
     */
    public long getReferenceTime() {
        return referenceTime;
    }

    /**
     * Sets reference time.
     *
     * @param referenceTime the reference time
     */
    public void setReferenceTime(long referenceTime) {
        this.referenceTime = referenceTime;
    }
}
