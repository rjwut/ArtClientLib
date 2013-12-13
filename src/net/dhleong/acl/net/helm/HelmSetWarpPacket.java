package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Set warp speed
 * @author dhleong
 */
public class HelmSetWarpPacket extends ShipActionPacket {
    public HelmSetWarpPacket(int warp) {
        super(TYPE_WARPSPEED, warp);
    }
}