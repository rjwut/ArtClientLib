package net.dhleong.acl.net.eng;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;


/**
 * Set whether engineering should use
 *  autonomous Damcon teams or not
 * 
 * @author dhleong
 *
 */
public class EngSetAutoDamconPacket extends BaseArtemisPacket {
    public static final int TYPE = 0x4C821D3C;
    private static final int FLAGS = 0x0c;
    
    public EngSetAutoDamconPacket(boolean useAutonomous) {
        super(0x02, FLAGS, TYPE, new byte[8]);
        
        PacketParser.putLendInt(0x0A, mData);
        PacketParser.putLendInt(useAutonomous ? 0x01 : 0x00, mData, 4);
    }
}
