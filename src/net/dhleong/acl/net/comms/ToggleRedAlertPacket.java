package net.dhleong.acl.net.comms;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class ToggleRedAlertPacket extends BaseArtemisPacket {

    private static final int TYPE = 0x4c821d3c;
    private static final int FLAGS = 0x0c;

    public ToggleRedAlertPacket() {
        super(0x2, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(0x00000008, mData);
        PacketParser.putLendInt(0x0, mData, 4);
    }
}
