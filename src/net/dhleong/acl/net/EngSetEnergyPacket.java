package net.dhleong.acl.net;


public class EngSetEnergyPacket extends BaseArtemisPacket {

    public enum SystemType {
        BEAMS,
        TORPEDOS,
        SENSORS,
        MANEUVER,
        IMPULSE,
        /** AKA warp */
        JUMP, 
        SHIELD_FRONT,
        SHIELD_REAR
    }

    private static final int FLAGS = 0x10;
    private static final int TYPE = 0x0351A5AC;

    public EngSetEnergyPacket(SystemType system, float value) {
        super(0x2, FLAGS, TYPE, new byte[12]);
        
        PacketParser.putLendInt(0x03, mData, 0); // ?
        PacketParser.putLendFloat(value, mData, 4);
        PacketParser.putLendInt(system.ordinal(), mData, 8);
    }
    
    public EngSetEnergyPacket(SystemType system, int percentage) {
        this(system, percentage / 300f);
    }
}
