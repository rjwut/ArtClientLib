package net.dhleong.acl.net;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.world.ArtemisWhale;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Updates for space whales.
 * @author rjwut
 */
public class WhaleUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
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

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    public WhaleUpdatePacket(PacketReader reader) {
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