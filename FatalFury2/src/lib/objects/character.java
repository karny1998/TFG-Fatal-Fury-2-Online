package lib.objects;

import lib.Enums.Animation_type;
import lib.Enums.Audio_Type;
import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.sound.Sound;

import java.util.HashMap;
import java.util.Map;

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
            new terry().generateMovs(combos, movements, voices);
        }
        // Por defecto está en STANDING
        movements.get(Movement.STANDING).start();
    }

    // Devuelve el frame correspondiente al movimiento identificado por el combo mov
    // en caso de no estar en un estado que no se pueda interrumpir
    // collides indica si colisiona o no con el otro personaje
    public screenObject getFrame(String mov, boolean collides){
        // Si el movimiento es infinito y el movimiento es diferente del actual
        // o el movimiento no es infinito pero ha terminado
        // Actualiza el estado
        if (movements.get(state).getAnim().getType() == Animation_type.HOLDABLE && movements.get(state).ended()
            && combos.get(mov) != state){
            Movement aux = Movement.NONE;
            switch (state){
                case CROUCH:
                    aux = Movement.UNDO_CROUCH;
                    break;
            }
            movements.get(state).getAnim().reset();
            state = aux;
            movements.get(state).start();
        }
        else if ((!movements.get(state).hasEnd() && combos.get(mov) != state)
                || movements.get(state).hasEnd() && movements.get(state).ended()){
            movements.get(state).getAnim().reset();
            state = combos.get(mov);
            movements.get(state).start();
        }
        // Frame a mostrar
        screenObject s =  movements.get(state).getFrame(x,y, orientation);
        // Si no colisiona, o está andando hacia atrás mirando a la izquierda
        // o está andando hacia adelante mirando hacia la derecha (ambos casos
        // se aleja del enemigo), se actualizan las coordenadas del personaje
        if(!collides || state == Movement.WALKING_BACK && orientation == 1
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
        life -= dmg;
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

    public void setState(Movement state) {
        this.state = state;
    }

    public hitBox getHitbox(){
        hitBox aux = movements.get(state).getHitbox();
        int auxX = x + aux.getX();
        if(orientation == -1){
            auxX = x - aux.getX() - aux.getWidth();
        }
        return new hitBox(auxX, y+aux.getY(), aux.getWidth(), aux.getHeight(), true);
    }

    public hitBox getHurtbox(){
        hitBox aux = movements.get(state).getHurtbox();
        int auxX = x + aux.getX();
        if(orientation == -1){
            auxX = x - aux.getX() - aux.getWidth();
        }
        return new hitBox(auxX, y+aux.getY(), aux.getWidth(), aux.getHeight(), false);
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
}
