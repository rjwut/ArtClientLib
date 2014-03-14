package net.dhleong.acl.protocol;

import net.dhleong.acl.enums.ConnectionType;

/**
 * Any packet received that isn't of a type recognized by a registered protocol
 * will be returned as this class. If you disable packet parsing (by calling
 * ThreadedArtemisNetworkInterface.setParsePackets(false)), all packets will be
 * of this type. This is mainly intended for reverse-engineering of the protocol
 * and debugging; most clients won't want to listen for these packets.
 * @author rjwut
 */
public class UnknownPacket extends RawPacket {
	public UnknownPacket(ConnectionType connectionType, int packetType,
			byte[] payload) {
		super(connectionType, packetType, payload);
	}
}