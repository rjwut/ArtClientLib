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
import net.dhleong.acl.world.ArtemisMesh;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Updates for generic mesh objects.
 * @author dhleong
 */
public class GenericMeshPacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	public static void register(PacketFactoryRegistry registry) {
		registry.register(ConnectionType.SERVER, WORLD_TYPE,
				ObjectType.GENERIC_MESH.getId(), new PacketFactory() {
			@Override
			public Class<? extends ArtemisPacket> getFactoryClass() {
				return GenericMeshPacket.class;
			}

			@Override
			public ArtemisPacket build(PacketReader reader)
					throws ArtemisPacketException {
				return new GenericMeshPacket(reader);
			}
		});
	}

	private enum Bit {
		X,
		Y,
		Z,
		UNK_1_4,
		UNK_1_5,
		UNK_1_6,
		UNK_1_7,
		UNK_1_8,

		UNK_2_1,
		UNK_2_2,
		NAME,
		MESH_PATH,
		TEXTURE_PATH,
		UNK_2_6,
		UNK_2_7,
		UNK_2_8,

		UNK_3_1,
		UNK_3_2,
		COLOR,
		FORE_SHIELDS,
		AFT_SHIELDS,
		UNK_3_6,
		UNK_3_7,
		UNK_3_8,

		UNK_4_1,
		UNK_4_2
	}

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();
    private float red, green, blue;

    private GenericMeshPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        float x, y, z;
        String name = null, mesh = null, texture = null;
        float shieldsFront, shieldsRear;

        while (reader.hasMore()) {
        	reader.startObject(Bit.values());
            
            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);

            reader.readObjectUnknown(Bit.UNK_1_4, 4);
            reader.readObjectUnknown(Bit.UNK_1_5, 4);
            reader.readObjectUnknown(Bit.UNK_1_6, 8);
            reader.readObjectUnknown(Bit.UNK_1_7, 4);
            reader.readObjectUnknown(Bit.UNK_1_8, 4);
            reader.readObjectUnknown(Bit.UNK_2_1, 4);
            reader.readObjectUnknown(Bit.UNK_2_2, 8);
            
            name = reader.readString(Bit.NAME);
            mesh = reader.readString(Bit.TEXTURE_PATH); // wtf?!
            texture = reader.readString(Bit.TEXTURE_PATH);
            
            reader.readObjectUnknown(Bit.UNK_2_6, 4);
            reader.readObjectUnknown(Bit.UNK_2_7, 2);
            reader.readObjectUnknown(Bit.UNK_2_8, 1);
            reader.readObjectUnknown(Bit.UNK_3_1, 1);
            reader.readObjectUnknown(Bit.UNK_3_2, 1);
            boolean hasColor = reader.has(Bit.COLOR);

            // color
            if (hasColor) {
                red = reader.readFloat();
                green = reader.readFloat();
                blue = reader.readFloat();
                hasColor = true;
            }

            shieldsFront = reader.readFloat(Bit.FORE_SHIELDS, Float.MIN_VALUE);
            shieldsRear  = reader.readFloat(Bit.AFT_SHIELDS, Float.MIN_VALUE);
            
            reader.readObjectUnknown(Bit.UNK_3_6, 1);
            reader.readObjectUnknown(Bit.UNK_3_7, 4);
            reader.readObjectUnknown(Bit.UNK_3_8, 4);
            reader.readObjectUnknown(Bit.UNK_4_1, 4);
            reader.readObjectUnknown(Bit.UNK_4_2, 4);

            final ArtemisMesh newObj = new ArtemisMesh(reader.getObjectId(), name);
            
            // shared updates
            newObj.setX(x);
            newObj.setY(y);
            newObj.setZ(z);
            newObj.setMesh(mesh);
            newObj.setTexture(texture);

            if (hasColor) {
            	newObj.setARGB(1.0f, red, green, blue);
            }

            newObj.setFakeShields(shieldsFront, shieldsRear);
            newObj.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(newObj);
        }
    }

    @Override
    public List<ArtemisObject> getObjects() {
        return mObjects;
    }

	@Override
	protected void appendPacketDetail(StringBuilder b) {
		for (ArtemisObject obj : mObjects) {
			b.append("\n").append(obj);
		}
	}

	@Override
	protected void writePayload(PacketWriter writer) {
		Bit[] bits = Bit.values();

		for (ArtemisObject obj : mObjects) {
			ArtemisMesh mesh = (ArtemisMesh) obj;
			writer	.startObject(obj, bits)
					.writeFloat(Bit.X, mesh.getX(), Float.MIN_VALUE)
					.writeFloat(Bit.Y, mesh.getY(), Float.MIN_VALUE)
					.writeFloat(Bit.Z, mesh.getZ(), Float.MIN_VALUE)
					.writeUnknown(Bit.UNK_1_4)
					.writeUnknown(Bit.UNK_1_5)
					.writeUnknown(Bit.UNK_1_6)
					.writeUnknown(Bit.UNK_1_7)
					.writeUnknown(Bit.UNK_1_8)
					.writeUnknown(Bit.UNK_2_1)
					.writeUnknown(Bit.UNK_2_2)
					.writeString(Bit.NAME, mesh.getName())
					.writeString(Bit.TEXTURE_PATH, mesh.getMesh())
					.writeString(Bit.TEXTURE_PATH, mesh.getTexture())
					.writeUnknown(Bit.UNK_2_6)
					.writeUnknown(Bit.UNK_2_7)
					.writeUnknown(Bit.UNK_2_8)
					.writeUnknown(Bit.UNK_3_1)
					.writeUnknown(Bit.UNK_3_2);

			if (mesh.hasColor()) {
				writer	.writeFloat(((float) mesh.getRed()) / 255)
						.writeFloat(((float) mesh.getGreen()) / 255)
						.writeFloat(((float) mesh.getBlue()) / 255);
			}

			writer	.writeFloat(Bit.FORE_SHIELDS, mesh.getShieldsFront(), Float.MIN_VALUE)
					.writeFloat(Bit.AFT_SHIELDS, mesh.getShieldsRear(), Float.MIN_VALUE)
					.writeUnknown(Bit.UNK_3_6)
					.writeUnknown(Bit.UNK_3_7)
					.writeUnknown(Bit.UNK_3_8)
					.writeUnknown(Bit.UNK_4_1)
					.writeUnknown(Bit.UNK_4_2)
					.endObject();
		}

		writer.writeInt(0);
	}
}