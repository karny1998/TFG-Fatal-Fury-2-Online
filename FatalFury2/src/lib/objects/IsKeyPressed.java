package lib.objects;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

enum controlKey {NONE, A, D, W, S, LEFT, RIGHT,UP,DOWN, ENTER, ESCAPE}

public class IsKeyPressed {

    private static volatile boolean aPressed = false, dPressed = false, wPressed = false, sPressed = false,
                                    leftPressed = false, rightPressed = false, upPressed = false, downPressed = false,
                                    enterPressed = false, escapePressed = false;

    public static controlKey keyPressed() {
        synchronized (IsKeyPressed.class) {
            controlKey key = controlKey.NONE;
            if(aPressed){
                key = controlKey.A;
            }
            else if (dPressed){
                key = controlKey.D;
            }
            else if (wPressed){
                key = controlKey.W;
            }
            else if (sPressed){
                key = controlKey.S;
            }
            else if (leftPressed){
                key = controlKey.LEFT;
            }
            else if (rightPressed){
                key = controlKey.RIGHT;
            }
            else if (upPressed){
                key = controlKey.UP;
            }
            else if (downPressed){
                key = controlKey.DOWN;
            }
            else if (enterPressed){
                key = controlKey.ENTER;
            }
            else if (escapePressed){
                key = controlKey.ESCAPE;
            }
            return key;
        }
    }

    public IsKeyPressed() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (IsKeyPressed.class) {
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            if (ke.getKeyCode() == KeyEvent.VK_A) {
                                aPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_D) {
                                dPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_W) {
                                wPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_S) {
                                sPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
                                leftPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
                                rightPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_UP) {
                                upPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
                                downPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                                enterPressed = true;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                escapePressed = true;
                            }
                            break;

                        case KeyEvent.KEY_RELEASED:
                            if (ke.getKeyCode() == KeyEvent.VK_A) {
                                aPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_D) {
                                dPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_W) {
                                wPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_S) {
                                sPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
                                leftPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
                                rightPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_UP) {
                                upPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
                                downPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                                enterPressed = false;
                            }
                            else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                escapePressed = false;
                            }
                            break;
                    }
                    return false;
                }
            }
        });
    }
}