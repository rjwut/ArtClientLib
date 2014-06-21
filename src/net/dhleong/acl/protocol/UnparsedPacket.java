package net.dhleong.acl.protocol;

import net.dhleong.acl.enums.ConnectionType;

/**
 * Any packet received for which no packet listeners have been registered will
 * be returned as this class. You cannot create packet listeners that will
 * receive this type of packet. However, paired ThreadedArtemisNetworkInterface
 * objects will pass these packets to each other.
 * @author rjwut
 */
public final class UnparsedPacket extends RawPacket {
	public UnparsedPacket(ConnectionType connectionType, int packetType,
			byte[] payload) {
		super(connectionType, packetType, payload);
	}
}