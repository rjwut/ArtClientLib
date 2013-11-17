package net.dhleong.acl.net.eng;

import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;


public class EngSetEnergyPacket extends BaseArtemisPacket {

    public enum SystemType {
        BEAMS("Beams"),
        TORPEDOS("Torpedos"),
        SENSORS("Sensors"),
        MANEUVER("Maneuvering"),
        IMPULSE("Impulse"),
        /** AKA warp */
        JUMP("Jump/Warp"), 
        SHIELD_FRONT("Front Shields"),
        SHIELD_REAR("Rear Shields");
        
        private String name;
        
        SystemType(String name) {
            this.name = name;
        }

        public String getReadableName() {
            return name;
        }

        public static final int count() {
            return values().length;
        }
    }

    private static final int FLAGS = 0x10;
    private static final int TYPE = 0x0351A5AC;

    public EngSetEnergyPacket(SystemType system, float value) {
        super(0x2, FLAGS, TYPE, new byte[12]);
        
        PacketParser.putLendInt(0x04, mData, 0); // set energy
        PacketParser.putLendFloat(value, mData, 4);
        PacketParser.putLendInt(system.ordinal(), mData, 8);
    }
    
    public EngSetEnergyPacket(SystemType system, int percentage) {
        this(system, percentage / 300f);
    }
}
