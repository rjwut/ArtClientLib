package net.dhleong.acl.world;

import java.util.HashMap;

import net.dhleong.acl.net.EngSetEnergyPacket.SystemType;


public class ArtemisPlayer extends BaseArtemisObject {

    private boolean mRedAlert;
    private final HashMap<SystemType, Float> mSystems = new HashMap<SystemType, Float>();
    private final HashMap<SystemType, Integer> mCoolant = new HashMap<SystemType, Integer>();

    public ArtemisPlayer(int objId, String name, boolean redAlert) {
        super(objId, name);
        
        mRedAlert = redAlert;
    }
    
    /**
     * Get coolant setting for a system, if we have it
     * @param sys
     * @return The setting as an int [0, 8], or -1 if we don't know
     */
    public int getSystemCoolant(SystemType sys) {
        return mCoolant.containsKey(sys)
                ? mCoolant.get(sys)
                : -1;
    }
    
    /**
     * Get the energy setting for a system, if we have it
     * @param sys
     * @return The setting as a float [0, 1] where 1f == 300%,
     *  or -1 if we don't know
     */
    public float getSystemEnergy(SystemType sys) {
        return mSystems.containsKey(sys) 
                ? mSystems.get(sys) 
                : -1f;
    }
    
    @Override
    public int getType() {
        return TYPE_PLAYER;
    }
    
    public boolean isRedAlert() {
        return mRedAlert;
    }

    @Override
    public String toString() {
        return String.format("[PLAYER:%s:%b]", mName, mRedAlert);
    }

    public void setRedAlert(boolean newState) {
        mRedAlert = newState;
    }

    public void setSystemCoolant(SystemType sys, int coolant) {
        mCoolant.put(sys, coolant);
    }

    public void setSystemEnergy(SystemType sys, float energy) {
        mSystems.put(sys, energy);
    }
}
