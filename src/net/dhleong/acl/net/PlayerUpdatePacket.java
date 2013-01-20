package net.dhleong.acl.net;

import java.io.IOException;
import java.io.OutputStream;

import net.dhleong.acl.ArtemisPacket;
import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.setup.SetShipSettingsPacket.DriveType;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.util.TextUtil;
import net.dhleong.acl.world.ArtemisPlayer;
import net.dhleong.acl.world.ArtemisPlayer.MainScreen;

public class PlayerUpdatePacket implements ArtemisPacket {
    
    private static final byte ACTION_DUNNO_0    = (byte) 0x01;
    
    private static final byte IMPULSE_SLIDER    = (byte) 0x02;
    private static final byte STEERING_SLIDER   = (byte) 0x04;
    private static final byte TOP_SPEED         = (byte) 0x08;
    private static final byte TURN_RATE         = (byte) 0x10;
    
    private static final byte ACTION_DUNNO_5    = (byte) 0x20;
    private static final byte ACTION_DUNNO_6    = (byte) 0x40;

    private static final byte ACTION_ENERGY  = (byte) 0x80;
    
    
    private static final int SHIELD_STATE  = 0x00000001;
    private static final int SHIP_NUMBER   = 0x00000002;
    /* I think so? */
    private static final int HULL_ID       = 0x00000004;
    private static final int POS_X         = 0x00000008;
    private static final int POS_Y         = 0x00000010;
    private static final int POS_Z         = 0x00000020;
    private static final int DUNNO_SKIP_3  = 0x00000040;
    private static final int DUNNO_SKIP_4  = 0x00000080;
    private static final int BEARING       = 0x00000100;
    
    private static final int VELOCITY      = 0x00000200;
    private static final int UNKNOWN_2     = 0x00000400;
    private static final int SHIP_NAME     = 0x00000800;
    
    private static final int SHLD_FRONT    = 0x00001000;
    private static final int SHLD_FRONT_MAX= 0x00002000;
    private static final int SHLD_REAR     = 0x00004000;
    private static final int SHLD_REAR_MAX = 0x00008000;
    
    private static final int DOCKING_STATION     = 0x00010000;

    private static final int RED_ALERT     = 0x00020000;
    
    private static final int UNKNOWN_FLT_0 = 0x00040000;
    private static final int MAIN_SCREEN   = 0x00080000;
    private static final int UNKNOWN_7     = 0x00100000;
    private static final int AVAILABLE_COOLANT     = 0x00200000;
    private static final int UNKNOWN_9     = 0x00400000;
    private static final int UNKNOWN_10    = 0x00800000;
    private static final int DRIVE_TYPE    = 0x01000000;
    
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
    
    private static final long TORP_HOMING   = 0x0002000000000000L;
    private static final long TORP_NUKES    = 0x0004000000000000L;
    private static final long TORP_MINES    = 0x0008000000000000L;
    private static final long TORP_ECMS     = 0x0010000000000000L;

    private static final long UNKNOWN_BYTE  = 0x0020000000000000L;

    private static final long TUBE_TIME_1   = 0x0040000000000000L;
    private static final long TUBE_TIME_2   = 0x0080000000000000L;
    private static final long TUBE_TIME_3   = 0x0100000000000000L;
    private static final long TUBE_TIME_4   = 0x0200000000000000L;
    private static final long TUBE_TIME_5   = 0x0400000000000000L;
    private static final long TUBE_TIME_6   = 0x0800000000000000L;

    /* IE: is this tube in use? */
    private static final long TUBE_USE_1    = 0x1000000000000000L;
    private static final long TUBE_USE_2    = 0x2000000000000000L;
    private static final long TUBE_USE_3    = 0x4000000000000000L;
    private static final long TUBE_USE_4    = 0x8000000000000000L;

    private static final int TUBE_USE_5     = 0x0001;
    private static final int TUBE_USE_6     = 0x0002;

    private static final int TUBE_TYPE_1    = 0x0004;
    private static final int TUBE_TYPE_2    = 0x0008;
    private static final int TUBE_TYPE_3    = 0x0001;
    private static final int TUBE_TYPE_4    = 0x0002;
    private static final int TUBE_TYPE_5    = 0x0004;
    private static final int TUBE_TYPE_6    = 0x0008;
    
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

    private static final long[] TORPEDOS = {
        TORP_HOMING, TORP_NUKES, TORP_MINES, TORP_ECMS
    };

    private static final long[] TUBE_TIMES = {
        TUBE_TIME_1, TUBE_TIME_2, TUBE_TIME_3,
        TUBE_TIME_4, TUBE_TIME_5, TUBE_TIME_6,
    };

    private static final int[] TUBE_TYPES = {
        TUBE_TYPE_1, TUBE_TYPE_2, TUBE_TYPE_3,
        TUBE_TYPE_4, TUBE_TYPE_5, TUBE_TYPE_6,
    };

    private final byte[] mData;
    
    //
    private ArtemisPlayer mPlayer;
    
    String name;
    int shipNumber, hullId;
    float energy;
    float x, y, z, bearing;
    BoolState mRedAlert, mShields;
    MainScreen mainScreen;
    
    float shieldsFront, shieldsFrontMax;
    float shieldsRear, shieldsRearMax;
    int dockingStation;
    int availableCoolant;
    
    float[] heat = new float[ SYSTEMS_HEAT.length ];
    float[] sysEnergy = new float[ SYSTEMS_ENRG.length ];
    int[] coolant = new int[ COOLANTS.length ];
    int[] torps = new int[ TORPEDOS.length ];

    float[] tubeTimes = new float[TUBE_TIMES.length];
    int[] tubeContents = new int[TUBE_TIMES.length];

    public float velocity;

    private float impulseSlider;

    public float steeringSlider;

    private byte driveType;

    private float topSpeed;

    private float turnRate;

//    public PlayerUpdatePacket(final SystemInfoPacket pkt) {
//        this(pkt.mData);
//    }

    public PlayerUpdatePacket(byte[] data) {

        mData = data;
        
        ObjectParser p = new ObjectParser(mData, 0);
        p.start(true);
        
        try {
            int extraArgs = p.readShort();

            p.readInt(ACTION_DUNNO_0);

            impulseSlider = p.readFloat(IMPULSE_SLIDER, -1); 
            steeringSlider = p.readFloat(STEERING_SLIDER, Float.MIN_VALUE);
            
            topSpeed = p.readFloat(TOP_SPEED, -1);
            turnRate = p.readFloat(TURN_RATE, -1);

            // ???
            p.readByte(ACTION_DUNNO_5, (byte)0);

            // warp speed?
            p.readByte(ACTION_DUNNO_6, (byte)-1);

            energy = p.readFloat(ACTION_ENERGY, -1);
            
            if (p.has(SHIELD_STATE)) {
                mShields = BoolState.from(p.readShort() != 0);
            } else {
                mShields = BoolState.UNKNOWN;
            }

            shipNumber = p.readInt(SHIP_NUMBER);
            hullId = p.readInt(HULL_ID);

            x = p.readFloat(POS_X, -1);
            y = p.readFloat(POS_Y, -1);
            z = p.readFloat(POS_Z, -1);

            p.readInt(DUNNO_SKIP_3);
            p.readInt(DUNNO_SKIP_4);

            bearing = p.readFloat(BEARING, Float.MIN_VALUE);
            velocity = p.readFloat(VELOCITY, -1);

            p.readByte(UNKNOWN_2, (byte)0); 
            p.readByte(UNKNOWN_2, (byte)0); 

//            // wtf? hax!?
//            if (p.has(VELOCITY) && p.has(UNKNOWN_2))
//                //p.readShort(SHIP_NAME);
//                p.readByte(SHIP_NAME, (byte)0);
            
            name = p.readName(SHIP_NAME);

            shieldsFront = p.readFloat(SHLD_FRONT, -1);
            shieldsFrontMax = p.readFloat(SHLD_FRONT_MAX, -1);
            shieldsRear = p.readFloat(SHLD_REAR, -1);
            shieldsRearMax = p.readFloat(SHLD_REAR_MAX, -1);

            // I don't *think* the server sends us
            //  this value when we undock...
            dockingStation = p.readInt(DOCKING_STATION, 0);

            if (p.has(RED_ALERT)) {
                mRedAlert = (p.readByte() != 0) ? BoolState.TRUE : BoolState.FALSE;
            } else {
                mRedAlert = BoolState.UNKNOWN;
            }
            
            p.readInt(UNKNOWN_FLT_0);

            if (p.has(MAIN_SCREEN))
                mainScreen = MainScreen.values()[p.readByte()];
            else
                mainScreen = null;

            //p.readShort(UNKNOWN_7);
            p.readByte(UNKNOWN_7, (byte)0);
            //p.readInt(UNKNOWN_7);

            // total available coolant?
            availableCoolant = p.readByte(AVAILABLE_COOLANT, (byte)-1); // MUST

            p.readInt(UNKNOWN_9);
            //p.readShort(UNKNOWN_9);

            //p.readByte(UNKNOWN_10, (byte)-1); 
            //p.readShort(UNKNOWN_10);
            p.readInt(UNKNOWN_10);

            driveType = p.readByte(DRIVE_TYPE, (byte)-1); 
            

            for (int i=0; i<heat.length; i++) {
                heat[i] = p.readFloat(SYSTEMS_HEAT[i], -1);
            }

            for (int i=0; i<sysEnergy.length; i++) {
                sysEnergy[i] = p.readFloat(SYSTEMS_ENRG[i], -1);
            }

            for (int i=0; i<coolant.length; i++) {
                coolant[i] = p.readByte(COOLANTS[i], (byte)-1);
            }

            for (int i=0; i<torps.length; i++) {
                torps[i] = ((byte)0xff & p.readByte(TORPEDOS[i], (byte)-1));
            }

            p.readByte(UNKNOWN_BYTE, (byte)-1);
   
            for (int i=0; i<TUBE_TIMES.length; i++) {
                tubeTimes[i] = p.readFloat(TUBE_TIMES[i], -1);
            }

            // after this, tubeContents[i]...
            // = 0 means that tube is EMPTY; 
            // > 0 means that tube is IN USE;
            // < 0  means we DON'T KNOW
            tubeContents[0] = p.readByte(TUBE_USE_1, (byte)-1);
            tubeContents[1] = p.readByte(TUBE_USE_2, (byte)-1);
            tubeContents[2] = p.readByte(TUBE_USE_3, (byte)-1);
            tubeContents[3] = p.readByte(TUBE_USE_4, (byte)-1);

            p.setArgs(extraArgs);

            tubeContents[4] = p.readByte(TUBE_USE_5, (byte)-1);
            tubeContents[5] = p.readByte(TUBE_USE_6, (byte)-1);

            // after this, tubeContents[i]...
            // = -1 means EMPTY;
            // = Integer.MIN_VALUE means we DON'T KNOW
            // else the type of torpedo there
            for (int i=0; i<TUBE_TYPES.length; i++) {
                byte torpType = p.readByte(TUBE_TYPES[i], (byte)-1);
                if (tubeContents[i] == 0)
                    tubeContents[i] = ArtemisPlayer.TUBE_EMPTY; // empty tube
                else if (tubeContents[i] < 0) {
                    // what's there? I don't even know
                    tubeContents[i] = ArtemisPlayer.TUBE_UNKNOWN;
                } else if (tubeContents[i] > 0 && torpType != (byte) -1)
                    tubeContents[i] = torpType;
                else
                    // IE: it's "in use" but type is unspecified/changed
                    //  DO we need another constant for this?
                    tubeContents[i] = ArtemisPlayer.TUBE_UNKNOWN; 
            }

            mPlayer = new ArtemisPlayer(p.getTargetId(), name, hullId, 
                shipNumber, mRedAlert, mShields);
            mPlayer.setTopSpeed(topSpeed);
            mPlayer.setTurnRate(turnRate);
            mPlayer.setImpulse(impulseSlider);
            mPlayer.setSteering(steeringSlider);
            mPlayer.setX(x);
            mPlayer.setY(y);
            mPlayer.setZ(z);
            mPlayer.setBearing(bearing);
            mPlayer.setVelocity(velocity);
            mPlayer.setShipEnergy(energy);
            mPlayer.setDockingStation(dockingStation);
            mPlayer.setMainScreen(mainScreen);
            mPlayer.setAvailableCoolant(availableCoolant);
            
            mPlayer.setShieldsFront(shieldsFront);
            mPlayer.setShieldsFrontMax(shieldsFrontMax);
            mPlayer.setShieldsRear(shieldsRear);
            mPlayer.setShieldsRearMax(shieldsRearMax);
            
            mPlayer.setDriveType(driveType == -1
                    ? null
                    : DriveType.values()[driveType]);
            
            for (int i=0; i<SYSTEMS_HEAT.length; i++) {
                SystemType sys = SystemType.values()[i];
                mPlayer.setSystemHeat(sys, heat[i]);
                mPlayer.setSystemEnergy(sys, sysEnergy[i]);
                mPlayer.setSystemCoolant(sys, coolant[i]);
            }

            for (int i=0; i<TORPEDOS.length; i++) {
                mPlayer.setTorpedoCount(i, torps[i]);
            }

            for (int i=0; i<TUBE_TIMES.length; i++) {
                mPlayer.setTubeStatus(i, tubeTimes[i], tubeContents[i]);
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
        return ArtemisPacket.WORLD_TYPE;
    }
    
    @Override
    public String toString() {
        return TextUtil.byteArrayToHexString(mData); 
    }

    public void debugPrint() {
        System.out.println(String.format("DEBUG: %s:%d(%.0f)@[%.2f,%.2f,%.2f]<%.2f>", 
                name, hullId, energy, x, y, z, bearing));
        System.out.println("-------Ship numb: " + shipNumber);
        System.out.println("-------Red Alert: " + mRedAlert);
        System.out.println("-------ShieldsUp: " + mShields);
        System.out.println("---------Coolant: " + availableCoolant);
        if (driveType != -1)
            System.out.println("-----------Drive: " + DriveType.values()[driveType]);
        System.out.println(String.format("-------Torp Cnts: %d:%d:%d:%d", 
            torps[0], torps[1], torps[2], torps[3]));
        System.out.println(String.format("-------[%.1f/%.2f  %.1f,%.1f]", 
                shieldsFront, shieldsFrontMax, shieldsRear, shieldsRearMax));
        for (int i=0; i<heat.length; i++) {
            System.out.println(SystemType.values()[i] + 
                    "= " + sysEnergy[i] +
                    " :: " + heat[i] + 
                    " :: " + coolant[i]);
        }
        for (int i=0; i<TUBE_TIMES.length; i++) {
            System.out.println(String.format("Tube#%d: (%f) %d", 
                i, tubeTimes[i], tubeContents[i]));
        }
    }

    public ArtemisPlayer getPlayer() {
        return mPlayer;
    }

//    public static boolean isExtensionOf(SystemInfoPacket pkt) {
//        return (pkt.getTargetType() == ArtemisObject.TYPE_PLAYER);
//    }

    public BoolState getRedAlert() {
        return mRedAlert;
    }
}
