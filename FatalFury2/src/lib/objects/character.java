package lib.objects;

import lib.Enums.Animation_type;
import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.characters.load_character;

import java.util.*;

public class character {
    private Playable_Character charac;
    // String que representa el combo para usar un tipo de ataque
    private Map<String, Movement> combos = new HashMap<String, Movement>();
    // Movimiento asociando a un tipo de ataque
    private Map<Movement, movement> movements = new HashMap<Movement, movement>();
    // Inverso del primero para la ia
    private Map<Movement, String> movementsKeys = new HashMap<>();;
    // Podría haberse puesto en un único mapa, pero fue para independizar
    // el movimiento en sí del combo necesario
    // Vida del personaje
    private int life = 100;
    // Orientación del personaje (1 mira hacia la izquierda, -1 hacia la derecha)
    private int orientation = 1;
    // Coordenadas actuales del personaje
    private int x = 150, y = 160;
    // Estado del personaje en cuanto a movimientos
    private Movement state = Movement.STANDING;
    // Para gestión de límites de mapa
    private hitBox mapLimit;
    // Rival
    private character rival;
    // Lista de movimientos realizados
    private List<Movement> executedMoves = new ArrayList<>();
    // Entero de victoria o derrota
    private int gameResult = 0;
    private int heightRef = 0;

    // Genera los movimientos en base al personaje deseado
    public character(Playable_Character c, int pN){
        charac = c;
        if(c == Playable_Character.MAI){
            new load_character().generateMovs("mai", pN, combos, movementsKeys, movements, 0.8);
        }
        else if(c == Playable_Character.ANDY){
            new load_character().generateMovs("andy", pN, combos, movementsKeys, movements, 0.8);
        }
        else{
            new load_character().generateMovs("terry", pN, combos, movementsKeys, movements, 0.8);
        }
        // Por defecto está en STANDING
        movements.get(Movement.STANDING).start(999);
        heightRef = movements.get(Movement.STANDING).getHurtbox().getY()+290;
    }

    public boolean onHeightRef(){
        return heightRef == movements.get(state).getHurtbox().getY()+290;
    }

    // Devuelve el frame correspondiente al movimiento identificado por el combo mov
    // en caso de no estar en un estado que no se pueda interrumpir
    // collides indica si colisiona o no con el otro personaje
    public screenObject getFrame(String mov, hitBox pHurt, hitBox eHurt, boolean enemyAttacking){
        boolean collides = pHurt.collides(eHurt);
        boolean collidesLimitLeft = pHurt.getX() <= mapLimit.getX();
        boolean collidesLimitRight = pHurt.getX()+pHurt.getWidth() >= mapLimit.getX()+mapLimit.getWidth();
        int dis = 0;
        if (pHurt.getX() > eHurt.getX()){
            dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
        }
        else if(pHurt.getX() < eHurt.getX()){
            dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
        }
        if(mov == null){
            mov = "";
        }
        if (mov.length() == 4 && (mov.contains("DE-") || mov.contains("IZ-"))
            && !(mov.equals("DE-C") && dis < 10)) {
            mov = String.valueOf(mov.charAt(mov.length() - 1));
        }

        // Si el movimiento es infinito y el movimiento es diferente del actual
        // o el movimiento no es infinito pero ha terminado
        // Actualiza el estado
        boolean stateChanged = false;
        if(state == Movement.THROWN_OUT && gameResult == 3){}
        else if(gameResult == 3 && state != Movement.THROWN_OUT){
            movements.get(state).reset();
            state = Movement.DEFEAT;
            movements.get(state).start(999);
            gameResult = 4;
            stateChanged = true;
        }
        else if(gameResult != 0 && (movements.get(state).ended() || !movements.get(state).hasEnd())
                && !isSpecial(state) && state != Movement.JUMP_ROLL_FALL){
            movements.get(state).reset();
            if(gameResult == 1){
                state = Movement.VICTORY_ROUND;
            }
            else if(gameResult == 2){
                state = Movement.VICTORY_FIGHT;
            }
            else{
                state = Movement.DEFEAT;
            }
            movements.get(state).start(999);
            stateChanged = true;
        }
        else if (isSpecial(state) && movements.get(state).ended()){
            movements.get(state).getAnim().reset();
            state = Movement.JUMP_ROLL_FALL;
            movements.get(state).start(dis);
            executedMoves.add(state);
            stateChanged = true;
        }
        else if((state == Movement.JUMP_PUNCH_DOWN || state == Movement.JUMP_ROLL_PUNCH_DOWN)
                && movements.get(state).getAnim().getState() == movements.get(state).getAnim().getFrames().size()-1
                && System.currentTimeMillis() - movements.get(state).getAnim().getStartTime() > 0.5 * movements.get(state).getAnim().getTimes().get(movements.get(state).getAnim().getState())){
            movements.get(state).getAnim().reset();
            if(state == Movement.JUMP_PUNCH_DOWN || state == Movement.JUMP_HARD_PUNCH_DOWN || state == Movement.JUMP_KICK_DOWN){
                state = Movement.JUMP_FALL;
            }
            else if(state == Movement.JUMP_ROLL_PUNCH_DOWN || state == Movement.JUMP_ROLL_HARD_PUNCH_DOWN || state == Movement.JUMP_KICK){
                state = Movement.JUMP_ROLL_FALL;
            }
            movements.get(state).start(dis);
            executedMoves.add(state);
            stateChanged = true;
        }
        else if((state == Movement.NORMAL_JUMP || state == Movement.JUMP_ROLL_RIGHT) && !movements.get(state).ended() && y < -90
                && (mov.endsWith("-A") ||  mov.endsWith("-B") ||  mov.endsWith("-C") ||  mov.endsWith("-D")
                    || mov.equals("A") || mov.equals("B") || mov.equals("C") || mov.equals("D"))){
            movements.get(state).getAnim().reset();
            if(state == Movement.NORMAL_JUMP){
                if(mov.endsWith("-A") || mov.equals("A")){
                    state = Movement.JUMP_PUNCH_DOWN;
                }
                else if(mov.endsWith("-C") || mov.equals("C")){
                    state = Movement.JUMP_HARD_PUNCH_DOWN;
                }
                else if(mov.endsWith("-D") || mov.endsWith("D")
                        || mov.endsWith("-B") || mov.equals("B")){
                    state = Movement.JUMP_KICK_DOWN;
                }
            }
            else{
                if(mov.endsWith("-A") || mov.equals("A")){
                    state = Movement.JUMP_ROLL_PUNCH_DOWN;
                }
                else if(mov.endsWith("-C") || mov.equals("C")){
                    state = Movement.JUMP_ROLL_HARD_PUNCH_DOWN;
                }
                else if(mov.endsWith("-B") || mov.endsWith("-D")
                        || mov.equals("B") || mov.equals("D")){
                    state = Movement.JUMP_KICK;
                }
            }
            movements.get(state).start(dis);
            executedMoves.add(state);
            stateChanged = true;
        }
        else if(isCrouched() && mov.equals("AB") && movements.get(state).ended()){
            movements.get(state).getAnim().reset();
            state = combos.get(mov);
            movements.get(state).start(dis);
            movements.get(state).getAnim().end();
            executedMoves.add(state);
            stateChanged = true;
        }
        else if(mov.contains("+") && combos.containsKey(mov) && combos.get(mov) != state && !isCombing()  && !isJumping()){
            if(state != Movement.JUMP_ROLL_FALL && state != Movement.JUMP_FALL && !((isJumping() || state == Movement.THROW) && combos.get(mov) == Movement.DASH)) {
                movements.get(state).getAnim().reset();
                state = combos.get(mov);
                movements.get(state).start(dis);
                executedMoves.add(state);
                stateChanged = true;
            }
        }

        else if ((movements.get(state).getAnim().getType() == Animation_type.HOLDABLE || isCrouched()) && movements.get(state).ended()
                && combos.get(mov) != state && !mov.startsWith("AB")){
            Movement aux = Movement.UNDO_CROUCH;
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
                || (state == Movement.WALKING || state == Movement.WALKING_BACK || state == Movement.CROUCHED_WALKING) && movements.get(state).ended()
                || (state == Movement.WALKING || state == Movement.WALKING_BACK || state == Movement.CROUCHED_WALKING) && combos.get(mov) != state){
            if(state != Movement.STANDING){
                movements.get(state).getAnim().reset();
            }
            Movement stateAnt = state;
            state = combos.get(mov);
            if(state != Movement.STANDING){
                if(state == Movement.WALKING && enemyAttacking){
                    movements.get(state).start(movements.get(state).getDistChange());
                }
                else if(stateAnt == Movement.CROUCHED_WALKING && state == Movement.CROUCH){
                    movements.get(state).start(dis);
                    movements.get(state).getAnim().end();
                }
                else if(state == Movement.THROW){
                    if(dis >= 10 || !rival.onHeightRef()){
                        state = Movement.HARD_PUNCH;
                    }
                    else{
                        rival.setY(290);
                    }
                    movements.get(state).start(dis);
                }
                else{
                    movements.get(state).start(dis);
                }
            }
            executedMoves.add(state);
            stateChanged = true;
        }

        // Frame a mostrar
        screenObject s =  movements.get(state).getFrame(x,y, orientation);

        if(state == Movement.STANDING && y != 290){
            y = 290;
            s.setY(290);
        }

        if(state != Movement.STANDING && state != Movement.WALKING_BACK && state != Movement.WALKING &&
                movements.get(state).ended() && !stateChanged && s.getY() == y
                && movements.get(state).getAnim().getType() != Animation_type.HOLDABLE
                && !(state == Movement.THROWN_OUT && gameResult == 3)){
            if(isCrouched()){
                movements.get(Movement.CROUCH).getAnim().end();
                s =  movements.get(Movement.CROUCH).getFrame(x,y, orientation);
            }
            else {
                s = movements.get(Movement.STANDING).getFrame(x, y, orientation);
            }
        }

        if(s.getY() > 290){
            y = 290;
            s.setY(290);
        }

        // Gestión de colisiones
        if (gameResult == 2 && stateChanged && charac == Playable_Character.TERRY) {
            s.setY(-270);
        }
        else if(collides && isAttacking() && inDisplacement()){
            s.setX(x);
        }
        else if(collidesLimitLeft && (s.getX() < x || collides)
                || collidesLimitRight && (s.getX() > x || collides)){
            if(inKnockback() && !rival.inKnockback()){
                rival.returnKnockback(Math.abs(x - s.getX()));
            }
            if(collides && pHurt.getY() < eHurt.getY()+eHurt.getHeight()){
                int increment = -orientation;
                if(orientation == 1 && pHurt.getX() <= eHurt.getX()
                        || orientation == -1 && pHurt.getX() > eHurt.getX()){
                    increment = -orientation;
                }
                 x += increment;
                 s.setX(x);
            }
            else{
                s.setX(x);
            }
        }
        else if(state == Movement.THROWN_OUT){
            x = s.getX();
        }
        //////////////////////////////////////////////////////////
        else if(state == Movement.THROW){
            s.setX(x);
        }
        //////////////////////////////////////////////////////////
        else if(collides && pHurt.getY() <= eHurt.getY()+eHurt.getHeight()){
            int increment = orientation;
            if(orientation == 1 && pHurt.getX() < eHurt.getX()
                    || orientation == -1 && pHurt.getX() > eHurt.getX()){
                increment = -orientation;
            }
            if(eHurt.getX() <= mapLimit.getX() && s.getX() < x
                || eHurt.getX()+pHurt.getWidth() >= mapLimit.getX()+mapLimit.getWidth()){
                x += increment;
            }
            else{
                x = s.getX() + increment;
            }
            s.setX(x);
        }
        // Si no colisiona, o está andando hacia atrás mirando a la izquierda
        // o está andando hacia adelante mirando hacia la derecha (ambos casos
        // se aleja del enemigo), se actualizan las coordenadas del personaje
        else if(!collides || state == Movement.WALKING_BACK && orientation == 1
                || state == Movement.WALKING && orientation == -1
                || state == Movement.CROUCHED_WALKING && orientation == -1) {
            x = s.getX();
        }
        // En caso contrario, las coordenadas del objeto son las sin actualizar del personaje
        else{
            s.setX(x);
        }
        y = s.getY();
        return s;
    }

    public void setVictory(int v){
        gameResult = v;
    }

    public void setDefeat(){
        y = 290;
        gameResult = 3;
    }

    // Aplicar un daño recibido al personaje
    public void applyDamage(int dmg){
        if(life-dmg < 0){life = 0;}
        else{life -= dmg;}
    }

    public void returnKnockback(int k){
        if(rival.getState() != Movement.THROWN_OUT) {
            this.x = x + orientation * k;
        }
        else{
            this.x = x - orientation * k;
        }
    }

    void reset(int x, int y, int orientation){
        life = 100;
        this.orientation = orientation;
        // Coordenadas actuales del personaje
        this.x = x;
        this.y = y;
        this.state = Movement.STANDING;
        this.gameResult = 0;
    }

    boolean isCrouched(){
        return (state == Movement.CROUCH || state == Movement.CROUCH_2
                || state == Movement.CROUCHED_BLOCK || state == Movement.CROUCHED_WALKING
                || state == Movement.CROUCHING_HARD_PUNCH || state == Movement.CROUCHING_SOFT_PUNCH
                || state == Movement.CROUCHING_HARD_KICK || state == Movement.CROUCHING_SOFT_KICK);
    }

    boolean isAttacking(){
        Movement array[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.THROW, Movement.ATTACK_POKE,
                Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN, Movement.CHARGED_PUNCH_A,
                Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK, Movement.JUMP_KICK_DOWN, Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK,
                Movement.CROUCHING_SOFT_PUNCH, Movement.CROUCHING_HARD_PUNCH};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(state);
    }

    boolean isJumping(){
        Movement array[] = {Movement.JUMP_KNOCKBACK, Movement.JUMP_ROLL_RIGHT, Movement.NORMAL_JUMP,
                            Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN, Movement.JUMP_ROLL_FALL,
                            Movement.JUMP_FALL, Movement.DASH, Movement.JUMP_KICK_DOWN, Movement.JUMP_KICK,
                            Movement.JUMP_HARD_PUNCH_DOWN, Movement.JUMP_ROLL_HARD_PUNCH_DOWN, Movement.JUMP_ROLL_LEFT,
                            Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D, Movement.CHARGED_PUNCH_A,
                            Movement.CHARGED_PUNCH_C, Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C};
        List<Movement> jumps = Arrays.asList(array);
        return jumps.contains(state);
    }

    boolean inKnockback(){
        Movement array[] = {Movement.JUMP_KNOCKBACK, Movement.STANDING_BLOCK_KNOCKBACK_HARD, Movement.STANDING_BLOCK_KNOCKBACK_SOFT,
                Movement.CROUCHED_KNOCKBACK,  Movement.MEDIUM_KNOCKBACK, Movement.SOFT_KNOCKBACK,
                Movement.HARD_KNOCKBACK, Movement.THROWN_OUT, Movement.CROUCHED_BLOCK_KNOCKBACK};
        List<Movement> knock = Arrays.asList(array);
        return knock.contains(state);
    }

    boolean inDisplacement(){
        Movement array[] = {Movement.JUMP_KNOCKBACK, Movement.STANDING_BLOCK_KNOCKBACK_HARD, Movement.STANDING_BLOCK_KNOCKBACK_SOFT,
                Movement.CROUCHED_KNOCKBACK,  Movement.MEDIUM_KNOCKBACK, Movement.SOFT_KNOCKBACK,
                Movement.HARD_KNOCKBACK, Movement.THROWN_OUT, Movement.WALKING_BACK, Movement.WALKING, Movement.JUMP_ROLL_RIGHT,
                Movement.DASH, Movement.CHARGED_PUNCH_A, Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK, Movement.JUMP_ROLL_LEFT,
                Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D, Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C
        };
        List<Movement> knock = Arrays.asList(array);
        return knock.contains(state);
    }

    boolean isCombing(){
        Movement array[] = {Movement.JUMP_KICK_DOWN, Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN,
                Movement.CHARGED_PUNCH_A, Movement.JUMP_HARD_PUNCH_DOWN,  Movement.JUMP_ROLL_HARD_PUNCH_DOWN,
                Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK, Movement.DASH, Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(state);
    }

    boolean isSpecial(Movement m){
        Movement array[] = {Movement.CHARGED_PUNCH_A, Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK,
                Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
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

    public  int getDamage(){
        return movements.get(state).getDamage();
    }

    public movement getMovement(Movement m){
        return movements.get(m);
    }

    public movement getMovement(String c){
        return movements.get(combos.get(c));
    }

    public void setState(Movement state) {
        this.state = state;
        this.movements.get(state).start(9999);
    }

    public hitBox getMapLimit() {
        return mapLimit;
    }

    public void setMapLimit(hitBox mapLimit) {
        this.mapLimit = mapLimit;
    }

    public character getRival() {
        return rival;
    }

    public void setRival(character rival) {
        this.rival = rival;
    }

    public Map<Movement, String> getMovementsKeys() {
        return movementsKeys;
    }

    public void setMovementsKeys(Map<Movement, String> movementsKeys) {
        this.movementsKeys = movementsKeys;
    }

    public List<Movement> getExecutedMoves() {
        return executedMoves;
    }

    public void setExecutedMoves(List<Movement> executedMoves) {
        this.executedMoves = executedMoves;
    }
}
