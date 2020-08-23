package lib.utils;

/**
 * The type Pair.
 *
 * @param <T> the type parameter
 * @param <V> the type parameter
 */
// Implementación de la clase Pair para evitar el uso de JavaFX
public class Pair<T, V> {

    /**
     * The First.
     */
    public T first;
    /**
     * The Second.
     */
    public V second;

    /**
     * Instantiates a new Pair.
     *
     * @param f the f
     * @param s the s
     */
    public Pair(T f, V s){
        first = f;
        second = s;
    }

    /**
     * Get key t.
     *
     * @return the t
     */
    public T getKey(){
        return first;
    }

    /**
     * Get value v.
     *
     * @return the v
     */
    public V getValue(){
        return second;
    }


}