package net.dhleong.acl.net.weap;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Load a type of torpedo into a tube
 * @author dhleong
 *
 */
public class LoadTubePacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    
    /**
     * @param tube Indexed from 0
     * @param torpedoType one of the TORP_* constants
     */
    public LoadTubePacket(int tube, OrdnanceType ordnanceType) {
        super(ConnectionType.CLIENT, TYPE, new byte[20]);
        PacketParser.putLendInt(2, mData);
        PacketParser.putLendInt(tube, mData, 4);
        PacketParser.putLendInt(ordnanceType.ordinal(), mData, 8);
    }
}