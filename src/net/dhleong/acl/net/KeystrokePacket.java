package net.dhleong.acl.net;

/**
 * Sends a keystroke to the server. This should only be done for the game master
 * station, or if keystroke capture has been enabled via the
 * KeyCaptureTogglePacket.
 * @author rjwut
 */
public class KeystrokePacket extends ShipActionPacket {
	/**
	 * @param keycode the key that was pressed
	 * @see {@link java.awt.event.KeyEvent} (for constants)
	 */
	public KeystrokePacket(int keycode) {
		super(TYPE_KEYSTROKE, keycode);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}