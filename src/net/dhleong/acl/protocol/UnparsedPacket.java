package net.dhleong.acl.protocol;

import net.dhleong.acl.enums.ConnectionType;

/**
 * Any packet received for which no packet listeners have been registered will
 * be returned as this class.
 * @author rjwut
 */
public final class UnparsedPacket extends RawPacket {
	public UnparsedPacket(ConnectionType connectionType, int packetType,
			byte[] payload) {
		super(connectionType, packetType, payload);
	}
}