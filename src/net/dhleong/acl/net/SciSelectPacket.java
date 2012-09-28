package net.dhleong.acl.net;

import net.dhleong.acl.world.ArtemisObject;

public class SciSelectPacket extends BaseArtemisPacket {

    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    public SciSelectPacket(ArtemisObject target) {
        super(0x2, FLAGS, TYPE, new byte[8]);
        
        // ??
        PacketParser.putLendInt(0x0e, mData, 0);
        PacketParser.putLendInt(target == null ? 1 : target.getId(), mData, 4);
    }

}
