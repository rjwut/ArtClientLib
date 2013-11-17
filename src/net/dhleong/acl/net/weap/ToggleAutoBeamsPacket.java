package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

public class ToggleAutoBeamsPacket extends ShipActionPacket {
	private static final int FLAGS = 0x0c;

	public ToggleAutoBeamsPacket() {
		super(FLAGS, TYPE_TOGGLE_AUTO_BEAMS, 0);
	}
}