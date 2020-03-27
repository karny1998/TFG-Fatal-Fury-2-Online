package lib.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;

public class controlListener implements KeyListener {

    Boolean[] keyStatus;
    Queue<Integer> inputQueue;
    int QUEUE_SIZE = 5;

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

    public int getLastKey(){
        if(this.inputQueue.size() == 0){
            return  -1;
        }
        return this.inputQueue.peek();
    }

    public boolean isPressed(int keycode){
        if(keycode == -1){
            return false;
        }
        return this.keyStatus[keycode];
    }

    public Queue<Integer> getQueue(){
        return this.inputQueue;
    }


}
