package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.ShipActionPacket;


/**
 * Set the ship you want to be on (BEFORE SetStationPacket). 
 *   
 * @author dhleong
 *
 */
public class SetShipPacket extends ShipActionPacket {
    public static final int TOTAL_SHIPS = 6;
    public static final int SHIP_1_ARTEMIS  = 0;
    public static final int SHIP_2_INTREPID = 1;
    public static final int SHIP_3_AEGIS    = 2;
    public static final int SHIP_4_HORATIO  = 3;
    public static final int SHIP_5_EXCALIBUR= 4;
    public static final int SHIP_6_HERA     = 5;
    
    /**
     * 
     * @param ship Index, from zero, of the ship
     *  you want to be on. The SHIP_* constants
     *  are provided for reference, but the names
     *  can, of course, be changed 
     */
    public SetShipPacket(int ship) {
        super(TYPE_SET_SHIP, ship);
    }
}