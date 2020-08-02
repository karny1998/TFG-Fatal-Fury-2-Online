package lib.objects.networking;

import lib.Enums.GameState;
import lib.Enums.guiItems;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import lib.Enums.GameState;

public class guiListener implements ActionListener {
    private online_mode_gui gui;
    private guiItems type;

    public guiListener(online_mode_gui gui, guiItems type){
        this.gui = gui;
        this.type = type;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String user, pass, pass2, email;
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
                        gui.clearGui();
                        gui.setOnlineState(GameState.LOGIN);
                        break;
                    case FRIEND_SEL_BUTTON:
                        gui.friendInteractionPopUp();
                        break;
                    case QUIT_BUTTON:
                        gui.popUpWithConfirmation("\n\n    Are you sure about\n    quitting the game?");
                        break;
                    case ADD_FRIEND:
                        gui.addFriend();
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
}