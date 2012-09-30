package net.dhleong.acl.net.helm;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Set steering amount. Just like the actual station,
 *  you need to send one packet to start turning,
 *  then another to reset the steering angle to stop turning.
 * @author dhleong
 *
 */
public class HelmSetSteeringPacket extends BaseArtemisPacket {
    public static final int TYPE = 0x0351A5AC;
    
    private static final int FLAGS = 0x0c;
    
    /**
     * 
     * @param steering float in [0, 1], where .5 is "centered"
     *  (IE: no turning), 0 is left (port), 1 is right (starboard)
     */
    public HelmSetSteeringPacket(float steering) {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(1, mData);
        PacketParser.putLendFloat(steering, mData, 4);
    }
}
