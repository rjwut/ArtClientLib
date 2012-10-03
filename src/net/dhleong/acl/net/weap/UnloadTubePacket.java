package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class UnloadTubePacket extends BaseArtemisPacket {

    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    public UnloadTubePacket(int tube) {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(7, mData);
        PacketParser.putLendInt(tube, mData, 4);
    }
}
