package net.dhleong.acl.net;

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

    public DroneUpdatePacket(PacketReader reader) {
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
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\nObject #").append(obj.getId()).append(obj);
		}
	}
}