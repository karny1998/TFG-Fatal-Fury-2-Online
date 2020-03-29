package lib.objects;

import lib.Enums.Playable_Character;

import java.util.Random;

// Clase que representa el control de un personaje por IA
public class enemy_controller {
    // Nombre del personaje seleccionado
    private String charac;
    // Personaje seleccionado
    character player;
    // Coordenadas del personaje
    private int x = 750, y = 160;
    // Generador de randoms
    private Random rand = new Random();
    // Gestión de que no se salga del escenario (no rula bien)
    private int posAprox = 0;

    public enemy_controller(Playable_Character ch){
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
            player.setOrientation(1);
        }
    }

    // Obtener el frame del personaje
    // collides indica si colisiona con el jugador o no
    // En esta función se llamaría a la IA
    // Por ahora se juega aleatoriamente
    public screenObject getAnimation(boolean collides){
        controlKey array1[] = {controlKey.LEFT, controlKey.RIGHT, controlKey.DOWN, controlKey.A, controlKey.S, controlKey.D, controlKey.W};
        controlKey key =  array1[rand.nextInt(array1.length)];
        if (key == controlKey.LEFT){++posAprox;}
        else if (key == controlKey.RIGHT){--posAprox;}
        if (posAprox > 10){
            key = controlKey.RIGHT;
            --posAprox;
        }
        else if (posAprox < 0){
            key = controlKey.LEFT;
            ++posAprox;
        }
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

    public String getCharac() {
        return charac;
    }

    public void setCharac(String charac) {
        this.charac = charac;
    }

    public void setPlayer(character player) {
        this.player = player;
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

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public int getPosAprox() {
        return posAprox;
    }

    public void setPosAprox(int posAprox) {
        this.posAprox = posAprox;
    }
}
