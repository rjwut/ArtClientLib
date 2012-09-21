package net.dhleong.acl;

public interface ArtemisNetworkInterface {
    
    public void addOnPacketListener(OnPacketListener listener);

    public void send(ArtemisPacket pkt);

    void start();

    void stop();
}
