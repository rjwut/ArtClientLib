package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Indicates that the client should play the indicated sound file.
 * @author rjwut
 */
public class SoundEffectPacket extends BaseArtemisPacket {
	private static final int TYPE = 0xf754c8fe;
	private static final byte MSG_TYPE = 0x03;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, MSG_TYPE, new PacketFactory() {
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

	private SoundEffectPacket(PacketReader reader) throws ArtemisPacketException {
		super(ConnectionType.SERVER, TYPE);
		int subtype = reader.readInt();

		if (subtype != MSG_TYPE) {
        	throw new ArtemisPacketException(
        			"Expected subtype " + MSG_TYPE + ", got " + subtype
        	);
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
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mFilename);
	}
}