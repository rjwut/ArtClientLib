package net.dhleong.acl.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;
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

	public static void register(PacketFactoryRegistry registry) {
		PacketFactory factory =  new PacketFactory() {
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
			registry.register(WORLD_TYPE, objType.getId(), factory);
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
                reader.readInt(Bit.NAME);
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
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	throw new UnsupportedOperationException(
    			getClass().getSimpleName() + " does not support write()"
    	);
    }

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\nObject #").append(obj.getId()).append(obj);
		}
	}
}