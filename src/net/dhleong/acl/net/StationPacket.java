package net.dhleong.acl.net;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.ArtemisPacketException;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.net.protocol.PacketFactory;
import net.dhleong.acl.net.protocol.PacketFactoryRegistry;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisStation;

/**
 * Provides updates for space stations.
 */
public class StationPacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(WORLD_TYPE, ObjectType.SPACE_STATION.getId(), new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return StationPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new StationPacket(reader);
			}
		});
	}

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

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

    private StationPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        String name;
        int index;
        float x, y, z;
        float shieldsFront, shieldsRear;

        while (reader.hasMore()) {
            reader.startObject(Bit.values());
            name = reader.readString(Bit.NAME);
            shieldsFront = reader.readFloat(Bit.FORE_SHIELDS, Float.MIN_VALUE);
            shieldsRear = reader.readFloat(Bit.AFT_SHIELDS, Float.MIN_VALUE);

            index = reader.readInt(Bit.INDEX, 4);
            reader.readObjectUnknown(Bit.UNK_1, 4); // hull ID?

            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);

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
            station.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(station);
        }
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\nStation #").append(obj.getId()).append(obj);
		}
	}

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }
}