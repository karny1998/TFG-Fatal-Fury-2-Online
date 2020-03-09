package lib.objects;

import javafx.util.Pair;
import lib.Enums.Character_Voices;
import lib.sound.Sound;

import java.util.ArrayList;
import java.util.List;

//Animación representada por sus frames, cambios de posición
//y tiempos de transición.
public class animation {
    //Imágenes que conforman la animación
    List<screenObject> frames = new ArrayList<screenObject>();
    //Incrementos de coordenadas entre frames
    List<Pair<Integer, Integer>> coords = new ArrayList<Pair<Integer, Integer>>();
    //Tiempos entre transición de un frame a otro
    List<Double> times = new ArrayList<Double>();
    //Si la animación puede ser infinita, si ha terminado, y si se puede interrumpir o no
    Boolean hasEnd = true, ended = false, unstoppable = false;
    //En qué frame de la animación se estaba
    int state = 0;
    // El sentido en que avanza la animación
    int increment = 1;
    //Momento en el que salió el último frame
    long startTime = 0;
    //También tendrá un sonido asignado
    Sound sound;
    Character_Voices soundType;
    Boolean playing = false;

    public Character_Voices getSoundType() {
        return soundType;
    }

    public void setSoundType(Character_Voices soundType) {
        this.soundType = soundType;
    }

    public animation() {}

    //Añade un frame a la imagen, con un tiempo de transición y un incremento de coordenadas
    public void addFrame(screenObject s, Double t, int iX, int iY){
        frames.add(frames.size(), s);
        times.add(times.size(), t);
        coords.add(coords.size(), new Pair(iX,iY));
    }

    //Inicia los cálculos de la animación
    public  void start(){
        playing = false;
        ended = false;
        state = 0;
        increment = 1;
        startTime = System.currentTimeMillis();
    }

    //Finaliza la animación
    public  void reset(){
        playing = false;
        ended = false;
        state = 0;
        increment = 1;
        startTime = 0;
    }

    public Boolean getEnded() {
        return ended;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    //Obtiene el frame de la animación correspondiente al momento actual
    //a partir de unas coordenadas base
    public screenObject getFrame(int x, int y, int orientation){
        if(!playing && hasEnd){
            sound.playCharacterVoice(soundType);
            playing = true;
        }
        screenObject result;
        if(ended){
            result = frames.get(frames.size()).cloneSO();
            result.setX(x+coords.get(frames.size()).getKey());
            result.setY(y+coords.get(frames.size()).getValue());
            result.setWidth(result.getWidth()*orientation);
            return frames.get(frames.size());
        }
        long current = System.currentTimeMillis();
        double elapsedTime = current - startTime;
        if(!hasEnd && increment < 0 && state == 0 && elapsedTime >= times.get(state)){
            increment = 1;
            state += increment;
            result = frames.get(state).cloneSO();
            result.setX(x);
            result.setY(y);
            startTime = current;
        }
        else if(state == frames.size()-1 && elapsedTime >= times.get(state)){
            if(hasEnd){
                ended = true;
                result = frames.get(state).cloneSO();
                result.setX(x);
                result.setY(y);
            }
            else{
                increment = -1;
                state += increment;
                startTime = current;
                result = frames.get(state).cloneSO();
                result.setX(x+coords.get(coords.size()-1).getKey());
                result.setY(y+coords.get(coords.size()-1).getValue());
            }
        }
        else if(elapsedTime >= times.get(state)){
            state += increment;
            startTime = current;
            result = frames.get(state).cloneSO();
            if (state == 0) {
                result.setX(x);
                result.setY(y);
            }
            else{
                result.setX(x+coords.get((state-1)%coords.size()).getKey());
                result.setY(y+coords.get((state-1)%coords.size()).getValue());
            }
        }
        else{
            result = frames.get(state).cloneSO();
            result.setX(x);
            result.setY(y);
        }
        result.setWidth(result.getWidth()*orientation);
        return result;
    }

    //Getters y setters
    public void setCoords(ArrayList<Pair<Integer, Integer>> coords) {
        this.coords = coords;
    }

    public Boolean getHasEnd() {
        return hasEnd;
    }

    public void setHasEnd(Boolean hasEnd) {
        this.hasEnd = hasEnd;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public List<screenObject> getFrames() {
        return frames;
    }

    public void setFrames(List<screenObject> frames) {
        this.frames = frames;
    }

    public List<Pair<Integer, Integer>> getCoords() {
        return coords;
    }

    public void setCoords(List<Pair<Integer, Integer>> coords) {
        this.coords = coords;
    }

    public List<Double> getTimes() {
        return times;
    }

    public void setTimes(List<Double> times) {
        this.times = times;
    }

    public Boolean getUnstoppable() {
        return unstoppable;
    }

    public void setUnstoppable(Boolean unstoppable) {
        this.unstoppable = unstoppable;
    }

    public void setSound(Sound s){sound = s;}

    public Sound getSound(){return sound;}
}
