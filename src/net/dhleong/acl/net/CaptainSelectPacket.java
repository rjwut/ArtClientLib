package net.dhleong.acl.net;

import net.dhleong.acl.world.ArtemisObject;

public class CaptainSelectPacket extends ShipActionPacket {

    private static final int FLAGS = 0x0c;
    public CaptainSelectPacket(ArtemisObject target) {
        super(FLAGS, TYPE_CAPTAIN_SELECT, target == null ? 1 : target.getId());
    }

}
