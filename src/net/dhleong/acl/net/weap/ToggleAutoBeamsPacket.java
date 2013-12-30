package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Toggles auto beams on/off.
 * @author rjwut
 */
public class ToggleAutoBeamsPacket extends ShipActionPacket {
	public ToggleAutoBeamsPacket() {
		super(TYPE_TOGGLE_AUTO_BEAMS, 0);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}