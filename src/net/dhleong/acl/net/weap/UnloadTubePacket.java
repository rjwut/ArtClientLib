package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

public class UnloadTubePacket extends ShipActionPacket {

    private static final int FLAGS = 0x0c;
    public UnloadTubePacket(int tube) {
        super(FLAGS, TYPE_UNLOAD_TUBE, tube);
    }
}
