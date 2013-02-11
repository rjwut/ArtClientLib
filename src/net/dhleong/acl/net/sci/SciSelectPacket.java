package net.dhleong.acl.net.sci;

import net.dhleong.acl.net.ShipActionPacket;
import net.dhleong.acl.world.ArtemisObject;

public class SciSelectPacket extends ShipActionPacket {

    private static final int FLAGS = 0x0c;
    public SciSelectPacket(ArtemisObject target) {
        super(FLAGS, TYPE_SCI_SELECT, target == null ? 1 : target.getId());
    }

}
