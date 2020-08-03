package lib.objects.networking;

import lib.Enums.GameState;
import lib.Enums.guiItems;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import lib.Enums.GameState;
import lib.objects.Screen;
import lib.utils.sendableObjects.simpleObjects.message;

public class guiListener implements ActionListener {
    private online_mode_gui gui;
    private guiItems type;

    public guiListener(online_mode_gui gui, guiItems type){
        this.gui = gui;
        this.type = type;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String user, pass, pass2, email, msg;
        GameState onlineState = gui.getOnlineState();
        switch (onlineState){
            case LOGIN_REGISTER:
                switch (type){
                    case LOGIN_BUTTON:
                        gui.clearGui();
                        gui.setOnlineState(GameState.LOGIN);
                        break;
                    case REGISTER_BUTTON:
                        gui.clearGui();
                        gui.setOnlineState(GameState.REGISTER);
                        break;
                    case BACK:
                        gui.clearGui();
                        ((Screen)gui.getPrincipal().getGame()).getGame().setState(GameState.NAVIGATION);
                        gui.getPrincipal().gameOn();
                    default:
                        break;
                }
                break;
            case LOGIN:
                pass = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD)).getText();
                user = ((JTextField)gui.getComponentsOnScreen().get(guiItems.USERNAME)).getText();
                switch (type){
                    case SHOW:
                        gui.clearGui();
                        gui.login(true,user,pass);
                        break;
                    case HIDE:
                        gui.clearGui();
                        gui.login(false,user,pass);
                        break;
                    case BACK:
                        gui.clearGui();
                        gui.setOnlineState(GameState.LOGIN_REGISTER);
                        break;
                    case LOGIN_BUTTON:
                        //popUp("TU PUTA MADRE, GRACIAS");
                        gui.clearGui();
                        gui.setOnlineState(GameState.PRINCIPAL_GUI);
                        break;
                    case POP_UP_BUTTON:
                        gui.clearGui();
                        gui.login(false,user,pass);
                    default:
                        System.out.println("SE HA PRETADO UN BOTON");
                        break;
                }
                break;
            case REGISTER:
                switch (type){
                    case SHOW:
                        pass = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD)).getText();
                        pass2 = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD_2)).getText();
                        user = ((JTextField)gui.getComponentsOnScreen().get(guiItems.USERNAME)).getText();
                        email = ((JTextField)gui.getComponentsOnScreen().get(guiItems.EMAIL)).getText();
                        gui.clearGui();
                        gui.register(true,user,pass,pass2,email);
                        break;
                    case HIDE:
                        pass = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD)).getText();
                        pass2 = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD_2)).getText();
                        user = ((JTextField)gui.getComponentsOnScreen().get(guiItems.USERNAME)).getText();
                        email = ((JTextField)gui.getComponentsOnScreen().get(guiItems.EMAIL)).getText();
                        gui.clearGui();
                        gui.register(false,user,pass,pass2,email);
                        break;
                    case BACK:
                        gui.clearGui();
                        gui.setOnlineState(GameState.LOGIN_REGISTER);
                        break;
                    default:
                        System.out.println("SE HA PRETADO UN BOTON");
                        break;
                }
                break;
            case PRINCIPAL_GUI:
                switch (type) {
                    case BACK:
                        gui.popUpWithConfirmation("\n\n    Are you sure you\n    want to log out?", guiItems.LOG_OUT_YES, guiItems.LOG_OUT_NO);
                        break;
                    case FRIEND_SEL_BUTTON:
                        if(!gui.getComponentsOnScreen().containsKey(guiItems.POP_UP_TABLE)) {
                            gui.closeChat();
                            gui.friendInteractionPopUp();
                        }
                        break;
                    case QUIT_BUTTON:
                        gui.popUpWithConfirmation("\n\n    Are you sure you\n   want to quit?", guiItems.QUIT_YES, guiItems.QUIT_NO);
                        break;
                    case ADD_FRIEND:
                        gui.closeChat();
                        gui.addFriend();
                        break;
                    case CALCEL_ADD_BUTTON:
                        gui.clearGui();
                        gui.principalGUI();
                        break;
                    case MESSAGE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        gui.setFriendMessages(generateRandomMsgs());
                        gui.chat(gui.getFriends().get(gui.getFriendSelected()));
                        break;
                    case PROFILE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        break;
                    case DELETE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        break;
                    case INVITE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        break;
                    case SEND_MESSAGE:
                        msg = ((JTextField)gui.getComponentsOnScreen().get(guiItems.MESSAGE_WRITER)).getText();
                        if(msg != null && !msg.trim().equals("")){
                            message aux = gui.getFriendMessages().get(gui.getFriendMessages().size()-1);
                            gui.getFriendMessages().add(new message(aux.getId()+1,"yo", gui.getFriends().get(gui.getFriendSelected()),msg));
                            gui.closeChat();
                            gui.chat(gui.getFriends().get(gui.getFriendSelected()));
                        }
                    case QUIT_YES:
                        System.exit(0);
                        break;
                    case QUIT_NO:
                        gui.clearGui();
                        gui.principalGUI();
                        break;
                    case LOG_OUT_YES:
                        gui.setOnlineState(GameState.LOGIN);
                        gui.clearGui();
                        break;
                    case LOG_OUT_NO:
                        gui.clearGui();
                        break;
                    default:
                        System.out.println("SE HA PRETADO UN BOTON");
                        break;
                }
                break;
            default:
                System.out.println("SE HA PRETADO UN BOTON");
                break;
        }
    }

    public List<message> generateRandomMsgs(){
        List<message> aux = new ArrayList<>();

        for(int i = 0; i < 50; ++i){
            aux.add(new message(i,gui.getFriends().get(gui.getFriendSelected()), "JOSH", "HOLAHOLAHOLAHOLAHOLAHOLAHOLAHOLAHOLA"));
            aux.add(new message(i, "JOSH",gui.getFriends().get(gui.getFriendSelected()),"HOLA"));
        }

        return aux;
    }
}