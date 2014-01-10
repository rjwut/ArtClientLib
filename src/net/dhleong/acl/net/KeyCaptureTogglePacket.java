package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;

/**
 * Enables/disables keystroke capture for this station. Note that the game
 * master station always captures keystrokes; all others don't by default unless
 * this packet enables it.
 * @author rjwut
 */
public class KeyCaptureTogglePacket extends BaseArtemisPacket {
	public static final int TYPE = 0xf754c8fe;
	public static final int MSG_TYPE = 0x11;

	private boolean mEnabled;

	public KeyCaptureTogglePacket(PacketReader reader) throws ArtemisPacketException {
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