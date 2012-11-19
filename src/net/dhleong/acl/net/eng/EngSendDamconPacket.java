package net.dhleong.acl.net.eng;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Send a Damcon team to a grid location
 * 
 * @author dhleong
 *
 */
public class EngSendDamconPacket extends BaseArtemisPacket {
    private static final int FLAGS = 0x18;
    private static final int TYPE = 0x69CC01D9;

    /**
     * Send the team to grid node at x,y,z
     * 
     * @param teamNumber int [0, TEAMS) where TEAMS is
     *  probably 3
     *  
     * @param x
     * @param y
     * @param z
     */
    public EngSendDamconPacket(int teamNumber, int x, int y, int z) {
        super(0x2, FLAGS, TYPE, new byte[20]);
        
        PacketParser.putLendInt(0x04, mData, 0); // the command?
        PacketParser.putLendInt(teamNumber, mData, 4);
        PacketParser.putLendInt(x, mData, 8);
        PacketParser.putLendInt(y, mData, 12);
        PacketParser.putLendInt(z, mData, 16);
    }
}
