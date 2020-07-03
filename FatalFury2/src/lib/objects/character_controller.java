package lib.objects;

import lib.Enums.Playable_Character;

import java.util.Random;

/**
 * The type Character controller.
 */
// Representa una clase para controlar a un personaje, independientemente de
// si es controlado por le usuario o la IA
public abstract  class character_controller {
    /**
     * The Charac.
     */
// Nombre del personaje seleccionado
    protected String charac;
    /**
     * The Player.
     */
// Personaje seleccionado
    protected character player;
    /**
     * The X.
     */
// Coordenadas del personaje
    protected int x = 0, /**
     * The Y.
     */
    y = 0;
    /**
     * The Rand.
     */
// Generador de randoms
    protected Random rand = new Random();
    /**
     * The Stand by.
     */
// Para las esperas
    protected boolean standBy = true;
    /**
     * The Player num.
     */
// Número de jugador en JVJ
    protected int playerNum = 1;
    /**
     * The Rival.
     */
// Rival al que se enfrenta
    protected character rival = null;

    /**
     * Instantiates a new Character controller.
     *
     * @param ch          the ch
     * @param pN          the p n
     * @param x           the x
     * @param y           the y
     * @param orientation the orientation
     */
// Constructor que pide identificador del personaje, número de jugador, y coordenadas y orientación iniciales
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
            player = new character(Playable_Character.MAI, pN);
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

    /**
     * Reset.
     */
// Resetea el personaje
    abstract void reset();

    /**
     * Stop ia.
     */
// Para la inteligencia artificial
    public void stopIA(){};

    /**
     * Reset.
     *
     * @param ch          the ch
     * @param x           the x
     * @param y           the y
     * @param orientation the orientation
     */
// Resetea el personaje con información concreta
    void reset(Playable_Character ch, int x, int y, int orientation) {
        this.x = x; this.y = y;
        player.reset(x,y, orientation);
    }

    /**
     * Gets animation.
     *
     * @param pHurt the p hurt
     * @param eHurt the e hurt
     * @return animation
     */
// Devuelve el frame del personaje teniendo en cuenta la colision de las hurtboxs
    public abstract screenObject getAnimation(hitBox pHurt, hitBox eHurt);

    /**
     * Stop launched threads.
     */
    public abstract void stop();

    /**
     * Gets charac.
     *
     * @return the charac
     */
// GETTERS Y SETTERS
    public String getCharac() {
        return charac;
    }

    /**
     * Sets charac.
     *
     * @param charac the charac
     */
    public void setCharac(String charac) {
        this.charac = charac;
    }

    /**
     * Gets player.
     *
     * @return the player
     */
    public character getPlayer() {
        return player;
    }

    /**
     * Sets player.
     *
     * @param player the player
     */
    public void setPlayer(character player) {
        this.player = player;
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x the x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y the y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets rand.
     *
     * @return the rand
     */
    public Random getRand() {
        return rand;
    }

    /**
     * Sets rand.
     *
     * @param rand the rand
     */
    public void setRand(Random rand) {
        this.rand = rand;
    }

    /**
     * Is stand by boolean.
     *
     * @return the boolean
     */
    public boolean isStandBy() {
        return standBy;
    }

    /**
     * Sets stand by.
     *
     * @param standBy the stand by
     */
    public void setStandBy(boolean standBy) {
        this.standBy = standBy;
    }

    /**
     * Start stand by.
     */
    public void startStandBy(){
        this.standBy = true;
    }

    /**
     * End stand by.
     */
    public void endStandBy(){
        this.standBy = false;
    }

    /**
     * Gets rival.
     *
     * @return the rival
     */
    public character getRival() {
        return rival;
    }

    /**
     * Sets rival.
     *
     * @param rival the rival
     */
    public void setRival(character rival) {
        this.player.setRival(rival);
        this.rival = rival;
    }

    /**
     * Gets player num.
     *
     * @return the player num
     */
    public int getPlayerNum() {
        return playerNum;
    }

    /**
     * Sets player num.
     *
     * @param playerNum the player num
     */
    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
        if(playerNum == 2){
            player.setX(750);
            player.setY(290);
            player.setOrientation(1);
        }
    }

    /**
     * Gets ia.
     *
     * @return the ia
     */
    public ia_controller getIa() {
        return new ia_controller();
    }

}
