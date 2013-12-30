package net.dhleong.acl.net;

import net.dhleong.acl.world.ArtemisObject;

/**
 * Selects (or deselects) a target on the captain's map.
 * @author rjwut
 */
public class CaptainSelectPacket extends ShipActionPacket {
	/**
	 * @param target The target to select, or null to deselect a target
	 */
    public CaptainSelectPacket(ArtemisObject target) {
        super(TYPE_CAPTAIN_SELECT, target == null ? 1 : target.getId());
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}