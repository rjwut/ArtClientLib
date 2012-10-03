package net.dhleong.acl.net.weap;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Load a type of torpedo into a tube
 * @author dhleong
 *
 */
public class LoadTubePacket extends BaseArtemisPacket {
    
    public static final int TORP_HOMING = 0;
    public static final int TORP_NUKE = 1;
    public static final int TORP_MINE = 2;
    public static final int TORP_ECM = 3;
    
    private static final int FLAGS = 0x18;
    private static final int TYPE = 0x69CC01D9;

    /**
     * 
     * @param tube Indexed from 0
     * @param torpedoType one of the TORP_* constants
     */
    public LoadTubePacket(int tube, int torpedoType) {
        super(0x02, FLAGS, TYPE, new byte[20]);
        
        PacketParser.putLendInt(2, mData);
        PacketParser.putLendInt(tube, mData, 4);
        PacketParser.putLendInt(torpedoType, mData, 8);
    }
}
