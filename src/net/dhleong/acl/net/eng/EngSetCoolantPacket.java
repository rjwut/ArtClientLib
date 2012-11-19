package net.dhleong.acl.net.eng;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;

/**
 * Set the amount of coolant in a system
 * 
 * @author dhleong
 *
 */
public class EngSetCoolantPacket extends BaseArtemisPacket {
    private static final int FLAGS = 0x18;
    private static final int TYPE = 0x69CC01D9;

    public EngSetCoolantPacket(SystemType system, int value) {
        super(0x2, FLAGS, TYPE, new byte[12]);
        
        PacketParser.putLendInt(0x00, mData, 0); // the command?
        PacketParser.putLendInt(system.ordinal(), mData, 4);
        PacketParser.putLendInt(value, mData, 8);
    }
}
