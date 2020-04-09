package lib.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;

public class  controlListener implements KeyListener {

    static Boolean[] keyStatus;
    static Queue<Integer> inputQueue;
    static int QUEUE_SIZE = 5;

    public controlListener(){
        keyStatus = new Boolean[256];
        for(int i = 0; i < keyStatus.length; i++){
            keyStatus[i] = false;
        }
        inputQueue = new LinkedList<>();

    }


    @Override
    public void keyTyped(KeyEvent e) {
        //
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.keyStatus[e.getKeyCode()] = true;
        this.inputQueue.add(e.getKeyCode());
        if(this.inputQueue.size() > this.QUEUE_SIZE){
            this.inputQueue.remove();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.keyStatus[e.getKeyCode()] = false;
    }


    public static void addNullkey(){
        inputQueue.add(-1);
        if(inputQueue.size() > QUEUE_SIZE){
            inputQueue.remove();
        }
    }


    public static int getLastKey(){
        if(inputQueue.size() == 0){
            return  -1;
        }
        return inputQueue.peek();
    }

    public static boolean isPressed(int keycode){
        if(keycode == -1){
            return false;
        }
        return keyStatus[keycode];
    }

    public static Queue<Integer> getQueue(){
        return inputQueue;
    }


    public static boolean anyKeyPressed(){
        Boolean status = false;
        for(int i = 0; i < keyStatus.length; i++){
            status = status || keyStatus[i];
        }
        return status;
    }
}
