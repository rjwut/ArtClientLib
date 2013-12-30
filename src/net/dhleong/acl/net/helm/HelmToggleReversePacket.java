package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Toggles reverse engines.
 * @author dhleong
 */
public class HelmToggleReversePacket extends ShipActionPacket {
    public HelmToggleReversePacket() {
        super(TYPE_REVERSE_ENGINES, 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}