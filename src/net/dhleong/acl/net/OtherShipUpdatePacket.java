package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisOtherShip;
import net.dhleong.acl.world.ArtemisPositionable;

public class OtherShipUpdatePacket implements ObjectUpdatingPacket {

    
    private static final byte ACTION_NAME_BYTE    = (byte) 0x01;
    private static final byte ACTION_SKIP_BYTES_1 = (byte) 0x02;
    private static final byte ACTION_SKIP_BYTES_2 = (byte) 0x04;
    
    /* shield frequencies? */
    private static final byte ACTION_SKIP_BYTES_3 = (byte) 0x08;
    private static final byte ACTION_SKIP_BYTES_4 = (byte) 0x10;
    
    private static final byte ACTION_FLEET_MAYBE  = (byte) 0x20;
    private static final byte ACTION_HULL_ID      = (byte) 0x40;
    
    private static final byte ACTION_POS_X  = (byte) 0x80;

    private static final int POS_Y       = 0x00000001; 
    private static final int POS_Z       = 0x00000002; 
    private static final int DUNNO_SKIP_0= 0x00000004; 
    private static final int STEERING    = 0x00000008; 
    private static final int BEARING     = 0x00000010; 
    private static final int VELOCITY    = 0x00000020; 
    private static final int DUNNO_SKIP_3= 0x00000040; // wtf?
    private static final int DUNNO_SKIP_4= 0x00000080; // wtf?

    private static final int SHLD_FRNT   = 0x00000100; 
    private static final int SHLD_FRNT_MX= 0x00000200; 
    private static final int SHLD_REAR   = 0x00000400; 
    private static final int SHLD_REAR_MX= 0x00000800; 
    private static final int DUNNO_NEW_0 = 0x00001000; // ?
    private static final int DUNNO_NEW_1 = 0x00002000; // ?

    /* 
     * I think the bits 0x003FC000 represent system 
     * damage for neutral ships....
     */
    private static final int UNKNOWN_1   = 0x00004000; // just a guess
    private static final int UNKNOWN_2   = 0x00008000; // ?
    
    private static final int UNKNOWN_3   = 0x00010000;

    private static final int UNKNOWN_4   = 0x00020000; // I think?

    private static final int UNUSED_1    = 0x00040000;
    private static final int UNUSED_2    = 0x00080000;
    private static final int UNUSED_3    = 0x00100000;
    private static final int UNUSED_4    = 0x00200000;
    private static final int SHLD_FREQ_A = 0x00400000;
    private static final int SHLD_FREQ_B = 0x00800000;
    private static final int SHLD_FREQ_C = 0x01000000;
    private static final int SHLD_FREQ_D = 0x02000000;
    
    private static final int SHLD_FREQ_E = 0x04000000; 
//    private static final int SHLD_FREQ_B = 0x08000000;
//    private static final int SHLD_FREQ_C = 0x10000000;
//    private static final int SHLD_FREQ_D = 0x20000000;
//    private static final int SHLD_FREQ_E = 0x40000000;
    private static final int[] SHLD_FREQS = new int[] {
        SHLD_FREQ_A, SHLD_FREQ_B, SHLD_FREQ_C,
        SHLD_FREQ_D, SHLD_FREQ_E
    };


    private final byte[] mData;

    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();

    private float velocity;

    public OtherShipUpdatePacket(byte[] data) {

        mData = data;

        float x, y, z, bearing, steering;
        float[] freqs = new float[SHLD_FREQS.length];
        String name = null;
        int hullId = -1;

        float shieldsFront, shieldsFrontMax;
        float shieldsRear, shieldsRearMax;

//        int base = 0;
        ObjectParser p = new ObjectParser(mData, 0);
        while (p.hasMore()) {
            try {
                p.start();
                
                if (p.getTargetType() != ArtemisObject.TYPE_OTHER) {
                    System.err.println("Type: " + Integer.toHexString(p.getTargetType()));
                    System.err.println("  id: " + Integer.toHexString(p.getTargetId()));
                    throw new RuntimeException("Not type " + ArtemisObject.TYPE_OTHER + " ...?! Raw=" + this);
                }
                
                name = p.readName(ACTION_NAME_BYTE);
                
                // these are floats, probably max speed,
                //  turn rate, steering, etc.
                p.readInt(ACTION_SKIP_BYTES_1);
                p.readInt(ACTION_SKIP_BYTES_2);
                
                p.readFloat(ACTION_SKIP_BYTES_3, -1);
                p.readFloat(ACTION_SKIP_BYTES_4, -1);
                
                // ?
                p.readInt(ACTION_FLEET_MAYBE);
                
                hullId = p.readInt(ACTION_HULL_ID);

                x = p.readFloat(ACTION_POS_X, -1);
                y = p.readFloat(POS_Y, -1);
                z = p.readFloat(POS_Z, -1);
                
                float dunno0 = p.readFloat(DUNNO_SKIP_0, -1);

                steering = p.readFloat(STEERING, Float.MIN_VALUE); // I *think* so
                bearing = p.readFloat(BEARING, Float.MIN_VALUE);
                velocity = p.readFloat(VELOCITY, -1);

                //p.readFloat(DUNNO_SKIP_3, -1);
                //p.readShort(DUNNO_SKIP_3);
//                p.readShort(DUNNO_SKIP_3);
////                p.readByte(DUNNO_SKIP_3, (byte)0);
//
//                if ((p.getAction() != (byte)0xff)) // hax?
////                if (dunno0 != -1 && dunno0 != 0) // ???
////                    p.readInt(DUNNO_SKIP_4);
//                p.readInt(DUNNO_SKIP_4);

                /*
                // hax?
                if (p.has(DUNNO_SKIP_3)) {
                    p.readByte(DUNNO_SKIP_3, (byte) 0);
                    p.readByte(DUNNO_SKIP_4, (byte) 0);
                } else {
                    p.readShort(DUNNO_SKIP_3);
                    
                    // WOW hax
                    if (dunno0 != 0)
                        p.readInt(DUNNO_SKIP_4);
                }
                */
                
                // doesn't really make sense, but
                //  works, and makes MORE sense than above
                if (name != null) {
                    p.readByte(DUNNO_SKIP_3, (byte) -1);
                    p.readByte(DUNNO_SKIP_4, (byte) -1);
                } else {
                    p.readShort(DUNNO_SKIP_3);
                    p.readInt(DUNNO_SKIP_4);
                }

                // WTF?!?! WHY!?!
                if (p.has(SHLD_FRNT_MX) && !p.has(SHLD_FRNT))
                    shieldsFront = p.readFloat();
                else
                    shieldsFront = p.readFloat(SHLD_FRNT, -1);
                shieldsFrontMax = p.readFloat(SHLD_FRNT_MX, -1);
                
                
                if (p.has(SHLD_REAR_MX) && !p.has(SHLD_REAR))
                    shieldsRear  = p.readFloat();
                else
                    shieldsRear = p.readFloat(SHLD_REAR, -1);
                shieldsRearMax = p.readFloat(SHLD_REAR_MX, -1);
                
//                System.out.println(">> " + shieldsFront + " / " + shieldsRearMax);

                /*
                p.readShort(DUNNO_NEW_0);
                p.readShort(DUNNO_NEW_1);
                */
                // MUST be, I think... 
                p.readByte(DUNNO_NEW_0, (byte)-1);
                p.readByte(DUNNO_NEW_1, (byte)-1);
                
                // total crap, and yet...
                if (name != null && p.has(DUNNO_NEW_0) && p.has(DUNNO_NEW_1))
                    p.readShort();

                // but bytes above make this weird
                p.readInt(UNKNOWN_1);
                p.readInt(UNKNOWN_2);
                p.readInt(UNKNOWN_3);
                p.readInt(UNKNOWN_4);

                p.readInt(UNUSED_1);
                p.readInt(UNUSED_2);
                p.readInt(UNUSED_3);
                p.readInt(UNUSED_4);

                // hax...?
                for (int i=0; i<SHLD_FREQS.length; i++) {
                    freqs[i] = p.readFloat(SHLD_FREQS[i], -1);
//                    if (freqs[i] != -1)
//                            System.out.println("Shield: " + freqs[i]);
                }
                
                ArtemisOtherShip newObj = new ArtemisOtherShip(
                        p.getTargetId(), name, hullId);
                
                // shared updates
                newObj.setX(x);
                newObj.setY(y);
                newObj.setZ(z);
                
                newObj.setSteering(steering);
                newObj.setBearing(bearing);
                newObj.setVelocity(velocity);
                
                newObj.setShieldsFront(shieldsFront);
                newObj.setShieldsFrontMax(shieldsFrontMax);
                newObj.setShieldsRear(shieldsRear);
                newObj.setShieldsRearMax(shieldsRearMax);
                
                for (int i=0; i<freqs.length; i++) {
                    newObj.setShieldFreq(i, freqs[i]);
                }

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

    /*
    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        // this may be a wrong assumption, but I'd think they're the same
        return (pkt.getTargetType() == ArtemisObject.TYPE_ENEMY ||
                pkt.getTargetType() == ArtemisObject.TYPE_OTHER);
//                && 
//                ((pkt.getAction() & SystemInfoPacket.ACTION_MASK) == ACTION_UPDATE_BYTE);
    }
    */

}
