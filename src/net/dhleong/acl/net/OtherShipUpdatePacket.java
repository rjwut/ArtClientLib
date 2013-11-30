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
	private enum Bit {
		NAME,
		UNK_0,
		UNK_1,
		UNK_2,
		UNK_3,
		UNK_4,	// fleet?
		SHIP_TYPE,
		X,

		Y,
		Z,
		UNK_5,
		RUDDER,
		HEADING,
		VELOCITY,
		UNK_6,
		UNK_7,

		FORE_SHIELD_MAX,
		FORE_SHIELD,
		AFT_SHIELD_MAX,
		AFT_SHIELD,
		UNK_8,
		UNK_9,
		UNK_10, // system damage?
		UNK_11, // system damage?

		UNK_12, // system damage?
		UNK_13, // system damage?
		UNK_14, // system damage?
		UNK_15, // system damage?
		UNK_16, // system damage?
		UNK_17, // system damage?
		SHIELD_FREQUENCY_A,
		SHIELD_FREQUENCY_B,

		SHIELD_FREQUENCY_C,
		SHIELD_FREQUENCY_D,
		SHIELD_FREQUENCY_E
	}

	/*
    private static final byte ACTION_NAME_BYTE    = (byte) 0x01;
    private static final byte ACTION_SKIP_BYTES_1 = (byte) 0x02;
    private static final byte ACTION_SKIP_BYTES_2 = (byte) 0x04;
    
    // shield frequencies?
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

    private static final int SHLD_FRNT_MX= 0x00000100; 
    private static final int SHLD_FRNT   = 0x00000200; 
    private static final int SHLD_REAR_MX= 0x00000400; 
    private static final int SHLD_REAR   = 0x00000800; 
    private static final int DUNNO_NEW_0 = 0x00001000; // ?
    private static final int DUNNO_NEW_1 = 0x00002000; // ?

    // I think the bits 0x003FC000 represent system damage for neutral ships....
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
    */ 

    private static final Bit[] SHLD_FREQS = new Bit[] {
		Bit.SHIELD_FREQUENCY_A,
		Bit.SHIELD_FREQUENCY_B,
		Bit.SHIELD_FREQUENCY_C,
		Bit.SHIELD_FREQUENCY_D,
		Bit.SHIELD_FREQUENCY_E
    };


    private final byte[] mData;
    public final List<ArtemisPositionable> mObjects = new ArrayList<ArtemisPositionable>();

    public OtherShipUpdatePacket(byte[] data) {
        mData = data;
        float x, y, z, bearing, steering, velocity;
        float[] freqs = new float[SHLD_FREQS.length];
        String name = null;
        int hullId = -1;
        float shieldsFront, shieldsFrontMax;
        float shieldsRear, shieldsRearMax;
        ObjectParser p = new ObjectParser(mData, 0);

        while (p.hasMore()) {
            try {
                p.start(Bit.values());

                if (p.getTargetType() != -1) {
                    System.err.println("Type: " + Integer.toHexString(p.getTargetType()));
                    System.err.println("  id: " + Integer.toHexString(p.getTargetId()));
                    throw new RuntimeException("Not type " + -1 + " ...?! Raw=" + this);
                }
                
                name = p.readName(Bit.NAME);
                
                // these are floats, probably max speed,
                //  turn rate, steering, etc.
                p.readInt(Bit.UNK_0);
                p.readInt(Bit.UNK_1);
                p.readFloat(Bit.UNK_2, -1);
                p.readFloat(Bit.UNK_3, -1);
                p.readInt(Bit.UNK_4); // fleet?

                hullId = p.readInt(Bit.SHIP_TYPE);
                x = p.readFloat(Bit.X, -1);
                y = p.readFloat(Bit.Y, -1);
                z = p.readFloat(Bit.Z, -1);
                
                p.readFloat(Bit.UNK_5, -1);

                steering = p.readFloat(Bit.RUDDER, Float.MIN_VALUE); // I *think* so
                bearing = p.readFloat(Bit.HEADING, Float.MIN_VALUE);
                velocity = p.readFloat(Bit.VELOCITY, -1);
                
                // doesn't really make sense, but works
                if (name != null && p.has(Bit.UNK_6)) {
                    p.readByte(Bit.UNK_6, (byte) -1);
                    p.readByte(Bit.UNK_7, (byte) -1);
                } else if (name == null) {
                    p.readShort(Bit.UNK_6);
                    p.readInt(Bit.UNK_7);
                }

                shieldsFrontMax = p.readFloat(Bit.FORE_SHIELD_MAX, -1);
                shieldsFront = p.readFloat(Bit.FORE_SHIELD, -1);
                shieldsRearMax = p.readFloat(Bit.AFT_SHIELD_MAX, -1);
                shieldsRear = p.readFloat(Bit.AFT_SHIELD, -1);
                
                // MUST be, I think... 
                p.readByte(Bit.UNK_8, (byte)-1);
                p.readByte(Bit.UNK_9, (byte)-1);
                
                // total crap, and yet...
                if (p.has(Bit.FORE_SHIELD_MAX, Bit.FORE_SHIELD,
                		Bit.AFT_SHIELD_MAX, Bit.AFT_SHIELD)) {
                    p.readShort();
                }

                // TODO These must be system damages!
                p.readInt(Bit.UNK_10);
                p.readInt(Bit.UNK_11);
                p.readInt(Bit.UNK_12);
                p.readInt(Bit.UNK_13);
                p.readInt(Bit.UNK_14);
                p.readInt(Bit.UNK_15);
                p.readInt(Bit.UNK_16);
                p.readInt(Bit.UNK_17);

                for (int i = 0; i < SHLD_FREQS.length; i++) {
                    freqs[i] = p.readFloat(SHLD_FREQS[i], -1);
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
}