package net.dhleong.acl.net.comms;

import net.dhleong.acl.net.ShipActionPacket;

public class ToggleRedAlertPacket extends ShipActionPacket {

    private static final int FLAGS = 0x0c;

    public ToggleRedAlertPacket() {
        super(FLAGS, TYPE_TOGGLE_REDALERT, 0);
    }
}
