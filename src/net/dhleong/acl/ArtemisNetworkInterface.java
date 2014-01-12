package net.dhleong.acl;

import net.dhleong.acl.net.protocol.CoreArtemisProtocol;
import net.dhleong.acl.net.protocol.Protocol;

/**
 * Interface for objects which can connect to an Artemis server and send and
 * receive packets.
 */
public interface ArtemisNetworkInterface {
	// The main Artemis server version we're targeting
    public static final float TARGET_VERSION = 2.000f;
    
    // The Artemis server versions we support
    public static final float[] SUPPORTED_VERSIONS = new float[] {
        TARGET_VERSION
    };

    /**
     * Adds a packet listener.
     */
    public void addPacketListener(Object listener);

    /**
     * Connects to the server.
     */
    void start();

    /**
     * Sends the given ArtemisPacket to the server.
     */
    public void send(ArtemisPacket pkt);

    /**
     * Disconnects from the server.
     */
    void stop();

    /**
     * Registers the packet types defined by the given Protocol with this
     * object. The {@link CoreArtemisProtocol} is registered automatically.
     */
	void registerProtocol(Protocol protocol);
}