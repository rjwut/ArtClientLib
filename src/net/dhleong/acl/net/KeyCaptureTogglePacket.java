package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Enables/disables keystroke capture for this station. Note that the game
 * master station always captures keystrokes; all others don't by default unless
 * this packet enables it.
 * @author rjwut
 */
public class KeyCaptureTogglePacket extends BaseArtemisPacket {
	private static final int TYPE = 0xf754c8fe;
	private static final byte MSG_TYPE = 0x11;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, MSG_TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return KeyCaptureTogglePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new KeyCaptureTogglePacket(reader);
			}
		});
	}

	private boolean mEnabled;

	private KeyCaptureTogglePacket(PacketReader reader) throws ArtemisPacketException {
		super(ConnectionType.SERVER, TYPE);
		int subtype = reader.readInt();

		if (subtype != MSG_TYPE) {
			throw new ArtemisPacketException(
					"Expected subtype " + MSG_TYPE + ", got " + subtype
			);
		}

		mEnabled = reader.readByte() == 1;
	}

	/**
	 * Returns true if this station should capture keystrokes; false otherwise.
	 */
	public boolean isEnabled() {
		return mEnabled;
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mEnabled ? "ON" : "OFF");
	}
}