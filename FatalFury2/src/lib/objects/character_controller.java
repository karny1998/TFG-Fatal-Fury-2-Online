package lib.objects;

import lib.Enums.Playable_Character;

import java.util.Random;

// Representa una clase para controlar a un personaje, independientemente de
// si es controlado por le usuario o la IA
public abstract  class character_controller {
    // Nombre del personaje seleccionado
    protected String charac;
    // Personaje seleccionado
    protected character player;
    // Coordenadas del personaje
    protected int x = 0, y = 0;
    // Generador de randoms
    protected Random rand = new Random();

    public character_controller(Playable_Character ch, int x, int y, int orientation){
        this.x = x; this.y = y;
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
            player.setOrientation(orientation);
        }
    }

    abstract void reset();

    void reset(Playable_Character ch, int x, int y, int orientation) {
        this.x = x; this.y = y;
        player = new character(ch);
        player.setX(x);
        player.setY(y);
        player.setOrientation(orientation);
    }

    abstract screenObject getAnimation(boolean collides);

    // GETTERS Y SETTERS
    public String getCharac() {
        return charac;
    }

    public void setCharac(String charac) {
        this.charac = charac;
    }

    public character getPlayer() {
        return player;
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

}
