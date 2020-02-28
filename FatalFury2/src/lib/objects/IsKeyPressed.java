package lib.objects;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

enum controlKey { NONE, A, D, W, S}

public class IsKeyPressed {

    private static volatile boolean aPressed = false, dPressed = false, wPressed = false, sPressed = false;

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
                            break;
                    }
                    return false;
                }
            }
        });
    }
}