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
import net.dhleong.acl.world.ArtemisWhale;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Updates for space whales.
 * @author rjwut
 */
public class WhaleUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, WORLD_TYPE,
				ObjectType.WHALE.getId(), new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return WhaleUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new WhaleUpdatePacket(reader);
			}
		});
	}

    private enum Bit {
    	NAME,
    	UNK_1_2,
    	UNK_1_3,
    	X,
    	Y,
    	Z,
    	PITCH,
    	ROLL,

    	HEADING,
    	UNK_2_2,
    	UNK_2_3,
    	UNK_2_4,
    	UNK_2_5
    }

    private List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    private WhaleUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        
        while (reader.hasMore()) {
            float x, y, z, heading, pitch, roll;
            String name;

            reader.startObject(Bit.values());
            name = reader.readString(Bit.NAME);

            reader.readObjectUnknown(Bit.UNK_1_2, 4);
            reader.readObjectUnknown(Bit.UNK_1_3, 4);

            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);
            pitch = reader.readFloat(Bit.PITCH, Float.MIN_VALUE);
            roll = reader.readFloat(Bit.ROLL, Float.MIN_VALUE);
            heading = reader.readFloat(Bit.HEADING, Float.MIN_VALUE);

            reader.readObjectUnknown(Bit.UNK_2_2, 4);
            reader.readObjectUnknown(Bit.UNK_2_3, 4);
            reader.readObjectUnknown(Bit.UNK_2_4, 4);
            reader.readObjectUnknown(Bit.UNK_2_5, 4);
            
            final ArtemisWhale obj = new ArtemisWhale(reader.getObjectId(),
            		name);
            obj.setX(x);
            obj.setY(y);
            obj.setZ(z);
            obj.setHeading(heading);
            obj.setPitch(pitch);
            obj.setRoll(roll);
            obj.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(obj);
        }
    }

	@Override
	protected void writePayload(PacketWriter writer) {
		for (ArtemisObject obj : mObjects) {
			ArtemisWhale whale = (ArtemisWhale) obj;
			writer	.startObject(whale, Bit.values())
					.writeString(Bit.NAME, whale.getName())
					.writeUnknown(Bit.UNK_1_2)
					.writeUnknown(Bit.UNK_1_3)
					.writeFloat(Bit.X, whale.getX(), Float.MIN_VALUE)
					.writeFloat(Bit.Y, whale.getY(), Float.MIN_VALUE)
					.writeFloat(Bit.Z, whale.getZ(), Float.MIN_VALUE)
					.writeFloat(Bit.PITCH, whale.getPitch(), Float.MIN_VALUE)
					.writeFloat(Bit.ROLL, whale.getRoll(), Float.MIN_VALUE)
					.writeFloat(Bit.HEADING, whale.getHeading(), Float.MIN_VALUE)
					.writeUnknown(Bit.UNK_2_2)
					.writeUnknown(Bit.UNK_2_3)
					.writeUnknown(Bit.UNK_2_4)
					.writeUnknown(Bit.UNK_2_5);
		}

		writer.writeInt(0);
	}

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }

    public void setObject(List<ArtemisObject> objects) {
    	mObjects = objects;
    }

    @Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\nObject #").append(obj.getId()).append(obj);
		}
	}
}