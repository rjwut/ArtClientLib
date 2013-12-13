package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.ShipActionPacket;

public class UnloadTubePacket extends ShipActionPacket {
    public UnloadTubePacket(int tube) {
        super(TYPE_UNLOAD_TUBE, tube);
    }
}