package net.dhleong.acl.protocol.core.setup;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;

/**
 * Gives the Artemis server's version number. Send immediately after
 * WelcomePacket.
 * @author rjwut
 */
public class VersionPacket extends BaseArtemisPacket {
	private static final int TYPE = 0xe548e74a;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
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

	private int mUnknown;
	private float mVersion;

	private VersionPacket(PacketReader reader) {
		super(ConnectionType.SERVER, TYPE);
		mUnknown = reader.readInt();
		mVersion = reader.readFloat();
	}

	public VersionPacket(float version) {
		super(ConnectionType.SERVER, TYPE);
		mVersion = version;
	}

	/**
	 * @return The version number
	 */
	public float getVersion() {
		return mVersion;
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(mUnknown).writeFloat(mVersion);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mVersion);
	}
}