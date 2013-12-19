package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisMesh;
import net.dhleong.acl.world.ArtemisPositionable;

/**
 * @author dhleong
 */
public class GenericMeshPacket implements ObjectUpdatingPacket {
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

    private final byte[] mData;
    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();
    float r, g, b;

    public GenericMeshPacket(byte[] data) {
        mData = data;
        float x, y, z;//, bearing;
        String name = null, mesh = null, texture = null;
        float shieldsFront, shieldsRear;
        ObjectParser p = new ObjectParser(mData, 0);

        while (p.hasMore()) {
            try {
            	p.start(Bit.values());
                
                x = p.readFloat(Bit.X, -1);
                y = p.readFloat(Bit.Y, -1);
                z = p.readFloat(Bit.Z, -1);

                p.readUnknown(Bit.UNK_0, 4);
                p.readUnknown(Bit.UNK_1, 4);
                p.readUnknown(Bit.UNK_2, 8);
                p.readUnknown(Bit.UNK_3, 4);
                p.readUnknown(Bit.UNK_4, 4);
                p.readUnknown(Bit.UNK_5, 4);
                p.readUnknown(Bit.UNK_6, 8);
                
                name = p.readName(Bit.NAME);
                mesh = p.readName(Bit.TEXTURE_PATH); // wtf?!
                texture = p.readName(Bit.TEXTURE_PATH);
                
                p.readUnknown(Bit.UNK_7, 4);
                p.readUnknown(Bit.UNK_8, 2);
                p.readUnknown(Bit.UNK_9, 1);
                p.readUnknown(Bit.UNK_10, 1);
                p.readUnknown(Bit.UNK_11, 1);
                
                // color
                if (p.has(Bit.COLOR)) {
                    r = p.readFloat();
                    g = p.readFloat();
                    b = p.readFloat();
                } else {
                    r = g = b = -1;
                }
                
                shieldsFront = p.readFloat(Bit.FORE_SHIELDS, -1);
                shieldsRear  = p.readFloat(Bit.AFT_SHIELDS, -1);
                
                p.readUnknown(Bit.UNK_12, 1);
                p.readUnknown(Bit.UNK_13, 4);
                p.readUnknown(Bit.UNK_14, 4);
                p.readUnknown(Bit.UNK_15, 4);
                p.readUnknown(Bit.UNK_16, 4);

                final ArtemisMesh newObj = new ArtemisMesh(p.getTargetId(), name);
                
                // shared updates
                newObj.setX(x);
                newObj.setY(y);
                newObj.setZ(z);
                newObj.setMesh(mesh);
                newObj.setTexture(texture);
                newObj.setARGB(1.0f, r, g, b);
                newObj.setFakeShields(shieldsFront, shieldsRear);
                newObj.setUnknownFields(p.getUnknownFields());
                mObjects.add(newObj);
            } catch (RuntimeException e) {
                debugPrint();
                System.out.println("!! DEBUG this = " + 
                        TextUtil.byteArrayToHexString(mData));
                throw e;
            }
        }
    }

    @Override
    public void debugPrint() {
        for (ArtemisPositionable u : mObjects) {
            System.out.println("- DEBUG: " + u);
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
    public String toString() {
        return TextUtil.byteArrayToHexString(mData); 
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return true;
    }

    @Override
    public List<ArtemisPositionable> getObjects() {
        return mObjects;
    }
}