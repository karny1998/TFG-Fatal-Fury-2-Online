package lib.objects.networking.gui.gui_components;

import lib.Enums.GameState;
import lib.objects.networking.connection;
import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.online_mode_gui;
import lib.objects.networking.msgID;
import lib.utils.sendableObjects.sendableObjectsList;
import lib.utils.sendableObjects.simpleObjects.string;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Notifications gui.
 */
public class notifications_gui {
    /**
     * The Gui.
     */
    private online_mode_gui gui;
    /**
     * The Friend request.
     */
    private List<String> friendRequest = new ArrayList<>();
    /**
     * The Pending messages.
     */
    private List<String> pendingMessages = new ArrayList<>();
    /**
     * The Con to server.
     */
    private connection conToServer;
    /**
     * The Timer aux.
     */
    private Timer timerAux;

    /**
     * Instantiates a new Notifications gui.
     *
     * @param gui the gui
     */
    public notifications_gui(online_mode_gui gui){
        this.gui = gui;
        this.conToServer = gui.getOnline_controller().getConToServer();
        sendableObjectsList request = (sendableObjectsList) conToServer.sendStringWaitingAnswerObject(msgID.toServer.request, "FRIENDS REQUESTS",0);
        for(int i = 0; i < request.getMsgs().size(); ++i){
            friendRequest.add(((string)request.getMsgs().get(i)).getContent());
        }
        /*request = (sendableObjectsList) conToServer.sendStringWaitingAnswerObject(msgID.toServer.request, "PENDING MESSAGES",0);
        for(int i = 0; i < request.getMsgs().size(); ++i){
            friendRequest.add(((string)request.getMsgs().get(i)).getContent());
        }*/
    }

    /**
     * Show notifications.
     */
    public void showNotifications(){
        if(gui.getOnlineState() == GameState.PRINCIPAL_GUI || gui.getOnlineState() == GameState.PROFILE_GUI){
            if(friendRequest.size() > 0){
                JButton button = gui.generateSimpleButton("Friend requests", guiItems.FRIEND_REQUEST_NOTIFICATION,gui.getF2(), Color.ORANGE,gui.getGrey4(),780,15,250,40,false);
                timerAux = new Timer(500, new ActionListener() {
                    private boolean on = false;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(on) {
                            button.setBorder(new LineBorder(Color.YELLOW));
                            on = false;
                        }
                        else{
                            button.setBorder(new LineBorder(gui.getGrey4()));
                            on = true;
                        }
                    }
                });
                timerAux.start();
                guiItems items[] = {guiItems.FRIEND_REQUEST_NOTIFICATION};
                Component comps[] = {button};
                gui.addComponents(items,comps);
                gui.getItemsOnScreen().remove(guiItems.FRIEND_REQUEST_NOTIFICATION);
                gui.getItemsOnScreen().add(0,guiItems.FRIEND_REQUEST_NOTIFICATION);
            }
            else{
                if(gui.getComponentsOnScreen().containsKey(guiItems.FRIEND_REQUEST_NOTIFICATION)) {
                    clearFriendRequestNotification();
                }
            }
        }
        else{
            if(gui.getComponentsOnScreen().containsKey(guiItems.FRIEND_REQUEST_NOTIFICATION)) {
                clearFriendRequestNotification();
            }
        }
        gui.reloadGUI();
    }

    /**
     * Clear friend request notification.
     */
    public void clearFriendRequestNotification(){
        if(friendRequest.size() == 0) {
            guiItems items[] = {guiItems.FRIEND_REQUEST_NOTIFICATION};
            gui.deleteComponents(items);
            showNotifications();
        }
    }

    /**
     * Add friend request.
     *
     * @param friend the friend
     */
    public void addFriendRequest(String friend){
        friendRequest.add(friend);
        showNotifications();
    }

    /**
     * Gets gui.
     *
     * @return the gui
     */
    public online_mode_gui getGui() {
        return gui;
    }

    /**
     * Sets gui.
     *
     * @param gui the gui
     */
    public void setGui(online_mode_gui gui) {
        this.gui = gui;
    }

    /**
     * Gets friend request.
     *
     * @return the friend request
     */
    public List<String> getFriendRequest() {
        return friendRequest;
    }

    /**
     * Sets friend request.
     *
     * @param friendRequest the friend request
     */
    public void setFriendRequest(List<String> friendRequest) {
        this.friendRequest = friendRequest;
    }

    /**
     * Gets pending messages.
     *
     * @return the pending messages
     */
    public List<String> getPendingMessages() {
        return pendingMessages;
    }

    /**
     * Sets pending messages.
     *
     * @param pendingMessages the pending messages
     */
    public void setPendingMessages(List<String> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    /**
     * Gets con to server.
     *
     * @return the con to server
     */
    public connection getConToServer() {
        return conToServer;
    }

    /**
     * Sets con to server.
     *
     * @param conToServer the con to server
     */
    public void setConToServer(connection conToServer) {
        this.conToServer = conToServer;
    }

    /**
     * Gets timer aux.
     *
     * @return the timer aux
     */
    public Timer getTimerAux() {
        return timerAux;
    }

    /**
     * Sets timer aux.
     *
     * @param timerAux the timer aux
     */
    public void setTimerAux(Timer timerAux) {
        this.timerAux = timerAux;
    }
}
