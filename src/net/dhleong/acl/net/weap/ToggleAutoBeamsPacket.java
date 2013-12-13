package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

public class ToggleAutoBeamsPacket extends ShipActionPacket {
	public ToggleAutoBeamsPacket() {
		super(TYPE_TOGGLE_AUTO_BEAMS, 0);
	}
}