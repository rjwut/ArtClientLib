package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Fire whatever's in the given tube
 * 
 * @author dhleong
 *
 */
public class FireTubePacket extends ShipActionPacket {
    public FireTubePacket(int tube) {
        super(TYPE_FIRE_TUBE, tube);
    }
}