package lib.objects.networking;

/**
 * The type Msg id.
 */
public class msgID {
    /**
     * The type To server.
     */
    public static class toServer{
        /**
         * The constant request.
         */
        public static int request = 1, /**
         * The Tramits.
         */
        tramits = -1, /**
         * The Notification.
         */
        notification = 2, /**
         * The Ping.
         */
        ping = 0;
    }

    /**
     * The type To client.
     */
    public static class toClient{
        /**
         * The constant tramits.
         */
        public static int tramits = -2, /**
         * The Hi.
         */
        hi = -1, /**
         * The Synchronization.
         */
        synchronization = 0, /**
         * The Character.
         */
        character = 2, /**
         * The Fight.
         */
        fight = 3;
    }
}
