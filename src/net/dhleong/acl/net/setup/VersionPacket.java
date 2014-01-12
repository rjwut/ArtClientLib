package net.dhleong.acl.net.setup;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketReader;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Gives the Artemis server's version number. Send immediately after
 * WelcomePacket.
 * @author rjwut
 */
public class VersionPacket extends BaseArtemisPacket {
	private static final int TYPE = 0xe548e74a;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return VersionPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new VersionPacket(reader);
			}
		});
	}

	private float mVersion;

	private VersionPacket(PacketReader reader) {
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