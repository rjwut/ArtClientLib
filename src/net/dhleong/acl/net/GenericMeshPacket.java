package net.dhleong.acl.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.world.ArtemisMesh;
import net.dhleong.acl.world.ArtemisObject;

/**
 * Updates for generic mesh objects.
 * @author dhleong
 */
public class GenericMeshPacket extends BaseArtemisPacket implements ObjectUpdatingPacket {
	private enum Bit {
		X,
		Y,
		Z,
		UNK_0,
		UNK_1,
		UNK_2,
		UNK_3,
		UNK_4,

		UNK_5,
		UNK_6,
		NAME,
		MESH_PATH,
		TEXTURE_PATH,
		UNK_7,
		UNK_8,
		UNK_9,

		UNK_10,
		UNK_11,
		COLOR,
		FORE_SHIELDS,
		AFT_SHIELDS,
		UNK_12,
		UNK_13,
		UNK_14,

		UNK_15,
		UNK_16
	}

    private final List<ArtemisObject> mObjects = new ArrayList<ArtemisObject>();
    private float red, green, blue;

    public GenericMeshPacket(PacketReader reader) {
    	super(ConnectionType.SERVER, WORLD_TYPE);
        float x, y, z;//, bearing;
        String name = null, mesh = null, texture = null;
        float shieldsFront, shieldsRear;

        while (reader.hasMore()) {
        	reader.startObject(Bit.values());
            
            x = reader.readFloat(Bit.X, Float.MIN_VALUE);
            y = reader.readFloat(Bit.Y, Float.MIN_VALUE);
            z = reader.readFloat(Bit.Z, Float.MIN_VALUE);

            reader.readObjectUnknown(Bit.UNK_0, 4);
            reader.readObjectUnknown(Bit.UNK_1, 4);
            reader.readObjectUnknown(Bit.UNK_2, 8);
            reader.readObjectUnknown(Bit.UNK_3, 4);
            reader.readObjectUnknown(Bit.UNK_4, 4);
            reader.readObjectUnknown(Bit.UNK_5, 4);
            reader.readObjectUnknown(Bit.UNK_6, 8);
            
            name = reader.readString(Bit.NAME);
            mesh = reader.readString(Bit.TEXTURE_PATH); // wtf?!
            texture = reader.readString(Bit.TEXTURE_PATH);
            
            reader.readObjectUnknown(Bit.UNK_7, 4);
            reader.readObjectUnknown(Bit.UNK_8, 2);
            reader.readObjectUnknown(Bit.UNK_9, 1);
            reader.readObjectUnknown(Bit.UNK_10, 1);
            reader.readObjectUnknown(Bit.UNK_11, 1);
            
            // color
            if (reader.has(Bit.COLOR)) {
                red = reader.readFloat();
                green = reader.readFloat();
                blue = reader.readFloat();
            } else {
                red = green = blue = Float.MIN_VALUE;
            }

            shieldsFront = reader.readFloat(Bit.FORE_SHIELDS, Float.MIN_VALUE);
            shieldsRear  = reader.readFloat(Bit.AFT_SHIELDS, Float.MIN_VALUE);
            
            reader.readObjectUnknown(Bit.UNK_12, 1);
            reader.readObjectUnknown(Bit.UNK_13, 4);
            reader.readObjectUnknown(Bit.UNK_14, 4);
            reader.readObjectUnknown(Bit.UNK_15, 4);
            reader.readObjectUnknown(Bit.UNK_16, 4);

            final ArtemisMesh newObj = new ArtemisMesh(reader.getObjectId(), name);
            
            // shared updates
            newObj.setX(x);
            newObj.setY(y);
            newObj.setZ(z);
            newObj.setMesh(mesh);
            newObj.setTexture(texture);
            newObj.setARGB(1.0f, red, green, blue);
            newObj.setFakeShields(shieldsFront, shieldsRear);
            newObj.setUnknownProps(reader.getUnknownObjectProps());
            mObjects.add(newObj);
        }
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.SERVER;
    }

    @Override
    public int getType() {
        return ArtemisPacket.WORLD_TYPE;
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
			b.append("\n").append(obj);
		}
	}
}