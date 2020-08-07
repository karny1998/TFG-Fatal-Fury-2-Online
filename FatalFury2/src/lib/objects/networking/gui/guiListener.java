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
        profile prof;
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
                            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request, "LOGIN:" + user + ":" + pass, 0);
                            if (res.equals("LOGGED")) {
                                gui.clearGui();
                                gui.setUserLogged(user);
                                gui.setOnlineState(GameState.PRINCIPAL_GUI);
                                gui.loadFriends();
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
                            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"REGISTER:"+user+":"+email+":"+pass, 0);
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
                    case CANCEL_ADD_BUTTON:
                        gui.closeAddFriend();
                        //gui.principalGUI();
                        break;
                    case MESSAGE_FRIEND:
                        if(!gui.getComponentsOnScreen().containsKey(guiItems.POP_UP_TABLE)) {
                            gui.closeFriendInteractionPopUp();
                            gui.chat(gui.getFriends().get(gui.getFriendSelected()));
                        }
                        break;
                    case DELETE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        gui.popUpWithConfirmation(gui.getFriends().get(gui.getFriendSelected())
                                +" will be removed\nfrom friends.\nAre you sure?", guiItems.CONFIRM_DELETE, guiItems.CANCEL_DELETE);
                        break;
                    case INVITE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        break;
                    case SEND_MESSAGE:
                        sendMessage();
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
                        conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"LOG OFF",0);
                        gui.setOnlineState(GameState.LOGIN);
                        gui.clearGui();
                        break;
                    case LOG_OUT_NO:
                        gui.clearGui();
                        break;
                    case CLOSE_CHAT:
                        gui.closeChat();
                        gui.setFriendMessages(new ArrayList<>());
                        break;
                    case PROFILE_BUTTON:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PROFILE_GUI);
                        prof = (profile) conToServer.sendStringWaitingAnswerObject(msgID.toServer.request,"PROFILE:"+gui.getUserLogged(),0);
                        gui.setProfileToShow(prof);
                        break;
                    case PROFILE_FRIEND:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PROFILE_GUI);
                        prof = (profile) conToServer.sendStringWaitingAnswerObject(msgID.toServer.request,"PROFILE:"+gui.getFriends().get(gui.getFriendSelected()),0);
                        gui.setProfileToShow(prof);
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
                    case POP_UP_BUTTON:
                        gui.closePopUp();
                        break;
                    case CANCEL_DELETE:
                        gui.closePopUpWithConfirmation(guiItems.CONFIRM_DELETE, guiItems.CANCEL_DELETE);
                        break;
                    case CONFIRM_DELETE:
                        deleteFriend();
                        break;
                    case RANKED_BUTTON:
                        conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"REGISTER GAME:"+gui.getUserLogged() +":TOMI1234:TERRY:ANDY:2:true",0);
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
                    case CANCEL_ADD_BUTTON:
                        gui.closeAddFriend();
                        break;
                    case MESSAGE_FRIEND:
                        if(!gui.getComponentsOnScreen().containsKey(guiItems.POP_UP_TABLE)) {
                            gui.closeFriendInteractionPopUp();
                            gui.getProfile().swapHistorial();
                            gui.chat(gui.getFriends().get(gui.getFriendSelected()));
                            break;
                        }
                    case DELETE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        gui.popUpWithConfirmation(gui.getFriends().get(gui.getFriendSelected())
                                +" will be removed\nfrom friends.\nAre you sure?", guiItems.CONFIRM_DELETE, guiItems.CANCEL_DELETE);
                        break;
                    case INVITE_FRIEND:
                        gui.closeFriendInteractionPopUp();
                        break;
                    case SEND_MESSAGE:
                        sendMessage();
                        break;
                    case CLOSE_CHAT:
                        gui.clearGui();
                        gui.setFriendMessages(new ArrayList<>());
                        gui.getProfile().swapHistorial();
                        break;
                    case PROFILE_FRIEND:
                        gui.clearGui();
                        gui.setOnlineState(GameState.PROFILE_GUI);
                        prof = (profile) conToServer.sendStringWaitingAnswerObject(msgID.toServer.request,"PROFILE:"+gui.getFriends().get(gui.getFriendSelected()),0);
                        gui.setProfileToShow(prof);
                        break;
                    case ACCEPT_FRIEND:
                        answerFriendRequest(true);
                        break;
                    case REJECT_FRIEND:
                        answerFriendRequest(false);
                        break;
                    case POP_UP_BUTTON:
                        gui.closePopUp();
                        break;
                    case CANCEL_DELETE:
                        gui.closePopUpWithConfirmation(guiItems.CONFIRM_DELETE, guiItems.CANCEL_DELETE);
                        break;
                    case CONFIRM_DELETE:
                        deleteFriend();
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

    private void deleteFriend(){
        String friend = gui.getFriends().get(gui.getFriendSelected());
        boolean end = false;
        for(int i = 0; !end && i <gui.getFriends().size(); ++i){
            if(gui.getFriends().get(i).equals(friend)){
                gui.getFriends().remove(i);
                end = true;
            }
        }
        gui.closeFriendList();
        new friend_list_gui(gui,gui.getFriends());
        //gui.reloadGUI();
        String res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"REMOVE FRIEND:"+friend,0);
        gui.closePopUpWithConfirmation(guiItems.CONFIRM_DELETE, guiItems.CANCEL_DELETE);
    }

    private void addFriendGestion(){
        String friend = ((JTextField)gui.getComponentsOnScreen().get(guiItems.INTRODUCE_NAME)).getText().toUpperCase();
        gui.closeAddFriend();
        if(friend.length() == 0){
            gui.popUp("Username cant be empty.");
        }
        else {
            String res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"SEND FRIEND REQUEST:"+gui.getUserLogged()+":"+friend, 0);
            gui.closePopUpWithConfirmation(guiItems.CONFIRM_ADD_BUTTON, guiItems.CANCEL_ADD_BUTTON);
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
            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"ACCEPT FRIEND REQUEST:"+gui.getUserLogged()+":"+additionalInformation, 0);
            gui.getFriends().add(additionalInformation);
            gui.closeFriendList();
            new friend_list_gui(gui,gui.getFriends());
        }
        else{
            res = conToServer.sendStringWaitingAnswerString(msgID.toServer.request,"REJECT FRIEND REQUEST:"+gui.getUserLogged()+":"+additionalInformation, 0);
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

    private void sendMessage(){
        String msg = ((JTextField)gui.getComponentsOnScreen().get(guiItems.MESSAGE_WRITER)).getText();
        String friend = gui.getFriends().get(gui.getFriendSelected());
        if(msg != null && !msg.trim().equals("")){
            int id = 0;
            if(gui.getFriendMessages() != null && !gui.getFriendMessages().isEmpty()){
                message aux = gui.getFriendMessages().get(gui.getFriendMessages().size()-1);
                id = aux.getId()+1;
            }
            gui.getChatgui().addMessage(new message(id,gui.getUserLogged(), friend,msg));
            ((JTextField)gui.getComponentsOnScreen().get(guiItems.MESSAGE_WRITER)).setText("");
            conToServer.sendString(msgID.toServer.request,"SEND MESSAGE:"+friend+":"+msg);
        }
    }
}