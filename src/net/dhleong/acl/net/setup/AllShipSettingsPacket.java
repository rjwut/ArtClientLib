package net.dhleong.acl.net.setup;

import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.util.ObjectParser;

/**
 * From the server to share what engines, ship types, 
 *  and ship names will be used
 *  
 * @author dhleong
 *
 */
public class AllShipSettingsPacket extends BaseArtemisPacket {
    
    private static final int FLAGS = 0xac;
    
    public static final int TYPE = 0xf754c8fe;
    
    public static final byte MSG_TYPE = 0x0f;
    
    /* These values are only instantiated on recv */
    public final int[] drives, shipTypes;
    public final String[] shipNames;

    private AllShipSettingsPacket(int len) {
        super(0x01, FLAGS, TYPE, new byte[len]);
        
        drives = shipTypes = null;
        shipNames = null;
    }
    
    public AllShipSettingsPacket(byte[] bucket) throws ArtemisPacketException {
        super(0x01, FLAGS, TYPE, bucket);
        
        drives = new int[SetShipPacket.TOTAL_SHIPS];
        shipTypes = new int[SetShipPacket.TOTAL_SHIPS];
        shipNames = new String[SetShipPacket.TOTAL_SHIPS];
        
        ObjectParser p = new ObjectParser(bucket, 0);
        if (MSG_TYPE != p.readInt())
            throw new ArtemisPacketException("Packet subtype indicator must = 0x0f");
        
        for (int i=0; i<SetShipPacket.TOTAL_SHIPS; i++) {
            drives[i] = p.readInt();
            shipTypes[i] = p.readInt();
            p.readInt();	// RJW: UNKNOWN INT (always seems to be 1 0 0 0)
            // TODO Figure out what this int is.
            shipNames[i] = p.readName();
        }
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
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int i=0; i<SetShipPacket.TOTAL_SHIPS; i++) {
            b.append(String.format("[%d:%d:%s]", drives[i], shipTypes[i], shipNames[i]));
        }
        return b.toString();
    }
}
