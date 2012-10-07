package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisEnemy;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisOtherShip;
import net.dhleong.acl.world.ArtemisPositionable;
import net.dhleong.acl.world.BaseArtemisShip;

public class ObjUpdatePacket implements ArtemisPacket {

    private static final byte ACTION_UPDATE_BYTE  = (byte) 0x80;
    
    private static final byte ACTION_NAME_BYTE    = (byte) 0x01;
    private static final byte ACTION_SKIP_BYTES_1 = (byte) 0x02;
    private static final byte ACTION_SKIP_BYTES_2 = (byte) 0x04;
    
    /* shield frequencies? */
    private static final byte ACTION_SKIP_BYTES_3 = (byte) 0x08;
    private static final byte ACTION_SKIP_BYTES_4 = (byte) 0x10;
    private static final byte ACTION_FLEET_MAYBE  = (byte) 0x20;
    private static final byte ACTION_HULL_ID      = (byte) 0x40;

    private static final int POS_Z       = 0x00000002; // huh?
    private static final int POS_Y       = 0x00000001; // wtf?
    private static final int DUNNO_SKIP_0= 0x00000004; 
    private static final int DUNNO_SKIP  = 0x00000008; 
    private static final int BEARING     = 0x00000010; 
    private static final int DUNNO_SKIP_2= 0x00000020; // wtf?
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
    private static final int ELITE       = 0x00004000; // just a guess
    private static final int DUNNO_NEW_2 = 0x00008000; // ?
    
    private static final int DUNNO_NEW_3 = 0x00010000;

    private static final int SCANNED     = 0x00020000; // I think?

    private static final int UNUSED_1    = 0x00040000;
    private static final int UNUSED_2    = 0x00080000;
    private static final int UNUSED_3    = 0x00100000;
    private static final int UNUSED_4    = 0x00200000;
    private static final int UNUSED_5    = 0x00400000;
    private static final int UNUSED_6    = 0x00800000;
    private static final int UNUSED_7    = 0x01000000;
    private static final int UNUSED_8    = 0x02000000;
    
    private static final int SHLD_FREQ_A = 0x04000000; 
    private static final int SHLD_FREQ_B = 0x08000000;
    private static final int SHLD_FREQ_C = 0x10000000;
    private static final int SHLD_FREQ_D = 0x20000000;
    private static final int SHLD_FREQ_E = 0x40000000;
    private static final int[] SHLD_FREQS = new int[] {
        SHLD_FREQ_A, SHLD_FREQ_B, SHLD_FREQ_C,
        SHLD_FREQ_D, SHLD_FREQ_E
    };


    private final byte[] mData;

    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();

    public ObjUpdatePacket(final SystemInfoPacket pkt) {
        this(pkt.mData);
    }

    public ObjUpdatePacket(byte[] data) {

        mData = data;

        float x, y, z, bearing;
        float[] freqs = new float[SHLD_FREQS.length];
        byte scanned = -1;
        String name = null;
        int hullId = -1;
        int elite = -1;

        float shieldsFront, shieldsFrontMax;
        float shieldsRear, shieldsRearMax;

//        int base = 0;
        ObjectParser p = new ObjectParser(mData, 0);
        while (p.hasMore()) {
            try {
                p.start();
                
                name = p.readName(ACTION_NAME_BYTE);
                
                // no idea what these are
                p.readInt(ACTION_SKIP_BYTES_1);
                p.readInt(ACTION_SKIP_BYTES_2);
                
//                 wild guessing, but 2 floats each?
                p.readFloat(ACTION_SKIP_BYTES_3, -1);
//                p.readFloat(ACTION_SKIP_BYTES_3, -1);
                p.readFloat(ACTION_SKIP_BYTES_4, -1);
//                p.readFloat(ACTION_SKIP_BYTES_4, -1);
                
                // ?
                p.readInt(ACTION_FLEET_MAYBE);
                
                hullId = p.readInt(ACTION_HULL_ID);

                x = p.readFloat(ACTION_UPDATE_BYTE, -1);
                y = p.readFloat(POS_Y, -1);
                z = p.readFloat(POS_Z, -1);
                
                p.readFloat(DUNNO_SKIP_0, -1);

                p.readInt(DUNNO_SKIP);

                bearing = p.readFloat(BEARING, Float.MIN_VALUE);

                p.readFloat(DUNNO_SKIP_2, -1);

                //p.readFloat(DUNNO_SKIP_3, -1);
                //p.readShort(DUNNO_SKIP_3);
                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY)
                    p.readByte(DUNNO_SKIP_3, (byte)0); 
                else
                    p.readShort(DUNNO_SKIP_3);

                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY)
                    p.readShort(DUNNO_SKIP_4);
                else if (p.getAction() != (byte)0xff) //hax?
                    p.readInt(DUNNO_SKIP_4);

                shieldsFront = p.readFloat(SHLD_FRNT, -1);
                shieldsFrontMax = p.readFloat(SHLD_FRNT_MX, -1);
                shieldsRear = p.readFloat(SHLD_REAR, -1);
                shieldsRearMax = p.readFloat(SHLD_REAR_MX, -1);

                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY
                        || p.getAction() == (byte)0xff) {
                    //p.readFloat(DUNNO_NEW_0, 0);
                    p.readShort(DUNNO_NEW_0);
                    //p.readByte(DUNNO_NEW_0, (byte)0);
                } else {
                    p.readInt(DUNNO_NEW_0);
                }
                
                // ????
                //p.readShort(DUNNO_NEW_1);
                p.readByte(DUNNO_NEW_1, (byte)0);

                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY
                        || p.getAction() != (byte)0xff) {

                    // don't care right now
                    elite = p.readInt(ELITE);
                    //p.readShort(ELITE);
                }

                //p.readByte(DUNNO_NEW_2, (byte)0);
//                p.readShort(DUNNO_NEW_2);
                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY
                        || p.getAction() != (byte)0xff) {
                    p.readInt(DUNNO_NEW_2);
                } else {
                    p.readShort(DUNNO_NEW_2);
                }

                /*
                if (p.has(ELITE))
                    p.readLong(DUNNO_NEW_3);
                else
                    p.readShort(DUNNO_NEW_3);
                */
                //p.readInt(DUNNO_NEW_3);
                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY
                        || p.getAction() != (byte) 0xff) {
                    p.readInt(DUNNO_NEW_3);
                } else {
                    p.readShort(DUNNO_NEW_3);
                }
                
                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY) {
                
                    scanned = p.readByte(SCANNED, (byte) -1);
                } else {
                    p.readInt(SCANNED);
                }

                p.readInt(UNUSED_1);
                p.readInt(UNUSED_2);
                p.readInt(UNUSED_3);
                p.readInt(UNUSED_4);
                p.readInt(UNUSED_5);
                p.readInt(UNUSED_6);
                
                // shield frequencies
                if (p.getTargetType() == ArtemisObject.TYPE_ENEMY) {
                    p.readInt(UNUSED_7);
                    p.readInt(UNUSED_8);

                    for (int i=0; i<SHLD_FREQS.length; i++) {
                        freqs[i] = p.readFloat(SHLD_FREQS[i], -1);
                    }
                } else {
                    p.readShort(UNUSED_7);
                    p.readShort(UNUSED_8);

                    // hax
                    for (int i=0; i<SHLD_FREQS.length; i++) {
                        freqs[i] = p.readFloat(SHLD_FREQS[0], -1);
                    }
                }
                
                final ArtemisPositionable newObj;
                switch (p.getTargetType()) {
                default:
                case ArtemisObject.TYPE_ENEMY:
                    ArtemisEnemy enemy = new ArtemisEnemy(p.getTargetId(), name, hullId);
                    enemy.setScanned(scanned);
                    enemy.setEliteBits(elite);

                    newObj = enemy;
                    break;
                case ArtemisObject.TYPE_OTHER:
                    ArtemisOtherShip other = new ArtemisOtherShip(
                            p.getTargetId(), name, hullId);
                    
                    newObj = other;
                    break;
                }
                
                // shared updates
                newObj.setX(x);
                newObj.setY(y);
                newObj.setZ(z);
                
                if (newObj instanceof BaseArtemisShip) {
                    BaseArtemisShip ship = (BaseArtemisShip) newObj;
                    ship.setBearing(bearing);
                    
                    ship.setShieldsFront(shieldsFront);
                    ship.setShieldsFrontMax(shieldsFrontMax);
                    ship.setShieldsRear(shieldsRear);
                    ship.setShieldsRearMax(shieldsRearMax);
                    
                    for (int i=0; i<freqs.length; i++) {
                        ship.setShieldFreq(i, freqs[i]);
                    }
                }

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
        return (pkt.getTargetType() == ArtemisObject.TYPE_ENEMY ||
                pkt.getTargetType() == ArtemisObject.TYPE_OTHER);
//                && 
//                ((pkt.getAction() & SystemInfoPacket.ACTION_MASK) == ACTION_UPDATE_BYTE);
    }

}
