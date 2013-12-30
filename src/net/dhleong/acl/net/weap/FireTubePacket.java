package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.Artemis;

/**
 * Fire whatever's in the given tube.
 * @author dhleong
 */
public class FireTubePacket extends ShipActionPacket {
	/**
	 * @param tube The index of the tube to fire, [0 - Artemis.MAX_TUBES)
	 */
    public FireTubePacket(int tube) {
        super(TYPE_FIRE_TUBE, tube);

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