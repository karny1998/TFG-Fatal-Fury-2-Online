package lib.objects;

import lib.Enums.Animation_type;
import lib.Enums.Movement;
import lib.Enums.Playable_Character;
import lib.characters.load_character;

import java.util.*;

// Clase que representa un personaje con sus correspondientes movimientos
public class character {
    // Identificador del personaje
    private Playable_Character charac;
    // String que representa el combo para usar un tipo de ataque
    private Map<String, Movement> combos = new HashMap<String, Movement>();
    // Movimiento asociando a un tipo de ataque
    private Map<Movement, movement> movements = new HashMap<Movement, movement>();
    // Inverso del primero para la ia
    private Map<Movement, String> movementsKeys = new HashMap<>();;
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
    // Entero de victoria o derrota (1 victoria ronda, 2 victoria pelea, 3 derrota y 4 auxiliar)
    private int gameResult = 0;
    // Altura de referencia del personaje (para throws)
    private int heightRef = 0;
    // Si ha sido rebalanceado o no
    private boolean rebalanced = false;

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

    // Devuelve el frame correspondiente al movimiento identificado por el combo mov
    // en caso de no estar en un estado que no se pueda interrumpir
    // enemyAttacking indica si el rival está atacando (ahora sería innecesario)
    public screenObject getFrame(String mov, hitBox pHurt, hitBox eHurt, boolean enemyAttacking){
        // Comprobación de si colisiona el personaje con el rival
        boolean collides = pHurt.collides(eHurt);
        // Comprobación de colisiones de los personajes con los límites del mapa
        boolean collidesLimitLeft = pHurt.getX() <= mapLimit.getX();
        boolean collidesLimitRight = pHurt.getX()+pHurt.getWidth() >= mapLimit.getX()+mapLimit.getWidth();
        boolean rivalCollidesLimitLeft = eHurt.getX() <= mapLimit.getX();
        boolean rivalCollidesLimitRight = eHurt.getX()+eHurt.getWidth() >= mapLimit.getX()+mapLimit.getWidth();

        // Cáculo de la distancia entre personajes
        int dis = 0;
        if (pHurt.getX() > eHurt.getX()){
            dis = pHurt.getX() - (eHurt.getX()+eHurt.getWidth());
        }
        else if(pHurt.getX() < eHurt.getX()){
            dis = eHurt.getX() - (pHurt.getX()+pHurt.getWidth());
        }

        // Para evitar posibles problemas
        if(mov == null){
            mov = "";
        }

        // Si el movimiento es uno compuesto por una flecha y un ataque básico
        // Y no es el throw, se ignora la flecha
        if (mov.length() == 4 && (mov.contains("DE-") || mov.contains("IZ-"))
            && !(mov.equals("DE-C") && dis < 10)) {
            mov = String.valueOf(mov.charAt(mov.length() - 1));
        }

        // Si ha cambiado de estado o no
        boolean stateChanged = false;
        // Si está siendo lanzado y se el resultado es derrota, termina la animación de ser lanzado
        if(state == Movement.THROWN_OUT && gameResult == 3){}
        // Si el resultado es derrota, y no se está siendo lanzado, se ejecuta la animación de derrota
        // y se pasa al estado axuliar
        else if(gameResult == 3 && state != Movement.THROWN_OUT){
            movements.get(state).reset();
            state = Movement.DEFEAT;
            movements.get(state).start(999);
            gameResult = 4;
            stateChanged = true;
        }
        // Si se está lanzando al rival, se está en el segundo frame de la animación, y el rival
        // no está siendo lanzado, se ha fallado el movimiento, y se termina
        else if(state == Movement.THROW && movements.get(Movement.THROW).getAnim().getState() == 2 && rival.getState() != Movement.THROWN_OUT){
            movements.get(state).reset();
            state = Movement.STANDING;
            movements.get(state).start(dis);
            stateChanged = true;
        }
        // Si la pelea ha terminado y se ha terminado el movimiento que se estaba haciendo (siempre que
        // no sea un ataque especual ni se esté cayendo del aire), se muestra la animación de victoria o derrota
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
        // Si se estaba haciendo un movimiento especial y ha terminado, se pasa a caer al suelo
        // (todos los especiales tienen saltos, y tras ellos se debe acabar en el suelo)
        else if (isSpecial(state) && movements.get(state).ended()){
            movements.get(state).getAnim().reset();
            state = Movement.JUMP_ROLL_FALL;
            movements.get(state).start(dis);
            executedMoves.add(state);
            stateChanged = true;
        }
        // Si se está haciendo un puñetado en el aire, se está en el último frame, y ha pasado tiempo suficiente
        // se pasa al estado de caída
        else if((state == Movement.JUMP_PUNCH_DOWN || state == Movement.JUMP_ROLL_PUNCH_DOWN
                || state == Movement.JUMP_HARD_PUNCH_DOWN || state == Movement.JUMP_ROLL_HARD_PUNCH_DOWN)
                && movements.get(state).getAnim().getState() == movements.get(state).getAnim().getFrames().size()-1
                && System.currentTimeMillis() - movements.get(state).getAnim().getStartTime() > 0.5 * movements.get(state).getAnim().getTimes().get(movements.get(state).getAnim().getState())){
            movements.get(state).getAnim().reset();
            if(state == Movement.JUMP_PUNCH_DOWN || state == Movement.JUMP_HARD_PUNCH_DOWN){
                state = Movement.JUMP_FALL;
            }
            else if(state == Movement.JUMP_ROLL_PUNCH_DOWN || state == Movement.JUMP_ROLL_HARD_PUNCH_DOWN){
                state = Movement.JUMP_ROLL_FALL;
            }
            movements.get(state).start(dis);
            executedMoves.add(state);
            stateChanged = true;
        }
        // Si se está saltando, se está por encima de la altura mínima, y se solicita hacer un puñetazo
        // o patada, se ejecuta el puñetado o patada en salto
        else if((state == Movement.NORMAL_JUMP || state == Movement.JUMP_ROLL_RIGHT) && !movements.get(state).ended() && y < 40
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
        // Si está agachado, el movimiento pedido es agacharse, y el movimiento anterior
        // ha terminado, se pasa al estado de agachado en su último frame
        else if(isCrouched() && mov.equals("AB") && movements.get(state).ended()){
            movements.get(state).getAnim().reset();
            state = combos.get(mov);
            movements.get(state).start(dis);
            movements.get(state).getAnim().end();
            executedMoves.add(state);
            stateChanged = true;
        }
        // Si el movimiento es combo (contiene +) y se está saltando o ejecutando un ataque especial, se omite
        else if(mov.contains("+") && combos.containsKey(mov) && combos.get(mov) != state && !isCombing()  && !isJumping()){
            // Si es un dash, y no se corta ninguna animación peligrosa, se ejecuta
            if(state != Movement.JUMP_ROLL_FALL && state != Movement.JUMP_FALL && !((isJumping() || state == Movement.THROW) && combos.get(mov) == Movement.DASH)) {
                movements.get(state).getAnim().reset();
                state = combos.get(mov);
                movements.get(state).start(dis);
                executedMoves.add(state);
                stateChanged = true;
            }
        }
        // Si se estaba agachado y se ha soltado el abajo, se hace la animación de levantarse
        else if ((movements.get(state).getAnim().getType() == Animation_type.HOLDABLE || isCrouched()) && movements.get(state).ended()
                && combos.get(mov) != state && !mov.startsWith("AB")){
            Movement aux = Movement.UNDO_CROUCH;
            movements.get(state).getAnim().reset();
            state = aux;
            movements.get(state).start(dis);
            stateChanged = true;
        }
        // Si no existe el movimiento
        else if(!combos.containsKey(mov)){
            // Si se ha terminado el movimiento anterior, se pone en standing
            if (!movements.get(state).hasEnd() || movements.get(state).hasEnd() && movements.get(state).ended()){
                movements.get(state).getAnim().reset();
                state = Movement.STANDING;
                movements.get(state).start(dis);
                stateChanged = true;
            }
        }
        // Si el movimiento actual no tiene final, o se está andando, o se ha terminado correctamente, se ejecuta el nuevo movimiento
        else if ((!movements.get(state).hasEnd() && combos.get(mov) != state)
                || movements.get(state).hasEnd() && movements.get(state).ended()  && combos.get(mov) != state
                || (state == Movement.WALKING || state == Movement.WALKING_BACK || state == Movement.CROUCHED_WALKING) && movements.get(state).ended()
                || (state == Movement.WALKING || state == Movement.WALKING_BACK || state == Movement.CROUCHED_WALKING) && combos.get(mov) != state){
            if(state != Movement.STANDING){
                movements.get(state).getAnim().reset();
            }
            Movement stateAnt = state;
            state = combos.get(mov);
            // Si el nuevo movimiento no es Standing
            if(state != Movement.STANDING){
                // Si el movimiento es andar y el enemigo está atacando, se cubre
                if(state == Movement.WALKING && enemyAttacking){
                    movements.get(state).start(movements.get(state).getDistChange());
                }
                // Si se estaba andando agachado y el nuevo estado es agachado simplemente
                // Se pasa al último frame de agachado
                else if(stateAnt == Movement.CROUCHED_WALKING && state == Movement.CROUCH){
                    movements.get(state).start(dis);
                    movements.get(state).getAnim().end();
                }
                // Si el nuevo movimiento es el lanzamiento
                else if(state == Movement.THROW){
                    // Se comprueba que se está lo suficientemente cerca, y ambos a las alturas de referencia
                    // y en caso de no estarlo, se ejecuta un puñetazo fuerte
                    if(dis >= 10 || !rival.onHeightRef()){
                        state = Movement.HARD_PUNCH;
                    }
                    else{
                        // Lleva al rival a la altura normal
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

        // Si no se está a la altura base al momento de ejecutar un movimiento, se pasa a la caída
        // para que el personaje baje al suelo
        if(state != Movement.THROWN_OUT && state != Movement.DEFEAT && !isJumping() && s.getY() != 290
        && !(gameResult == 2 && charac == Playable_Character.TERRY) && !rival.isJumping()){
            movements.get(state).getAnim().reset();
            state = Movement.JUMP_FALL;
            movements.get(state).getAnim().start();;
            s =  movements.get(state).getFrame(x,y, orientation);
        }
        // Si la animación del movimiento ha terminado, pero se manteniene presionado,
        // se coge el último frame de agacharse, o de estarse de pie según corresponda
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

        // Se impide atravesar el suelo
        if(s.getY() > 290){
            y = 290;
            s.setY(290);
        }

        // Gestión de colisiones
        // Si es la animación de victoria de terry, se ajusta la altura
        if (gameResult == 2 && stateChanged && (state == Movement.VICTORY_FIGHT || state == Movement.VICTORY_ROUND) && charac == Playable_Character.TERRY) {
            s.setY(-270);
        }
        // Si está haciendo un ataque que se desplaza y choca con el enemigo, no avanza en x
        else if(collides && isAttacking() && inDisplacement()){
            s.setX(x);
        }
        // Si está siendo lanzado, se queda en el sitio
        else if(state == Movement.THROW){
            s.setX(x);
        }
        // Si colisiona con el límite izquierdo del map y está siendo lanzado en la dirección contraria
        else if(collidesLimitLeft && state == Movement.THROWN_OUT && orientation == -1){
            x = s.getX();
        }
        // Si colisiona con el límite izquierdo del mapa
        else if(collidesLimitLeft){
            // Distancia avanzada
            int d = s.getX()-x;
            // Si se está en knockback y el rival no, se le aplica a él el desplazamiento en x para alejarlo
            if(inKnockback() && !rival.inKnockback()){
                rival.returnKnockback(Math.abs(d));
            }
            else{
                // Si se intenta salir del mapa por la izquierda
                if(d <= 0){
                    s.setX(x);
                    // Si el rival también choca contra el límite y el rival está por encima
                    // y colisionan, desplaza hacia la derecha al personaje
                    if(rivalCollidesLimitLeft && pHurt.getY() > eHurt.getY() && collides){
                        s.setX(x+10);
                        x += 10;
                    }
                    // Si el personaje está por encima del rival y colisionan, le hace retirarse
                    else if(pHurt.getY() < eHurt.getY() && collides){
                        rival.returnKnockback(-Math.abs(d));
                    }
                    // Si colisiona con el rival, ambos estan a la altura referencia y la distancia
                    // es menor que cero (están superpuestos), aleja al personaje
                    else if(collides && dis < 0 && onHeightRef() && rival.onHeightRef()){
                        rival.setX(rival.getX()+1);
                    }
                }
                // Si se aleja del borde
                else{
                    x = s.getX();
                }
            }
        }
        // Si colisiona con el límite derecho del map y está siendo lanzado en la dirección contraria
        else if(collidesLimitRight && state == Movement.THROWN_OUT && orientation == 1){
             x = s.getX();
        }
        // Si colisiona con el límite derecho del mapa
        else if(collidesLimitRight){
            // Distancia avanzada
            int d = s.getX()-x;
            // Si se está en knockback y el rival no, se le aplica a él el desplazamiento en x para alejarlo
            if(inKnockback() && !rival.inKnockback()){
                rival.returnKnockback(Math.abs(d));
            }
            else {
                // Si se intenta salir del mapa por la derecha
                if (d >= 0) {
                    s.setX(x);
                    // Si el rival también choca contra el límite y el rival está por encima
                    // y colisionan, desplaza hacia la izquierda al personaje
                    if (rivalCollidesLimitRight && pHurt.getY() > eHurt.getY() && collides) {
                        s.setX(x - 10);
                        x -= 10;
                    }
                    // Si el personaje está por encima del rival y colisionan, le hace retirarse
                    else if (pHurt.getY() < eHurt.getY() && collides) {
                        rival.returnKnockback(-Math.abs(d));
                    }
                    // Si colisiona con el rival, ambos estan a la altura referencia y la distancia
                    // es menor que cero (están superpuestos), aleja al personaje
                    else if (collides && dis < 0 && onHeightRef() && rival.onHeightRef()) {
                        rival.setX(rival.getX() - 1);
                    }
                }
                // Si se aleja del borde
                else {
                    x = s.getX();
                }
            }
        }
        // Si está siendo lanzado avanza
        else if(state == Movement.THROWN_OUT){
            x = s.getX();
        }
        // Si el rival choca con el limite izquierdo o derecho, se está por debajo del rival y se intenta avanzar hacia el límite,
        // se queda en el sitio
        else if(rivalCollidesLimitLeft && eHurt.getY() < pHurt.getY() && s.getX()-x <= 0 && collides
                || rivalCollidesLimitRight && eHurt.getY() < pHurt.getY() && s.getX()-x >= 0  && collides){
            s.setX(x);
        }
        // Si el rival choca con el limite izquierdo o derecho, se está por encima del rival y se intenta avanzar hacia el límite,
        // se avanza
        else if(rivalCollidesLimitLeft && eHurt.getY() < pHurt.getY() && s.getX()-x > 0 && collides
                || rivalCollidesLimitRight && eHurt.getY() < pHurt.getY() && s.getX()-x < 0  && collides){
            x = s.getX();
        }
        // Si ninguno de los dos esta saltando o ambos están saltando y colisionan, se repelen
        else if((!isJumping() && !rival.isJumping()
                || isJumping() && rival.isJumping()) && collides && dis < 0){
            if(orientation == -1){
                x += dis;
            }
            else{
                x-= dis;
            }
            s.setX(x);
        }
        // Si se está saltando y el rival no, y colisionan, y el rival no está en knockback, se le hace retroceser
        else if(isJumping() && !rival.isJumping() && collides && dis < 0 && !rival.inKnockback()){
            boolean pos = false;
            if(orientation == 1){
                pos = (pHurt.getX() > eHurt.getX());
            }
            else{
                pos = (pHurt.getX() < eHurt.getX());
            }
            int d = Math.abs(s.getX()-x);
            if(d == 0){d = 5;}
            if(pos) {
                rival.returnKnockback(d);
            }
            else{
                rival.returnKnockback(-d);
            }
        }
        // Si se está en knockback
        else if(inKnockback()){
            // Si el rival está en un ataque con desplazamiento y colisionan
            // avanza adicional
            if(rival.isAttacking() && rival.inDisplacement() && collides){
                s.setX(s.getX()+orientation);
                x = s.getX();
            }
            else {
                // Se avanza
                x = s.getX();
            }
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
        // Se avanza en y
        y = s.getY();
        // Si se ha sobrepasado el mapa por la izquierda, se le devuelve hacia dentro
        if(pHurt.getX() < mapLimit.getX()){
            x += 1;
            s.setX(x);
        }
        // Si se ha sobrepasado el mapa por la derecha, se le devuelve hacia dentro
        else if(pHurt.getX()+pHurt.getWidth() > mapLimit.getX()+mapLimit.getWidth()){
            x -= 1;
            s.setX(x);
        }
        return s;
    }

    // Si está o no en altura de referencia
    public boolean onHeightRef(){
        return heightRef == movements.get(state).getHurtbox().getY()+290;
    }

    // Aplicar un daño recibido al personaje
    public void applyDamage(int dmg){
        if(life-dmg < 0){life = 0;}
        else{life -= dmg;}
    }

    // Se le aplica un retroceso al personaje
    public void returnKnockback(int k){
        if(rival.getState() != Movement.THROWN_OUT) {
            this.x = x + orientation * k;
        }
        else{
            this.x = x - orientation * k;
        }
    }

    // Se resetea el personaje
    void reset(int x, int y, int orientation){
        life = 100;
        this.orientation = orientation;
        // Coordenadas actuales del personaje
        this.x = x;
        this.y = y;
        this.state = Movement.STANDING;
        this.gameResult = 0;
    }

    // Se para la animación actual del personaje y se pone en standing
    void stop(){
        movements.get(state).getAnim().reset();
        state = Movement.STANDING;
        movements.get(state).getAnim().start();
        y = 290;
    }

    // Rebalancea los daños de los movimientos en base al multiplicador d,
    // y rebalancea los tiempos de los movimientos en base al multiplicador t
    void rebalance(double d, double t){
        rebalanced = true;
        for(Map.Entry<Movement, movement> e : movements.entrySet()){
            if(isAttack(e.getKey()) && e.getKey() != Movement.THROW) {
                e.getValue().getAnim().updateTimes(t);
                e.getValue().setDamage((int) (e.getValue().getDamage() * d));
                if (e.getValue().getSubMovement() != null) {
                    e.getValue().getSubMovement().setDamage((int) (e.getValue().getSubMovement().getDamage() * d));
                    e.getValue().getSubMovement().getAnim().updateTimes(t);
                }
            }
        }
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

    boolean isAttack(Movement m){
        Movement array[] = {Movement.SOFT_PUNCH, Movement.SOFT_KICK, Movement.HARD_PUNCH,
                Movement.HARD_KICK, Movement.THROW, Movement.ATTACK_POKE,
                Movement.JUMP_PUNCH_DOWN,  Movement.JUMP_ROLL_PUNCH_DOWN, Movement.CHARGED_PUNCH_A,
                Movement.CHARGED_PUNCH_C, Movement.JUMP_KICK, Movement.JUMP_KICK_DOWN, Movement.REVERSE_KICK_B, Movement.REVERSE_KICK_D,
                Movement.SPIN_PUNCH_A, Movement.SPIN_PUNCH_C, Movement.CROUCHING_HARD_KICK, Movement.CROUCHING_SOFT_KICK,
                Movement.CROUCHING_SOFT_PUNCH, Movement.CROUCHING_HARD_PUNCH};
        List<Movement> attacks = Arrays.asList(array);
        return attacks.contains(m);
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
    public void setVictory(int v){
        gameResult = v;
    }

    public void setDefeat(){
        y = 290;
        gameResult = 3;
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
        this.movements.get(state).reset();
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

    public int getGameResult() {
        return gameResult;
    }

    public void setGameResult(int gameResult) {
        this.gameResult = gameResult;
    }

    public int getHeightRef() {
        return heightRef;
    }

    public void setHeightRef(int heightRef) {
        this.heightRef = heightRef;
    }

    public boolean isRebalanced() {
        return rebalanced;
    }

    public void setRebalanced(boolean rebalanced) {
        this.rebalanced = rebalanced;
    }
}
