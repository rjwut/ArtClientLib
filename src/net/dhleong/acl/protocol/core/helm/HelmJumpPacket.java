package net.dhleong.acl.protocol.core.helm;

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
 * Initiate a jump. There is no confirmation; that's all client-side.
 * @author dhleong
 */
public class HelmJumpPacket extends BaseArtemisPacket {
    private static final int TYPE = 0x0351A5AC;
    private static final byte SUBTYPE = 0x05;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, SUBTYPE,
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return HelmJumpPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new HelmJumpPacket(reader);
			}
		});
	}

    private float mHeading;
    private float mDistance;

    /**
     * Initiates a jump for the indicated direction and distance.
     * @param heading Heading as a percentage of 360
     * @param distance Distance as a percentage of the max possible jump
     * 		distance, 50K
     */
    public HelmJumpPacket(float heading, float distance) {
        super(ConnectionType.CLIENT, TYPE);

        if (heading < 0 || heading > 1) {
        	throw new IllegalArgumentException("Heading out of range");
        }

        if (distance < 0 || distance > 1) {
        	throw new IllegalArgumentException("Distance out of range");
        }
        
        mHeading = heading;
        mDistance = distance;
    }

    private HelmJumpPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
        int subtype = reader.readInt();

        if (subtype != SUBTYPE) {
        	throw new UnexpectedTypeException(subtype, SUBTYPE);
        }

        mHeading = reader.readFloat();
        mDistance = reader.readFloat();
    }

	@Override
	protected void writePayload(PacketWriter writer) {
    	writer	.writeInt(SUBTYPE)
				.writeFloat(mHeading)
				.writeFloat(mDistance);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append("heading = ").append(mHeading * 360)
		.append(" deg; distance = ").append(mDistance * 50).append('k');
	}
}
