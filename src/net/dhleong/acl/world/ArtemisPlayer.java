package net.dhleong.acl.world;

import java.util.Arrays;
import java.util.SortedMap;

import net.dhleong.acl.enums.BeamFrequency;
import net.dhleong.acl.enums.DriveType;
import net.dhleong.acl.enums.MainScreenView;
import net.dhleong.acl.enums.ObjectType;
import net.dhleong.acl.enums.OrdnanceType;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.enums.TubeState;
import net.dhleong.acl.util.BoolState;

/**
 * A player ship.
 * @author dhleong
 */
public class ArtemisPlayer extends BaseArtemisShip {
    private BoolState mAutoBeams, mRedAlert, mShields;
    private int mShipNumber = -1;
    private final float[] mHeat = new float[Artemis.SYSTEM_COUNT];
    private final float[] mSystems = new float[Artemis.SYSTEM_COUNT];
    private final int[] mCoolant = new int[Artemis.SYSTEM_COUNT];
    private final int[] mTorpedos = new int[OrdnanceType.COUNT];
    private final float[] mTubeTimes = new float[Artemis.MAX_TUBES];
    private final TubeState[] mTubeState = new TubeState[Artemis.MAX_TUBES];
    private final byte[] mTubeContents = new byte[Artemis.MAX_TUBES];
    private float mEnergy = -1;
    private int mDockingBase = -1;
    private MainScreenView mMainScreen;
    private int mAvailableCoolant = -1;
    private int mWeaponsTarget = -1;
    private float mImpulse = -1;
    private byte mWarp = -1;
    private BeamFrequency mBeamFreq;
    private DriveType mDriveType;
    private BoolState mReverse;
    private int mScienceTarget = -1;
    private float mScanProgress = -1;
    private int mCaptainTarget = -1;
    private int mScanningId = -1;
    
    /**
     * Special constructor for a very incomplete ArtemisPlayer
     * @param objId
     */
    public ArtemisPlayer(int objId) {
        this(objId, null, -1, -1, BoolState.UNKNOWN, BoolState.UNKNOWN);
    }

    /**
     * @param objId
     * @param name
     * @param hullId
     * @param shipNumber The number [1,Artemis.SHIP_COUNT] of the ship,
     *  as found in the packet. NOT the ship index!
     * @param redAlert
     */
    public ArtemisPlayer(int objId, String name, int hullId, int shipNumber,
    		BoolState redAlert, BoolState shields) {
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
        Arrays.fill(mTubeTimes, -1);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.PLAYER_SHIP;
    }
    
    /**
     * The ship's energy reserves.
     * Unspecified: -1
     */
    public float getEnergy() {
        return mEnergy;
    }

    public void setEnergy(float energy) {
        mEnergy = energy;
    }

    /**
     * Get this ship's player ship index. This is NOT the displayed number, but
     * the INDEX (used in SystemManager#getPlayerShip).
     * Unspecified: -1
     * @return int in [0,Artemis.SHIP_COUNT), or -1 if undefined
     */
    public int getShipIndex() {
        return mShipNumber == -1 ? -1 : mShipNumber - 1;
    }
    
    /**
     * The amount of coolant allocated to the given system.
     * Unspecified: -1
     */
    public int getSystemCoolant(ShipSystem sys) {
        return mCoolant[sys.ordinal()];
    }

    public void setSystemCoolant(ShipSystem sys, int coolant) {
        mCoolant[sys.ordinal()] = coolant;
    }
    
    /**
     * The energy allocation level for a system, as a value between 0 (no energy
     * allocated) and 1 (maximum energy allocated).
     * Unspecified: -1
     */
    public float getSystemEnergy(ShipSystem sys) {
        return mSystems[sys.ordinal()];
    }

    public void setSystemEnergy(ShipSystem sys, float energy) {
        if (energy > 1f) {
            throw new IllegalArgumentException("Illegal energy value: " + energy);
        }
        mSystems[sys.ordinal()] = energy;
    }

    /**
     * Convenience, set energy as an int percentage [0, 300]
     */
    public void setSystemEnergy(ShipSystem sys, int energyPercentage) {
        setSystemEnergy(sys, energyPercentage / (float) Artemis.MAX_ENERGY_ALLOCATION_PERCENT);
    }

    /**
     * The heat level for a system.
     * Unspecified: -1
     */
    public float getSystemHeat(ShipSystem sys) {
    	return mHeat[sys.ordinal()];
    }

    public void setSystemHeat(ShipSystem sys, float heat) {
        mHeat[sys.ordinal()] = heat;
    }

    /**
     * Whether the shields are up or not.
     */
    public BoolState getShieldsState() {
        return mShields;
    }

    public void setShields(boolean newState) {
        mShields = BoolState.from(newState);
    }

    /**
     * Whether or not we're at red alert.
     */
    public BoolState getRedAlertState() {
        return mRedAlert;
    }

    public void setRedAlert(boolean newState) {
        mRedAlert = BoolState.from(newState);
    }

    /**
     * Get the ID of the base at which we're docking. Note that this property is
     * only updated in a packet when the docking process commences; undocking
     * does not update this property. However, if an existing ArtemisPlayer
     * object is docked, is updated by another one, and the update has the ship
     * engaging impulse or warp drive, this property will be set to 0 to
     * indicate that the ship has undocked.
     * Unspecified: -1
     */
    public int getDockingBase() {
        return mDockingBase;
    }

    public void setDockingBase(int baseId) {
        mDockingBase = baseId;
    }

    /**
     * The number of torpedoes of the given type in the ship's stores.
     * Unspecified: -1
     */
    public int getTorpedoCount(OrdnanceType type) {
        return mTorpedos[type.ordinal()];
    }

    public void setTorpedoCount(int torpType, int count) {
        mTorpedos[torpType] = count;
    }

    /**
     * Whether or not the ship's impulse drive is in reverse.
     */
    public BoolState getReverseState() {
        return mReverse;
    }

    public void setReverse(BoolState reverse) {
        mReverse = reverse;
    }

    /**
     * The loading state of the given tube.
     * Unspecified: null
     */
    public TubeState getTubeState(int tube) {
    	return mTubeState[tube];
    }

    public void setTubeState(int tube, TubeState state) {
    	mTubeState[tube] = state;
    }

    /**
     * Returns a value indicating the contents of the tube. If the tube's state
     * is TubeState.UNLOADED, 0 means that the tube is empty. In any other
     * state, the number will be the ordinal value of the OrdnanceType in the
     * tube.
     * Unspecified: any negative number
     */
    public byte getTubeContentsValue(int tube) {
    	return mTubeContents[tube];
    }

    public void setTubeContentsValue(int tube, byte value) {
    	mTubeContents[tube] = value;
    }

    /**
     * Returns an OrdnanceType value indicating the contents of the tube. This
     * method will return null if the tube contents are unspecified, or if the
     * tube is empty.
     */
    public OrdnanceType getTubeContents(int tube) {
    	TubeState state = mTubeState[tube];
    	int contents = mTubeContents[tube];

    	if (state == null || contents < 0) {
    		return null;
    	}

    	return state == TubeState.UNLOADED ? null : OrdnanceType.values()[contents];
    }

    /**
     * Sents the contents of the given tube. The tube state must be set to a
     * value other than TubeState.UNLOADED before invoking this method.
     */
    public void setTubeContents(int tube, OrdnanceType type) {
    	TubeState state = mTubeState[tube];

    	if (state == null) {
    		throw new IllegalStateException("Tube state not set"); 
    	}

    	if (state == TubeState.UNLOADED) {
    		if (type != null) {
    			throw new IllegalArgumentException(
    					"Unloaded tubes cannot contain ordnance"
    			);
    		}

    		mTubeContents[tube] = 0;
    	} else {
    		if (type == null) {
    			throw new IllegalArgumentException(
    					"No OrdnanceType specified for " + state + " tube state"
    			);
    		}

    		mTubeContents[tube] = (byte) type.ordinal();
    	}
    }

    /**
     * Returns the number of seconds until the current (un)load operation is
     * complete.
     * Unspecified: -1
     */
    public float getTubeCountdown(int tube) {
        return mTubeTimes[tube];
    }

    public void setTubeCountdown(int tube, float seconds) {
    	mTubeTimes[tube] = seconds;
    }

    /**
     * Amount of coolant in the ship's reserves.
     * Unspecified: -1
     */
    public int getAvailableCoolant() {
        return mAvailableCoolant;
    }
    
    public void setAvailableCoolant(int availableCoolant) {
        mAvailableCoolant = availableCoolant;
    }

    /**
     * The view for the main screen.
     * Unspecified: null
     */
    public MainScreenView getMainScreen() {
        return mMainScreen;
    }

    public void setMainScreen(MainScreenView screen) {
        mMainScreen = screen;
    }

    /**
     * The frequency at which beam weapons are to be tuned.
     * Unspecified: null
     */
    public BeamFrequency getBeamFrequency() {
    	return mBeamFreq;
    }

    public void setBeamFrequency(BeamFrequency beamFreq) {
    	mBeamFreq = beamFreq;
	}

    /**
     * Impulse setting, as a value from 0 (all stop) and 1 (full impulse).
     * Unspecified: -1
     */
    public float getImpulse() {
        return mImpulse;
    }

    public void setImpulse(float impulseSlider) {
        mImpulse = impulseSlider;
    }

    /**
     * The type of drive system the ship has.
     * Unspecified: null
     */
    public DriveType getDriveType() {
        return mDriveType;
    }

    public void setDriveType(DriveType driveType) {
        mDriveType = driveType;
    }

    /**
     * Whether or not auto beams are enabled.
     * Unspecified: null
     */
    public BoolState getAutoBeams() {
    	return mAutoBeams;
    }

    public void setAutoBeams(BoolState autoBeams) {
    	mAutoBeams = autoBeams;
    }

    /**
     * The ID of the object targeted by the science officer. If the target is
     * cleared, this method returns 1.
     * Unspecified: -1
     */
    public int getScienceTarget() {
        return mScienceTarget;
    }

    public void setScienceTarget(int scienceTarget) {
        mScienceTarget = scienceTarget;
    }

    /**
     * The progress of the current scan, as a value between 0 and 1.
     * Unspecified: -1
     */
    public float getScanProgress() {
        return mScanProgress;
    }

    public void setScanProgress(float scanProgress) {
        mScanProgress = scanProgress;
    }
    
    /**
     * The ID of the object targeted by the captain officer. If the target is
     * cleared, this method returns 1.
     * Unspecified: -1
     */
    public int getCaptainTarget() {
        return mCaptainTarget;
    }

    public void setCaptainTarget(int captainTarget) {
        mCaptainTarget = captainTarget;
    }

    /**
     * The ID of the object targeted by the weapons officer. If the target is
     * cleared, this method returns 1.
     * Unspecified: -1
     */
    public int getWeaponsTarget() {
        return mWeaponsTarget;
    }

    public void setWeaponsTarget(int weaponsTarget) {
        mWeaponsTarget = weaponsTarget;
    }

    /**
     * The ID of the object being scanned. Note that this is distinct from the
     * science target: science can start a scan then change targets, but the
     * scan continues on the original target.
     * Unspecified: -1
     */
    public int getScanObjectId() {
        return mScanningId;
    }

    public void setScanObjectId(int scanningId) {
        mScanningId = scanningId;
    }

    /**
     * Warp factor, between 0 (not at warp) and Artemis.MAX_WARP.
     * Unspecified: -1
     */
    public byte getWarp() {
    	return mWarp;
    }

    public void setWarp(byte warp) {
		mWarp = warp;
	}

    @Override
    public void updateFrom(ArtemisObject eng) {
        super.updateFrom(eng);
        
        // it should be!
        if (eng instanceof ArtemisPlayer) {
            ArtemisPlayer plr = (ArtemisPlayer) eng;

            if (mShipNumber == -1) {
                mShipNumber = plr.mShipNumber;
            }
            
            if (BoolState.isKnown(plr.mAutoBeams)) {
            	mAutoBeams = plr.mAutoBeams;
            }

            if (plr.mWeaponsTarget != -1) {
                mWeaponsTarget = plr.mWeaponsTarget;
            }

            if (plr.mImpulse != -1) {
                mImpulse = plr.mImpulse;
            }

            if (plr.mWarp != -1) {
            	mWarp = plr.mWarp;
            }

            if (plr.mDockingBase != -1) {
                mDockingBase = plr.mDockingBase;
            } else if (plr.mImpulse != -1 || plr.mWarp != -1) {
            	mDockingBase = 0;
            }

            if (plr.mBeamFreq != null) {
            	mBeamFreq = plr.mBeamFreq;
            }

            if (BoolState.isKnown(plr.mRedAlert)) {
                mRedAlert = plr.mRedAlert;
            }

            if (BoolState.isKnown(plr.mShields)) {
                mShields = plr.mShields;
            }

            if (plr.mEnergy != -1) {
                mEnergy = plr.mEnergy;
            }
            
            if (plr.mAvailableCoolant != -1) {
                mAvailableCoolant = plr.mAvailableCoolant;
            }
            
            if (plr.mDriveType != null) {
                mDriveType = plr.mDriveType;
            }
            
            for (int i = 0; i < Artemis.SYSTEM_COUNT; i++) {
                if (plr.mHeat[i] != -1) {
                    mHeat[i] = plr.mHeat[i];
                }
                
                if (plr.mSystems[i] != -1) {
                    mSystems[i] = plr.mSystems[i];
                }
                
                if (plr.mCoolant[i] != -1) {
                    mCoolant[i] = plr.mCoolant[i];
                }
            }

            for (int i=0; i < OrdnanceType.COUNT; i++) {
                if (plr.mTorpedos[i] != -1) {
                    mTorpedos[i] = plr.mTorpedos[i];
                }
            }

            for (int i = 0; i < Artemis.MAX_TUBES; i++) {
            	float time = plr.mTubeTimes[i];

            	if (time >= 0) {
                	mTubeTimes[i] = time < 0.05f ? 0 : time;
                }

                TubeState state = plr.mTubeState[i];

                if (state != null) {
                	mTubeState[i] = state;
                }

                byte contents = plr.mTubeContents[i];

                if (contents != -1) {
                	mTubeContents[i] = contents;
                } else if (state == TubeState.UNLOADED) {
                	mTubeContents[i] = 0;
                }
            }

            if (plr.mMainScreen != null) {
                mMainScreen = plr.mMainScreen;
            }
            
            if (BoolState.isKnown(plr.mReverse)) {
                mReverse = plr.mReverse;
            }
            
            if (plr.mScienceTarget != -1) {
                mScienceTarget = plr.mScienceTarget;
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
    		TubeState state = mTubeState[i];
    		int contents = mTubeContents[i];
    		putProp(props, "Tube " + i + " state", state, includeUnspecified);
    		String contentsStr;

    		if (state != null && contents != -1) {
    			if (state == TubeState.UNLOADED) {
    				contentsStr = "EMPTY";
    			} else {
    				contentsStr = OrdnanceType.values()[contents].name();
    			}

    			putProp(props, "Tube " + i + " contents", contentsStr, includeUnspecified);
    		}

    		putProp(props, "Tube " + i + " countdown", mTubeTimes[i], -1, includeUnspecified);
    	}

    	putProp(props, "Energy", mEnergy, -1, includeUnspecified);
    	putProp(props, "Docking base", mDockingBase, -1, includeUnspecified);
    	putProp(props, "Main screen view", mMainScreen, includeUnspecified);
    	putProp(props, "Coolant", mAvailableCoolant, -1, includeUnspecified);
    	putProp(props, "Impulse", mImpulse, -1, includeUnspecified);
    	putProp(props, "Warp", mWarp, -1, includeUnspecified);
    	putProp(props, "Beam frequency", mBeamFreq, includeUnspecified);
    	putProp(props, "Drive type", mDriveType, includeUnspecified);
    	putProp(props, "Reverse", mReverse, includeUnspecified);
    	putProp(props, "Scan target", mScienceTarget, -1, includeUnspecified);
    	putProp(props, "Scan progress", mScanProgress, -1, includeUnspecified);
    	putProp(props, "Scan object ID", mScanningId, -1, includeUnspecified);
    	putProp(props, "Captain target", mCaptainTarget, -1, includeUnspecified);
    }
}
