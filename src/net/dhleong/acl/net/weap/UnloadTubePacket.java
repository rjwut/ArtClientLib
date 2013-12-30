package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.Artemis;

/**
 * Unloads the indicated tube.
 */
public class UnloadTubePacket extends ShipActionPacket {
	/**
	 * @param tube Index of the tube to unload, [0 - Artemis.MAX_TUBES)
	 */
    public UnloadTubePacket(int tube) {
        super(TYPE_UNLOAD_TUBE, tube);

        if (tube < 0 || tube >= Artemis.MAX_TUBES) {
        	throw new IndexOutOfBoundsException(
        			"Invalid tube index: " + tube
        	);
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}