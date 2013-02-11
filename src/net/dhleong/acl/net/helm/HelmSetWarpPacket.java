package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Set warp speed
 * 
 * @author dhleong
 *
 */
public class HelmSetWarpPacket extends ShipActionPacket {
    
    private static final int FLAGS = 0x0c;
    
    public HelmSetWarpPacket(int warp) {
        super(FLAGS, TYPE_WARPSPEED, 0);
    }
}
