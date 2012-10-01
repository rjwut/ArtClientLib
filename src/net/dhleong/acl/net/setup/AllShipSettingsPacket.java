package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * From the server to share what engines, ship types, 
 *  and ship names will be used
 *  
 * TODO: Fix this for receiving
 *  
 * @author dhleong
 *
 */
public class AllShipSettingsPacket extends BaseArtemisPacket {
    
    private static final int FLAGS = 0xac;
    private static final int TYPE = 0xf754c8fe;

    private AllShipSettingsPacket(int len) {
        super(0x01, FLAGS, TYPE, new byte[len]);
    }

    /**
     * 
     * @param drives array[6] of ints; 0 = warp, 1 = jump
     * @param shipTypes IE: hullId of the ship
     * @param shipNames name of the ship
     * @return
     */
    public static AllShipSettingsPacket newInstance(int[] drives, int[] shipTypes, 
            String...shipNames) {
        int len = 4 + SetShipPacket.TOTAL_SHIPS * 3 * 4; // header, 3 ints per ship
        
        for (String name : shipNames)
            len += name.length() * 2 + 2; // 2 bytes each char, plus null bytes
        
        AllShipSettingsPacket pkt = new AllShipSettingsPacket(len);
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
