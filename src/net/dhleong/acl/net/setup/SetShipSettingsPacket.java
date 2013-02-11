package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.ShipActionPacket;


/**
 * Set the name, drive, and type of ship you want 
 *   
 * @author dhleong
 *
 */
public class SetShipSettingsPacket extends ShipActionPacket {
    
    
    public enum DriveType {
        WARP,
        JUMP
    };

    private static final int FLAGS = 0x26;
    public SetShipSettingsPacket(DriveType drive, int shipHullId, String name) {
        this(drive.ordinal(), shipHullId, name);
    }

    public SetShipSettingsPacket(int drive, int shipHullId, String name) {
        super(FLAGS, TYPE_SHIP_SETUP, new byte[12 + PacketParser.getNameLengthBytes(name)]);
        
        PacketParser.putLendInt(drive, mData, 4);
        PacketParser.putLendInt(shipHullId, mData, 8);
        PacketParser.putNameString(name, mData, 12);
    }
}
