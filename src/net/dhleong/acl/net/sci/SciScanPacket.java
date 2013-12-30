package net.dhleong.acl.net.sci;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Scans the indicated target.
 */
public class SciScanPacket extends ShipActionPacket {
	/**
	 * @param target The target to scan
	 */
    public SciScanPacket(ArtemisObject target) {
        super(TYPE_SCI_SCAN, target != null ? target.getId() : 0);

        if (target == null) {
        	throw new IllegalArgumentException("You must provide a target");
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mArg);
	}
}