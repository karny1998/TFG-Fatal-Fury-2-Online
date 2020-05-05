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
    // Para las esperas
    boolean standBy = true;
    // Número de jugador en JVJ
    protected int playerNum = 1;

    protected character rival = null;

    public character_controller(Playable_Character ch, int pN, int x, int y, int orientation){
        playerNum = pN;
        this.x = x; this.y = y;
        if(ch == Playable_Character.ANDY){
            charac = "andy";
            player = new character(Playable_Character.ANDY, pN);
            player.setX(x);
            player.setY(y);
            player.setOrientation(orientation);
        }
        else if(ch == Playable_Character.MAI){
            charac = "mai";
            player = new character(Playable_Character.TERRY, pN);
            player.setX(x);
            player.setY(y);
            player.setOrientation(orientation);
        }
        else{
            charac = "terry";
            player = new character(Playable_Character.TERRY, pN);
            player.setX(x);
            player.setY(y);
            player.setOrientation(orientation);
        }
    }

    abstract void reset();

    public void stopIA(){};

    void reset(Playable_Character ch, int x, int y, int orientation) {
        this.x = x; this.y = y;
        player.reset(x,y, orientation);
    }

    abstract screenObject getAnimation(hitBox pHurt, hitBox eHurt);

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

    public boolean isStandBy() {
        return standBy;
    }

    public void setStandBy(boolean standBy) {
        this.standBy = standBy;
    }

    public void startStandBy(){
        this.standBy = true;
    }

    public void endStandBy(){
        this.standBy = false;
    }

    public character getRival() {
        return rival;
    }

    public void setRival(character rival) {
        this.player.setRival(rival);
        this.rival = rival;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
        if(playerNum == 2){
            player.setX(750);
            player.setY(290);
            player.setOrientation(1);
        }
    }
    public ia_controller getIa() {
        return new ia_controller();
    }

}
