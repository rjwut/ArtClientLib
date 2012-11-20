package net.dhleong.acl;

public interface OnConnectedListener {
    
    /** No error, just disconnected */
    public static final int ERROR_NONE  = 0;
    
    /** Packet parsing error */
    public static final int ERROR_PARSE = 1;
    
    /** IO exception */
    public static final int ERROR_IO = 2;
    
    /** Unknown error */
    public static final int ERROR_UNKNOWN = -1;


    /**
     * Called when we've been disconnected
     *  from the server.
     */
    public void onDisconnected(int errorCode);
    
    /**
     * Called when we've completed
     *  connecting to the server
     */
    public void onConnected();
}
