package lib.objects;

import lib.Enums.Animation_type;
import lib.Enums.box_type;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Animation.
 */
//Animación representada por sus frames, cambios de posición
//y tiempos de transición.
public class animation {
    /**
     * The Type.
     */
// Tipo de animación
    Animation_type type = Animation_type.ENDABLE;
    /**
     * The Frames.
     */
// Imágenes que conforman la animación
    List<screenObject> frames = new ArrayList<screenObject>();
    /**
     * The Coords.
     */
// Incrementos de coordenadas entre frames
    List<Pair<Integer, Integer>> coords = new ArrayList<Pair<Integer, Integer>>();
    /**
     * The Times.
     */
// Tiempos entre transición de un frame a otro
    List<Double> times = new ArrayList<Double>();
    /**
     * The Waited coords.
     */
// Coordenadas objetivo para terminar el frame
    List<Pair<Integer, Integer>> waitedCoords = new ArrayList<Pair<Integer, Integer>>();
    /**
     * The Has hit box.
     */
// Si un frame tiene hitbox o no
    List<Boolean> hasHitBox = new ArrayList<Boolean>();
    /**
     * The Has end.
     */
// Si la animación puede ser infinita, si ha terminado, y si se puede interrumpir o no
    Boolean hasEnd = true, /**
     * The Ended.
     */
    ended = false;
    /**
     * The State.
     */
// En qué frame de la animación se estaba
    int state = 0;
    /**
     * The Increment.
     */
// El sentido en que avanza la animación (frame 1, 2, 3.. o 3, 2, 1)
    int increment = 1;
    /**
     * The Start time.
     */
// Momento en el que salió el último frame
    long startTime = 0;
    /**
     * The Aux time.
     */
// Momento de referencia para "creación de frames intermedios"
    long auxTime = 0;
    /**
     * The Sound type.
     */
// Sonido asignado y su tipo
    fight_audio.voice_indexes soundType;
    /**
     * The Is player 1.
     */
// Si es jugador 1 o no
    boolean isPlayer1;
    /**
     * The Playing.
     */
// Si se está reproduciendo el sonido
    Boolean playing = false;
    /**
     * The Hitbox.
     */
//Hitbox asociada
    hitBox hitbox = new hitBox((int) (-10000.0*Math.random())-5000,(int) (-10000.0*Math.random())-5000,1,1, box_type.HITBOX);
    /**
     * The Hurt box.
     */
// Hurtbox asociada
    hitBox hurtBox = new hitBox((int) (-10000.0*Math.random())-5000,(int) (-10000.0*Math.random())-5000,1,1, box_type.HURTBOX);
    /**
     * The Coverbox.
     */
// Cover asociada
    hitBox coverbox = new hitBox((int) (-10000.0*Math.random())-5000,(int) (-10000.0*Math.random())-5000,1,1, box_type.COVERBOX);
    /**
     * The Has sound.
     */
// Si tiene un sonido asociadoo
    boolean hasSound = false;
    /**
     * The Y completed.
     */
// La y que se ha recorrido entre frames
    int yCompleted = 0;
    /**
     * The Y aux.
     */
// Si se ha completado la y esperada
    boolean yAux = false;
    /**
     * The Total increment y.
     */
// El incremento total en y de la animación
    int totalIncrementY = 0;
    /**
     * The Desired y.
     */
// La y final de la animación
    int desiredY = 0;
    /**
     * The Desired assigned.
     */
// Si desiredY está asignada o no
    boolean desiredAssigned = false;

    /**
     * Instantiates a new Animation.
     */
// Constructor por defecto
    public animation() {}

    /**
     * Add frame.
     *
     * @param s  the s
     * @param t  the t
     * @param iX the x
     * @param iY the y
     */
// Añade un frame a la imagen, con un tiempo de transición y un incremento de coordenadas
    public void addFrame(screenObject s, Double t, int iX, int iY){
        frames.add(frames.size(), s);
        times.add(times.size(), t);
        coords.add(coords.size(), new Pair(iX,iY));
        waitedCoords.add(waitedCoords.size(), new Pair(-1,-1));
        hasHitBox.add(hasHitBox.size(), false);
        totalIncrementY += iY;
    }

    /**
     * Add frame.
     *
     * @param s      the s
     * @param t      the t
     * @param iX     the x
     * @param iY     the y
     * @param wX     the w x
     * @param wY     the w y
     * @param hasHit the has hit
     */
// Añade un frame a la imagen, con un tiempo de transición y un incremento de coordenadas, indicando
    // si tiene hitbox o no
    public void addFrame(screenObject s, Double t, int iX, int iY, int wX, int wY, Boolean hasHit){
        frames.add(frames.size(), s);
        times.add(times.size(), t);
        coords.add(coords.size(), new Pair(iX,iY));
        waitedCoords.add(waitedCoords.size(), new Pair(wX,wY));
        hasHitBox.add(hasHitBox.size(), hasHit);
        totalIncrementY += iY;
    }

    /**
     * Start.
     */
// Inicia los cálculos de la animación
    public  void start(){
        playing = false;
        ended = false;
        state = 0;
        increment = 1;
        startTime = System.currentTimeMillis();
        auxTime = System.currentTimeMillis();
        yCompleted = 0;
        yAux = false;
        desiredY = 0;
        desiredAssigned = false;
    }

    /**
     * End.
     */
// Termina la animación, y la deja en el final
    public  void end(){
        playing = true;
        ended = true;
        state = frames.size()-1;
        increment = 1;
        startTime = 0;
        auxTime = 0;
        yCompleted = 0;
        yAux = false;
        desiredY = 0;
        desiredAssigned = false;
    }

    /**
     * Reset.
     */
// Finaliza y reinicia la animación
    public  void reset(){
        playing = false;
        ended = false;
        state = 0;
        increment = 1;
        startTime = 0;
        auxTime = 0;
        yCompleted = 0;
        yAux = false;
        desiredY = 0;
        desiredAssigned = false;

    }

    /**
     * Get frame screen object.
     *
     * @param x           the x
     * @param y           the y
     * @param orientation the orientation
     * @return the screen object
     */
// Obtiene el frame de la animación correspondiente al momento actual
    // a partir de unas coordenadas base. La orientación indica a donde mira
    // la animación (1 hacia la izquierda, -1 hacia la derecha)
    public screenObject getFrame(int x, int y, int orientation){
        // Si no está asignada la y deseada, la calculamos
        if(!desiredAssigned){
            desiredY = y + totalIncrementY;
            desiredAssigned = true;
        }
        // Si no se está reprodciendo el sonido (y tiene), se reproducre
        if(hasSound && !playing && hasEnd){
            audio_manager.fight.playVoice(isPlayer1, soundType);
            playing = true;
        }
        screenObject result;
        // Si ha terminado, devuelve el último frame
        if(ended || type == Animation_type.HOLDABLE && state == frames.size()-1){
            result = frames.get(frames.size()-1).cloneSO();
            result.setX(x);
            result.setY(y);
            result.setWidth(result.getWidth()*orientation);
            ended = true;
            return result;
        }
        long current = System.currentTimeMillis();
        double elapsedTime = current - startTime;
        double elapsedTimeAux = current - auxTime;
        // Si se está en un frame que no es el primero, y los incrementos x e y del frame anterior son 0, y
        // el tiempo auxiliar es mayor que el de comienzo de la animación, se actualiza el auxiliar
        if(state > 0 && coords.get(state-1).first == 0 && coords.get(state-1).second == 0 && elapsedTimeAux > elapsedTime){
            elapsedTimeAux  = elapsedTime;
        }

        // Incremento en x en base al tiempo transcurrido
        int incrementOnX = (int)(coords.get((state)%coords.size()).getKey()*(elapsedTimeAux/times.get(state)));
        incrementOnX *= -orientation;
        // Incremento en y en base al tiempo transcurrido
        int incrementOnY = (int)(coords.get((state)%coords.size()).getValue()*(elapsedTimeAux/times.get(state)));

        int cX = coords.get((state)%coords.size()).getKey();
        int cY = coords.get((state)%coords.size()).getValue();
        int wX = waitedCoords.get(state).getKey();
        int wY = waitedCoords.get(state).getValue();
        // Si el frame avanza en alguna coordenada, actualiza el tiempo de referencia
        if(cX != 0 && cY == 0 && incrementOnX != 0
            || cX == 0 && cY != 0 && incrementOnY != 0
            || cX != 0 && cY != 0 && incrementOnX != 0 && incrementOnY != 0) {
            auxTime = current;
        }

        // Si con corresponde al último frame intermedio, se avanza lo que quede
        if(wY != -1 &&  (wY > 0 && y + incrementOnY > wY
                || wY < 0 && y + incrementOnY < wY)){
            incrementOnY = wY - y;
            state = frames.size()-1;
            yAux = true;
            yCompleted = 0;
        }
        else if(cY > 0 && yCompleted + incrementOnY > cY
                || cY < 0 && yCompleted + incrementOnY < cY){
            incrementOnY = cY - yCompleted;
            yCompleted = 0;
            yAux = true;
        }

        // Si es infinita, ha terminado y el sentido era el inverso, cambia el sentido
        if(!hasEnd && increment < 0 && state == 0 && elapsedTime >= times.get(state)){
            increment = 1;
            state += increment;
            result = frames.get(state).cloneSO();
            result.setX((int) (x + coords.get((state-1)%coords.size()).getKey()*(elapsedTimeAux/times.get(state))));
            result.setY((int) (y + coords.get((state-1)%coords.size()).getValue()*(elapsedTimeAux/times.get(state))));
            startTime = current;
        }
        // Si se ha alcanzado el último frame de de la animación
        else if(state == frames.size()-1 &&
                (elapsedTime >= times.get(state) || yAux)){
            // Si tiene final devuelve el último frame
            if(hasEnd){
                ended = true;
            }
            // Sino, es infinita y cambia el sentido de avance
            else{
                increment = -1;
                state += increment;
                startTime = current;
            }
            result = frames.get(state).cloneSO();
            result.setX(x + incrementOnX);
            if(wY == -1) {
                result.setY(desiredY);
            }
            else{
                result.setY(wY);
            }
            yAux = false;
        }
        // Si ha pasado el tiempo requerido entre frame y frame
        else if(elapsedTime >= times.get(state)){
            // Si falta por avanzar algo, se avanza eso en el siguiente frame
            if(yCompleted != 0 && yCompleted+incrementOnY != coords.get(state).getValue()){
                incrementOnY = coords.get(state).getValue() - yCompleted;
                yCompleted = 0;
            }
            result = frames.get(state).cloneSO();
            result.setX(x + incrementOnX);
            result.setY(y + incrementOnY);
            state += increment;
            startTime = current;
            yAux = false;
        }
        // Si se está en un estado intermedio entre frame y frame
        else{
            result = frames.get(state).cloneSO();
            result.setX(x + incrementOnX);
            result.setY(y +incrementOnY);
            if(!yAux){yCompleted += incrementOnY;}
        }
        // Ajusta el ancho según la orientación para que mire a un lado y a otro
        result.setWidth(result.getWidth()*orientation);
        return result;
    }

    /**
     * Update times.
     *
     * @param x the x
     */
// Actualiza los tiempos de transición entre frames en basea un multiplicador
    void updateTimes(double x){
        for(int i = 0; i < times.size(); ++i){
            double aux = times.get(i) * x;
            times.remove(i);
            times.add(i, aux);
        }
    }

    /**
     * Sets coords.
     *
     * @param coords the coords
     */
//Getters y setters
    public void setCoords(ArrayList<Pair<Integer, Integer>> coords) {
        this.coords = coords;
    }

    /**
     * Get frame screen object.
     *
     * @return the screen object
     */
    screenObject getFrame(){
        return getFrame(frames.get(state).getX(),frames.get(state).getY(),1);
    }

    /**
     * Gets has end.
     *
     * @return the has end
     */
    public Boolean getHasEnd() {
        return hasEnd;
    }

    /**
     * Sets has end.
     *
     * @param hasEnd the has end
     */
    public void setHasEnd(Boolean hasEnd) {
        this.hasEnd = hasEnd;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets frames.
     *
     * @return the frames
     */
    public List<screenObject> getFrames() {
        return frames;
    }

    /**
     * Sets frames.
     *
     * @param frames the frames
     */
    public void setFrames(List<screenObject> frames) {
        this.frames = frames;
    }

    /**
     * Gets coords.
     *
     * @return the coords
     */
    public List<Pair<Integer, Integer>> getCoords() {
        return coords;
    }

    /**
     * Sets coords.
     *
     * @param coords the coords
     */
    public void setCoords(List<Pair<Integer, Integer>> coords) {
        this.coords = coords;
    }

    /**
     * Gets times.
     *
     * @return the times
     */
    public List<Double> getTimes() {
        return times;
    }

    /**
     * Sets times.
     *
     * @param times the times
     */
    public void setTimes(List<Double> times) {
        this.times = times;
    }

    /**
     * Gets has hit box.
     *
     * @return the has hit box
     */
    public Boolean getHasHitBox() {
        return hasHitBox.get(state);
    }

    /**
     * Sets unstoppable.
     *
     * @param has the has
     * @param i   the
     */
    public void setUnstoppable(Boolean has, int i) {
        this.hasHitBox.set(i, has) ;
    }

    /**
     * Gets playing.
     *
     * @return the playing
     */
    public Boolean getPlaying() {
        return playing;
    }

    /**
     * Sets playing.
     *
     * @param playing the playing
     */
    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    /**
     * Gets hitbox.
     *
     * @return the hitbox
     */
// Si no tiene hitbox, devuelve una aleatoria con la que nunca se golpeara
    public hitBox getHitbox() {
        if(ended || !hasHitBox.get(state)){
            return new hitBox((int) (-10000.0*Math.random())-5000,(int) (-10000.0*Math.random())-5000,1,1, box_type.HITBOX);
        }
        return hitbox;
    }

    /**
     * Sets hitbox.
     *
     * @param hitbox the hitbox
     */
    public void setHitbox(hitBox hitbox) {
        this.hitbox = hitbox;
    }

    /**
     * Sets hitbox.
     *
     * @param originX the origin x
     * @param originY the origin y
     * @param width   the width
     * @param height  the height
     */
    public void setHitbox(int originX, int originY, int width, int height) {
        this.hitbox = new hitBox(originX, originY, width, height, box_type.HITBOX);
    }

    /**
     * Gets hurt box.
     *
     * @return the hurt box
     */
    public hitBox getHurtBox() {
        return hurtBox;
    }

    /**
     * Sets hurt box.
     *
     * @param hurtBox the hurt box
     */
    public void setHurtBox(hitBox hurtBox) {
        this.hurtBox = hurtBox;
    }

    /**
     * Sets hurt box.
     *
     * @param originX the origin x
     * @param originY the origin y
     * @param width   the width
     * @param height  the height
     */
    public void setHurtBox(int originX, int originY, int width, int height) {
        this.hurtBox = new hitBox(originX, originY, width, height, box_type.HURTBOX);
    }

    /**
     * Sets coverbox.
     *
     * @param originX the origin x
     * @param originY the origin y
     * @param width   the width
     * @param height  the height
     */
    public void setCoverbox(int originX, int originY, int width, int height) {
        this.coverbox = new hitBox(originX, originY, width, height, box_type.COVERBOX);
    }

    /**
     * Gets coverbox.
     *
     * @return the coverbox
     */
    public hitBox getCoverbox() {
        return coverbox;
    }

    /**
     * Gets sound type.
     *
     * @return the sound type
     */
    public fight_audio.voice_indexes getSoundType() {
        return soundType;
    }

    /**
     * Sets sound type.
     *
     * @param soundType the sound type
     */
    public void setSoundType(fight_audio.voice_indexes soundType) {
        this.soundType = soundType;
    }

    /**
     * Gets ended.
     *
     * @return the ended
     */
    public Boolean getEnded() {
        return ended;
    }

    /**
     * Sets ended.
     *
     * @param ended the ended
     */
    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    /**
     * Gets increment.
     *
     * @return the increment
     */
    public int getIncrement() {
        return increment;
    }

    /**
     * Sets increment.
     *
     * @param increment the increment
     */
    public void setIncrement(int increment) {
        this.increment = increment;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Animation_type getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(Animation_type type) {
        this.type = type;
    }

    /**
     * Gets waited coords.
     *
     * @return the waited coords
     */
    public List<Pair<Integer, Integer>> getWaitedCoords() {
        return waitedCoords;
    }

    /**
     * Sets waited coords.
     *
     * @param waitedCoords the waited coords
     */
    public void setWaitedCoords(List<Pair<Integer, Integer>> waitedCoords) {
        this.waitedCoords = waitedCoords;
    }

    /**
     * Gets has hit box vector.
     *
     * @return the has hit box vector
     */
    public List<Boolean> getHasHitBoxVector() {
        return hasHitBox;
    }

    /**
     * Sets has hit box.
     *
     * @param hasHitBoxVector the has hit box vector
     */
    public void setHasHitBox(List<Boolean> hasHitBoxVector) {
        this.hasHitBox = hasHitBoxVector;
    }

    /**
     * Gets aux time.
     *
     * @return the aux time
     */
    public long getAuxTime() {
        return auxTime;
    }

    /**
     * Sets aux time.
     *
     * @param auxTime the aux time
     */
    public void setAuxTime(long auxTime) {
        this.auxTime = auxTime;
    }

    /**
     * Sets coverbox.
     *
     * @param coverbox the coverbox
     */
    public void setCoverbox(hitBox coverbox) {
        this.coverbox = coverbox;
    }

    /**
     * Gets completed.
     *
     * @return the completed
     */
    public int getyCompleted() {
        return yCompleted;
    }

    /**
     * Sets completed.
     *
     * @param yCompleted the y completed
     */
    public void setyCompleted(int yCompleted) {
        this.yCompleted = yCompleted;
    }

    /**
     * Isy aux boolean.
     *
     * @return the boolean
     */
    public boolean isyAux() {
        return yAux;
    }

    /**
     * Sets aux.
     *
     * @param yAux the y aux
     */
    public void setyAux(boolean yAux) {
        this.yAux = yAux;
    }

    /**
     * Gets total increment y.
     *
     * @return the total increment y
     */
    public int getTotalIncrementY() {
        return totalIncrementY;
    }

    /**
     * Sets total increment y.
     *
     * @param totalIncrementY the total increment y
     */
    public void setTotalIncrementY(int totalIncrementY) {
        this.totalIncrementY = totalIncrementY;
    }

    /**
     * Gets desired y.
     *
     * @return the desired y
     */
    public int getDesiredY() {
        return desiredY;
    }

    /**
     * Sets desired y.
     *
     * @param desiredY the desired y
     */
    public void setDesiredY(int desiredY) {
        this.desiredY = desiredY;
    }

    /**
     * Is desired assigned boolean.
     *
     * @return the boolean
     */
    public boolean isDesiredAssigned() {
        return desiredAssigned;
    }

    /**
     * Sets desired assigned.
     *
     * @param desiredAssigned the desired assigned
     */
    public void setDesiredAssigned(boolean desiredAssigned) {
        this.desiredAssigned = desiredAssigned;
    }

    /**
     * Has sound boolean.
     *
     * @return the boolean
     */
    public boolean hasSound(){
        return hasSound;
    }

    /**
     * Set has sound.
     *
     * @param has the has
     */
    public void setHasSound(boolean has){
        hasSound = has;
    }


    /**
     * Is player 1 boolean.
     *
     * @return the boolean
     */
    public boolean isPlayer1() {
        return isPlayer1;
    }

    /**
     * Sets player 1.
     *
     * @param player1 the player 1
     */
    public void setPlayer1(boolean player1) {
        isPlayer1 = player1;
    }
}
