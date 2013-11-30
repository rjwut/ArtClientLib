package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisEnemy;
import net.dhleong.acl.world.ArtemisPositionable;

public class EnemyUpdatePacket implements ObjectUpdatingPacket {
	public enum Bit {
		NAME,
		UNK_0,
		UNK_1,
		UNK_2,
		UNK_3,
		UNK_4,
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

		FORE_SHIELD,
		FORE_SHIELD_MAX,
		AFT_SHIELD,
		AFT_SHIELD_MAX,
		UNK_8,
		UNK_9,
		UNK_10,

		UNK_11,
		UNK_12,
		UNK_13,
		UNK_14,
		UNK_15,
		UNK_16,
		UNK_17,
		UNK_18,

		UNK_19,
		UNK_20,
		UNK_21,
		SHIELD_FREQUENCY_A,
		SHIELD_FREQUENCY_B,
		SHIELD_FREQUENCY_C,
		SHIELD_FREQUENCY_D,
		SHIELD_FREQUENCY_E,

		UNK_22,
		UNK_23,
		UNK_24,
		UNK_25,
		UNK_26,
		UNK_27,
		UNK_28,
		UNK_29
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
    
    private static final byte POS_X               = (byte) 0x80;

    private static final int POS_Z       = 0x00000002; // huh?
    private static final int POS_Y       = 0x00000001; // wtf?
    private static final int DUNNO_SKIP_0= 0x00000004; 
    private static final int STEERING    = 0x00000008; 
    private static final int BEARING     = 0x00000010; 
    private static final int VELOCITY    = 0x00000020; // wtf?
    private static final int DUNNO_SKIP_3= 0x00000040; // wtf?
    private static final int DUNNO_SKIP_4= 0x00000080; // wtf?

    private static final int SHLD_FRNT   = 0x00000100; 
    private static final int SHLD_FRNT_MX= 0x00000200; 
    private static final int SHLD_REAR   = 0x00000400; 
    private static final int SHLD_REAR_MX= 0x00000800; 
    private static final int DUNNO_NEW_0 = 0x00001000; // ?
    private static final int DUNNO_NEW_1 = 0x00002000; // ?

    // I think the bits 0x003FC000 represent system 
    // damage for neutral ships....
    private static final int ELITE       = 0x00004000; // just a guess
    private static final int ELITE_STATE = 0x00008000; // ?
    
    private static final int SCANNED     = 0x00010000; // I think?
    private static final int DUNNO_NEW_3 = 0x00020000;

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

    private float velocity;

    public EnemyUpdatePacket(byte[] data) {

        mData = data;

        float x, y, z, bearing, steering;
        float[] freqs = new float[SHLD_FREQS.length];
        int scanned = -1;
        String name = null;
        int hullId = -1;
        int elite = -1;
        int eliteState = -1;

        float shieldsFront, shieldsFrontMax;
        float shieldsRear, shieldsRearMax;

//        int base = 0;
        ObjectParser p = new ObjectParser(mData, 0);

        while (p.hasMore()) {
            try {
                p.start(Bit.values());
                name = p.readName(Bit.NAME);
                
                // no idea what these are
                p.readInt(Bit.UNK_0);
                p.readInt(Bit.UNK_1);
                p.readFloat(Bit.UNK_2, -1);
                p.readFloat(Bit.UNK_3, -1);
                
                // ?
                p.readInt(Bit.UNK_4);
                
                hullId = p.readInt(Bit.SHIP_TYPE);

                x = p.readFloat(Bit.X, -1);
                y = p.readFloat(Bit.Y, -1);
                z = p.readFloat(Bit.Z, -1);
                
                p.readFloat(Bit.UNK_5, -1);

                steering = p.readFloat(Bit.RUDDER, Float.MIN_VALUE); // I *think* so
                bearing = p.readFloat(Bit.HEADING, Float.MIN_VALUE);
                velocity = p.readFloat(Bit.VELOCITY, -1);

                p.readByte(Bit.UNK_6, (byte)0); 
                p.readShort(Bit.UNK_7);

                shieldsFront = p.readFloat(Bit.FORE_SHIELD, -1);
                shieldsFrontMax = p.readFloat(Bit.FORE_SHIELD_MAX, -1);
                shieldsRear = p.readFloat(Bit.AFT_SHIELD, -1);
                shieldsRearMax = p.readFloat(Bit.AFT_SHIELD_MAX, -1);

                p.readShort(Bit.UNK_8);
                
                // ????
                p.readByte(Bit.UNK_9, (byte) 0);

                elite = p.readInt(Bit.UNK_10);
                eliteState = p.readInt(Bit.UNK_11); // what abilities are active?

                scanned = p.readInt(Bit.UNK_12);  // confirmed

                p.readInt(Bit.UNK_13); // confirmed

                // TODO These must be system damages!
                p.readInt(Bit.UNK_14); // confirmed
                p.readInt(Bit.UNK_15);
                p.readInt(Bit.UNK_16);
                p.readInt(Bit.UNK_17);
                p.readInt(Bit.UNK_18);
                p.readInt(Bit.UNK_19);
                p.readInt(Bit.UNK_20);
                p.readInt(Bit.UNK_21);

                // shield frequencies
                for (int i = 0; i < SHLD_FREQS.length; i++) {
                    freqs[i] = p.readFloat(SHLD_FREQS[i], -1);
                }

                p.readByte(Bit.UNK_22, (byte) -1);
                p.readByte(Bit.UNK_23, (byte) -1);
                p.readFloat(Bit.UNK_24, -1);
                p.readFloat(Bit.UNK_25, -1);
                p.readFloat(Bit.UNK_26, -1);

                if (p.has(Bit.UNK_27) && p.hasMore()) {
                	p.readFloat(Bit.UNK_27, -1);
                }

                if (p.has(Bit.UNK_28) && p.hasMore()) {
                	p.readFloat();
                }

                if (p.has(Bit.UNK_29) && p.hasMore()) {
                	p.readInt();
                }

                ArtemisEnemy enemy = new ArtemisEnemy(p.getTargetId(), name, hullId);
                enemy.setScanned((byte) scanned);
                enemy.setEliteBits(elite);
                enemy.setEliteState(eliteState);

                
                // shared updates
                enemy.setX(x);
                enemy.setY(y);
                enemy.setZ(z);
                
                enemy.setSteering(steering);
                enemy.setBearing(bearing);
                enemy.setVelocity(velocity);
                
                enemy.setShieldsFront(shieldsFront);
                enemy.setShieldsFrontMax(shieldsFrontMax);
                enemy.setShieldsRear(shieldsRear);
                enemy.setShieldsRearMax(shieldsRearMax);
                
                for (int i=0; i<freqs.length; i++) {
                    enemy.setShieldFreq(i, freqs[i]);
                }
                
                mObjects.add(enemy);
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
    	StringBuilder b = new StringBuilder();

    	for (ArtemisPositionable obj : mObjects) {
    		b.append(obj).append('\n');
    	}

    	return b.toString();
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