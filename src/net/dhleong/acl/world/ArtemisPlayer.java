package net.dhleong.acl.world;

import java.util.Arrays;
import java.util.SortedMap;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.MainScreenView;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.util.BoolState;

/**
 * A player ship
 * 
 * @author dhleong
 *
 */
public class ArtemisPlayer extends BaseArtemisShip {
    /** constant result of getTubeContents(), means NOTHING */
    public static final int TUBE_EMPTY = -1;

    /** constant result of getTubeContents(), means we DON'T KNOW */
    public static final int TUBE_UNKNOWN = Integer.MIN_VALUE;

    private BoolState mAutoBeams, mRedAlert, mShields;
    private int mShipNumber = -1;
    private final float[] mHeat = new float[Artemis.SYSTEM_COUNT];
    private final float[] mSystems = new float[Artemis.SYSTEM_COUNT];
    private final int[] mCoolant = new int[Artemis.SYSTEM_COUNT];
    private final int[] mTorpedos = new int[OrdnanceType.COUNT];
    private final float[] mTubeTimes = new float[Artemis.MAX_TUBES]; 
    private final int[] mTubeTypes = new int[Artemis.MAX_TUBES];
    private float mEnergy = -1;
    private int mDockingStation = 0;
    private MainScreenView mMainScreen;
    private int mAvailableCoolant = -1;
    private float mImpulse = -1;
    private byte mWarp = -1;
    private BeamFrequency mBeamFreq;
    private DriveType mDriveType;
    private BoolState mReverse;
    private int mScanTarget = -1;
    private float mScanProgress = -1;
    private int mCaptainTarget = -1;
    private int mScanningId = 0;
    
    /**
     * Special constructor for a very incomplete ArtemisPlayer
     * @param objId
     */
    public ArtemisPlayer(int objId) {
        this(objId, null, -1, -1, BoolState.UNKNOWN, BoolState.UNKNOWN); // ?
        setSteering(Float.MIN_VALUE);
        setBearing(Float.MIN_VALUE);
        setVelocity(Float.MIN_VALUE);
        setHullId(-1);
    }

    /**
     * 
     * @param objId
     * @param name
     * @param hullId
     * @param shipNumber The number [1,6] of the ship,
     *  as found in the packet. NOT the ship index!
     * @param redAlert
     */
    public ArtemisPlayer(int objId, String name, int hullId, 
            int shipNumber, BoolState redAlert, BoolState shields) {
        super(objId, name, hullId);
        
        mRedAlert = redAlert;
        mShields = shields;
        mShipNumber = shipNumber;
        
        // pre-fill
        for (int i = 0; i < Artemis.SYSTEM_COUNT; i++) {
            mHeat[i] = -1;
            mSystems[i] = -1;
            mCoolant[i] = -1;
        }

        Arrays.fill(mTorpedos, -1);
        Arrays.fill(mTubeTypes, TUBE_UNKNOWN);
        Arrays.fill(mTubeTimes, -1);
    }
    

    public float getEnergy() {
        return mEnergy;
    }
    
    /**
     * Get coolant setting for a system, if we have it
     * @param sys
     * @return The setting as an int [0, 8], or -1 if we don't know
     */
    public int getSystemCoolant(ShipSystem sys) {
//        return mCoolant.containsKey(sys)
//                ? mCoolant.get(sys)
//                : -1;
        return mCoolant[sys.ordinal()];
    }

    /**
     * Get this ship's player ship index. This
     *  is NOT the displayed number, but the INDEX
     *  (used in SystemManager#getPlayerShip) 
     * @return int in [0,5], or -1 if undefined
     */
    public int getShipIndex() {
        return mShipNumber == -1 ? -1 : mShipNumber-1;
    }
    
    /**
     * Get the energy setting for a system, if we have it
     * @param sys
     * @return The setting as a float [0, 1] where 1f == 300%,
     *  or -1 if we don't know
     */
    public float getSystemEnergy(ShipSystem sys) {
//        return mSystems.containsKey(sys) 
//                ? mSystems.get(sys) 
//                : -1f;
        return mSystems[sys.ordinal()];
    }
    
    public float getSystemHeat(ShipSystem sys) {
//      return mSystems.containsKey(sys) 
//              ? mSystems.get(sys) 
//              : -1f;
      return mHeat[sys.ordinal()];
  }
    
    @Override
    public ObjectType getType() {
        return ObjectType.PLAYER_SHIP;
    }
    
    public boolean hasShieldsActive() {
        return mShields == BoolState.TRUE;
    }

    public boolean isRedAlert() {
        return mRedAlert == BoolState.TRUE;
    }

    public void setRedAlert(boolean newState) {
        mRedAlert = BoolState.from(newState);
    }

    public void setShields(boolean newState) {
        mShields = BoolState.from(newState);
    }

    public void setSystemCoolant(ShipSystem sys, int coolant) {
        mCoolant[sys.ordinal()] = coolant;
    }
    
    /**
     * Convenience, set energy as an int percentage [0, 300]
     * @param sys
     * @param energyPercentage
     */
    public void setSystemEnergy(ShipSystem sys, int energyPercentage) {
        setSystemEnergy(sys, energyPercentage / (float) Artemis.MAX_ENERGY_ALLOCATION_PERCENT);
    }

    public void setSystemEnergy(ShipSystem sys, float energy) {
        if (energy > 1f) {
            throw new IllegalArgumentException("Illegal energy value: " + energy);
        }
        mSystems[sys.ordinal()] = energy;
    }

    public void setSystemHeat(ShipSystem sys, float heat) {
        mHeat[sys.ordinal()] = heat;
    }


    public void setShipEnergy(float energy) {
        mEnergy = energy;
    }

    /**
     * Get the ID of the station at which we're docking
     * @return The station's ID, or 0 if not docking
     */
    public int getDockingStation() {
        return mDockingStation;
    }

    /**
     * set 0 to indicate not docked
     */
    public void setDockingStation(int stationId) {
        mDockingStation = stationId;
    }

    public int getTorpedoCount(OrdnanceType type) {
        return mTorpedos[type.ordinal()];
    }

    public void setTorpedoCount(int torpType, int count) {
        mTorpedos[torpType] = count;
    }
    
    @Override
    public void updateFrom(ArtemisPositionable eng) {
        super.updateFrom(eng);
        
        // it should be!
        if (eng instanceof ArtemisPlayer) {
            ArtemisPlayer plr = (ArtemisPlayer) eng;

            if (mShipNumber == -1) {
                mShipNumber = plr.mShipNumber;
            }
            
            if (plr.mAutoBeams != BoolState.UNKNOWN) {
            	mAutoBeams = plr.mAutoBeams;
            }

            if (plr.mImpulse != -1) {
                mImpulse = plr.mImpulse;
            }

            if (plr.mWarp != -1) {
            	mWarp = plr.mWarp;
            }

            if (plr.mImpulse != -1 || plr.mWarp != -1) {
            	mDockingStation = 0;
            }

            if (plr.mBeamFreq != null) {
            	mBeamFreq = plr.mBeamFreq;
            }

            if (plr.mDockingStation != 0) {
                mDockingStation = plr.mDockingStation;
            }
            
            if (plr.mRedAlert != BoolState.UNKNOWN)
                mRedAlert = plr.mRedAlert;

            if (plr.mShields != BoolState.UNKNOWN)
                mShields = plr.mShields;
            
            if (plr.mEnergy != -1)
                mEnergy = plr.mEnergy;
            
            if (plr.mAvailableCoolant != -1)
                mAvailableCoolant = plr.mAvailableCoolant;
            
            if (plr.mDriveType != null)
                mDriveType = plr.mDriveType;
            
            for (int i = 0; i < Artemis.SYSTEM_COUNT; i++) {
                if (plr.mHeat[i] != -1)
                    mHeat[i] = plr.mHeat[i];
                
                if (plr.mSystems[i] != -1)
                    mSystems[i] = plr.mSystems[i];
                
                if (plr.mCoolant[i] != -1)
                    mCoolant[i] = plr.mCoolant[i];
            }

            for (int i=0; i < OrdnanceType.COUNT; i++) {
                if (plr.mTorpedos[i] != -1)
                    mTorpedos[i] = plr.mTorpedos[i];
            }

            for (int i = 0; i < Artemis.MAX_TUBES; i++) {
                // just copy this
                if (plr.mTubeTimes[i] >= 0) {
                    mTubeTimes[i] = plr.mTubeTimes[i];
                    
                    // round down
                    if (mTubeTimes[i] < 0.05)
                        mTubeTimes[i] = 0;
                }
                

                if (plr.mTubeTypes[i] != TUBE_UNKNOWN)
                    mTubeTypes[i] = plr.mTubeTypes[i];
            }
            
            if (plr.mMainScreen != null) {
                mMainScreen = plr.mMainScreen;
            }
            
            if (BoolState.isKnown(plr.mReverse)) {
                mReverse = plr.mReverse;
            }
            
            if (plr.mScanTarget != -1) {
                mScanTarget = plr.mScanTarget;
            }
                
            if (plr.mScanProgress != -1) {
                mScanProgress = plr.mScanProgress;
            }
            
            if (plr.mCaptainTarget != -1) {
                mCaptainTarget = plr.mCaptainTarget;
            }
            
            if (plr.mScanningId != -1) {
                mScanningId = plr.mScanningId;
            }
        }
    }

    public BoolState getRedAlertState() {
        return mRedAlert;
    }
    
    public BoolState getReverseState() {
        return mReverse;
    }
    
    public BoolState getShieldsState() {
        return mShields;
    }

    /**
     * Set by PlayerUpdatePacket. 
     * @param tube Tube index number [0, 5]
     * @param timeToLoad Time in seconds until it's loaded
     * @param typeLoading Type of torpedo being loaded. This seems
     *  to only be set when the process first begins
     */
    public void setTubeStatus(int tube, float timeToLoad, int typeLoading) {
        mTubeTimes[tube] = timeToLoad;
        mTubeTypes[tube] = typeLoading;
    }

    public int getTubeContents(int tube) {
        return mTubeTypes[tube];
    }

    public float getTubeCountdown(int tube) {
        return mTubeTimes[tube];
    }

    public void setTubeContents(int tube, int contents) {
        mTubeTypes[tube] = contents;
    }

    /**
     * Sets the current main screen in use
     * @param mainScreen
     */
    public void setMainScreen(MainScreenView screen) {
        mMainScreen = screen;
    }
    
    /**
     * How much coolant do we have available?
     */
    public int getAvailableCoolant() {
        return mAvailableCoolant;
    }
    
    public void setAvailableCoolant(int availableCoolant) {
        mAvailableCoolant = availableCoolant;
    }

    public MainScreenView getMainScreen() {
        return mMainScreen;
    }

    public BeamFrequency getBeamFrequency() {
    	return mBeamFreq;
    }

    public void setBeamFrequency(BeamFrequency beamFreq) {
    	mBeamFreq = beamFreq;
	}

    /** Get the value of the impulse slider */
    public float getImpulse() {
        return mImpulse;
    }

    public void setImpulse(float impulseSlider) {
        mImpulse = impulseSlider;
    }

    public void setDriveType(DriveType driveType) {
        mDriveType = driveType;
    }

    /**
     * 
     * @return null if unknown, else either 
     *  {@link DriveType#WARP} or {@link DriveType#JUMP}
     */
    public DriveType getDriveType() {
        return mDriveType;
    }

    public BoolState getAutoBeams() {
    	return mAutoBeams;
    }

    public void setAutoBeams(BoolState autoBeams) {
    	mAutoBeams = autoBeams;
    }

    public void setReverse(BoolState reverse) {
        mReverse = reverse;
    }

    public void setScanTarget(int scanTarget) {
        mScanTarget = scanTarget;
    }

    public void setScanProgress(float scanProgress) {
        mScanProgress = scanProgress;
    }
    
    /**
     * Get the id of the current science target,
     *  that is, whatever object Science
     *  has selected
     *  
     * @return The id of the target, 1 if
     *  no target, or {@link Integer#MIN_VALUE}
     *  if unknown
     */
    public int getScienceTarget() {
        return mScanTarget;
    }
    
    /**
     * The progress of scanning as a percentage,
     *  or -1 if unknown
     * @return
     */
    public float getScanProgress() {
        return mScanProgress;
    }

    public void setCaptainTarget(int captainTarget) {
        mCaptainTarget = captainTarget;
    }
    
    /**
     * Like {@link #getScienceTarget()}, but for
     *  the captain's map selection
     * @return
     */
    public int getCaptainTarget() {
        return mCaptainTarget;
    }

    /**
     * Set the ID of the object being scanned
     * 
     * @param scanningId
     */
    public void setScanObjectId(int scanningId) {
        mScanningId = scanningId;
    }
    
    public int getScanObjectId() {
        return mScanningId;
    }

    public byte getWarp() {
    	return mWarp;
    }

    public void setWarp(byte warp) {
		mWarp = warp;
	}

    @Override
	public void appendObjectProps(SortedMap<String, Object> props, boolean includeUnspecified) {
    	super.appendObjectProps(props, includeUnspecified);
    	putProp(props, "Auto beams", mAutoBeams, includeUnspecified);
    	putProp(props, "Red alert", mRedAlert, includeUnspecified);
    	putProp(props, "Shield state", mShields, includeUnspecified);
    	putProp(props, "Player ship number", mShipNumber, -1, includeUnspecified);

    	for (ShipSystem system : ShipSystem.values()) {
    		int i = system.ordinal();
    		putProp(props, "System heat: " + system, mHeat[i], -1, includeUnspecified);
    		putProp(props, "System energy: " + system, mSystems[i], -1, includeUnspecified);
    		putProp(props, "System coolant: " + system, mCoolant[i], -1, includeUnspecified);
    	}

    	OrdnanceType[] ordValues = OrdnanceType.values();

    	for (OrdnanceType ordnanceType : ordValues) {
    		int i = ordnanceType.ordinal();
    		putProp(props, "Ordnance count: " + ordnanceType, mTorpedos[i], -1, includeUnspecified);
    	}

    	for (int i = 0; i < Artemis.MAX_TUBES; i++) {
    		int ordType = mTubeTypes[i];
    		String ordName;

    		if (ordType == -1) {
				ordName = "empty";
			} else if (ordType == TUBE_UNKNOWN) {
				ordName = null;
			} else {
				ordName = ordValues[ordType].toString();
			}

    		putProp(props, "Tube " + i + " contents", ordName, includeUnspecified);
    		putProp(props, "Tube " + i + " countdown", mTubeTimes[i], -1, includeUnspecified);
    	}

    	putProp(props, "Energy", mEnergy, -1, includeUnspecified);
    	putProp(props, "Docking station", mDockingStation, 0, includeUnspecified);
    	putProp(props, "Main screen view", mMainScreen, includeUnspecified);
    	putProp(props, "Coolant", mAvailableCoolant, -1, includeUnspecified);
    	putProp(props, "Impulse", mImpulse, -1, includeUnspecified);
    	putProp(props, "Warp", mWarp, -1, includeUnspecified);
    	putProp(props, "Beam frequency", mBeamFreq, includeUnspecified);
    	putProp(props, "Drive type", mDriveType, includeUnspecified);
    	putProp(props, "Reverse", mReverse, includeUnspecified);
    	putProp(props, "Scan target", mScanTarget, -1, includeUnspecified);
    	putProp(props, "Scan progress", mScanProgress, -1, includeUnspecified);
    	putProp(props, "Scan object ID", mScanningId, -1, includeUnspecified);
    	putProp(props, "Captain target", mCaptainTarget, -1, includeUnspecified);
    }
}