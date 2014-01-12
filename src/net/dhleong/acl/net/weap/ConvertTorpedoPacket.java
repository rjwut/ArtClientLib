package net.dhleong.acl.net.weap;

import java.io.IOException;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.net.BaseArtemisPacket;
import net.dhleong.acl.net.PacketWriter;

/**
 * Converts a homing torpedo to energy or vice-versa.
 */
public class ConvertTorpedoPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    private static final int SUBTYPE = 0x03;

	public enum Direction {
		TORPEDO_TO_ENERGY, ENERGY_TO_TORPEDO
	}

    private Direction mDirection;

    /**
     * @param direction The Direction value indicating the desired conversion type
     */
    public ConvertTorpedoPacket(final Direction direction) {
        super(ConnectionType.CLIENT, TYPE);

        if (direction == null) {
        	throw new IllegalArgumentException("You must specify a direction");
        }

        mDirection = direction;
    }

	@Override
    public void write(PacketWriter writer) throws IOException {
    	writer	.start(TYPE)
    			.writeInt(TYPE)
    			.writeInt(SUBTYPE)
    			.writeInt(mDirection.ordinal());
    	// Old code indicated the payload supposed to be 20 bytes. Is this true?
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mDirection);
	}
}