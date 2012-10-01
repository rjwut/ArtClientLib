package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;


/**
 * Set the name, drive, and type of ship you want 
 *   
 * @author dhleong
 *
 */
public class SetShipSettingsPacket extends BaseArtemisPacket {
    
    
    public enum DriveType {
        WARP,
        JUMP
    };

    private static final int FLAGS = 0x26;
    private static final int TYPE = 0x4C821D3C;

    public SetShipSettingsPacket(DriveType drive, int shipHullId, String name) {
        super(0x2, FLAGS, TYPE, new byte[12 + 4 + name.length()*2 + 2]);
        
        PacketParser.putLendInt(0x13, mData, 0);
        PacketParser.putLendInt(drive.ordinal(), mData, 4);
        PacketParser.putLendInt(shipHullId, mData, 8);
        PacketParser.putNameString(name, mData, 12);
    }
}
