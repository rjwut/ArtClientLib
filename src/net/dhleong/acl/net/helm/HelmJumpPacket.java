package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Initiate a jump. There is no confirm or anything...
 *  that's all client-side
 * 
 * @author dhleong
 *
 */
public class HelmJumpPacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    
    private static final int FLAGS = 0x10;
    
    /**
     * 
     * @param bearing Bearing as a percentage of 360
     * @param distance Distance as a percentage of the
     *  max possible jump distance, 50K
     */
    public HelmJumpPacket(float bearing, float distance) {
        super(0x02, FLAGS, TYPE, new byte[12]);
        
        PacketParser.putLendInt(1, mData);
        PacketParser.putLendFloat(bearing, mData, 4);
        PacketParser.putLendFloat(distance, mData, 8);
    }
}
