package net.dhleong.acl.net.sci;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Sets the science officer's current target.
 */
public class SciSelectPacket extends ShipActionPacket {
	/**
	 * Sets the science officer's current target. If the argument is null, the
	 * target is cleared.
	 * @param target
	 */
    public SciSelectPacket(ArtemisObject target) {
        super(TYPE_SCI_SELECT, target == null ? 1 : target.getId());
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}