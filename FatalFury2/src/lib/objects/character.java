package lib.objects;

import lib.Enums.Animation_type;
import lib.Enums.Audio_Type;
import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.characters.load_character;
import lib.sound.Sound;

import java.util.*;

public class character {
    Playable_Character charac;
    // String que representa el combo para usar un tipo de ataque
    Map<String, Movement> combos = new HashMap<String, Movement>();
    // Movimiento asociando a un tipo de ataque
    Map<Movement, movement> movements = new HashMap<Movement, movement>();
    // Podría haberse puesto en un único mapa, pero fue para independizar
    // el movimiento en sí del combo necesario
    // Vida del personaje
    int life = 100;
    // Orientación del personaje (1 mira hacia la izquierda, -1 hacia la derecha)
    int orientation = 1;
    // Coordenadas actuales del personaje
    int x = 150, y = 160;
    // Estado del personaje en cuanto a movimientos
    Movement state = Movement.STANDING;
    // Reproductor de voces del personaje
    Sound voices;

    // Genera los movimientos en base al personaje deseado
    public character(Playable_Character c){
        charac = c;
        if(c == Playable_Character.MAI){
            //generar movimientos de mai
        }
        else if(c == Playable_Character.ANDY){
            //generar movimientos de andy
        }
        else{
            voices = new Sound(Audio_Type.Andy_audio);
            //new terry().generateMovs(combos, movements, voices);
            new load_character().generateMovs("terry", combos, movements, voices, 0.8);
        }
        // Por defecto está en STANDING
        movements.get(Movement.STANDING).start(999);
    }

    // Devuelve el frame correspondiente al movimiento identificado por el combo mov
    // en caso de no estar en un estado que no se pueda interrumpir
    // collides indica si colisiona o no con el otro personaje
    public screenObject getFrame(String mov, hitBox pHurt, hitBox eHurt, boolean enemyAttacking){

        if(mov.contains("DE") && orientation == 1){
            mov = mov.replace("DE", "IZ");
        }
        else if(mov.contains("IZ") && orientation == 1){
            mov =  mov.replace("IZ", "DE");
        }

        boolean collides = pHurt.collides(eHurt);

        int dis = 0;
        if (pHurt.getX() > eHurt.getX()){
            dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
        }
        else if(pHurt.getX() < eHurt.getX()){
            dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
        }

        // Si el movimiento es infinito y el movimiento es diferente del actual
        // o el movimiento no es infinito pero ha terminado
        // Actualiza el estado
        boolean stateChanged = false;
        if(mov.contains("+") && combos.containsKey(mov)){
            movements.get(state).getAnim().reset();
            state = combos.get(mov);
            movements.get(state).start(dis);
            stateChanged = true;
        }
        else if (movements.get(state).getAnim().getType() == Animation_type.HOLDABLE && movements.get(state).ended()
                && combos.get(mov) != state){
            Movement aux = Movement.NONE;
            switch (state){
                case CROUCH:
                    aux = Movement.UNDO_CROUCH;
                    break;
            }
            movements.get(state).getAnim().reset();
            state = aux;
            movements.get(state).start(dis);
            stateChanged = true;
        }
        else if(!combos.containsKey(mov)){
            if (!movements.get(state).hasEnd() || movements.get(state).hasEnd() && movements.get(state).ended()){
                movements.get(state).getAnim().reset();
                state = Movement.STANDING;
                movements.get(state).start(dis);
                stateChanged = true;
            }
        }
        else if ((!movements.get(state).hasEnd() && combos.get(mov) != state)
                || movements.get(state).hasEnd() && movements.get(state).ended()  && combos.get(mov) != state
                || (state == Movement.WALKING || state == Movement.WALKING_BACK) && movements.get(state).ended()
                || (state == Movement.WALKING || state == Movement.WALKING_BACK) && combos.get(mov) != state){
            if(state != Movement.STANDING){
                movements.get(state).getAnim().reset();
            }
            state = combos.get(mov);
            if(state != Movement.STANDING){
                if(state == Movement.WALKING && enemyAttacking){
                    movements.get(state).start(movements.get(state).getDistChange());
                }
                else{
                    movements.get(state).start(dis);
                }
            }
            stateChanged = true;
        }
        // Frame a mostrar
        screenObject s =  movements.get(state).getFrame(x,y, orientation);

        if(state != Movement.STANDING && state != Movement.WALKING_BACK && state != Movement.WALKING &&
                movements.get(state).ended() && !stateChanged && s.getY() == y
                && movements.get(state).getAnim().getType() != Animation_type.HOLDABLE){
            s =  movements.get(Movement.STANDING).getFrame(x,y, orientation);
        }

        // Gestión de colisiones
        if(state == Movement.THROWN_OUT){
            x = s.getX();
        }
        else if(collides /*&& state == Movement.STANDING*/ && pHurt.getY() <= eHurt.getY()+eHurt.getHeight()){
            int increment = orientation;
            if(orientation == 1 && pHurt.getX() < eHurt.getX()
                    || orientation == -1 && pHurt.getX() > eHurt.getX()){
                increment = -orientation;
            }
            x = s.getX() + increment;
            s.setX(x);
        }
        // Si no colisiona, o está andando hacia atrás mirando a la izquierda
        // o está andando hacia adelante mirando hacia la derecha (ambos casos
        // se aleja del enemigo), se actualizan las coordenadas del personaje
        else if(!collides || state == Movement.WALKING_BACK && orientation == 1
                || state == Movement.WALKING && orientation == -1) {
            x = s.getX();
        }
        // En caso contrario, las coordenadas del objeto son las sin actualizar del personaje
        else{
            s.setX(x);
        }
        y = s.getY();
        return s;
    }

    // Aplicar un daño recibido al personaje
    public void applyDamage(int dmg){
        if(life-dmg < 0){life = 0;}
        else{life -= dmg;}
    }

    void reset(int x, int y, int orientation){
        life = 100;
        this.orientation = orientation;
        // Coordenadas actuales del personaje
        this.x = x;
        this.y = y;
        this.state = Movement.STANDING;
    }

    boolean isCrouched(){
        return (state == Movement.CROUCH || state == Movement.CROUCH_2
                || state == Movement.CROUCHED_BLOCK || state == Movement.CROUCHED_WALKING);
    }

    boolean isAttacking(){
        Movement array[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.GUARD_ATTACK, Movement.THROW,
                Movement.DESPERATION_MOVE, Movement.ATTACK_POKE, Movement.RANGED_ATTACK,
                Movement.JUMP_PUNCH_DOWN};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(state);
    }

    boolean isJumping(){
        Movement array[] = {Movement.JUMP_KNOCKBACK, Movement.JUMP_ROLL_RIGHT, Movement.NORMAL_JUMP,
                            Movement.JUMP_PUNCH_DOWN};
        List<Movement> jumps = Arrays.asList(array);
        return jumps.contains(state);
    }

    //Getters y setters
    public Playable_Character getCharac() {
        return charac;
    }

    public void setCharac(Playable_Character charac) {
        this.charac = charac;
    }

    public Map<String, Movement> getCombos() {
        return combos;
    }

    public void setCombos(Map<String, Movement> combos) {
        this.combos = combos;
    }

    public Map<Movement, movement> getMovements() {
        return movements;
    }

    public void setMovements(Map<Movement, movement> movements) {
        this.movements = movements;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
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

    public Movement getState() {
        return state;
    }

    public void setState(Movement state, hitBox pHurt, hitBox eHurt) {
        this.movements.get(state).reset();
        this.state = state;
        int dis = 0;
        if (pHurt.getX() > eHurt.getX()){
            dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
        }
        else if(pHurt.getX() < eHurt.getX()){
            dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
        }
        this.movements.get(state).start(dis);
    }

    public hitBox getHitbox(){
        hitBox aux = movements.get(state).getHitbox();
        int auxX = x + aux.getX();
        if(orientation == -1){
            auxX = x - aux.getX() - aux.getWidth();
        }
        return new hitBox(auxX, y+aux.getY(), aux.getWidth(), aux.getHeight(), box_type.HITBOX);
    }

    public hitBox getHurtbox(){
        hitBox aux = movements.get(state).getHurtbox();
        int auxX = x + aux.getX();
        if(orientation == -1){
            auxX = x - aux.getX() - aux.getWidth();
        }
        return new hitBox(auxX, y+aux.getY(), aux.getWidth(), aux.getHeight(),  box_type.HURTBOX);
    }

    public hitBox getCoverbox(){
        hitBox aux = movements.get(state).getCoverbox();
        int auxX = x + aux.getX();
        if(orientation == -1){
            auxX = x - aux.getX() - aux.getWidth();
        }
        return new hitBox(auxX, y+aux.getY(), aux.getWidth(), aux.getHeight(),  box_type.COVERBOX);
    }

    public boolean endedMovement(){
        return !movements.get(state).getAnim().getHasEnd() || movements.get(state).getAnim().getEnded();
    }

    public Sound getVoices() {
        return voices;
    }

    public void setVoices(Sound voices) {
        this.voices = voices;
    }

    public  int getDamage(){
        return movements.get(state).getDamage();
    }

    public movement getMovement(Movement m){
        return movements.get(m);
    }

    public movement getMovement(String c){
        return movements.get(combos.get(c));
    }
}
