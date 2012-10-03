package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisObject;
import net.dhleong.acl.world.ArtemisPlayer;

public class PlayerUpdatePacket implements ArtemisPacket {
    
    private static final byte ACTION_NAME_BYTE    = (byte) 0x01;
    
    private static final byte ACTION_DUNNO_1      = (byte) 0x02;
    private static final byte ACTION_DUNNO_2      = (byte) 0x04;
    private static final byte ACTION_DUNNO_3      = (byte) 0x08;
    private static final byte ACTION_DUNNO_4      = (byte) 0x10;
    private static final byte ACTION_DUNNO_5      = (byte) 0x20;
    
    private static final byte ACTION_DUNNO_6      = (byte) 0x40;

    private static final byte ACTION_UPDATE_BYTE  = (byte) 0x80;
    
    
    private static final long DUNNO_SKIP_2     = 0x0000000000000001L;
    private static final long SHIP_NUMBER   = 0x0000000000000002L;
    /* I think so? */
    private static final long HULL_ID       = 0x0000000000000004L;
    private static final long POS_X         = 0x0000000000000008L;
    private static final long POS_Y         = 0x0000000000000010L;
    private static final long POS_Z         = 0x0000000000000020L;
    private static final long DUNNO_SKIP_3  = 0x0000000000000040L;
    private static final long DUNNO_SKIP_4  = 0x0000000000000080L;
    private static final long BEARING       = 0x0000000000000100L;
    
    private static final long UNKNOWN_1     = 0x0000000000000200L;
    private static final long UNKNOWN_2     = 0x0000000000000400L;
    private static final long UNKNOWN_3     = 0x0000000000000800L;
    
    private static final long SHLD_FRONT    = 0x0000000000001000L;
    private static final long SHLD_FRONT_MAX= 0x0000000000002000L;
    private static final long SHLD_REAR     = 0x0000000000004000L;
    private static final long SHLD_REAR_MAX = 0x0000000000008000L;
    
    private static final long DOCKING_STATION     = 0x0000000000010000L;

    private static final long RED_ALERT     = 0x0000000000020000L;
    
    private static final long UNKNOWN_FLT_0 = 0x0000000000040000L;
    private static final long UNKNOWN_6     = 0x0000000000080000L;
    private static final long UNKNOWN_7     = 0x0000000000100000L;
    private static final long UNKNOWN_8     = 0x0000000000200000L;
    private static final long UNKNOWN_9     = 0x0000000000400000L;
    private static final long UNKNOWN_10    = 0x0000000000800000L;
    private static final long UNKNOWN_11    = 0x0000000001000000L;
    
    private static final long HEAT_BEAMS    = 0x0000000002000000L;
    private static final long HEAT_TORPS    = 0x0000000004000000L;
    private static final long HEAT_SENSR    = 0x0000000008000000L;
    private static final long HEAT_MANEU    = 0x0000000010000000L;
    private static final long HEAT_IMPLS    = 0x0000000020000000L;
    private static final long HEAT_JUMPS    = 0x0000000040000000L;
    private static final long HEAT_SFRNT    = 0x0000000080000000L;
    private static final long HEAT_SREAR    = 0x0000000100000000L;
    
    /** energy allocation from engineering */
    private static final long ENRG_BEAMS    = 0x0000000200000000L;
    private static final long ENRG_TORPS    = 0x0000000400000000L;
    private static final long ENRG_SENSR    = 0x0000000800000000L;
    private static final long ENRG_MANEU    = 0x0000001000000000L;
    private static final long ENRG_IMPLS    = 0x0000002000000000L;
    private static final long ENRG_JUMPS    = 0x0000004000000000L;
    private static final long ENRG_SFRNT    = 0x0000008000000000L;
    private static final long ENRG_SREAR    = 0x0000010000000000L;
    
    private static final long COOLANT_BEAMS = 0x0000020000000000L;
    private static final long COOLANT_TORPS = 0x0000040000000000L;
    private static final long COOLANT_SENSR = 0x0000080000000000L;
    private static final long COOLANT_MANEU = 0x0000100000000000L;
    private static final long COOLANT_IMPLS = 0x0000200000000000L;
    private static final long COOLANT_JUMPS = 0x0000400000000000L;
    private static final long COOLANT_SFRNT = 0x0000800000000000L;
    private static final long COOLANT_SREAR = 0x0001000000000000L;
    
    
    private static final long[] SYSTEMS_HEAT = {
        HEAT_BEAMS, HEAT_TORPS, HEAT_SENSR,
        HEAT_MANEU, HEAT_IMPLS, HEAT_JUMPS,
        HEAT_SFRNT, HEAT_SREAR
    };
    private static final long[] SYSTEMS_ENRG = {
        ENRG_BEAMS, ENRG_TORPS, ENRG_SENSR,
        ENRG_MANEU, ENRG_IMPLS, ENRG_JUMPS,
        ENRG_SFRNT, ENRG_SREAR
    };
    private static final long[] COOLANTS = {
        COOLANT_BEAMS, COOLANT_TORPS, COOLANT_SENSR,
        COOLANT_MANEU, COOLANT_IMPLS, COOLANT_JUMPS,
        COOLANT_SFRNT, COOLANT_SREAR
    };

    private final byte[] mData;
    
    //
    private ArtemisPlayer mPlayer;
    
    String name;
    int shipNumber, hullId;
    float energy;
    float x, y, z, bearing;
    BoolState mRedAlert;
    
    float shieldsFront, shieldsFrontMax;
    float shieldsRear, shieldsRearMax;
    int dockingStation;
    
    float[] heat = new float[ SYSTEMS_HEAT.length ];
    float[] sysEnergy = new float[ SYSTEMS_ENRG.length ];
    int[] coolant = new int[ COOLANTS.length ];

    public PlayerUpdatePacket(final SystemInfoPacket pkt) {
        this(pkt.mData);
    }

    public PlayerUpdatePacket(byte[] data) {

        mData = data;
        
        ObjectParser p = new ObjectParser(mData, 0);
        p.start(true);
        
        try {
            p.readShort();

            // ???
            p.readInt(ACTION_DUNNO_1); // float [0,1]?
            p.readInt(ACTION_DUNNO_2);
            p.readInt(ACTION_DUNNO_3);

            p.readInt(ACTION_DUNNO_4);
            p.readInt(ACTION_DUNNO_4);

            p.readByte(ACTION_DUNNO_5, (byte)0);

            // warp speed?
            p.readByte(ACTION_DUNNO_6, (byte)-1);

           
            energy = p.readFloat(ACTION_UPDATE_BYTE, -1);
            
            p.readShort(DUNNO_SKIP_2);

            shipNumber = p.readInt(SHIP_NUMBER);
            hullId = p.readInt(HULL_ID);

            x = p.readFloat(POS_X, -1);
            y = p.readFloat(POS_Y, -1);
            z = p.readFloat(POS_Z, -1);

            p.readInt(DUNNO_SKIP_3);
            p.readInt(DUNNO_SKIP_4);

            bearing = p.readFloat(BEARING, Float.MIN_VALUE);

            // 6 bytes
            p.readInt(UNKNOWN_1);
            p.readByte(UNKNOWN_2, (byte)0);

            // wtf? hax!?
            if (p.has(UNKNOWN_1) && p.has(UNKNOWN_2))
                //p.readShort(UNKNOWN_3);
                p.readByte(UNKNOWN_3, (byte)0);
            
            name = p.readName(ACTION_NAME_BYTE);

            shieldsFront = p.readFloat(SHLD_FRONT, -1);
            shieldsFrontMax = p.readFloat(SHLD_FRONT_MAX, -1);
            shieldsRear = p.readFloat(SHLD_REAR, -1);
            shieldsRearMax = p.readFloat(SHLD_REAR_MAX, -1);

            dockingStation = p.readInt(DOCKING_STATION);

            if (p.has(RED_ALERT)) {
                mRedAlert = (p.readByte() != 0) ? BoolState.TRUE : BoolState.FALSE;
            } else {
                mRedAlert = BoolState.UNKNOWN;
            }
            
            p.readInt(UNKNOWN_FLT_0);

            p.readByte(UNKNOWN_6, (byte)-1);
            //p.readShort(UNKNOWN_6);
            //p.readInt(UNKNOWN_6);

            //p.readShort(UNKNOWN_7);
            p.readByte(UNKNOWN_7, (byte)0);
            //p.readInt(UNKNOWN_7);

            // total available coolant?
            p.readByte(UNKNOWN_8, (byte)-1); // MUST

            p.readInt(UNKNOWN_9);
            p.readShort(UNKNOWN_9);

            //p.readByte(UNKNOWN_10, (byte)-1); 
            p.readShort(UNKNOWN_10);

            // I wonder if something in here
            //  indicates torpedo tube status? would
            //  explain the weird discrepancies
            //  sometimes seen... (differing torpedo
            //  tube counts)

            // this doesn't seem right...
            if (p.has(UNKNOWN_7))
                p.readByte(UNKNOWN_11, (byte)-1); 
            else {
                p.readByte(UNKNOWN_11, (byte)-1);
                p.readByte(UNKNOWN_11, (byte)-1);
                p.readByte(UNKNOWN_11, (byte)-1);
            }

            for (int i=0; i<heat.length; i++) {
                heat[i] = p.readFloat(SYSTEMS_HEAT[i], -1);
            }

            for (int i=0; i<sysEnergy.length; i++) {
                sysEnergy[i] = p.readFloat(SYSTEMS_ENRG[i], -1);
            }

            for (int i=0; i<coolant.length; i++) {
                coolant[i] = p.readByte(COOLANTS[i], -1);
            }
            
            mPlayer = new ArtemisPlayer(p.getTargetId(), name, hullId, 
                shipNumber, mRedAlert);
            mPlayer.setX(x);
            mPlayer.setY(y);
            mPlayer.setZ(z);
            mPlayer.setBearing(bearing);
            mPlayer.setShipEnergy(energy);
            mPlayer.setDockingStation(dockingStation);
            
            mPlayer.setShieldsFront(shieldsFront);
            mPlayer.setShieldsFrontMax(shieldsFrontMax);
            mPlayer.setShieldsRear(shieldsRear);
            mPlayer.setShieldsRearMax(shieldsRearMax);
            
            for (int i=0; i<SYSTEMS_HEAT.length; i++) {
                SystemType sys = SystemType.values()[i];
                mPlayer.setSystemHeat(sys, heat[i]);
                mPlayer.setSystemEnergy(sys, sysEnergy[i]);
                mPlayer.setSystemCoolant(sys, coolant[i]);
            }
        
        } catch (RuntimeException e) {
            System.out.println("!!! Error!");
            debugPrint();
            System.out.println("this -->" + this);
            throw e;
        }
    }

    @Override
    public long getMode() {
        return 0x01;
    }

    @Override
    public boolean write(OutputStream os) throws IOException {
        return false;
    }

    @Override
    public int getType() {
        return SystemInfoPacket.TYPE;
    }
    
    @Override
    public String toString() {
        return BaseArtemisPacket.byteArrayToHexString(mData); 
    }

    public void debugPrint() {
        System.out.println(String.format("DEBUG: %s:%d(%.0f)@[%.2f,%.2f,%.2f]<%.2f>", 
                name, hullId, energy, x, y, z, bearing));
        System.out.println("-------Ship numb: " + shipNumber);
        System.out.println("-------Red Alert: " + mRedAlert);
        System.out.println(String.format("-------[%.1f/%.2f  %.1f,%.1f]", 
                shieldsFront, shieldsFrontMax, shieldsRear, shieldsRearMax));
        for (int i=0; i<heat.length; i++) {
            System.out.println(SystemType.values()[i] + 
                    "= " + sysEnergy[i] +
                    " :: " + heat[i] + 
                    " :: " + coolant[i]);
        }
    }

    public ArtemisPlayer getPlayer() {
        return mPlayer;
    }

    public static boolean isExtensionOf(SystemInfoPacket pkt) {
        return (pkt.getTargetType() == ArtemisObject.TYPE_PLAYER);
    }

    public BoolState getRedAlert() {
        return mRedAlert;
    }
}
