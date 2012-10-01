package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * Used to set what engines, ship types, and ship names
 *  should be used
 *  
 * @author dhleong
 *
 */
public class ShipSettingsPacket extends BaseArtemisPacket {
    
    private static final int FLAGS = 0xac;
    private static final int TYPE = 0xf754c8fe;

    private ShipSettingsPacket(int len) {
        super(0x02, FLAGS, TYPE, new byte[len]);
    }

    /**
     * 
     * @param drives array[6] of ints; 0 = warp, 1 = jump
     * @param shipTypes IE: hullId of the ship
     * @param shipNames name of the ship
     * @return
     */
    public static ShipSettingsPacket newInstance(int[] drives, int[] shipTypes, 
            String...shipNames) {
        int len = 4 + SetShipPacket.TOTAL_SHIPS * 3 * 4; // header, 3 ints per ship
        
        for (String name : shipNames)
            len += name.length() * 2 + 2; // 2 bytes each char, plus null bytes
        
        ShipSettingsPacket pkt = new ShipSettingsPacket(len);
        PacketParser.putLendInt(0x0f, pkt.mData);
        
        int offset = 4;
        for (int i=0; i<SetShipPacket.TOTAL_SHIPS; i++) {
            PacketParser.putLendInt(drives[i], pkt.mData, offset);
            PacketParser.putLendInt(shipTypes[i], pkt.mData, offset+4);
            PacketParser.putNameString(shipNames[0], pkt.mData, offset+8);
            
            offset += 12 + 2 * shipNames[i].length() + 2;
        }
        
        return pkt;
    }
}
