package net.dhleong.acl.net.eng;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketParser;


public class EngSetEnergyPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x0351A5AC;

    public EngSetEnergyPacket(ShipSystem system, float value) {
        super(ConnectionType.CLIENT, TYPE, new byte[12]);
        PacketParser.putLendInt(0x04, mData, 0); // set energy
        PacketParser.putLendFloat(value, mData, 4);
        PacketParser.putLendInt(system.ordinal(), mData, 8);
    }

    public EngSetEnergyPacket(ShipSystem system, int percentage) {
        this(system, percentage / 300f);
    }
}