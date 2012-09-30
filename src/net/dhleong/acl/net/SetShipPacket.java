package net.dhleong.acl.net;


/**
 * Set the ship you want to be on (BEFORE SetStationPacket). 
 *   
 * @author dhleong
 *
 */
public class SetShipPacket extends BaseArtemisPacket {
    
    public static final int SHIP_1_ARTEMIS  = 0;
    public static final int SHIP_2_INTREPID = 1;
    public static final int SHIP_3_AEGIS    = 2;
    public static final int SHIP_4_HORATIO  = 3;
    public static final int SHIP_5_EXCALIBUR= 4;
    public static final int SHIP_6_HERA     = 5;
    
    private static final int FLAGS = 0x0c;
    private static final int TYPE = 0x4C821D3C;

    /**
     * 
     * @param ship Index, from zero, of the ship
     *  you want to be on. The SHIP_* constants
     *  are provided for reference, but the names
     *  can, of course, be changed 
     */
    public SetShipPacket(int ship) {
        super(0x2, FLAGS, TYPE, new byte[8]);
        
        // ??
        PacketParser.putLendInt(0x0b, mData, 0);
        PacketParser.putLendInt(ship, mData, 4);
    }
}
