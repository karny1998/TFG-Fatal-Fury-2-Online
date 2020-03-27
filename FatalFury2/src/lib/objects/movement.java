package lib.objects;

import lib.Enums.Movement;

//Clase que representa los movimientos de un personaje
public class movement {
    Movement type = Movement.NONE;
    animation anim, throwable;
    int damage = 0;
    //HITBOX
    //HURTBOX

    public movement(Movement type, animation anim) {
        this.type = type;
        this.anim = anim;
    }

    public movement(Movement type) {
        this.type = type;
        anim = new animation();
        if(type == Movement.THROW){
            throwable = new animation();
        }
    }

    public boolean unstoppable(){return anim.getUnstoppable();}

    public Movement getType() {
        return type;
    }

    public void setType(Movement type) {
        this.type = type;
    }

    public animation getAnim() {
        return anim;
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

    public void start(){
        anim.start();
        if(type == Movement.THROW){
            //ASEGURARSE DE QUE LA ANIMACION DEL LANZAMIENTO
            //TIENE UN PRIMER FRAME VACIO CON EL TIEMPO ENTRE
            //EL COMIENZO DE LA ANIM Y LA SUYA
            throwable.start();
        }
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void reset(){
        anim.reset();
        if(type == Movement.THROW){
            //ASEGURARSE DE QUE LA ANIMACION DEL LANZAMIENTO
            //TIENE UN PRIMER FRAME VACIO CON EL TIEMPO ENTRE
            //EL COMIENZO DE LA ANIM Y LA SUYA
            throwable.reset();
        }
    }

    public boolean ended(){
        return anim.getEnded();
    }

    public boolean hasEnd(){
        return anim.getHasEnd();
    }

    public screenObject getFrame(int x, int y, int orientation) {
        return anim.getFrame(x,y, orientation);
    }

    public hitBox getHitbox(){
        return anim.getHitbox();
    }

    public hitBox getHurtbox(){
        return anim.getHurtBox();
    }
}
