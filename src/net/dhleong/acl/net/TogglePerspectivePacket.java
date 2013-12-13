package net.dhleong.acl.net;

public class TogglePerspectivePacket extends ShipActionPacket {
	public TogglePerspectivePacket() {
		super(TYPE_TOGGLE_PERSPECTIVE, 0);
	}
}