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
import net.dhleong.acl.world.ArtemisNebula;
import net.dhleong.acl.world.ArtemisObject;

public class NebulaUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	public static void register(PacketFactoryRegistry registry) {
		PacketFactory factory = new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return NebulaUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new NebulaUpdatePacket(reader);
			}
		};

		registry.register(ConnectionType.SERVER, WORLD_TYPE,
				ObjectType.NEBULA.getId(), factory);
	}

	private enum Bit {
    	X,
    	Y,
    	Z,
    	RED,
    	GREEN,
    	BLUE,
    	UNK_1_7,
    	UNK_1_8
    }

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    private NebulaUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        float x, y, z;
        float r, g, b;

        while (reader.hasMore()) {
            reader.startObject(Bit.values());
            ObjectType type = reader.getObjectType();

        	if (type == ObjectType.TORPEDO) {
                reader.readObjectUnknown("UNK_TORPEDO", 1);
            }

            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);
            r = reader.readFloat(Bit.RED, Float.MIN_VALUE);
            g = reader.readFloat(Bit.GREEN, Float.MIN_VALUE);
            b = reader.readFloat(Bit.BLUE, Float.MIN_VALUE);
            reader.readObjectUnknown(Bit.UNK_1_7, 4);
            reader.readObjectUnknown(Bit.UNK_1_8, 4);

            final ArtemisNebula obj = new ArtemisNebula(reader.getObjectId());
            obj.setX(x);
            obj.setY(y);
            obj.setZ(z);
            obj.setARGB(0, r, g, b);
            obj.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(obj);
        }

    	reader.skip(4);	// skip 0x00 terminator
    }

	@Override
	public List<ArtemisObject> getObjects() {
		return mObjects;
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		Bit[] bits = Bit.values();

		for (ArtemisObject obj : mObjects) {
			ArtemisNebula nObj = (ArtemisNebula) obj;
			writer.startObject(obj, bits);

        	writer	.writeFloat(Bit.X, nObj.getX(), Float.MIN_VALUE)
					.writeFloat(Bit.Y, nObj.getY(), Float.MIN_VALUE)
					.writeFloat(Bit.Z, nObj.getZ(), Float.MIN_VALUE)
					.writeFloat(Bit.RED, nObj.getRed(), Float.MIN_VALUE)
					.writeFloat(Bit.GREEN, nObj.getGreen(), Float.MIN_VALUE)
					.writeFloat(Bit.BLUE, nObj.getBlue(), Float.MIN_VALUE)
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