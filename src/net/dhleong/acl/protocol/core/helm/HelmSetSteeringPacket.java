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
 * Set steering amount. Just like the actual station, you need to send one
 * packet to start turning, then another to reset the steering angle to stop
 * turning.
 * @author dhleong
 */
public class HelmSetSteeringPacket extends BaseArtemisPacket {
	private static final int TYPE = 0x0351A5AC;
    private static final int SUBTYPE = 0x01;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return HelmSetSteeringPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new HelmSetSteeringPacket(reader);
			}
		});
	}

    private float mSteering;

    /**
     * @param steering float in [0, 1], where 0.5 is "centered" (no turning),
     * 0.0 is left (hard to port), 1.0 is right (hard to starboard)
     */
    public HelmSetSteeringPacket(float steering) {
        super(ConnectionType.CLIENT, TYPE);

        if (steering < 0 || steering > 1) {
        	throw new IllegalArgumentException("Steering out of range");
        }
    }

    private HelmSetSteeringPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
    	int subtype = reader.readInt();

    	if (subtype != SUBTYPE) {
        	throw new UnexpectedTypeException(subtype, SUBTYPE);
    	}

    	mSteering = reader.readFloat();
    }

	@Override
	protected void writePayload(PacketWriter writer) {
    	writer.writeInt(SUBTYPE).writeFloat(mSteering);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append(mSteering);
	}
}