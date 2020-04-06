package lib.objects;

import javafx.util.Pair;
import lib.Enums.Animation_type;
import lib.Enums.Character_Voices;
import lib.sound.Sound;

import java.util.ArrayList;
import java.util.List;

//Animación representada por sus frames, cambios de posición
//y tiempos de transición.
public class animation {
    // Tipo de animación
    Animation_type type = Animation_type.ENDABLE;
    //Imágenes que conforman la animación
    List<screenObject> frames = new ArrayList<screenObject>();
    //Incrementos de coordenadas entre frames
    List<Pair<Integer, Integer>> coords = new ArrayList<Pair<Integer, Integer>>();
    //Tiempos entre transición de un frame a otro
    List<Double> times = new ArrayList<Double>();
    // Veces que se repeti un frame antes de pasar al siguiente
    List<Integer> repetitions = new ArrayList<Integer>();
    // Coordenadas objetivo para terminar el frame
    List<Pair<Integer, Integer>> waitedCoords = new ArrayList<Pair<Integer, Integer>>();
    // Si un frame puede ser cortado o no por otra animación
    List<Boolean> unstoppableVector = new ArrayList<Boolean>();
    //Si la animación puede ser infinita, si ha terminado, y si se puede interrumpir o no
    Boolean hasEnd = true, ended = false, unstoppable = false;
    //En qué frame de la animación se estaba
    int state = 0;
    // El sentido en que avanza la animación (frame 1, 2, 3.. o 3, 2, 1)
    int increment = 1;
    //Momento en el que salió el último frame
    long startTime = 0;
    long auxTime = 0;
    //Sonido asignado y su tipo
    Sound sound;
    Character_Voices soundType;
    // Si se está reproduciendo el sonido
    Boolean playing = false;
    //Hitbox asociada
    hitBox hitbox = new hitBox(-10000,-10000,1,1,true);
    // Hurtbox asociada
    hitBox hurtBox = new hitBox(-10000,-10000,1,1,false);;

    public animation() {}

    //Añade un frame a la imagen, con un tiempo de transición y un incremento de coordenadas
    public void addFrame(screenObject s, Double t, int iX, int iY){
        frames.add(frames.size(), s);
        times.add(times.size(), t);
        coords.add(coords.size(), new Pair(iX,iY));
        repetitions.add(repetitions.size(), 1);
        waitedCoords.add(waitedCoords.size(), new Pair(-1,-1));
        unstoppableVector.add(unstoppableVector.size(), false);
    }

    public void addFrame(screenObject s, Double t, int iX, int iY, int rep, int wX, int wY, Boolean unstoppable){
        frames.add(frames.size(), s);
        times.add(times.size(), t);
        coords.add(coords.size(), new Pair(iX,iY));
        repetitions.add(repetitions.size(), rep);
        waitedCoords.add(waitedCoords.size(), new Pair(wX,wY));
        unstoppableVector.add(unstoppableVector.size(), unstoppable);
    }

    //Inicia los cálculos de la animación
    public  void start(){
        playing = false;
        ended = false;
        state = 0;
        increment = 1;
        startTime = System.currentTimeMillis();
        auxTime = System.currentTimeMillis();
    }

    //Finaliza y reinicia la animación
    public  void reset(){
        playing = false;
        ended = false;
        state = 0;
        increment = 1;
        startTime = 0;
        auxTime = 0;
    }

    // Obtiene el frame de la animación correspondiente al momento actual
    // a partir de unas coordenadas base. La orientación indica a donde mira
    // la animación (1 hacia la izquierda, -1 hacia la derecha)
    public screenObject getFrame(int x, int y, int orientation){
        // Si no se está reprodciendo el sonido, se reproducre
        if(!playing && hasEnd){
            sound.playCharacterVoice(soundType);
            playing = true;
        }
        screenObject result;
        // Si ha terminado, devuelve el último frame
        if(ended || type == Animation_type.HOLDABLE && state == frames.size()-1){
            result = frames.get(frames.size()-1).cloneSO();
            result.setX(x+coords.get(frames.size()-1).getKey());
            result.setY(y+coords.get(frames.size()-1).getValue());
            result.setWidth(result.getWidth()*orientation);
            ended = true;
            return result;
        }
        long current = System.currentTimeMillis();
        double elapsedTime = current - startTime;
        double elapsedTimeAux = current - auxTime;
        auxTime = current;
        // Si es infinita, ha terminado y el sentido era el inverso, cambia el sentido
        if(!hasEnd && increment < 0 && state == 0 && elapsedTime >= times.get(state)){
            increment = 1;
            state += increment;
            result = frames.get(state).cloneSO();
            result.setX((int) (x + coords.get(state).getKey()*(elapsedTimeAux/times.get(state))));
            result.setY((int) (y + coords.get(state).getValue()*(elapsedTimeAux/times.get(state))));
            startTime = current;
        }
        // Si se ha alcanzado el último frame de de la animación
        else if(state == frames.size()-1 && elapsedTime >= times.get(state)){
            // Si tiene final devuelve el último frame
            if(hasEnd){
                ended = true;
                result = frames.get(state).cloneSO();
                result.setX((int) (x + coords.get(state).getKey()*(elapsedTimeAux/times.get(state))));
                result.setY((int) (y + coords.get(state).getValue()*(elapsedTimeAux/times.get(state))));
            }
            // Sino, es infinita y cambia el sentido de avance
            else{
                increment = -1;
                state += increment;
                startTime = current;
                result = frames.get(state).cloneSO();
                result.setX(x+coords.get(coords.size()-1).getKey());
                result.setY(y+coords.get(coords.size()-1).getValue());
            }
        }
        // Si ha pasado el tiempo requerido entre frame y frame
        else if(elapsedTime >= times.get(state)){
            state += increment;
            startTime = current;
            result = frames.get(state).cloneSO();
            // Si es el primer frame, no tiene avance en coordenadas
            if (state == 0) {
                result.setX((int) (x + coords.get(state).getKey()*(elapsedTimeAux/times.get(state))));
                result.setY((int) (y + coords.get(state).getValue()*(elapsedTimeAux/times.get(state))));
            }
            // Sino es el primer frame, se coge el avance en coordenadas indicado en el
            // frame anterior
            else{
                result.setX(x+coords.get((state-1)%coords.size()).getKey());
                result.setY(y+coords.get((state-1)%coords.size()).getValue());
            }
        }
        // Caso por defecto
        else{
            result = frames.get(state).cloneSO();
            result.setX((int) (x + coords.get(state).getKey()*(elapsedTimeAux/times.get(state))));
            result.setY((int) (y + coords.get(state).getValue()*(elapsedTimeAux/times.get(state))));
        }
        // Ajusta el ancho según la orientación para que mire a un lado y a otro
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

    public Boolean getPlaying() {
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public hitBox getHitbox() {
        return hitbox;
    }

    public void setHitbox(hitBox hitbox) {
        this.hitbox = hitbox;
    }

    public void setHitbox(int originX, int originY, int width, int height) {
        this.hitbox = new hitBox(originX, originY, width, height, true);
    }

    public hitBox getHurtBox() {
        return hurtBox;
    }

    public void setHurtBox(hitBox hurtBox) {
        this.hurtBox = hurtBox;
    }

    public void setHurtBox(int originX, int originY, int width, int height) {
        this.hurtBox = new hitBox(originX, originY, width, height, false);
    }

    public Character_Voices getSoundType() {
        return soundType;
    }

    public void setSoundType(Character_Voices soundType) {
        this.soundType = soundType;
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

    public Animation_type getType() {
        return type;
    }

    public void setType(Animation_type type) {
        this.type = type;
    }

    public List<Integer> getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(List<Integer> repetitions) {
        this.repetitions = repetitions;
    }

    public List<Pair<Integer, Integer>> getWaitedCoords() {
        return waitedCoords;
    }

    public void setWaitedCoords(List<Pair<Integer, Integer>> waitedCoords) {
        this.waitedCoords = waitedCoords;
    }

    public List<Boolean> getUnstoppableVector() {
        return unstoppableVector;
    }

    public void setUnstoppableVector(List<Boolean> unstoppableVector) {
        this.unstoppableVector = unstoppableVector;
    }
}
