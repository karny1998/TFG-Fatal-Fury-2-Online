package lib.objects.networking.gui;

import lib.Enums.GameState;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import lib.objects.Screen;
import lib.objects.networking.connection;
import lib.objects.networking.msgID;
import lib.objects.networking.online_mode;
import lib.utils.sendableObjects.simpleObjects.game;
import lib.utils.sendableObjects.simpleObjects.message;
import lib.utils.sendableObjects.simpleObjects.profile;

public class guiListener implements ActionListener {
    private online_mode_gui gui;
    private online_mode online_controller;
    private guiItems type;
    private connection conToServer;
    private String additionalInformation;

    public guiListener(online_mode_gui gui, guiItems type){
        this.gui = gui;
        this.type = type;
        this.online_controller = gui.getOnline_controller();
        this.conToServer = online_controller.getConToServer();
    }
    public guiListener(online_mode_gui gui, guiItems type, String additionalInformation){
        this.gui = gui;
        this.type = type;
        this.online_controller = gui.getOnline_controller();
        this.conToServer = online_controller.getConToServer();
        this.additionalInformation = additionalInformation;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String user, pass, pass2, email, msg, friend, res;
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
                pass = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD)).getText().toUpperCase();
                user = ((JTextField)gui.getComponentsOnScreen().get(guiItems.USERNAME)).getText().toUpperCase();
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
                        if(pass.length() == 0 || user.length() == 0){
                            gui.popUp("The pass or user is empty.");
                        }
                        else {
                            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request, "LOGIN:" + user + ":" + pass, 200);
                            if (res.equals("LOGGED")) {
                                gui.clearGui();
                                gui.setUserLogged(user);
                                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                                System.out.println("Te has logeado");
                            } else {
                                if (res == null || res.equals("NONE") || res.equals("")) {
                                    gui.popUp("No se conseguido contactar con el servidor");
                                } else {
                                    gui.popUp(res.split(":")[1]);
                                }
                            }
                        }
                        break;
                    case POP_UP_BUTTON:
                        /*gui.clearGui();
                        gui.login(false,user,pass);*/
                        gui.closePopUp();
                        break;
                    default:
                        System.out.println("SE HA PRETADO UN BOTON");
                        break;
                }
                break;
            case REGISTER:
                pass = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD)).getText().toUpperCase();
                pass2 = ((JTextField)gui.getComponentsOnScreen().get(guiItems.PASSWORD_2)).getText().toUpperCase();
                user = ((JTextField)gui.getComponentsOnScreen().get(guiItems.USERNAME)).getText().toUpperCase();
                email = ((JTextField)gui.getComponentsOnScreen().get(guiItems.EMAIL)).getText().toUpperCase();
                switch (type){
                    case SHOW:
                        gui.clearGui();
                        gui.register(true,user,pass,pass2,email);
                        break;
                    case HIDE:
                        gui.clearGui();
                        gui.register(false,user,pass,pass2,email);
                        break;
                    case BACK:
                        gui.clearGui();
                        gui.setOnlineState(GameState.LOGIN_REGISTER);
                        break;
                    case REGISTER_BUTTON:
                        if(pass.length() == 0 || user.length() == 0 || pass2.length() == 0 || email.length() == 0){
                            gui.popUp("No field can be empty.");
                        }
                        else if(!pass2.equals(pass)){
                            gui.popUp("Both passwords must be the same.");
                        }
                        else{
                            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"REGISTER:"+user+":"+email+":"+pass, 200);
                            if (res.equals("REGISTERED")) {
                                gui.clearGui();
                                gui.setOnlineState(GameState.LOGIN_REGISTER);
                                System.out.println("Te has registrado");
                            } else {
                                if (res == null || res.equals("NONE") || res.equals("")) {
                                    gui.popUp("No se conseguido contactar con el servidor");
                                } else {
                                    gui.popUp(res.split(":")[1]);
                                }
                            }
                        }
                        break;
                    case POP_UP_BUTTON:
                        /*gui.clearGui();
                        gui.register(false,user,pass,pass2,email);*/
                        gui.closePopUp();
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
                            if(gui.getComponentsOnScreen().containsKey(guiItems.CHAT)) {
                                gui.closeChat();
                            }
                            gui.friendInteractionPopUp();
                        }
                        break;
                    case QUIT_BUTTON:
                        gui.popUpWithConfirmation("\n\n    Are you sure you\n   want to quit?", guiItems.QUIT_YES, guiItems.QUIT_NO);
                        break;
                    case ADD_FRIEND:
                        gui.closeChat();
                        gui.closeFriendInteractionPopUp();
                        gui.addFriend();
                        break;
                    case CONFIRM_ADD_BUTTON:
                        addFriendGestion();
                        break;
                    case CALCEL_ADD_BUTTON:
                        gui.clearGui();
                        gui.principalGUI();
                        break;
                    case MESSAGE_FRIEND:
                        if(!gui.getComponentsOnScreen().containsKey(guiItems.POP_UP_TABLE)) {
                            gui.closeFriendInteractionPopUp();
                            gui.setFriendMessages(generateRandomMsgs());
                            gui.chat(gui.getFriends().get(gui.getFriendSelected()));
                        }
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
                        break;
                    case QUIT_YES:
                        conToServer.sendString(msgID.toServer.tramits,"DISCONNECT");
                        conToServer.close();
                        System.exit(0);
                        break;
                    case QUIT_NO:
                        gui.clearGui();
                        gui.principalGUI();
                        break;
                    case LOG_OUT_YES:
                        conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"LOG OFF",200);
                        gui.setOnlineState(GameState.LOGIN);
                        gui.clearGui();
                        break;
                    case LOG_OUT_NO:
                        gui.clearGui();
                        break;
                    case CLOSE_CHAT:
                        gui.closeChat();
                        break;
                    case PROFILE_BUTTON:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PROFILE_GUI);
                        gui.setProfileToShow(randomProfile());
                        break;
                    case PROFILE_FRIEND:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PROFILE_GUI);
                        gui.setProfileToShow(randomProfile());
                        break;
                    case NORMAL_BUTTON:
                        gui.setOnlineState(GameState.CHARACTER_SELECTION);
                        gui.clearGui();
                        break;
                    case ACCEPT_FRIEND:
                        answerFriendRequest(true);
                        break;
                    case REJECT_FRIEND:
                        answerFriendRequest(false);
                        break;
                    default:
                        System.out.println("SE HA PRETADO UN BOTON");
                        break;
                }
                break;
            case PROFILE_GUI:
                switch (type) {
                    case BACK:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PRINCIPAL_GUI);
                        break;
                    case FRIEND_SEL_BUTTON:
                        if(!gui.getComponentsOnScreen().containsKey(guiItems.POP_UP_TABLE)) {
                            if(gui.getComponentsOnScreen().containsKey(guiItems.CHAT)) {
                                gui.closeChat();
                                gui.getProfile().swapHistorial();
                            }
                            gui.friendInteractionPopUp();
                        }
                        break;
                    case ADD_FRIEND:
                        if(gui.getComponentsOnScreen().containsKey(guiItems.CHAT)) {
                            gui.closeChat();
                            gui.getProfile().swapHistorial();
                        }
                        gui.closeFriendInteractionPopUp();
                        gui.addFriend();
                        break;
                    case CONFIRM_ADD_BUTTON:
                        addFriendGestion();
                        break;
                    case CALCEL_ADD_BUTTON:
                        gui.clearGui();
                        break;
                    case MESSAGE_FRIEND:
                        if(!gui.getComponentsOnScreen().containsKey(guiItems.POP_UP_TABLE)) {
                            gui.closeFriendInteractionPopUp();
                            gui.setFriendMessages(generateRandomMsgs());
                            gui.getProfile().swapHistorial();
                            gui.chat(gui.getFriends().get(gui.getFriendSelected()));
                            break;
                        }
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
                        break;
                    case CLOSE_CHAT:
                        gui.clearGui();
                        gui.getProfile().swapHistorial();
                        break;
                    case PROFILE_FRIEND:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PROFILE_GUI);
                        gui.setProfileToShow(randomProfile());
                        break;
                    case ACCEPT_FRIEND:
                        answerFriendRequest(true);
                        break;
                    case REJECT_FRIEND:
                        answerFriendRequest(false);
                        break;
                    default:
                        System.out.println("SE HA PRETADO UN BOTON");
                        break;
                }
                break;
            case CHARACTER_SELECTION:
                switch (type){
                    case BACK:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PRINCIPAL_GUI);
                        break;
                    default:
                        System.out.println("SE HA PRETADO UN BOTON");
                        break;
                }
            default:
                System.out.println("SE HA PRETADO UN BOTON");
                break;
        }
    }

    private void addFriendGestion(){
        String friend = ((JTextField)gui.getComponentsOnScreen().get(guiItems.INTRODUCE_NAME)).getText().toUpperCase();
        if(friend.length() == 0){
            gui.popUp("Username cant be empty.");
        }
        else {
            String res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"SEND FRIEND REQUEST:"+gui.getUserLogged()+":"+friend, 200);
            if (res.equals("FRIEND REQUEST SENT")) {
                gui.clearGui();
                System.out.println("Has enviado una solicitud de amistad");
            } else {
                if (res == null || res.equals("NONE") || res.equals("")) {
                    gui.popUp("No se conseguido contactar con el servidor");
                } else {
                    gui.popUp(res.split(":")[1]);
                }
            }
        }
    }

    private void answerFriendRequest(boolean accepted){
        String res = "";
        if(accepted){
            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"ACCEPT FRIEND REQUEST:"+gui.getUserLogged()+":"+additionalInformation, 200);
        }
        else{
            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"REJECT FRIEND REQUEST:"+gui.getUserLogged()+":"+additionalInformation, 200);
        }
        gui.closePopUpWithConfirmation(guiItems.ACCEPT_FRIEND, guiItems.REJECT_FRIEND);
        if (res.equals("FRIEND REQUEST ACCEPTED") || res.equals("FRIEND REQUEST REJECTED")) {
            System.out.println("Has respondido a una solicitud de amistad");
        } else {
            if (res == null || res.equals("NONE") || res.equals("")) {
                gui.popUp("No se conseguido contactar con el servidor");
            } else {
                gui.popUp(res.split(":")[1]);
            }
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

    public profile randomProfile(){
        return new profile("caca",1000,10,1,10,1,randomHistorial());
    }

    public ArrayList<game> randomHistorial(){
        ArrayList<game>  games = new ArrayList<>();
        for(int i = 0; i < 10; ++i){
            games.add(new game(i,"0123456789", "caca2", LocalDateTime.now(), (int) (Math.random()*3), true,0,0,"",""));
        }
        return games;
    }
}