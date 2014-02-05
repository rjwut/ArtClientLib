package net.dhleong.acl.protocol.core.weap;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.protocol.UnexpectedTypeException;

/**
 * Converts a homing torpedo to energy or vice-versa.
 */
public class ConvertTorpedoPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x69CC01D9;
    private static final int SUBTYPE = 0x03;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return ConvertTorpedoPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new ConvertTorpedoPacket(reader);
			}
		});
	}

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

    private ConvertTorpedoPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
        int subtype = reader.readInt();

        if (subtype != SUBTYPE) {
        	throw new UnexpectedTypeException(subtype, SUBTYPE);
        }

        mDirection = Direction.values()[reader.readInt()];
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(SUBTYPE).writeInt(mDirection.ordinal());
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mDirection);
	}
}