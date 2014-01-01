package net.dhleong.acl.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.world.ArtemisGenericObject;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Update/create ArtemisGenericObjects
 * @author dhleong
 */
public class GenericUpdatePacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	private enum Bit {
    	X,
    	Y,
    	Z,
    	NAME,
    	UNK_0,
    	UNK_1,
    	UNK_2,
    	UNK_3
    }

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    public GenericUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);

    	try {
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

                reader.readObjectUnknown(Bit.UNK_0, 4);
                reader.readObjectUnknown(Bit.UNK_1, 4);
                reader.readObjectUnknown(Bit.UNK_2, 4);
                reader.readObjectUnknown(Bit.UNK_3, 4);
                
                final ArtemisGenericObject obj = new ArtemisGenericObject(
                		reader.getObjectId(), name, type);
                obj.setX(x);
                obj.setY(y);
                obj.setZ(z);
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