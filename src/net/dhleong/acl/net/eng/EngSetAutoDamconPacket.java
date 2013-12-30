package net.dhleong.acl.net.eng;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Set whether DAMCON teams should be autonomous or not.
 * @author dhleong
 */
public class EngSetAutoDamconPacket extends ShipActionPacket {
	/**
	 * @param useAutonomous Whether DAMCON teams should be autonomous
	 */
    public EngSetAutoDamconPacket(boolean useAutonomous) {
        super(TYPE_AUTO_DAMCON, useAutonomous ? 1 : 0);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg == 1 ? "on" : "off");
	}
}