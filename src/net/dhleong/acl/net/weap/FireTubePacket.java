package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

/**
 * Fire whatever's in the given tube
 * 
 * @author dhleong
 *
 */
public class FireTubePacket extends ShipActionPacket {

    private static final int FLAGS = 0x0c;
    public FireTubePacket(int tube) {
        super(FLAGS, TYPE_FIRE_TUBE, tube);
    }
}
