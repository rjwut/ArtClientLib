package net.dhleong.acl.world;

import java.util.Arrays;

import net.dhleong.acl.net.eng.EngSetEnergyPacket.SystemType;
import net.dhleong.acl.net.weap.LoadTubePacket;
import net.dhleong.acl.util.BoolState;

/**
 * A player ship
 * 
 * @author dhleong
 *
 */
public class ArtemisPlayer extends BaseArtemisShip {
    
    /** default amount of available coolant */
    public static final int DEFAULT_COOLANT = 8;
    
    /** maximum per-system value for coolant allocation */
    public static final int MAX_COOLANT_PER_SYSTEM = 8;

    /** I guess? Maybe...? */
    public static final int MAX_TUBES = 6;

    /** constant result of getTubeContents(), means NOTHING */
    public static final int TUBE_EMPTY = -1;

    /** constant result of getTubeContents(), means we DON'T KNOW */
    public static final int TUBE_UNKNOWN = Integer.MIN_VALUE;
    
    public enum MainScreen {
        FRONT,
        LEFT,
        RIGHT,
        REAR,
        TACTICAL,
        LONG_RANGE,
        STATUS
    }
    
    private static final int SYS_COUNT = SystemType.values().length;

    private BoolState mRedAlert, mShields;
    private int mShipNumber;
    private final float[] mHeat = new float[SYS_COUNT];
    private final float[] mSystems = new float[SYS_COUNT];
    private final int[] mCoolant = new int[SYS_COUNT];

    private final int[] mTorpedos = new int[LoadTubePacket.TORPEDO_COUNT];
    private final float[] mTubeTimes = new float[MAX_TUBES]; 
    private final int[] mTubeTypes = new int[MAX_TUBES];

    private float mEnergy;

    private int mDockingStation = 0;

    private MainScreen mMainScreen;

    private int mAvailableCoolant = DEFAULT_COOLANT;

    private float mImpulse;

    private float mSteering = 0.5f; // default to centered

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
        for (int i=0; i<SYS_COUNT; i++) {
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
    public int getSystemCoolant(SystemType sys) {
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
    public float getSystemEnergy(SystemType sys) {
//        return mSystems.containsKey(sys) 
//                ? mSystems.get(sys) 
//                : -1f;
        return mSystems[sys.ordinal()];
    }
    
    public float getSystemHeat(SystemType sys) {
//      return mSystems.containsKey(sys) 
//              ? mSystems.get(sys) 
//              : -1f;
      return mHeat[sys.ordinal()];
  }
    
    @Override
    public int getType() {
        return TYPE_PLAYER;
    }
    
    public boolean hasShieldsActive() {
        return mShields == BoolState.TRUE;
    }

    public boolean isRedAlert() {
        return mRedAlert == BoolState.TRUE;
    }

    @Override
    public String toString() {
        return String.format("[PLAYER#%d:%s:%d:%c]%s", 
                mShipNumber,
                mName,
                mHullId,
                isRedAlert() ? 'R' : '_',
                        super.toString());
    }

    public void setRedAlert(boolean newState) {
        mRedAlert = BoolState.from(newState);
    }

    public void setShields(boolean newState) {
        mShields = BoolState.from(newState);
    }

    public void setSystemCoolant(SystemType sys, int coolant) {
        mCoolant[sys.ordinal()] = coolant;
    }

    public void setSystemEnergy(SystemType sys, float energy) {
        mSystems[sys.ordinal()] = energy;
    }

    public void setSystemHeat(SystemType sys, float heat) {
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

    public int getTorpedoCount(int torpType) {
        return mTorpedos[torpType];
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

            if (mShipNumber == -1)
                mShipNumber = plr.mShipNumber;
            
            if (plr.mImpulse != -1)
                mImpulse = plr.mImpulse;
            if (plr.mSteering != -1)
                mSteering = plr.mSteering;

            if (plr.mDockingStation != 0)
                mDockingStation = plr.mDockingStation;
            
            if (plr.mRedAlert != BoolState.UNKNOWN)
                mRedAlert = plr.mRedAlert;

            if (plr.mShields != BoolState.UNKNOWN)
                mShields = plr.mShields;
            
            if (plr.mEnergy != -1)
                mEnergy = plr.mEnergy;
            
            if (plr.mAvailableCoolant != -1)
                mAvailableCoolant = plr.mAvailableCoolant;
            
            for (int i=0; i<SYS_COUNT; i++) {
                if (plr.mHeat[i] != -1)
                    mHeat[i] = plr.mHeat[i];
                
                if (plr.mSystems[i] != -1)
                    mSystems[i] = plr.mSystems[i];
                
                if (plr.mCoolant[i] != -1)
                    mCoolant[i] = plr.mCoolant[i];
            }

            for (int i=0; i<LoadTubePacket.TORPEDO_COUNT; i++) {
                if (plr.mTorpedos[i] != -1)
                    mTorpedos[i] = plr.mTorpedos[i];
            }

            for (int i=0; i<ArtemisPlayer.MAX_TUBES; i++) {
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
        }
    }

    public BoolState getRedAlertState() {
        return mRedAlert;
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
    public void setMainScreen(MainScreen screen) {
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

    public MainScreen getMainScreen() {
        return mMainScreen;
    }
    
    /** Get the value of the impulse slider */
    public float getImpulse() {
        return mImpulse;
    }

    public void setImpulse(float impulseSlider) {
        mImpulse = impulseSlider;
    }
    
    public float getSteering() {
        return mSteering;
    }

    public void setSteering(float steeringSlider) {
        mSteering = steeringSlider;
    }

}
