package net.dhleong.acl.net.setup;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;

/**
 * Sent by the server immediately on connection. The receipt of this packet
 * indicates a successful connection to the server.
 * @author rjwut
 */
public class WelcomePacket extends BaseArtemisPacket {
	public static final int TYPE = 0x6d04b3da;

	public WelcomePacket(PacketReader reader) {
		super(ConnectionType.SERVER, TYPE);
		int length = reader.readInt();
		reader.skip(length);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}