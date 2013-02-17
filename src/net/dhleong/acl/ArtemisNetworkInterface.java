package net.dhleong.acl;

public interface ArtemisNetworkInterface {
    
    /** The main Artemis server version we're targeting */
    public static final float TARGET_VERSION = 1.700f;
    
    /** The Artemis server versions we support */
    public static final float[] SUPPORTED_VERSIONS = new float[] {
        TARGET_VERSION,
        1.701f // iOS servers
    };
    
    public void addOnPacketListener(OnPacketListener listener);

    public void send(ArtemisPacket pkt);

    void start();

    void stop();
}
