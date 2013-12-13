package net.dhleong.acl.net.sci;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

public class SciSelectPacket extends ShipActionPacket {
    public SciSelectPacket(ArtemisObject target) {
        super(TYPE_SCI_SELECT, target == null ? 1 : target.getId());
    }
}