package lib.utils;

public class Pair<T, V> {

    public T first;
    public V second;

    public Pair(T f, V s){
        first = f;
        second = s;
    }

    public T getKey(){
        return first;
    }
    public V getValue(){
        return second;
    }


}