package server;

public class msgID {
    public static class toServer{
        public static int request = 1, tramits = -1, notification = 2, ping = 0;
    }
    public static class toClient{
        public static int tramits = -2, hi = -1, synchronization = 0, character = 2, fight = 3;
    }
}
