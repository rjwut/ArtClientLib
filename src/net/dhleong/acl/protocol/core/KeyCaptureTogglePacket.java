package net.dhleong.acl.protocol.core;

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
 * Enables/disables keystroke capture for this console. Note that the game
 * master console always captures keystrokes; all others don't by default unless
 * this packet enables it.
 * @author rjwut
 */
public class KeyCaptureTogglePacket extends BaseArtemisPacket {
	private static final int TYPE = 0xf754c8fe;
	private static final byte MSG_TYPE = 0x11;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, MSG_TYPE,
				new PacketFactory() {
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

	private KeyCaptureTogglePacket(PacketReader reader) {
		super(ConnectionType.SERVER, TYPE);
		int subtype = reader.readInt();

		if (subtype != MSG_TYPE) {
			throw new UnexpectedTypeException(subtype, MSG_TYPE);
		}

		mEnabled = reader.readByte() == 1;
	}

	public KeyCaptureTogglePacket(boolean enabled) {
		super(ConnectionType.SERVER, TYPE);
		mEnabled = enabled;
	}

	/**
	 * Returns true if this console should capture keystrokes; false otherwise.
	 */
	public boolean isEnabled() {
		return mEnabled;
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(MSG_TYPE).writeInt(mEnabled ? 1 : 0);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mEnabled ? "ON" : "OFF");
	}
}