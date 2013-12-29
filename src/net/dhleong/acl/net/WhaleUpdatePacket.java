package net.dhleong.acl.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.world.ArtemisCreature;
import net.dhleong.acl.world.ArtemisPositionable;

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

    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();

    public WhaleUpdatePacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);

    	try {
            float x, y, z, bearing;
            String name;
            
            while (reader.hasMore()) {
                reader.startObject(Bit.values());
                name = reader.readString(Bit.NAME);

                reader.readObjectUnknown(Bit.UNK_0, 4);
                reader.readObjectUnknown(Bit.UNK_1, 4);
                
                x = reader.readFloat(Bit.X, -1);
                y = reader.readFloat(Bit.Y, -1);
                z = reader.readFloat(Bit.Z, -1);
                
                reader.readObjectUnknown(Bit.UNK_2, 4);
                reader.readObjectUnknown(Bit.UNK_3, 4);

                bearing = reader.readFloat(Bit.HEADING, Float.MIN_VALUE);

                reader.readObjectUnknown(Bit.UNK_4, 4);
                reader.readObjectUnknown(Bit.UNK_5, 4);
                reader.readObjectUnknown(Bit.UNK_6, 4);
                reader.readObjectUnknown(Bit.UNK_7, 4);
                
                final ArtemisCreature obj = new ArtemisCreature(
                        reader.getObjectId(), name, reader.getObjectType());
                obj.setX(x);
                obj.setY(y);
                obj.setZ(z);
                obj.setBearing(bearing);
                obj.setUnknownFields(reader.getUnknownObjectFields());
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
    public List<ArtemisPositionable> getObjects() {
        return mObjects;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisPositionable obj : mObjects) {
			b.append("\nObject #").append(obj.getId()).append(obj);
		}
	}
}