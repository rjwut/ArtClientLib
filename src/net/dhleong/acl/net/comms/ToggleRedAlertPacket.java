package net.dhleong.acl.net.comms;

import net.dhleong.acl.net.ShipActionPacket;

public class ToggleRedAlertPacket extends ShipActionPacket {
    public ToggleRedAlertPacket() {
        super(TYPE_TOGGLE_REDALERT, 0);
    }
}