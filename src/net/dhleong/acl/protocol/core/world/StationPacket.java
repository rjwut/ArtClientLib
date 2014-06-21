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
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisStation;

/**
 * Provides updates for space stations.
 */
public class StationPacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, WORLD_TYPE,
				ObjectType.SPACE_STATION.getId(), new PacketFactory() {
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
		UNK_1_5,
		X,
		Y,
		Z,

		UNK_2_1,
		UNK_2_2,
		UNK_2_3,
		UNK_2_4,
		UNK_2_5,
		UNK_2_6
	}

    private List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();

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

            index = reader.readInt(Bit.INDEX, -1);
            reader.readObjectUnknown(Bit.UNK_1_5, 4); // hull ID?

            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);

            reader.readObjectUnknown(Bit.UNK_2_1, 4);
            reader.readObjectUnknown(Bit.UNK_2_2, 4);
            reader.readObjectUnknown(Bit.UNK_2_3, 4);
            reader.readObjectUnknown(Bit.UNK_2_4, 4);
            reader.readObjectUnknown(Bit.UNK_2_5, 1);
            reader.readObjectUnknown(Bit.UNK_2_6, 1);
            
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
	protected void writePayload(PacketWriter writer) {
		Bit[] bits = Bit.values();

		for (ArtemisObject obj : mObjects) {
			ArtemisStation station = (ArtemisStation) obj;
			writer	.startObject(station, bits)
					.writeString(Bit.NAME, station.getName())
					.writeFloat(Bit.FORE_SHIELDS, station.getShieldsFront(), Float.MIN_VALUE)
					.writeFloat(Bit.AFT_SHIELDS, station.getShieldsRear(), Float.MIN_VALUE)
					.writeInt(Bit.INDEX, station.getIndex(), -1)
					.writeUnknown(Bit.UNK_1_5)
					.writeFloat(Bit.X, station.getX(), Float.MIN_VALUE)
					.writeFloat(Bit.Y, station.getY(), Float.MIN_VALUE)
					.writeFloat(Bit.Z, station.getZ(), Float.MIN_VALUE)
					.writeUnknown(Bit.UNK_2_1)
					.writeUnknown(Bit.UNK_2_2)
					.writeUnknown(Bit.UNK_2_3)
					.writeUnknown(Bit.UNK_2_4)
					.writeUnknown(Bit.UNK_2_5)
					.writeUnknown(Bit.UNK_2_6);
		}

		writer.writeInt(0);
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

    public void setObjects(List<ArtemisObject> objects) {
        mObjects = objects;
    }
}