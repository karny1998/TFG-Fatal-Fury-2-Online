package lib.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;


//TODO  eliminar por completo la clase keybinding

//TODO crear 2 vectores de booleanos que seran los mandos

//TODO hacer los keybindings aqui
public class controlListener implements KeyListener {

    static Boolean[] keyStatus;
    static Queue<Integer> inputQueue_1, inputQueue_2;
    static int QUEUE_SIZE = 10;
    static int currentKey;

    public static Boolean[] mando1, mando2;

    public static final int AR_INDEX = 0;
    public static final int AB_INDEX = 1;
    public static final int IZ_INDEX = 2;
    public static final int DE_INDEX = 3;
    public static final int A_INDEX = 4;
    public static final int B_INDEX = 5;
    public static final int C_INDEX = 6;
    public static final int D_INDEX = 7;
    public static final int ESC_INDEX = 8;
    public static final int ENT_INDEX = 9;


    public static String[] movimientos = {
                        "AR",
                        "AB",
                        "IZ",
                        "DE",
                        "A",
                        "B",
                        "C",
                        "D"
                    };

    static int[] keyBindings1, keyBindings2;



    public controlListener(){
        keyStatus = new Boolean[526];

        for(int i = 0; i < keyStatus.length; i++){
            keyStatus[i] = false;
        }
        mando1 = new Boolean[10];
        mando2 = new Boolean[10];

        for(int i = 0; i < mando1.length; i++){
            mando1[i] = false;  mando2[i] = false;
        }


        keyBindings1 = new int[]{
                38, //AR_INDEX - ABAJO
                40, //AB_INDEX - ARRIBA
                37, //IZ_INDEX - IZQUIERDA
                39, //DE_INDEX - DERECHA
                65, //A_INDEX - A
                81, //B_INDEX - Q
                83, //C_INDEX - S
                87, //D_INDEX - W
                27, //ESC_INDEX - ESCAPE
                10  //ENT_INDEX - ENTER
        };

        keyBindings2 = new int[]{
                -1,  //AR_INDEX
                -1,  //AB_INDEX
                -1,  //IZ_INDEX
                -1,  //DE_INDEX
                -1,  //A_INDEX
                -1,  //B_INDEX
                -1,  //C_INDEX
                -1,  //D_INDEX
                -1,  //ESC_INDEX
                -1   //ENT_INDEX
        };

        inputQueue_1 = new LinkedList<>();
        inputQueue_2 = new LinkedList<>();
        currentKey = -1;

    }


    @Override
    public void keyTyped(KeyEvent e) {
        currentKey = e.getKeyChar();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.keyStatus[e.getKeyCode()] = true;

        for(int i = 0; i < keyBindings1.length; i++){
            if(e.getKeyCode() == keyBindings1[i]){
                mando1[i] = true;
                this.inputQueue_1.add(e.getKeyCode());
                if(this.inputQueue_1.size() > this.QUEUE_SIZE){
                    this.inputQueue_1.remove();
                }
            }
            if(e.getKeyCode() == keyBindings2[i]){
                mando2[i] = true;
                this.inputQueue_2.add(e.getKeyCode());
                if(this.inputQueue_2.size() > this.QUEUE_SIZE){
                    this.inputQueue_2.remove();
                }
            }
        }



    }

    @Override
    public void keyReleased(KeyEvent e) {
        this.keyStatus[e.getKeyCode()] = false;
        for(int i = 0; i < keyBindings1.length; i++){
            if(e.getKeyCode() == keyBindings1[i]){
                mando1[i] = false;
            }
            if(e.getKeyCode() == keyBindings2[i]){
                mando2[i] = false;
            }
        }
        currentKey = -1;
    }


    public static char getCurrentKey(){
        return (char)currentKey;
    }


    public static int getLastKey(int player){

        switch (player){
            case 1:
                if(inputQueue_1.size() == 0){
                    return  -1;
                }
                return inputQueue_1.peek();
            case 2:
                if(inputQueue_2.size() == 0){
                    return  -1;
                }
                return inputQueue_2.peek();
            default:
                throw new RuntimeException("Invalid player number");
        }
    }

    public static boolean isPressed(int keycode){
        if(keycode == -1){
            return false;
        }
        return keyStatus[keycode];
    }

    public static Queue<Integer> getQueue(int player){
        switch (player){
            case 1:
                return inputQueue_1;
            case 2:
                return inputQueue_2;
            default:
                throw new RuntimeException("Invalid player number");
        }
    }


    public static boolean anyKeyPressed(){
        Boolean status = false;
        for(int i = 0; i < keyStatus.length; i++){
            status = status || keyStatus[i];
        }
        return status;
    }

    public static boolean getStatus(int player, int index){
        switch (player){
            case 0:
                return mando1[index] || mando2[index];
            case 1:
                return mando1[index];
            case 2:
                return mando2[index];
            default:
                throw new RuntimeException("Invalid player number");
        }
    }

    public static String getMove(int player){
        Boolean[] actual;
        String mov = "";
        switch (player){
            case 1:
                actual = mando1;
                break;
            case 2:
                actual =  mando2;
                break;
            default:
                throw new RuntimeException("Invalid player number");
        }
        for(int i = 0; i < actual.length - 2; i++){
            if( actual[i] ){
                if(!mov.equals("")){mov += "-";}
                mov += movimientos[i];
            }
        }
        System.out.println(mov);
        return mov;

    }




}
