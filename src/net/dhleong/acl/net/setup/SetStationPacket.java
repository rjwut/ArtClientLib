package net.dhleong.acl.net.setup;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;

/**
 * "Take" or "Untake" a station
 * 
 * @author dhleong
 *
 */
public class SetStationPacket extends BaseArtemisPacket {

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
    public static final int TYPE = 0x4C821D3C; 

    public SetStationPacket(StationType station, boolean isSelected) {
        super(0x02, FLAGS, TYPE, new byte[12]);
        
        PacketParser.putLendInt(0x0c, mData, 0); // ?
        PacketParser.putLendInt(station.ordinal(), mData, 4);
        PacketParser.putLendInt(isSelected ? 1 : 0, mData, 8);
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
