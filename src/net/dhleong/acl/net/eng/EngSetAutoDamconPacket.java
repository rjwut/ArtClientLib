package net.dhleong.acl.net.eng;

import net.dhleong.acl.net.ShipActionPacket;


/**
 * Set whether engineering should use
 *  autonomous Damcon teams or not
 * 
 * @author dhleong
 *
 */
public class EngSetAutoDamconPacket extends ShipActionPacket {
    public EngSetAutoDamconPacket(boolean useAutonomous) {
        super(TYPE_AUTO_DAMCON, useAutonomous ? 1 : 0);
    }
}