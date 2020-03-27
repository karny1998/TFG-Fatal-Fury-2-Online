package lib.input;

public class keyBinding {
    private int up, down, left, right;
    private int Weak_Punch, Weak_Kick, Strong_Punch, Strong_Kick;

    public keyBinding(){
        this.up = 38;
        this.down = 40;
        this.left = 37;
        this.right = 39;

        //A
        this.Weak_Punch = 65;
        //S
        this.Weak_Kick = 83;
        //Q
        this.Strong_Punch = 81;
        //W
        this.Strong_Kick = 87;
    }

    public int getUpKey(){
        return this.up;
    }

    public int getDown() {
        return down;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getWeak_Punch() {
        return Weak_Punch;
    }

    public int getWeak_Kick() {
        return Weak_Kick;
    }

    public int getStrong_Punch() {
        return Strong_Punch;
    }

    public int getStrong_Kick() {
        return Strong_Kick;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public void setWeak_Punch(int weak_Punch) {
        Weak_Punch = weak_Punch;
    }

    public void setWeak_Kick(int weak_Kick) {
        Weak_Kick = weak_Kick;
    }

    public void setStrong_Punch(int strong_Punch) {
        Strong_Punch = strong_Punch;
    }

    public void setStrong_Kick(int strong_Kick) {
        Strong_Kick = strong_Kick;
    }
}
