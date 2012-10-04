package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

public class ConvertTorpedoPacket extends BaseArtemisPacket {
    
    /** IE: ENE TO TORP */
    public static final float FROM_ENERGY = 1f;
    
    /** IE: TORP TO ENE */
    public static final float TO_ENERGY   = 0f;
    
    public static final int FLAGS = 0x18;
    public static final int TYPE = 0x69CC01D9;
    
    /**
     * @param mode Either {@link #FROM_ENERGY} 
     *  or {@link #TO_ENERGY}
     */
    public ConvertTorpedoPacket(final float mode) {
        super(0x02, FLAGS, TYPE, new byte[20]);
        
        PacketParser.putLendInt(0x03, mData);
        PacketParser.putLendFloat(mode, mData, 4);
        
        // the rest are zero...?
    }
}
