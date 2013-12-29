package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.ShipActionPacket;


/**
 * Set the ship you want to be on. You must send this packet before
 * SetStationPacket.
 * @author dhleong
 */
public class SetShipPacket extends ShipActionPacket {
    public static final int SHIP_1_ARTEMIS  = 0;
    public static final int SHIP_2_INTREPID = 1;
    public static final int SHIP_3_AEGIS    = 2;
    public static final int SHIP_4_HORATIO  = 3;
    public static final int SHIP_5_EXCALIBUR= 4;
    public static final int SHIP_6_HERA     = 5;
    public static final int SHIP_7_CERES	= 6;
    public static final int SHIP_8_DIANA	= 7;
    
    /**
     * Set the ship you want to be on.
     * @param shipIndex Index [0,7] of the ship you want to be on. The SHIP_*
     * constants are provided for reference, but the names can, of course, be
     * changed.
     */
    public SetShipPacket(int shipIndex) {
        super(TYPE_SET_SHIP, shipIndex);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mArg);
	}
}