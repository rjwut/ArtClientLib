package net.dhleong.acl.protocol.core.world;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.iface.PacketFactory;
import net.dhleong.acl.iface.PacketFactoryRegistry;
import net.dhleong.acl.iface.PacketReader;
import net.dhleong.acl.iface.PacketWriter;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.ArtemisPacketException;
import net.dhleong.acl.protocol.BaseArtemisPacket;
import net.dhleong.acl.world.ArtemisGenericObject;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Update/create ArtemisGenericObjects
 * @author dhleong
 */
public class GenericUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	private static ObjectType[] GENERIC_TYPES = {
		ObjectType.MINE, ObjectType.ANOMALY, ObjectType.NEBULA,
		ObjectType.TORPEDO, ObjectType.BLACK_HOLE, ObjectType.ASTEROID
	};

	private static final byte[] UNK_TORPEDO = { 0 };

	public static void register(PacketFactoryRegistry registry) {
		PacketFactory factory = new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return GenericUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new GenericUpdatePacket(reader);
			}
		};

		for (ObjectType objType : GENERIC_TYPES) {
			registry.register(ConnectionType.SERVER, WORLD_TYPE,
					objType.getId(), factory);
		}
	}

	private enum Bit {
    	X,
    	Y,
    	Z,
    	NAME,
    	UNK_1_5,
    	UNK_1_6,
    	UNK_1_7,
    	UNK_1_8
    }

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    private GenericUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        float x, y, z;
        String name;
        
        while (reader.hasMore()) {
            reader.startObject(Bit.values());
            ObjectType type = reader.getObjectType();

        	if (type == ObjectType.TORPEDO) {
                reader.readObjectUnknown("UNK_TORPEDO", 1);
            }

            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);

            if (type.isNamed()) {
                name = reader.readString(Bit.NAME);
            } else {
                name = null;
                reader.readObjectUnknown(Bit.NAME, 4);
            }

            reader.readObjectUnknown(Bit.UNK_1_5, 4);
            reader.readObjectUnknown(Bit.UNK_1_6, 4);
            reader.readObjectUnknown(Bit.UNK_1_7, 4);
            reader.readObjectUnknown(Bit.UNK_1_8, 4);

            final ArtemisGenericObject obj = new ArtemisGenericObject(
            		reader.getObjectId(), name, type);
            obj.setX(x);
            obj.setY(y);
            obj.setZ(z);
            obj.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(obj);
        }

    	reader.skip(4);	// skip 0x00 terminator
    }

    public GenericUpdatePacket() {
    	super(ConnectionType.SERVER, WORLD_TYPE);
    }

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		Bit[] bits = Bit.values();

		for (ArtemisObject obj : mObjects) {
			ArtemisGenericObject gObj = (ArtemisGenericObject) obj;
			ObjectType type = obj.getType();
			writer.startObject(obj, bits);

        	if (type == ObjectType.TORPEDO) {
                writer.writeUnknown("UNK_TORPEDO", UNK_TORPEDO);
            }

        	writer	.writeFloat(Bit.X, gObj.getX(), Float.MIN_VALUE)
					.writeFloat(Bit.Y, gObj.getY(), Float.MIN_VALUE)
					.writeFloat(Bit.Z, gObj.getZ(), Float.MIN_VALUE);

            if (obj.getType().isNamed()) {
            	writer.writeString(Bit.NAME, gObj.getName());
            } else {
            	writer.writeUnknown(Bit.NAME);
            }

            writer	.writeUnknown(Bit.UNK_1_5)
					.writeUnknown(Bit.UNK_1_6)
					.writeUnknown(Bit.UNK_1_7)
					.writeUnknown(Bit.UNK_1_8)
					.endObject();
		}

		writer.writeInt(0);
	}

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\nObject #").append(obj.getId()).append(obj);
		}
	}
}