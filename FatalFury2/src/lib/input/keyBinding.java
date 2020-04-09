package lib.input;

public class keyBinding {
    private static int escape = 27;
    private static int enter = 10;

    private static int up = 38;
    private static int down= 40;
    private static int left = 37;
    private static int right = 39;

    //A
    private static int Weak_Punch = 65;
    //S
    private static int Weak_Kick= 83;
    //Q
    private static int Strong_Punch = 81;
    //W
    private static int Strong_Kick = 87;







    public static int getUp(){
        return up;
    }

    public static int getDown() {
        return down;
    }

    public static int getLeft() {
        return left;
    }

    public static int getRight() {
        return right;
    }

    public static int getWeak_Punch() {
        return Weak_Punch;
    }

    public static int getWeak_Kick() {
        return Weak_Kick;
    }

    public static int getStrong_Punch() {
        return Strong_Punch;
    }

    public static int getStrong_Kick() {
        return Strong_Kick;
    }

    public static int getEscape() {
        return escape;
    }

    public static int getEnter() {
        return enter;
    }



    public static void setUp(int _up) {
        up = _up;
    }

    public static void setDown(int _down) {
        down = _down;
    }

    public static void setLeft(int _left) {
        left = _left;
    }

    public static void setRight(int _right) {
        right = _right;
    }

    public static void setWeak_Punch(int weak_Punch) {
        Weak_Punch = weak_Punch;
    }

    public static void setWeak_Kick(int weak_Kick) {
        Weak_Kick = weak_Kick;
    }

    public static void setStrong_Punch(int strong_Punch) {
        Strong_Punch = strong_Punch;
    }

    public static void setStrong_Kick(int strong_Kick) {
        Strong_Kick = strong_Kick;
    }

    public static void setEscape(int esc) {
        escape = esc;
    }

    public static void setEnter(int ent) {
        enter = ent;
    }

    public static String getMove(){

        if( controlListener.isPressed(getUp())) {
            return "UP";
        } else if ( controlListener.isPressed(getDown())) {
            return "DOWN";
        } else if ( controlListener.isPressed(getRight())) {
            return "RIGHT";
        } else if ( controlListener.isPressed(getLeft())) {
            return "LEFT";
        } else if ( controlListener.isPressed(getWeak_Punch())) {
            return "WEAK_PUNCH";
        } else if ( controlListener.isPressed(getWeak_Kick())) {
            return "WEAK_KICK";
        } else if ( controlListener.isPressed(getStrong_Punch())) {
            return "STRONG_PUNCH";
        } else if ( controlListener.isPressed(getStrong_Kick())) {
            return "STRONG_KICK";
        } else {
            return "";
        }
    }


}
