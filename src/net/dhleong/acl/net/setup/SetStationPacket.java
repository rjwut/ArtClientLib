package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.PacketParser;
import net.dhleong.acl.net.ShipActionPacket;

/**
 * "Take" or "Untake" a station
 * 
 * @author dhleong
 *
 */
public class SetStationPacket extends ShipActionPacket {

    public enum StationType {
        MAINSCREEN,
        HELM,
        WEAPONS,
        ENGINEERING,
        SCIENCE,
        COMMS,
        OBSERVER,
        CAPTAINS_MAP,
        GAME_MASTER;
        
        /** Fancier valueOf; ignores case, replaces space with underscore */
        public static StationType fromString(String name) {
            return valueOf(name.replace(' ', '_').toUpperCase());
        }
    }
    
    private static final int FLAGS = 0x10;
    public SetStationPacket(StationType station, boolean isSelected) {
        super(FLAGS, TYPE_SET_STATION, new byte[12]);
        
        PacketParser.putLendInt(station.ordinal(), mData, 4);
        PacketParser.putLendInt(isSelected ? 1 : 0, mData, 8);
    }
}
