package net.dhleong.acl.net.sci;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.world.ArtemisObject;

public class SciScanPacket extends BaseArtemisPacket {

    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    public SciScanPacket(ArtemisObject target) {
        super(0x2, FLAGS, TYPE, new byte[8]);
        
        // ??
        PacketParser.putLendInt(0x11, mData, 0);
        PacketParser.putLendInt(target.getId(), mData, 4);
    }

}
