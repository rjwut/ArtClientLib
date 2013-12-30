package net.dhleong.acl.net;

/**
 * Toggles between first- and third-person perspectives on the main screen.
 * @author rjwut
 */
public class TogglePerspectivePacket extends ShipActionPacket {
	public TogglePerspectivePacket() {
		super(TYPE_TOGGLE_PERSPECTIVE, 0);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}