package net.dhleong.acl;

import java.io.IOException;
import java.nio.charset.Charset;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.PacketWriter;

/**
 * Interface for all packets that can be received or sent.
 */
public interface ArtemisPacket {
	/**
	 * The preamble of every packet starts with this value.
	 */
	public static final int HEADER = 0xdeadbeef;

	/**
	 * This is the Charset used to read String from packets.
	 */
	public static final Charset CHARSET = Charset.forName("UTF-16LE");

    /**
     * Type for packets that create and/or update various world objects
     */
	public static final int WORLD_TYPE = 0x80803df9;

    /**
     * Type of packet used for various ship actions initiated by the player
     */
    public static final int SHIP_ACTION_TYPE = 0x4c821d3c;

    /**
     * Returns a ConnectionType value indicating the type of connection from
     * which this packet originates. SERVER means that this packet type is sent
     * by the server; CLIENT means it's sent by the client.
     */
    public ConnectionType getConnectionType();

    /**
     * Returns the type value for this packet, specified as the last field of
     * the preamble.
     */
    int getType();

    /**
     * Writes this packet to the given PacketWriter.
     */
    public void write(PacketWriter writer) throws IOException;
}