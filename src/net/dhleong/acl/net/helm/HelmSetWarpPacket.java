package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Set warp speed
 * 
 * @author dhleong
 *
 */
public class HelmSetWarpPacket extends BaseArtemisPacket {
    
    public static final int TYPE = 0x4C821D3C;
    
    private static final int FLAGS = 0x0c;
    
    public HelmSetWarpPacket(int warp) {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(0, mData);
        PacketParser.putLendInt(warp, mData, 4);
    }
}
