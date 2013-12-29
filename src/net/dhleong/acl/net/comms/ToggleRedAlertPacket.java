package net.dhleong.acl.net.comms;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Toggles red alert on and off.
 */
public class ToggleRedAlertPacket extends ShipActionPacket {
    public ToggleRedAlertPacket() {
        super(TYPE_TOGGLE_REDALERT, 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}