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
import net.dhleong.acl.world.ArtemisDrone;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Status updates for Torgoth drones.
 * @author rjwut
 */
public class DroneUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
    private static final byte[] ZERO_ARR = { (byte) 0 };

	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, WORLD_TYPE,
				ObjectType.DRONE.getId(),
				new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return DroneUpdatePacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new DroneUpdatePacket(reader);
			}
		});
	}

    private enum Bit {
    	UNK_1_1,
    	X,
    	UNK_1_3,
    	Z,
    	UNK_1_5,
    	Y,
    	HEADING,
    	UNK_1_8
    }

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    private DroneUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        float x, y, z, heading;
        
        while (reader.hasMore() && reader.peekByte() == ObjectType.DRONE.getId()) {
            reader.startObject(Bit.values());
            reader.readObjectUnknown("UNK", 1);
        	reader.readObjectUnknown(Bit.UNK_1_1, 4);
        	x = reader.readFloat(Bit.X, Float.MIN_VALUE);
        	reader.readObjectUnknown(Bit.UNK_1_3, 4);
        	z = reader.readFloat(Bit.Z, Float.MIN_VALUE);
        	reader.readObjectUnknown(Bit.UNK_1_5, 4);
        	y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
        	heading = reader.readFloat(Bit.HEADING, Float.MIN_VALUE);
        	reader.readObjectUnknown(Bit.UNK_1_8, 4);
            final ArtemisDrone obj = new ArtemisDrone(reader.getObjectId());
            obj.setX(x);
            obj.setY(y);
            obj.setZ(z);
            obj.setHeading(heading);
            obj.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(obj);
        }
    }

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }

    @Override
	protected void writePayload(PacketWriter writer) {
		Bit[] bits = Bit.values();

		for (ArtemisObject obj : mObjects) {
			ArtemisDrone drone = (ArtemisDrone) obj;
			writer	.startObject(obj, bits)
					.writeUnknown("UNK", ZERO_ARR)
					.writeUnknown(Bit.UNK_1_1)
					.writeFloat(Bit.X, drone.getX(), Float.MIN_VALUE)
					.writeUnknown(Bit.UNK_1_3)
					.writeFloat(Bit.Z, drone.getZ(), Float.MIN_VALUE)
					.writeUnknown(Bit.UNK_1_5)
					.writeFloat(Bit.Y, drone.getY(), Float.MIN_VALUE)
					.writeFloat(Bit.HEADING, drone.getHeading(), Float.MIN_VALUE)
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