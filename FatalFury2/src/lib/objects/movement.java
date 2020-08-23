package lib.objects;

import lib.Enums.Movement;

/**
 * The type Movement.
 */
//Clase que representa los movimientos de un personaje
public class movement {
    /**
     * The Type.
     */
// Tipo de movimiento
    Movement type = Movement.NONE;
    /**
     * The Anim.
     */
// Animaciones del personaje y de las cosas que lanza
    animation anim, /**
     * The Throwable.
     */
    throwable;
    /**
     * The Damage.
     */
// Daño del movimiento
    int damage = 0;
    /**
     * The Dist change.
     */
// Distancia para movimiento hijo
    int distChange = 20;
    /**
     * The Distance.
     */
// Distancia cuando se inicio el movimiento
    int distance = 100;
    /**
     * The Sub movement.
     */
// Submovimiento (solo se soporta tener hijos, no nietos)
    movement subMovement = null;

    /**
     * Instantiates a new Movement.
     *
     * @param type the type
     * @param anim the anim
     */
// Constructor que pide el identificador de movimiento y la animación
    public movement(Movement type, animation anim) {
        if(type == Movement.WALKING ||type == Movement.CROUCHED_WALKING){
            distChange = -999;
        }
        this.type = type;
        this.anim = anim;
    }

    /**
     * Instantiates a new Movement.
     *
     * @param type the type
     */
// Constructor que pide el identificador de movimiento
    public movement(Movement type) {
        this.type = type;
        anim = new animation();
        if(type == Movement.THROW){
            throwable = new animation();
        }
    }

    /**
     * Start.
     *
     * @param dist the dist
     */
// Inicia las animaciones
    public void start(int dist){
        distance = dist;
        if(dist > distChange || subMovement == null) {
            anim.start();
        }
        else{
            subMovement.start(999);
        }
    }

    /**
     * Reset.
     */
// Termina y reinicia las animaciones
    public void reset(){
        anim.reset();
        if(subMovement != null) {
            subMovement.reset();
        }
    }

    /**
     * Ended boolean.
     *
     * @return the boolean
     */
// Si ha terminado o no el movimiento
    public boolean ended(){
        if(distance > distChange || subMovement == null) {
            return anim.getEnded();
        }
        else {
            return subMovement.getAnim().getEnded();
        }
    }

    /**
     * Gets anim.
     *
     * @return the anim
     */
// GETTERS y SETTERS, en caso de tener submovimiento se devuelve su get si corresponde
    public animation getAnim() {
        if(distance > distChange || subMovement == null) {
            return anim;
        }
        else {
            return subMovement.getAnim();
        }
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Movement getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(Movement type) {
        this.type = type;
    }

    /**
     * Sets anim.
     *
     * @param anim the anim
     */
    public void setAnim(animation anim) {
        this.anim = anim;
    }

    /**
     * Add frame.
     *
     * @param s  the s
     * @param t  the t
     * @param iX the x
     * @param iY the y
     */
    public void addFrame(screenObject s, Double t, int iX, int iY){
        anim.addFrame(s,t,iX,iY);
    }

    /**
     * Add throwable frame.
     *
     * @param s  the s
     * @param t  the t
     * @param iX the x
     * @param iY the y
     */
    public void addThrowableFrame(screenObject s, Double t, int iX, int iY){
        throwable.addFrame(s,t,iX,iY);
    }

    /**
     * Gets damage.
     *
     * @return the damage
     */
    public int getDamage() {
        if(distance > distChange || subMovement == null) {
            return damage;
        }
        else {
            return subMovement.getDamage();
        }
    }

    /**
     * Sets damage.
     *
     * @param damage the damage
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Has end boolean.
     *
     * @return the boolean
     */
    public boolean hasEnd(){
        return anim.getHasEnd();
    }

    /**
     * Gets frame.
     *
     * @param x           the x
     * @param y           the y
     * @param orientation the orientation
     * @return the frame
     */
    public screenObject getFrame(int x, int y, int orientation) {
        if(distance > distChange || subMovement == null) {
            return anim.getFrame(x,y, orientation);
        }
        else {
            return subMovement.getAnim().getFrame(x,y, orientation);
        }
    }

    /**
     * Get hitbox hit box.
     *
     * @return the hit box
     */
    public hitBox getHitbox(){
        if(distance > distChange || subMovement == null) {
            return anim.getHitbox();
        }
        else {
            return subMovement.getAnim().getHitbox();
        }
    }

    /**
     * Get hurtbox hit box.
     *
     * @return the hit box
     */
    public hitBox getHurtbox(){
        if(distance > distChange || subMovement == null) {
            return anim.getHurtBox();
        }
        else {
            return subMovement.getAnim().getHurtBox();
        }
    }

    /**
     * Get coverbox hit box.
     *
     * @return the hit box
     */
    public hitBox getCoverbox(){
        if(distance > distChange || subMovement == null) {
            return anim.getCoverbox();
        }
        else {
            return subMovement.getAnim().getCoverbox();
        }
    }

    /**
     * Gets throwable.
     *
     * @return the throwable
     */
    public animation getThrowable() {
        return throwable;
    }

    /**
     * Sets throwable.
     *
     * @param throwable the throwable
     */
    public void setThrowable(animation throwable) {
        this.throwable = throwable;
    }

    /**
     * Gets sub movement.
     *
     * @return the sub movement
     */
    public movement getSubMovement() {
        return subMovement;
    }

    /**
     * Sets sub movement.
     *
     * @param subMovement the sub movement
     */
    public void setSubMovement(movement subMovement) {
        this.subMovement = subMovement;
    }

    /**
     * Gets dist change.
     *
     * @return the dist change
     */
    public int getDistChange() {
        return distChange;
    }

    /**
     * Sets dist change.
     *
     * @param distChange the dist change
     */
    public void setDistChange(int distChange) {
        this.distChange = distChange;
    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Sets distance.
     *
     * @param distance the distance
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }
}
