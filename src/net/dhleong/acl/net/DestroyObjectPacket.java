package net.dhleong.acl.net;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;

/**
 * Sent by the server when an object is destroyed.
 */
public class DestroyObjectPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xcc5a3e30;
    
	public static void register(PacketFactoryRegistry registry) {
		registry.register(TYPE, new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return DestroyObjectPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new DestroyObjectPacket(reader);
			}
		});
	}

    private final ObjectType mTargetType;
    private final int mTarget;

    private DestroyObjectPacket(PacketReader reader) {
        super(ConnectionType.SERVER, TYPE);
        mTargetType = ObjectType.fromId(reader.readByte());
        mTarget = reader.readInt();
    }

    /**
     * The ObjectType of the destroyed object
     */
    public ObjectType getTargetType() {
        return mTargetType;
    }

    /**
     * The destroyed object's ID
     */
    public int getTarget() {
        return mTarget;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mTarget).append(" (").append(mTargetType).append(')');
	}
}