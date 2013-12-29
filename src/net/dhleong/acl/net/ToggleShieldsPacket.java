package net.dhleong.acl.net;

/**
 * Toggle shields
 * @author dhleong
 */
public class ToggleShieldsPacket extends ShipActionPacket {
    public ToggleShieldsPacket() {
        super(TYPE_TOGGLE_SHIELDS, 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		// do nothing
	}
}