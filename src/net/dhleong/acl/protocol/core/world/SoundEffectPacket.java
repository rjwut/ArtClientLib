package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;

/**
 * Indicates that the client should play the indicated sound file.
 * @author rjwut
 */
public class SoundEffectPacket extends BaseArtemisPacket {
	private static final int TYPE = 0xf754c8fe;
	private static final byte MSG_TYPE = 0x03;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return SoundEffectPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new SoundEffectPacket(reader);
			}
		});
	}

	private String mFilename;

	private SoundEffectPacket(PacketReader reader) {
		super(ConnectionType.SERVER, TYPE);
		int subtype = reader.readInt();

		if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
		}

		mFilename = reader.readString();
	}

	/**
	 * Returns the path of the file to play, relative to the Artemis install
	 * directory.
	 */
	public String getFilename() {
		return mFilename;
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeString(mFilename);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mFilename);
	}
}