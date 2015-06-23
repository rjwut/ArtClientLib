package net.dhleong.acl.protocol.core.weap;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Manually fire beams.
 */
public class FireBeamPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xc2bee72e;

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.CLIENT, TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return FireBeamPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new FireBeamPacket(reader);
			}
		});
	}

	private int mId;
	private float mX;
	private float mY;
	private float mZ;

    /**
     * Fire at the given target. The x, y and z parameters indicate the
     * coordinates on the target at which to fire.
     */
    public FireBeamPacket(ArtemisObject target, float x, float y, float z) {
        super(ConnectionType.CLIENT, TYPE);
        mId = target.getId();
        mX = x;
        mY = y;
        mZ = z;
    }

    private FireBeamPacket(PacketReader reader) {
        super(ConnectionType.CLIENT, TYPE);
        mId = reader.readInt();
        mX = reader.readFloat();
        mY = reader.readFloat();
        mZ = reader.readFloat();
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		writer.writeInt(mId).writeFloat(mX).writeFloat(mY).writeFloat(mZ);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#')
		.append(mId)
		.append(": ")
		.append(mX)
		.append(',')
		.append(mY)
		.append(',')
		.append(mZ);
	}
}