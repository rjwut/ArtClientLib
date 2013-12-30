package net.dhleong.acl.net.setup;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;

/**
 * Gives the Artemis server's version number. Send immediately after
 * WelcomePacket.
 * @author rjwut
 */
public class VersionPacket extends BaseArtemisPacket {
	public static final int TYPE = 0xe548e74a;

	private float mVersion;

	public VersionPacket(PacketReader reader) {
		super(ConnectionType.SERVER, TYPE);
		reader.readUnknown("Unknown", 4);
		mVersion = reader.readFloat();
	}

	/**
	 * @return The version number
	 */
	public float getVersion() {
		return mVersion;
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mVersion);
	}
}