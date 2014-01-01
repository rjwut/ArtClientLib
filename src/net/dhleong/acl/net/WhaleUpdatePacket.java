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
    	UNK_0,
    	UNK_1,
    	X,
    	Y,
    	Z,
    	UNK_2,
    	UNK_3,

    	HEADING,
    	UNK_4,
    	UNK_5,
    	UNK_6,
    	UNK_7
    }

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    public WhaleUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        float x, y, z, bearing;
        String name;
        
        while (reader.hasMore()) {
            reader.startObject(Bit.values());
            name = reader.readString(Bit.NAME);

            reader.readObjectUnknown(Bit.UNK_0, 4);
            reader.readObjectUnknown(Bit.UNK_1, 4);
            
            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);
            
            reader.readObjectUnknown(Bit.UNK_2, 4);
            reader.readObjectUnknown(Bit.UNK_3, 4);

            bearing = reader.readFloat(Bit.HEADING, Float.MIN_VALUE);

            reader.readObjectUnknown(Bit.UNK_4, 4);
            reader.readObjectUnknown(Bit.UNK_5, 4);
            reader.readObjectUnknown(Bit.UNK_6, 4);
            reader.readObjectUnknown(Bit.UNK_7, 4);
            
            final ArtemisWhale obj = new ArtemisWhale(reader.getObjectId(),
            		name);
            obj.setX(x);
            obj.setY(y);
            obj.setZ(z);
            obj.setBearing(bearing);
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