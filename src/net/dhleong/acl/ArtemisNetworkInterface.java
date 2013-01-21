package net.dhleong.acl;

public interface ArtemisNetworkInterface {
    
    /** The Artemis server version we support */
    public static final float TARGET_VERSION = 1.661f;
    
    public void addOnPacketListener(OnPacketListener listener);

    public void send(ArtemisPacket pkt);

    void start();

    void stop();
}
