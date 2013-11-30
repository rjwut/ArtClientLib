package net.dhleong.acl.net.player;


import net.dhleong.acl.net.setup.SetShipSettingsPacket.DriveType;
import net.dhleong.acl.util.BoolState;
import net.dhleong.acl.util.ObjectParser;
import net.dhleong.acl.world.ArtemisPlayer;
import net.dhleong.acl.world.ArtemisPlayer.MainScreen;

public class MainPlayerUpdatePacket extends PlayerUpdatePacket {
    private enum Bit {
    	UNK_0,
    	IMPULSE,
    	RUDDER,
    	TOP_SPEED,
    	TURN_RATE,
    	UNK_1,
    	UNK_2,	// warp speed?
    	ENERGY,

    	SHIELD_STATE,
    	SHIP_NUMBER,
    	SHIP_TYPE,
    	X,
    	Y,
    	Z,
    	UNK_3,
    	UNK_4,

    	HEADING,
    	VELOCITY,
    	UNK_5,
    	NAME,
    	FORE_SHIELDS,
    	FORE_SHIELDS_MAX,
    	AFT_SHIELDS,
    	AFT_SHIELDS_MAX,

    	DOCKING_STATION,
    	RED_ALERT,
    	UNK_6,
    	MAIN_SCREEN,
    	UNK_7,
    	AVAILABLE_COOLANT,
    	SCIENCE_TARGET,
    	CAPTAIN_TARGET,
    	DRIVE_TYPE,
    	SCAN_OBJECT_ID,
    	SCAN_PROGRESS,
    	REVERSE_STATE
    }

    /*
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
    // I think so?
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
    private static final int CAPTAIN_TARGET= 0x00800000;
    private static final int DRIVE_TYPE    = 0x01000000;
    private static final int SCAN_OBJECT_ID= 0x02000000;
    private static final int SCAN_PROGRESS = 0x04000000; 
    private static final int REVERSE_STATE = 0x08000000;
    */

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
    private int captainTarget;
    private int scanningId;

    public MainPlayerUpdatePacket(byte[] data) {
        super(data);
        ObjectParser p = new ObjectParser(mData, 0);
        p.start(Bit.values());
        
        try {
            p.readInt(Bit.UNK_0);

            impulseSlider = p.readFloat(Bit.IMPULSE, -1); 
            steeringSlider = p.readFloat(Bit.RUDDER, Float.MIN_VALUE);
            topSpeed = p.readFloat(Bit.TOP_SPEED, -1);
            turnRate = p.readFloat(Bit.TURN_RATE, -1);

            p.readByte(Bit.UNK_1, (byte)0); // ???
            p.readByte(Bit.UNK_2, (byte)-1); // warp speed?

            energy = p.readFloat(Bit.ENERGY, -1);
            
            if (p.has(Bit.SHIELD_STATE)) {
                mShields = BoolState.from(p.readShort() != 0);
            } else {
                mShields = BoolState.UNKNOWN;
            }

            shipNumber = p.readInt(Bit.SHIP_NUMBER);
            hullId = p.readInt(Bit.SHIP_TYPE);
            x = p.readFloat(Bit.X, -1);
            y = p.readFloat(Bit.Y, -1);
            z = p.readFloat(Bit.Z, -1);

            p.readInt(Bit.UNK_3);
            p.readInt(Bit.UNK_4);

            bearing = p.readFloat(Bit.HEADING, Float.MIN_VALUE);
            velocity = p.readFloat(Bit.VELOCITY, -1);

            p.readShort(Bit.UNK_5);

            name = p.readName(Bit.NAME);
            shieldsFront = p.readFloat(Bit.FORE_SHIELDS, -1);
            shieldsFrontMax = p.readFloat(Bit.FORE_SHIELDS_MAX, -1);
            shieldsRear = p.readFloat(Bit.AFT_SHIELDS, -1);
            shieldsRearMax = p.readFloat(Bit.AFT_SHIELDS_MAX, -1);

            // I don't *think* the server sends us
            //  this value when we undock...
            dockingStation = p.readInt(Bit.DOCKING_STATION, 0);
            mRedAlert = p.readBoolByte(Bit.RED_ALERT);
            
            p.readInt(Bit.UNK_6);

            if (p.has(Bit.MAIN_SCREEN))
                mainScreen = MainScreen.values()[p.readByte()];
            else
                mainScreen = null;

            p.readByte(Bit.UNK_7, (byte)0);

            // total available coolant?
            availableCoolant = p.readByte(Bit.AVAILABLE_COOLANT, (byte)-1); // MUST
            scanTarget = p.readInt(Bit.SCIENCE_TARGET, Integer.MIN_VALUE); // 1 means no target
            captainTarget = p.readInt(Bit.CAPTAIN_TARGET, Integer.MIN_VALUE);
            driveType = p.readByte(Bit.DRIVE_TYPE, (byte)-1);
            scanningId = p.readInt(Bit.SCAN_OBJECT_ID);
            scanProgress = p.readFloat(Bit.SCAN_PROGRESS, -1);
            mReverse = p.readBoolByte(Bit.REVERSE_STATE);

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
            mPlayer.setCaptainTarget(captainTarget);
            mPlayer.setScanObjectId(scanningId);
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

    @Override
    public ArtemisPlayer getPlayer() {
        return mPlayer;
    }
    
    @Override
    public String toString() {
        return "[" + name + "(" + hullId + ")] energy=" + energy + " coords=(" + x + "," + y +
        		"," + z + ") heading=" + bearing + " alert=" + mRedAlert + " shieldsUp=" + mShields;
    }
}
