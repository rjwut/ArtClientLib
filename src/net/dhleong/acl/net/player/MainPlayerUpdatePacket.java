package net.dhleong.acl.net.player;


import net.dhleong.acl.net.setup.SetShipSettingsPacket.DriveType;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisPlayer;
import net.dhleong.acl.world.ArtemisPlayer.MainScreen;

public class MainPlayerUpdatePacket extends PlayerUpdatePacket {
    
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
    private static final int SCI_TARGET    = 0x00400000; 
    private static final int UNKNOWN_10    = 0x00800000;
    private static final int DRIVE_TYPE    = 0x01000000;
    private static final int UNKNOWN_11    = 0x02000000;
    private static final int SCAN_PROGRESS = 0x04000000; 
    private static final int REVERSE_STATE = 0x08000000;
    
    
    //
    ArtemisPlayer mPlayer;
    
    String name;
    int shipNumber, hullId;
    float energy;
    float x, y, z, bearing;
    BoolState mRedAlert, mShields, mReverse;
    MainScreen mainScreen;
    
    float shieldsFront, shieldsFrontMax;
    float shieldsRear, shieldsRearMax;
    int dockingStation;
    int availableCoolant;
    public float velocity;

    private float impulseSlider;

    public float steeringSlider;

    byte driveType;

    private float topSpeed;

    private float turnRate;

    private float scanProgress;

    private int scanTarget;

//    public PlayerUpdatePacket(final SystemInfoPacket pkt) {
//        this(pkt.mData);
//    }

    public MainPlayerUpdatePacket(byte[] data) {
        super(data);
        
        ObjectParser p = new ObjectParser(mData, 0);
        p.start();
        
        try {
            //int extraArgs = p.readShort();

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

            mRedAlert = p.readBoolByte(RED_ALERT);
            
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

            scanTarget = p.readInt(SCI_TARGET, Integer.MIN_VALUE); // 1 means no target
            //p.readShort(UNKNOWN_9);

            //p.readByte(UNKNOWN_10, (byte)-1); 
            //p.readShort(UNKNOWN_10);
            p.readInt(UNKNOWN_10);
            
            driveType = p.readByte(DRIVE_TYPE, (byte)-1); 
            
            p.readInt(UNKNOWN_11);
            scanProgress = p.readFloat(SCAN_PROGRESS, -1);
            
            mReverse = p.readBoolByte(REVERSE_STATE);

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
            
            mPlayer.setScanTarget(scanTarget);
            mPlayer.setScanProgress(scanProgress);
            
            mPlayer.setShieldsFront(shieldsFront);
            mPlayer.setShieldsFrontMax(shieldsFrontMax);
            mPlayer.setShieldsRear(shieldsRear);
            mPlayer.setShieldsRearMax(shieldsRearMax);
            
            mPlayer.setDriveType(driveType == -1
                    ? null
                    : DriveType.values()[driveType]);
            mPlayer.setReverse(mReverse);
            
                 
        } catch (RuntimeException e) {
            System.out.println("!!! Error!");
            debugPrint();
            System.out.println("this -->" + this);
            throw e;
        }
    }
    
    
    @Override
    public void debugPrint() {
        System.out.println(String.format("DEBUG: %s:%d(%.0f)@[%.2f,%.2f,%.2f]<%.2f>", 
                name, hullId, energy, x, y, z, bearing));
        System.out.println("-------Ship numb: " + shipNumber);
        System.out.println("-------Red Alert: " + mRedAlert);
        System.out.println("-------ShieldsUp: " + mShields);
        System.out.println("---------Coolant: " + availableCoolant);
        if (driveType != -1)
            System.out.println("-----------Drive: " + DriveType.values()[driveType]);
        System.out.println(String.format("-------[%.1f/%.2f  %.1f,%.1f]", 
                shieldsFront, shieldsFrontMax, shieldsRear, shieldsRearMax));

    }


//    public static boolean isExtensionOf(SystemInfoPacket pkt) {
//        return (pkt.getTargetType() == ArtemisObject.TYPE_PLAYER);
//    }

    @Override
    public ArtemisPlayer getPlayer() {
        return mPlayer;
    }
}
