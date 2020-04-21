package lib.menus;

import javafx.util.Pair;
import lib.Enums.Selectionable;
import lib.input.controlListener;
import lib.objects.screenObject;
import lib.objects.selectionable;

import java.util.HashMap;
import java.util.Map;

// Clase que representa un menú
public class menu {
    // Orden de los tipos de las opciones a seleccionar
    private Selectionable orden[];
    // "Cursor"
    private int sel = 0;
    // Lista de las opciones del menú
    private Map<Selectionable, selectionable> selectionables = new HashMap<Selectionable, selectionable>();
    // Tiempo de referencia (para evitar problemas de que una tecla avance 2 menús)
    private long referenceTime = System.currentTimeMillis();
    // Coordenadas
    private int x = 0, y = 0;

    public menu(){}

    public menu(Selectionable[] orden, Map<Selectionable, selectionable> selectionables) {
        this.orden = orden;
        this.selectionables = selectionables;
    }

    // Actualizar tiempo de referencia (hacerlo cuando se acabe de seleccionar)
    public void updateTime(){referenceTime = System.currentTimeMillis();}

    // Obtener el frame del menú (también sube y baja el cursor)
    public screenObject getFrame(){
        long current = System.currentTimeMillis();
        if(current - referenceTime > 300.0){
            if(( controlListener.getStatus(1, controlListener.AR_INDEX) || controlListener.getStatus(1, controlListener.IZ_INDEX) ) && sel > 0){
                sel--;
                referenceTime = current;
            }
            else if(( controlListener.getStatus(1, controlListener.AB_INDEX) || controlListener.getStatus(1, controlListener.DE_INDEX) )&& sel < orden.length-1){
                sel++;
                referenceTime = current;
            }
        }
        return selectionables.get(orden[sel]).getAnim().getFrame(x,y,1);
    }

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

    // Getters y setters
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

}
