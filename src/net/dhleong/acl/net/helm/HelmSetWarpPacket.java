package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.Artemis;

/**
 * Set warp speed.
 * @author dhleong
 */
public class HelmSetWarpPacket extends ShipActionPacket {
	/**
	 * @param warp Value between 0 (no warp) and 4 (max warp)
	 */
    public HelmSetWarpPacket(int warp) {
        super(TYPE_WARPSPEED, warp);

        if (warp < 0 || warp > Artemis.MAX_WARP) {
        	throw new IndexOutOfBoundsException("Warp speed out of range");
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}