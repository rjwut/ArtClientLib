package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisMesh;
import net.dhleong.acl.world.ArtemisPositionable;

/**
 * 
 * @author dhleong
 *
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
	/*
    private static final int POS_X       = 0x00000001; 
    private static final int POS_Y       = 0x00000002; 
    private static final int POS_Z       = 0x00000004; // huh? 
    
    private static final int q1  = 0x00000008; 
    private static final int q2= 0x00000010; 
    private static final int q3= 0x00000020; 
    private static final int q4= 0x00000040; 
    private static final int q5 = 0x00000080;
    private static final int q6   = 0x00000100; 
    private static final int q7= 0x00000200;
    
    private static final int NAME         = 0x00000400; 
    @SuppressWarnings("unused")
    private static final int PATH_MESH    = 0x00000800; 
    private static final int PATH_TEXTURE = 0x00001000; // ?
    
    private static final int UNKNOWN_FLT = 0x00002000; // ?

    private static final int UNKNOWN_SHORT = 0x00004000; // just a guess
    private static final int UNKNOWN_FLT_1 = 0x00008000; // ?
    
    private static final int COLOR_R    = 0x00010000;
    private static final int COLOR_G    = 0x00020000;
    
    private static final int COLOR    = 0x00040000;
    
    private static final int FAKE_SHIELDS_FRONT = 0x00080000;
    private static final int FAKE_SHIELDS_REAR  = 0x00100000;
    
    private static final int UNKNOWN_BYTE   = 0x00200000;
    private static final int UNKNOWN_INT_1  = 0x00400000;
    private static final int UNKNOWN_INT_2  = 0x00800000;
    private static final int UNKNOWN_INT_3  = 0x01000000;
    private static final int UNKNOWN_INT_4  = 0x02000000;
    */

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

                p.readInt(Bit.UNK_0);
                p.readInt(Bit.UNK_1);
                p.readLong(Bit.UNK_2);
                p.readInt(Bit.UNK_3);
                p.readInt(Bit.UNK_4);
                p.readInt(Bit.UNK_5);
                p.readLong(Bit.UNK_6);
                
                name = p.readName(Bit.NAME);
                mesh = p.readName(Bit.TEXTURE_PATH); // wtf?!
                texture = p.readName(Bit.TEXTURE_PATH);
                
                p.readInt(Bit.UNK_7);
                p.readShort(Bit.UNK_8);
                p.readByte(Bit.UNK_9, (byte)-1);
                p.readByte(Bit.UNK_10, (byte)-1);
                p.readByte(Bit.UNK_11, (byte)-1);
                
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
                
                p.readByte(Bit.UNK_12, (byte) 0xff);
                p.readInt(Bit.UNK_11);
                p.readInt(Bit.UNK_12);
                p.readInt(Bit.UNK_13);
                p.readInt(Bit.UNK_14);
                
                final ArtemisMesh newObj = new ArtemisMesh(p.getTargetId(), name);
                
                // shared updates
                newObj.setX(x);
                newObj.setY(y);
                newObj.setZ(z);
                
                newObj.setMesh(mesh);
                newObj.setTexture(texture);
                
                newObj.setARGB(1.0f, r, g, b);
                
                newObj.setFakeShields(shieldsFront, shieldsRear);
                
                // may have fake shields?
//                if (newObj instanceof BaseArtemisShip) {
//                    BaseArtemisShip ship = (BaseArtemisShip) newObj;
//                    ship.setBearing(bearing);
//                    
//                    ship.setShieldsFront(shieldsFront);
//                    ship.setShieldsFrontMax(shieldsFrontMax);
//                    ship.setShieldsRear(shieldsRear);
//                    ship.setShieldsRearMax(shieldsRearMax);
//                    
//                    for (int i=0; i<freqs.length; i++) {
//                        ship.setShieldFreq(i, freqs[i]);
//                    }
//                }

                mObjects.add(newObj);
//                base = offset;
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
    public long getMode() {
        return 0x01;
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