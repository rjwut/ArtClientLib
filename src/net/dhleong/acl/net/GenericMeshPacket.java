package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisMesh;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPositionable;

public class GenericMeshPacket implements ArtemisPacket {

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
    


    private final byte[] mData;

    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();

    float r, g, b;
    
    public GenericMeshPacket(final SystemInfoPacket pkt) {
        this(pkt.mData);
    }

    public GenericMeshPacket(byte[] data) {

        mData = data;

        float x, y, z;//, bearing;
        
        String name = null, mesh = null, texture = null;

        float shieldsFront, shieldsRear;

//        int base = 0;
        ObjectParser p = new ObjectParser(mData, 0);
        while (p.hasMore()) {
            try {
                p.startNoAction();
                
                x = p.readFloat(POS_X, -1);
                y = p.readFloat(POS_Y, -1);
                z = p.readFloat(POS_Z, -1);

                p.readInt(q1);
                p.readInt(q2);
                p.readLong(q3);
                p.readInt(q4);
                p.readInt(q5);
                p.readInt(q6);
                p.readLong(q7);
                
                name = p.readName(NAME);
                mesh = p.readName(PATH_TEXTURE); // wtf?!
                texture = p.readName(PATH_TEXTURE);
                
//                if (p.has(PATH_MESH))
//                    p.readShort(UNKNOWN_FLT);
                p.readInt(UNKNOWN_FLT);
                
                p.readShort(UNKNOWN_SHORT);
                p.readByte(UNKNOWN_FLT_1, (byte)-1);
                p.readByte(COLOR_R, (byte)-1);
                p.readByte(COLOR_G, (byte)-1);
                
                // color
                boolean hasColor = p.has(COLOR);
                
                if (hasColor) {
                    r = p.readFloat();
                    g = p.readFloat();
                    b = p.readFloat();
                } else {
                    r = g = b = -1;
                }
                
                shieldsFront = p.readFloat(FAKE_SHIELDS_FRONT, -1);
                shieldsRear  = p.readFloat(FAKE_SHIELDS_REAR, -1);
                
                p.readByte(UNKNOWN_BYTE, (byte)0xff);
                p.readInt(UNKNOWN_INT_1);
                p.readInt(UNKNOWN_INT_2);
                p.readInt(UNKNOWN_INT_3);
                p.readInt(UNKNOWN_INT_4);
                
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
                        BaseArtemisPacket.byteArrayToHexString(mData));
                throw e;
            }
        }
    }

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
        return SystemInfoPacket.TYPE;
    }

    @Override
    public String toString() {
        return BaseArtemisPacket.byteArrayToHexString(mData); 
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return true;
    }

    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        // this may be a wrong assumption, but I'd think they're the same
        return (pkt.getTargetType() == ArtemisObject.TYPE_MESH);
    }

}
