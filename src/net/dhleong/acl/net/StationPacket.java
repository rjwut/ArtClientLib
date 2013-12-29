package net.dhleong.acl.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.world.ArtemisPositionable;
import net.dhleong.acl.world.ArtemisStation;

public class StationPacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	private enum Bit {
		NAME,
		FORE_SHIELDS,
		AFT_SHIELDS,
		INDEX,
		UNK_1,
		X,
		Y,
		Z,

		UNK_2,
		UNK_3,
		UNK_4,
		UNK_5,
		UNK_6,
		UNK_7
	}

    private final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();

    public StationPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        String name;
        int index;
        float x, y, z;
        float shieldsFront, shieldsRear;

        while (reader.hasMore()) {
            reader.startObject(Bit.values());
            
            try {
                name = reader.readString(Bit.NAME);
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("DEBUG: Packet = " + this);
                throw e;
            }

            shieldsFront = reader.readFloat(Bit.FORE_SHIELDS, -1);
            shieldsRear = reader.readFloat(Bit.AFT_SHIELDS, -1);

            index = reader.readInt(Bit.INDEX, 4);
            reader.readObjectUnknown(Bit.UNK_1, 4); // hull ID?

            x = reader.readFloat(Bit.X, -1);
            y = reader.readFloat(Bit.Y, -1);
            z = reader.readFloat(Bit.Z, -1);

            reader.readObjectUnknown(Bit.UNK_2, 4);
            reader.readObjectUnknown(Bit.UNK_3, 4);
            reader.readObjectUnknown(Bit.UNK_4, 4);
            reader.readObjectUnknown(Bit.UNK_5, 4);
            reader.readObjectUnknown(Bit.UNK_6, 1);
            reader.readObjectUnknown(Bit.UNK_7, 1);
            
            ArtemisStation station = new ArtemisStation(reader.getObjectId(), name);
            station.setIndex(index);
            station.setX(x);
            station.setY(y);
            station.setZ(z);
            station.setShieldsFront(shieldsFront);
            station.setShieldsRear(shieldsRear);
            station.setUnknownFields(reader.getUnknownObjectFields());
            mObjects.add(station);
        }
    }

    public List<ArtemisPositionable> getCreatedObjects() {
        return mObjects;
    }

    @Override
    public void write(PacketWriter writer) throws IOException {
    	throw new UnsupportedOperationException(
    			getClass().getSimpleName() + " does not support write()"
    	);
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisPositionable obj : mObjects) {
			b.append("\nStation #").append(obj.getId()).append(obj);
		}
	}

    @Override
    public List<ArtemisPositionable> getObjects() {
        return mObjects;
    }
}