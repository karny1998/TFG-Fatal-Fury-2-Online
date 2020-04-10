package lib.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;

public class  controlListener implements KeyListener {

    static Boolean[] keyStatus;
    static Queue<Integer> inputQueue;
    static Queue<Integer> inputQueue_timestamps;
    static int QUEUE_SIZE = 5;
    static int currentKey;

    public controlListener(){
        keyStatus = new Boolean[256];
        for(int i = 0; i < keyStatus.length; i++){
            keyStatus[i] = false;
        }
        inputQueue = new LinkedList<>();
        inputQueue_timestamps = new LinkedList<>();
        currentKey = -1;

    }


    @Override
    public void keyTyped(KeyEvent e) {
        currentKey = e.getKeyChar();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.keyStatus[e.getKeyCode()] = true;
        this.inputQueue.add(e.getKeyCode());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.inputQueue_timestamps.add((int)timestamp.getTime());
        if(this.inputQueue.size() > this.QUEUE_SIZE){
            this.inputQueue.remove();
            this.inputQueue_timestamps.remove();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.keyStatus[e.getKeyCode()] = false;
        currentKey = -1;
    }


    public static char getCurrentKey(){
        return (char)currentKey;
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
    public static Queue<Integer> getInputQueue_timestamps(){
        return inputQueue_timestamps;
    }

    public static boolean anyKeyPressed(){
        Boolean status = false;
        for(int i = 0; i < keyStatus.length; i++){
            status = status || keyStatus[i];
        }
        return status;
    }
}
