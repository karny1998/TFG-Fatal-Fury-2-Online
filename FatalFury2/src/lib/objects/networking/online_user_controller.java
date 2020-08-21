package lib.objects.networking;

import lib.Enums.Playable_Character;
import lib.input.controlListener;
import lib.objects.hitBox;
import lib.objects.screenObject;
import lib.objects.user_controller;
import lib.utils.Pair;

/**
 * The type Online user controller.
 */
public class online_user_controller extends user_controller {
    /**
     * The Con.
     */
    private connection con;
    /**
     * The Is local.
     */
    private boolean isServer = false, /**
     * The Is local.
     */
    isLocal = true;
    /**
     * The Menssage identifier.
     */
    private int menssageIdentifier = 1;

    /**
     * The Time reference.
     */
    private long timeReference = System.currentTimeMillis();

    /**
     * The Connection lost.
     */
    private boolean connectionLost = false;

    /**
     * The Online mov.
     */
    private String onlineMov = "";

    /**
     * Instantiates a new Online user controller.
     *
     * @param ch       the ch
     * @param pN       the p n
     * @param con      the con
     * @param isServer the is local
     * @param isLocal  the is local
     */
    public online_user_controller(Playable_Character ch, int pN, connection con, boolean isServer, boolean isLocal){
        super(ch, pN);
        this.con = con;
        this.isServer = isServer;
        this.isLocal = isLocal;
        if(!isServer){
            this.input_control.stop();
        }
    }

    /**
     * Inputs gestion.
     */
    @Override
    protected void inputsGestion(){
        mov = "";
        if(!standBy && isServer) {
            if(isLocal) {
                mov = controlListener.getMove(1);
            }
            else{
                String newMov = con.receiveString(menssageIdentifier);
                if (newMov != null && !newMov.equals("NONE")) {
                    mov = newMov;
                    timeReference = System.currentTimeMillis();
                }
            }
            if(System.currentTimeMillis() - timeReference > 5000){
                connectionLost = true;
            }
            inputsGestionAux();
        }
    }

    /**
     * Get animation screen object.
     *
     * @param pHurt the p hurt
     * @param eHurt the e hurt
     * @return the screen object
     */
    @Override
    public screenObject getAnimation(hitBox pHurt, hitBox eHurt){
        this.x = this.player.getX();
        this.y = this.player.getY();
        String movToExecute = "";
        if(!standBy) {
            if(isServer){
                movToExecute = mov;
            }
            else{
                if(System.currentTimeMillis() - timeReference > 5000){
                    connectionLost = true;
                }
                con.sendString(menssageIdentifier, controlListener.getMove(1));
                movToExecute = onlineMov;
                if(player.getState() == player.getCombos().get(onlineMov)) {
                    onlineMov = "";
                }
            }

            if(rival == null) {
                return player.getFrame(movToExecute, pHurt, eHurt, false);
            } else {
                return player.getFrame(movToExecute, pHurt, eHurt, rival.isAttacking());
            }
        }
        else{
            return player.getFrame("", pHurt, eHurt, false);
        }
    }

    /**
     * Gets con.
     *
     * @return the con
     */
    public connection getCon() {
        return con;
    }

    /**
     * Sets con.
     *
     * @param con the con
     */
    public void setCon(connection con) {
        this.con = con;
    }

    /**
     * Is local boolean.
     *
     * @return the boolean
     */
    public boolean isLocal() {
        return isLocal;
    }

    /**
     * Sets local.
     *
     * @param local the local
     */
    public void setLocal(boolean local) {
        isLocal = local;
    }

    /**
     * Gets menssage identifier.
     *
     * @return the menssage identifier
     */
    public int getMenssageIdentifier() {
        return menssageIdentifier;
    }

    /**
     * Sets menssage identifier.
     *
     * @param menssageIdentifier the menssage identifier
     */
    public void setMenssageIdentifier(int menssageIdentifier) {
        this.menssageIdentifier = menssageIdentifier;
    }

    /**
     * Gets time reference.
     *
     * @return the time reference
     */
    @Override
    public long getTimeReference() {
        return timeReference;
    }

    /**
     * Sets time reference.
     *
     * @param timeReference the time reference
     */
    @Override
    public void setTimeReference(long timeReference) {
        this.timeReference = timeReference;
    }

    /**
     * Is connection lost boolean.
     *
     * @return the boolean
     */
    public boolean isConnectionLost() {
        return connectionLost;
    }

    /**
     * Sets connection lost.
     *
     * @param connectionLost the connection lost
     */
    public void setConnectionLost(boolean connectionLost) {
        this.connectionLost = connectionLost;
    }

    /**
     * Start stand by.
     */
    @Override
    public void startStandBy(){
        this.timeReference = System.currentTimeMillis();
        this.standBy = true;
    }

    /**
     * End stand by.
     */
    @Override
    public void endStandBy(){
        this.timeReference = System.currentTimeMillis();
        this.standBy = false;
    }

    /**
     * Is server boolean.
     *
     * @return the boolean
     */
    public boolean isServer() {
        return isServer;
    }

    /**
     * Sets server.
     *
     * @param server the server
     */
    public void setServer(boolean server) {
        isServer = server;
    }

    /**
     * Gets online mov.
     *
     * @return the online mov
     */
    public String getOnlineMov() {
        return onlineMov;
    }

    /**
     * Sets online mov.
     *
     * @param onlineMov the online mov
     */
    public void setOnlineMov(String onlineMov) {
        this.timeReference = System.currentTimeMillis();
        this.onlineMov = onlineMov;
    }

    /**
     * Update time reference.
     */
    public void updateTimeReference(){
        this.timeReference = System.currentTimeMillis();
    }
}
