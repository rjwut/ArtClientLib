package net.dhleong.acl.protocol.core.world;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Sent by the server when an object is destroyed.
 */
public class DestroyObjectPacket extends BaseArtemisPacket {
    private static final int TYPE = 0xcc5a3e30;
    
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, TYPE, new PacketFactory() {
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
    	this(ObjectType.fromId(reader.readByte()), reader.readInt());
    }

    public DestroyObjectPacket(ArtemisObject obj) {
    	this(obj.getType(), obj.getId());
    }

    public DestroyObjectPacket(ObjectType targetType, int id) {
        super(ConnectionType.SERVER, TYPE);
        mTargetType = targetType;
        mTarget = id;
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
	protected void writePayload(PacketWriter writer) {
		writer.writeByte(mTargetType.getId()).writeInt(mTarget);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		b.append('#').append(mTarget).append(" (").append(mTargetType).append(')');
	}
}