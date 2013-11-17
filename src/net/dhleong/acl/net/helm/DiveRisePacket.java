package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class DiveRisePacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    
    private static final int FLAGS = 0x10;
    
    /**
     * @param up True if you want to tilt the ship up, false to tilt it down.
     */
    public DiveRisePacket(boolean up) {
        super(0x02, FLAGS, TYPE, new byte[12]);
        PacketParser.putLendInt(0x1b, mData);
        PacketParser.putLendInt(up ? 0x11111111 : 0x01, mData, 4);
    }
}