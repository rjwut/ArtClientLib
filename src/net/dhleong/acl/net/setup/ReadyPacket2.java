package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * The official client sends this sometimes. We currently don't know why. It
 * seems to work fine without it.
 * @author dhleong
 */
public class ReadyPacket2 extends ShipActionPacket {
    public ReadyPacket2() {
        super(TYPE_READY2, 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}