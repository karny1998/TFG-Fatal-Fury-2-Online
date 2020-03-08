package lib.objects;

import lib.Enums.Movement;
import lib.Enums.Playable_Character;

import java.util.HashMap;
import java.util.Map;

public class character {
    Playable_Character charac;
    //EL string contendra la cadena de botones que representa
    //cada movimiento
    Map<String, Movement> combos = new HashMap<String, Movement>();
    Map<Movement, movement> movements = new HashMap<Movement, movement>();
    int life = 100;
    int orientation = 1;
    int x = 150, y = 260;
    Movement state = Movement.STANDING;

    public character(Playable_Character c){
        charac = c;
        if(c == Playable_Character.MAI){
            //generar movimientos de mai
        }
        else if(c == Playable_Character.ANDY){
            //generar movimientos de andy
        }
        else{
            new terry().generateMovs(combos, movements);
        }
        movements.get(Movement.STANDING).start();
    }

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

    public screenObject getFrame(String mov){
        if ((!movements.get(state).hasEnd() && combos.get(mov) != state)
                || movements.get(state).hasEnd() && movements.get(state).ended()){
            movements.get(state).getAnim().reset();
            state = combos.get(mov);
            movements.get(state).start();
        }
        screenObject s =  movements.get(state).getFrame(x,y, -orientation);
        x = s.getX();
        y = s.getY();
        return s;
    }
}
