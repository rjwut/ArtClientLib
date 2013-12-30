package net.dhleong.acl.net.eng;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;

/**
 * Set the amount of coolant in a system.
 * @author dhleong
 */
public class EngSetCoolantPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    private static final int SUBTYPE = 0x00;

    private ShipSystem mSystem;
    private int mValue;

    /**
     * @param system The ShipSystem whose coolant level is to be set
     * @param value The amount of coolant to allocate
     */
    public EngSetCoolantPacket(ShipSystem system, int value) {
        super(ConnectionType.CLIENT, TYPE);

        if (system == null) {
        	throw new IllegalArgumentException("You must provide a system");
        }

        if (value < 0) {
        	throw new IllegalArgumentException(
        			"You cannot allocate a negative amount of coolant"
        	);
        }

        mSystem = system;
        mValue = value;
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(SUBTYPE)
    			.writeInt(mSystem.ordinal())
    			.writeInt(mValue);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mSystem).append(" = ").append(mValue);
	}
}