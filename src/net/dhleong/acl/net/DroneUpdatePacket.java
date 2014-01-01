package net.dhleong.acl.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.world.ArtemisDrone;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Status updates for Torgoth drones.
 * @author rjwut
 */
public class DroneUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
    private enum Bit {
    	UNK_0,
    	X,
    	UNK_2,
    	Z,
    	UNK_4,
    	Y,
    	HEADING,
    	UNK_7,

    	UNK_8,
    	UNK_9,
    	UNK_10,
    	UNK_11,
    	UNK_12,
    	UNK_13,
    	UNK_14,
    	UNK_15
    }

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    public DroneUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);

    	try {
            float x, y, z, bearing;
            
            while (reader.hasMore() && reader.peekByte() == ObjectType.DRONE.getId()) {
                reader.startObject(Bit.values());
            	reader.readObjectUnknown(Bit.UNK_0, 4);
            	x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            	reader.readObjectUnknown(Bit.UNK_2, 4);
            	z = reader.readFloat(Bit.Z, Float.MIN_VALUE);
            	reader.readObjectUnknown(Bit.UNK_4, 4);
            	y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            	bearing = reader.readFloat(Bit.HEADING, Float.MIN_VALUE);
            	reader.readObjectUnknown(Bit.UNK_7, 4);
            	reader.readObjectUnknown(Bit.UNK_8, 4);
            	reader.readObjectUnknown(Bit.UNK_9, 4);
            	reader.readObjectUnknown(Bit.UNK_10, 4);
            	reader.readObjectUnknown(Bit.UNK_11, 4);
            	reader.readObjectUnknown(Bit.UNK_12, 4);
            	reader.readObjectUnknown(Bit.UNK_13, 4);
            	reader.readObjectUnknown(Bit.UNK_14, 4);
            	reader.readObjectUnknown(Bit.UNK_15, 4);
                final ArtemisDrone obj = new ArtemisDrone(reader.getObjectId());
                obj.setX(x);
                obj.setY(y);
                obj.setZ(z);
                obj.setBearing(bearing);
                obj.setUnknownProps(reader.getUnknownObjectProps());
                mObjects.add(obj);
            }
        } catch (RuntimeException e) {
            System.out.println("--> " + this);
            throw e;
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