package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class ClimbDivePacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    private static final int UP = -1;
    private static final int DOWN = 1;
    
    /**
     * @param up True if you want to tilt the ship up, false to tilt it down.
     */
    public ClimbDivePacket(boolean up) {
        super(0x02, TYPE, new byte[12]);
        PacketParser.putLendInt(0x1b, mData);
        PacketParser.putLendInt(up ? UP : DOWN, mData, 4);
    }
}