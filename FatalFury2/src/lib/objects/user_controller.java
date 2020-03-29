package lib.objects;

import lib.Enums.Playable_Character;

// Clase que representa un controlador de interacción entre
// el personaje y el jugador
public class user_controller {
    // Nombre del personaje seleccionado
    private String charac;
    // Personaje seleccionado
    character player;
    // Coordenadas iniciales del personaje
    // (en verdad creo que se podrían quitar)
    private int x = 500, y = 160;
    private animation anim = new animation();

    public user_controller(Playable_Character ch){
        new IsKeyPressed();
        if(ch == Playable_Character.ANDY){
            charac = "andy";
        }
        else if(ch == Playable_Character.MAI){
            charac = "mai";
        }
        else{
            charac = "terry";
            player = new character(Playable_Character.TERRY);
            player.setX(x);
            player.setY(y);
            player.setOrientation(-1);
        }
    }

    // Obtiene el frame del personaje
    // collides indica si colisiona con el enemigo
    public screenObject getAnimation(boolean collides){
        controlKey key = IsKeyPressed.keyPressed();
        controlKey array1[] = {controlKey.LEFT, controlKey.RIGHT, controlKey.DOWN, controlKey.A, controlKey.S, controlKey.D, controlKey.W};
        String array2[] = {"LEFT", "RIGHT", "DOWN", "A","S", "D", "W"};
        for(int i = 0; i < array1.length; ++i){
            if(array1[i] == key){
                return player.getFrame(array2[i], collides);
            }
        }
        return player.getFrame("", collides);
    }

    // Getters y setters
    public character getPlayer() {
        return player;
    }

    public void setPlayer(character player) {
        this.player = player;
    }

    public String getCharac() {
        return charac;
    }

    public void setCharac(String charac) {
        this.charac = charac;
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

    public animation getAnim() {
        return anim;
    }

    public void setAnim(animation anim) {
        this.anim = anim;
    }
}
