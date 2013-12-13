package net.dhleong.acl.net.sci;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

public class SciScanPacket extends ShipActionPacket {
    public SciScanPacket(ArtemisObject target) {
        super(TYPE_SCI_SCAN, target.getId());
    }
}