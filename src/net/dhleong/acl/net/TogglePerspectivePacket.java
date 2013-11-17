package net.dhleong.acl.net;

public class TogglePerspectivePacket extends ShipActionPacket {
	private static final int FLAGS = 0x0c;

	public TogglePerspectivePacket() {
		super(FLAGS, TYPE_TOGGLE_PERSPECTIVE, 0);
	}
}