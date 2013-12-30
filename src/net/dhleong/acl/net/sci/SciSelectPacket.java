package net.dhleong.acl.net.sci;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Sets the science officer's current target.
 */
public class SciSelectPacket extends ShipActionPacket {
	/**
	 * @param target The target to select (or null to clear the taregt)
	 */
    public SciSelectPacket(ArtemisObject target) {
        super(TYPE_SCI_SELECT, target == null ? 1 : target.getId());
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}