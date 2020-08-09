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

public class notifications_gui {
    private online_mode_gui gui;
    private List<String> friendRequest = new ArrayList<>();
    private List<String> pendingMessages = new ArrayList<>();
    private connection conToServer;
    private Timer timerAux;

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

    public void clearFriendRequestNotification(){
        if(friendRequest.size() == 0) {
            guiItems items[] = {guiItems.FRIEND_REQUEST_NOTIFICATION};
            gui.deleteComponents(items);
            showNotifications();
        }
    }

    public void addFriendRequest(String friend){
        friendRequest.add(friend);
        showNotifications();
    }

    public online_mode_gui getGui() {
        return gui;
    }

    public void setGui(online_mode_gui gui) {
        this.gui = gui;
    }

    public List<String> getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(List<String> friendRequest) {
        this.friendRequest = friendRequest;
    }

    public List<String> getPendingMessages() {
        return pendingMessages;
    }

    public void setPendingMessages(List<String> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    public connection getConToServer() {
        return conToServer;
    }

    public void setConToServer(connection conToServer) {
        this.conToServer = conToServer;
    }

    public Timer getTimerAux() {
        return timerAux;
    }

    public void setTimerAux(Timer timerAux) {
        this.timerAux = timerAux;
    }
}
