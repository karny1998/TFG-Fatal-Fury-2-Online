package lib.objects;

import lib.Enums.Movement;

//Clase que representa los movimientos de un personaje
public class movement {
    // Tipo de movimiento
    Movement type = Movement.NONE;
    // Animaciones del personaje y de las cosas que lanza
    animation anim, throwable;
    // Daño del movimiento
    int damage = 0;
    // Distancia para movimiento hijo
    int distChange = 20;
    // Distancia cuando se inicio el movimiento
    int distance = 100;
    // Submovimiento (solo se soporta tener hijos, no nietos)
    movement subMovement = null;

    // Constructor que pide el identificador de movimiento y la animación
    public movement(Movement type, animation anim) {
        if(type == Movement.WALKING ||type == Movement.CROUCHED_WALKING){
            distChange = -999;
        }
        this.type = type;
        this.anim = anim;
    }

    // Constructor que pide el identificador de movimiento
    public movement(Movement type) {
        this.type = type;
        anim = new animation();
        if(type == Movement.THROW){
            throwable = new animation();
        }
    }

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

    // Termina y reinicia las animaciones
    public void reset(){
        anim.reset();
        if(subMovement != null) {
            subMovement.reset();
        }
    }

    // Si ha terminado o no el movimiento
    public boolean ended(){
        if(distance > distChange || subMovement == null) {
            return anim.getEnded();
        }
        else {
            return subMovement.getAnim().getEnded();
        }
    }

    // GETTERS y SETTERS, en caso de tener submovimiento se devuelve su get si corresponde
    public animation getAnim() {
        if(distance > distChange || subMovement == null) {
            return anim;
        }
        else {
            return subMovement.getAnim();
        }
    }

    public Movement getType() {
        return type;
    }

    public void setType(Movement type) {
        this.type = type;
    }

    public void setAnim(animation anim) {
        this.anim = anim;
    }

    public void addFrame(screenObject s, Double t, int iX, int iY){
        anim.addFrame(s,t,iX,iY);
    }

    public void addThrowableFrame(screenObject s, Double t, int iX, int iY){
        throwable.addFrame(s,t,iX,iY);
    }

    public int getDamage() {
        if(distance > distChange || subMovement == null) {
            return damage;
        }
        else {
            return subMovement.getDamage();
        }
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean hasEnd(){
        return anim.getHasEnd();
    }

    public screenObject getFrame(int x, int y, int orientation) {
        if(distance > distChange || subMovement == null) {
            return anim.getFrame(x,y, orientation);
        }
        else {
            return subMovement.getAnim().getFrame(x,y, orientation);
        }
    }

    public hitBox getHitbox(){
        if(distance > distChange || subMovement == null) {
            return anim.getHitbox();
        }
        else {
            return subMovement.getAnim().getHitbox();
        }
    }

    public hitBox getHurtbox(){
        if(distance > distChange || subMovement == null) {
            return anim.getHurtBox();
        }
        else {
            return subMovement.getAnim().getHurtBox();
        }
    }

    public hitBox getCoverbox(){
        if(distance > distChange || subMovement == null) {
            return anim.getCoverbox();
        }
        else {
            return subMovement.getAnim().getCoverbox();
        }
    }

    public animation getThrowable() {
        return throwable;
    }

    public void setThrowable(animation throwable) {
        this.throwable = throwable;
    }

    public movement getSubMovement() {
        return subMovement;
    }

    public void setSubMovement(movement subMovement) {
        this.subMovement = subMovement;
    }

    public int getDistChange() {
        return distChange;
    }

    public void setDistChange(int distChange) {
        this.distChange = distChange;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
